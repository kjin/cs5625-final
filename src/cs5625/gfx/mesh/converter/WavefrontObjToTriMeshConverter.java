/*
 * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  Copyright (c) 2015, Department of Computer Science, Cornell University.
 *
 *  This code repository has been authored collectively by:
 *  Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  Pramook Khungurn (pk395), and Sean Ryan (ser99)
 */

package cs5625.gfx.mesh.converter;

import cs5625.gfx.gldata.FileTexture2DData;
import cs5625.gfx.gldata.PosTexNorTanData;
import cs5625.gfx.gldata.Texture2DData;
import cs5625.gfx.gldata.VertexData;
import cs5625.gfx.material.BlinnPhongMaterial;
import cs5625.gfx.material.LambertianMaterial;
import cs5625.gfx.material.Material;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.util.IOUtil;
import cs5625.util.StringUtil;
import org.apache.commons.io.FilenameUtils;

import javax.vecmath.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WavefrontObjToTriMeshConverter {
    /**
     * Loads models from a wavefront object (.obj) file.
     * <p/>
     * Each object defined in the file (with the 'o' command) will be loaded as a separate Geometry object,
     * and each material (specified with the 'mtllib' and 'usemtl' commands) will be loaded as a separate
     * mesh. This loader does not support relative (negative) indexing.
     * <p/>
     * Polygons specified using the 'f' command will be triangulated and stored in the triangle mesh.
     * Edges (specified with the 'f' command followed by only 2 vertices) will be stored in the corresponding
     * mesh's edge data buffer.
     * <p/>
     * The 'mtllib' command will look for material files in the same directory as the model file, loading them
     * with `Material.load()`.
     *
     * @param fileName      the model file to load.
     *                      Typically of the form "models/foo.obj".
     * @param loadMaterials If true, material and texture files referenced from the .obj model will be loaded, and
     *                      errors generated if they cannot be found. If false, all objects are left with the default material.
     * @param centerObjects All vertices in the .obj file are in the same coordinate frame. If
     *                      `centerObjects` is true, each object is positioned at the average position of its
     *                      vertices, and that average position is subtracted from its vertices. The objects will
     *                      render in the same place, but will translate and rotate about their own centers, instead
     *                      of about the overall scene origin. If false, positions are not modified.
     * @return A list of geometry objects from the model file. Null if the file doesn't exist.
     */
    public static ArrayList<TriMesh> load(String fileName, boolean loadMaterials, boolean centerObjects) throws IOException {
        /* Declare temporary lists used to store all the data read from the file before indexing
         * and breaking up into individual mesh parts. */
        ArrayList<Vector3f> allVertices = new ArrayList<Vector3f>();
        ArrayList<Vector3f> allNormals = new ArrayList<Vector3f>();
        ArrayList<Vector2f> allTexcoords = new ArrayList<Vector2f>();
        ArrayList<Point3i[]> allPolygons = new ArrayList<Point3i[]>();
        ArrayList<Point2i> allEdges = new ArrayList<Point2i>();

		/* Lists to store the separations between objects specified in the model file. */
        ArrayList<Integer> meshDelimiters = new ArrayList<Integer>();
        ArrayList<String> meshNames = new ArrayList<String>();

		/* Lists to store the separations between meshes (materials) specified in the model file. */
        ArrayList<Integer> meshPartDelimiters = new ArrayList<Integer>();
        ArrayList<String> meshPartMaterialNames = new ArrayList<String>();
        ArrayList<Integer> edgeDelimeters = new ArrayList<Integer>();

		/* Map to store all materials loaded from the file. */
        HashMap<String, Material> allMaterials = new HashMap<String, Material>();

		/* Read the file into those temporary buffers. */
        parseRawOBJfile(
                fileName, loadMaterials,
                allVertices, allNormals, allTexcoords, allPolygons, allEdges,
                meshDelimiters, meshNames, meshPartDelimiters, meshPartMaterialNames, edgeDelimeters, allMaterials);

        /* PHASE 2 -- reassemble temporary buffers into our desired data structures. */

		/* Create a list to hold the results. */
        ArrayList<TriMesh> results = new ArrayList<TriMesh>();

		/* Keep track of our current index into the mesh delimeter arrays. */
        int meshPartIndex = 0;

		/* Loop and create each object. */
        for (int meshIndex = 0; meshIndex < meshDelimiters.size() - 1; ++meshIndex) {
            /* Create this object and gets its bounds in the index list. */
            int meshLastIndex = meshDelimiters.get(meshIndex + 1);

			/* Keep track of the average vertex position in this object. */
            Point3f currentPositionAverage = new Point3f(0.0f, 0.0f, 0.0f);

            /* The wavefront object format indexes vertices, normals, and texture coordinates separately, but
                 * OpenGL indexes them together. To handle that, we have to find all unique "vIndex/txIndex/nIndex" triples
				 * and make a vertex in the trimesh for each one. */
            HashMap<Point3i, Integer> uniqueVertices = new HashMap<Point3i, Integer>();
            int oldMeshPartIndex = meshPartIndex;
            while (meshPartIndex < meshPartDelimiters.size() - 1 &&
                    meshPartDelimiters.get(meshPartIndex + 1) <= meshLastIndex) {
                /* Grab first and last indices in this mesh part. */
                int meshPartFirstIndex = meshPartDelimiters.get(meshPartIndex);
                int meshPartLastIndex = meshPartDelimiters.get(meshPartIndex + 1);

                /* Skip empty meshes. */
                if (meshPartFirstIndex == meshPartLastIndex) {
                    ++meshPartIndex;
                    continue;
                }

                for (int meshVertexIndex = meshPartFirstIndex; meshVertexIndex < meshPartLastIndex; ++meshVertexIndex) {
					/* Map each index triplet to the index at which it first appears. */
                    Point3i[] poly = allPolygons.get(meshVertexIndex);

					/* Add each {v, t, n} index triplet of this polygon to the unique list of index triplets. */
                    for (Point3i indexSet : poly) {
                        if (!uniqueVertices.containsKey(indexSet)) {
                            uniqueVertices.put(indexSet, uniqueVertices.size());
                        }
                    }
                }

                /* Move to the next mesh defined in the file. */
                ++meshPartIndex;
            }

            TriMesh triMesh = new TriMesh();
            TriMesh.Builder builder = triMesh.startBuild();

            /* Now loop over the unique index triplets and make a vertex for each one. */
            int currentPositionCount = 0;
            for (Point3i indexSet : uniqueVertices.keySet()) {
                /* The index triplet is {position, texcoord, normal}, so retrieve each of those. */
                Vector3f vertex = allVertices.get(indexSet.x);
                Vector2f texcoord = allTexcoords.get(indexSet.y);
                Vector3f normal = allNormals.get(indexSet.z);

                /* Look up the index of this triplet into the resulting vertices. */
                //int vIndex = uniqueVertices.get(indexSet);
                uniqueVertices.put(indexSet, currentPositionCount);

                /* Store the vertex data into the mesh buffers. */
                builder.addPosition(vertex);
                builder.addTexCoord(texcoord);
                builder.addNormal(normal);

                /* Accumulate in position average. */
                currentPositionAverage.add(vertex);
                ++currentPositionCount;
            }

			/* Loop over each mesh in this object. */
            meshPartIndex = oldMeshPartIndex;
            while (meshPartIndex < meshPartDelimiters.size() - 1 &&
                    meshPartDelimiters.get(meshPartIndex + 1) <= meshLastIndex) {
				/* Grab first and last indices in this mesh. */
                int meshPartFirstIndex = meshPartDelimiters.get(meshPartIndex);
                int meshPartLastIndex = meshPartDelimiters.get(meshPartIndex + 1);

				/* Skip empty meshes. */
                if (meshPartFirstIndex == meshPartLastIndex) {
                    ++meshPartIndex;
                    continue;
                }

				/* If not all polygons are quads, we need to know how many triangles they break into. */
                int triangleCount = 0;

				/* Create a triangle mesh, triangulate polygons, and fill index buffer. */

                for (int meshVertexIndex = meshPartFirstIndex; meshVertexIndex < meshPartLastIndex; ++meshVertexIndex) {
                    Point3i poly[] = allPolygons.get(meshVertexIndex);
                    triangleCount += poly.length - 2;
                    for (int i = 2; i < poly.length; ++i) {
                        builder.addTriangle(uniqueVertices.get(poly[0]),
                                uniqueVertices.get(poly[i - 1]),
                                uniqueVertices.get(poly[i]));
                    }
                }


				/* Assign the mesh's name and material. */
                String meshMaterialName = meshPartMaterialNames.get(meshPartIndex);

                if (loadMaterials && allMaterials.containsKey(meshMaterialName)) {
                    builder.addPart(new Value<Material>(allMaterials.get(meshMaterialName)), triangleCount);
                } else {
                    builder.addPart(new Value<Material>(new LambertianMaterial()), triangleCount);
                }

				/* Move to the next mesh defined in the file. */
                ++meshPartIndex;
            }

            builder.setName(meshNames.get(meshIndex));
            builder.endBuild();

			/* If we are supposed to re-center each object, do that now. */
            if (centerObjects) {
                currentPositionAverage.scale(1.0f / currentPositionCount);
                PosTexNorTanData vertices = (PosTexNorTanData) (
                        (Value<VertexData>) triMesh.getVertexData()).get();
                int vertexCount = vertices.getVertexCount();
                Point3f p = new Point3f();
                for (int v = 0; v < vertexCount; ++v) {
                    vertices.getPosition(v, p);
                    p.sub(currentPositionAverage);
                    vertices.setPosition(v, p);
                }
                vertices.bumpVersion();
            }

			/* Add the finished object. */
            if (triMesh.getTriangleCount() > 0)
                results.add(triMesh);
        }

        return results;
    }

    private static void parseRawOBJfile(
            String fileName,
            boolean loadMaterials,
            ArrayList<Vector3f> allVertices,
            ArrayList<Vector3f> allNormals,
            ArrayList<Vector2f> allTexcoords,
            ArrayList<Point3i[]> allPolygons,
            ArrayList<Point2i> allEdges,
            ArrayList<Integer> meshDelimiters,
            ArrayList<String> meshNames,
            ArrayList<Integer> meshPartDelimiters,
            ArrayList<String> meshPartMaterialNames,
            ArrayList<Integer> edgeDelimeters,
            HashMap<String, Material> allMaterials) throws IOException {
		/* Open the file. */
        String content = IOUtil.readTextFile(fileName);
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String directory = new File(fileName).getAbsoluteFile().getParent();

		/* Dummy entries at index 0 so that the 1-based indexing of the OBJ format can be used directly. */
        allVertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        allNormals.add(new Vector3f(0.0f, 0.0f, 0.0f));
        allTexcoords.add(new Vector2f(0.0f, 0.0f));

		/* Add a default object and material, in case none is declared. */
        meshDelimiters.add(allPolygons.size());
        meshNames.add(fileName);

        meshPartDelimiters.add(allPolygons.size());
        edgeDelimeters.add(allEdges.size());

        meshPartMaterialNames.add("");
        allMaterials.put("", new BlinnPhongMaterial());

		/* Read each line and add the parsed data to the appropriate temporary list. */
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();

            if (line.length() == 0) {
				/* Skip blank lines. */
            } else if (line.charAt(0) == '#') {
				/* Skip comment lines. */
            } else {
				/* Split the line on whitespace to identify the command. */
                String words[] = StringUtil.splitString(line, " \t\n\r", false);

                if (words[0].equals("v")) {
					/* Parse the 3 coordinates of the vertex and add to vertices list. */
                    allVertices.add(new Vector3f(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3])));
                } else if (words[0].equals("vn")) {
					/* Parse the 3 coordinates of the normal and add to normals list. */
                    allNormals.add(new Vector3f(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3])));
                } else if (words[0].equals("vt")) {
					/* Parse the 2 coordinates of the texture coordinate and add to texcoords list. */
                    allTexcoords.add(new Vector2f(Float.parseFloat(words[1]), Float.parseFloat(words[2])));
                } else if (words[0].equals("f")) {
					/* This is a face command. In the .obj format, faces may be arbitrary-sized polygons. This framework
					 * only supports triangle and quad meshes; any mesh which contains only 4-gons will be loaded into a
					 * Quadmesh, and all others will be triangulated and loaded into a Trimesh. */

					/* If the face is really an edge, add it to the edges array, otherwise add to faces. */
                    if (words.length == 3) {
                        allEdges.add(new Point2i(parseOBJIndices(words[1]).x, parseOBJIndices(words[2]).x));
                    } else if (words.length > 3) {
                        Point3i polygon[] = new Point3i[words.length - 1];

                        for (int i = 1; i < words.length; ++i) {
                            polygon[i - 1] = parseOBJIndices(words[i]);
                        }

                        allPolygons.add(polygon);
                    }
                } else if (words[0].equals("o")) {
					/* Record the number of indices up until this new mesh. */
                    meshDelimiters.add(allPolygons.size());
                    meshNames.add(line.substring("o".length()).trim());

					/* Start a new mesh at the same point as this new mesh. */
                    meshPartDelimiters.add(allPolygons.size());
                    meshPartMaterialNames.add(meshPartMaterialNames.get(meshPartMaterialNames.size() - 1));

					/* Remember which edges go to which mesh. */
                    edgeDelimeters.add(allEdges.size());
                } else if (words[0].equals("mtllib") && loadMaterials) {
					/* Try to load the named material file, assumed to be in the same directory as the model. */
                    String mtlFileName = line.substring("mtllib".length()).trim();
                    allMaterials.putAll(loadMaterial(directory + File.separator + mtlFileName));
                } else if (words[0].equals("usemtl")) {
					/* Record the number of indices up to this new material. */
                    meshPartDelimiters.add(allPolygons.size());
                    meshPartMaterialNames.add(line.substring("usemtl".length()).trim());

					/* Remember which edges go to which mesh. */
                    edgeDelimeters.add(allEdges.size());
                } else {
					/* Silently ignore unknown commands. */
                }
            }

			/* Read the next line until there are no more. */
            line = reader.readLine();
        }

		/* Add dummy entries to the delimeter lists to simplify the loop logic in the next step. */
        meshDelimiters.add(allPolygons.size());
        meshPartDelimiters.add(allPolygons.size());
        edgeDelimeters.add(allEdges.size());
    }

    /**
     * Helper function to parse a face vertex index triplet string from a wavefront obj file.
     *
     * @param str The index triplet, of the format "v/t/n", where 'v' is the vertex index, 't' is
     *            the texture coordinate index, and 'n' is the normal index. The texture coordinate
     *            and normal indices may both be omitted, so "v" and "v/t" and "v//n" are both also
     *            valid forms. The indices of any omitted fields are assigned 0 (which is an invalid
     *            index in the 1-based OBJ format).
     * @return The three indices parsed from 'str'.
     * @throws IOException If 'str' does not have 1, 2, or 3 slash-separated fields.
     */
    private static Point3i parseOBJIndices(String str) throws IOException {
        String indices[] = StringUtil.splitString(str, "/", true);

        switch (indices.length) {
            case 1:
                return new Point3i(parseIntDefaultZero(indices[0]), 0, 0);
            case 2:
                return new Point3i(parseIntDefaultZero(indices[0]), parseIntDefaultZero(indices[1]), 0);
            case 3:
                return new Point3i(parseIntDefaultZero(indices[0]), parseIntDefaultZero(indices[1]), parseIntDefaultZero(indices[2]));
        }

        throw new IOException("Malformed face vertex specification '" + str + "'.");
    }

    /**
     * Helper function for parsing OBJ index fields; parses the string into an integer,
     * defaulting to 0 for an empty string.
     */
    private static int parseIntDefaultZero(String str) {
        if (str.length() > 0) {
            return Integer.parseInt(str);
        } else {
            return 0;
        }
    }

    /**
     * Returns an array of all {v, t, n} vertices which have a given {v} position.
     *
     * @param uniqueVertices      The map of {v, t, n} index triplets to final vertex indices.
     * @param vertexPositionIndex The {v} value to search for.
     * @return List of all final vertex indices whose {v} value equals vertexPositionIndex.
     */
    private static ArrayList<Integer> findAllVerticesWithPositionIndex(HashMap<Point3i, Integer> uniqueVertices, int vertexPositionIndex) {
        ArrayList<Integer> results = new ArrayList<Integer>();

        for (Point3i indexTriplet : uniqueVertices.keySet()) {
            if (indexTriplet.x == vertexPositionIndex) {
                results.add(uniqueVertices.get(indexTriplet));
            }
        }

        return results;
    }

    /**
     * Loads materials from a .mtl file.
     *
     * @param fileName the material file to load
     * @return A map containing the named materials loaded from the file, keyed on material name.
     */
    public static Map<String, Material> loadMaterial(String fileName) throws IOException {
		/* Open the file. */
        String content = IOUtil.readTextFile(fileName);
        Map<String, Material> materialsMap = new HashMap<String, Material>();
        BlinnPhongMaterial currentMaterial = null;
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String directory = new File(fileName).getAbsoluteFile().getParent();

		/* Read and parse each line. */
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();

            if (line.length() == 0) {
				/* Skip blank lines. */
            } else if (line.charAt(0) == '#') {
				/* Skip comment lines. */
            } else {
				/* Split the line on whitespace to identify the command. */
                String words[] = StringUtil.splitString(line, " \t\n\r", false);

                if (words[0].equals("newmtl")) {
					/* Create the material. All materials here are Blinn-Phong. */
                    currentMaterial = new BlinnPhongMaterial();
                    String materialName = line.substring("newmtl".length()).trim();
                    materialsMap.put(materialName, currentMaterial);
                } else if (words[0].equals("Kd")) {
					/* Set diffuse color. */
                    currentMaterial.setDiffuseColor(new Color4f(
                            Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3]), 1.0f));
                } else if (words[0].equals("Ks")) {
					/* Set specular color. */
                    currentMaterial.setSpecularColor(new Color3f(
                            Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3])));
                } else if (words[0].equals("Ns")) {
					/* Set shininess. */
                    currentMaterial.setExponent(Float.parseFloat(words[1]));
                } else if (words[0].equals("map_Kd")) {
					/* Set diffuse texture. */
                    String textureName = line.substring("map_Kd".length()).trim();
                    textureName = FilenameUtils.separatorsToUnix(new File(directory + File.separator + textureName).getAbsolutePath());
                    String key = ObjectCacheKey.makeKey(FileTexture2DData.class, textureName);
                    currentMaterial.setDiffuseTexture(new Reference<Texture2DData>(key));
                } else if (words[0].equals("map_Ks")) {
					/* Set diffuse texture. */
                    String textureName = line.substring("map_Ks".length()).trim();
                    textureName = FilenameUtils.separatorsToUnix(new File(directory + File.separator + textureName).getAbsolutePath());
                    String key = ObjectCacheKey.makeKey(FileTexture2DData.class, textureName);
                    currentMaterial.setDiffuseTexture(new Reference<Texture2DData>(key));
                } else {
					/* Other commands ignored. */
                }
            }

            line = reader.readLine();
        }

		/* Done. */
        return materialsMap;
    }
}
