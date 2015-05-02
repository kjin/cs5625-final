/*
 *
 *  * Written for Cornell CS 5625 (Interactive Computer Graphics).
 *  * Copyright (c) 2015, Department of Computer Science, Cornell University.
 *  *
 *  * This code repository has been authored collectively by:
 *  * Ivaylo Boyadzhiev (iib2), John DeCorato (jd537), Asher Dunn (ad488),
 *  * Pramook Khungurn (pk395), Steve Marschner (srm2), and Sean Ryan (ser99)
 *
 */

package cs5625.gfx.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs5625.gfx.objcache.LoadableByKey;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Value;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cs5625.gfx.gldata.FileProgramData;
import cs5625.gfx.gldata.FileTexture2DData;
import cs5625.gfx.gldata.IndexData;
import cs5625.gfx.gldata.PosTexNorTanData;
import cs5625.gfx.gldata.ProgramData;
import cs5625.gfx.gldata.Texture2DData;
import cs5625.gfx.gldata.VertexData;
import cs5625.gfx.objcache.Reference;
import cs5625.jogl.Program;
import cs5625.jogl.Texture2D;
import cs5625.jogl.TextureUnit;
import cs5625.jogl.Vbo;

public class Font implements LoadableByKey {
	private static Logger logger = LoggerFactory.getLogger(Font.class);
	public static final int JUSTIFY_LEFT = 0;
	public static final int CENTER = 1;
	public static final int JUSTIFY_RIGHT = 2;
	
	private String textureFileName, metricsFileName;
	private String name;
	private int rasterSize;
	private float texFontSize;
	private HashMap<Integer, CharInfo> charInfo;
	private Texture2DData glyphSDData;

	/**
	 * The default constructor, which does nothing. Created so that the object can be loaded by the cache system.
	 */
	public Font() {
		// NOP
	}

	/**
	 * Create a font object given the basename of the font files.
	 * @param fontFilesBasename the basename (no extension but with directory) of the font files
	 */
	public Font(String fontFilesBasename) {
		load(fontFilesBasename);
	}

	/**
	 * Create a reference that can be loaded by the cache system from the given basename of font files
	 * @param fontFilesBasename the basename (no extension, but with directory) of the font files
	 * @return the reference to the font
	 */
	public static Reference<Font> makeReference(String fontFilesBasename) {
		return new Reference<Font>(Font.class, fontFilesBasename);
	}

	/**
	 * Load the font data given the basename of the font files.
	 * @param fontFilesBasename the basename (no extension, but with directory) of the font files
	 */
	private void load(String fontFilesBasename) {
		textureFileName = FilenameUtils.separatorsToUnix(new File(fontFilesBasename + ".png").getAbsolutePath());
		metricsFileName = FilenameUtils.separatorsToUnix(new File(fontFilesBasename + ".txt").getAbsolutePath());

		// Load texture data
		glyphSDData = new FileTexture2DData(textureFileName, false);

		// Read metrics from file
		readMetrics(new File(metricsFileName));
	}

	@Override
	public void loadByKey(String key) {
		String fontFilePrefix = ObjectCacheKey.getKeyData(key);
		load(fontFilePrefix);
	}

	@Override
	public <T extends LoadableByKey> Value<T> wrap(Class<T> klass) {
		return new Value<T>((T)this);
	}

	private static class CharInfo {
		// Bounding box of character in texture coordinates
		float x, y, w, h;
		// Offset from lower-left of bounding box to reference point (multiple of font height)
		float xOffset, yOffset;
		// Horizontal distance to advance to following character (multiple of font height)
		float advanceWidth;

		public String toString() {
			return " x=" + x + " y=" + y + " w=" + w + " h=" + h +
					" xOffset=" + xOffset + " yOffset=" + yOffset + " advanceWidth=" + advanceWidth;
		}
	}
	
	/**
	 * Draw a string into the current OpenGL context.
	 * @param gl The context for drawing
	 * @param s The string to render
	 * @param loc The point where the string is located.  Is updated to the end of the string
	 * @param fontSize The font size
	 * @param hAlign How the string is positioned relative to its location
	 */
	public void renderString(GL2 gl, Matrix4f m, String s, Vector2f loc, float fontSize, int hAlign) {
		if (hAlign == JUSTIFY_RIGHT)
			loc.x -= stringWidth(s, fontSize);
		else if (hAlign == CENTER)
			loc.x -= stringWidth(s, fontSize) / 2.0f;
		for (int i = 0; i < s.length(); i++) 
			renderCharacter(gl, m, s.charAt(i), loc, fontSize);
	}

	/**
	 * Compute the width of a string in this font.  That is, the distance from the reference point
	 * of the first character to the reference point where a character following the last character
	 * of s should be drawn.
	 * @param s The string to be drawn
	 * @param fontSize The character height of the font
	 * @return The width of the string.
	 */
	public float stringWidth(String s, float  fontSize) {
		float sum = 0;
		for (int i = 0; i < s.length(); i++) 
			sum += charInfo.get((int) s.charAt(i)).advanceWidth * fontSize;
		return sum;
	}

	private Reference<ProgramData> textProgramRef = FileProgramData.makeReference(
            "src/shaders/text.vert", "src/shaders/text.frag");

	private void renderCharacter(GL2 gl, Matrix4f m, char c, Vector2f loc, float fontSize) {
        CharInfo ci = charInfo.get((int) c);
        if (ci == null) return;
        Program program = textProgramRef.get().getGLResource(gl);
        Texture2D tex = glyphSDData.getGLResource(gl);
        tex.useWith(TextureUnit.getTextureUnit(gl, 0));
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        program.use();
        program.getUniform("glyphSDData").set1Int(0);
        program.getUniform("textColor").setTuple3(new Vector3f(1.0f, 1.0f, 1.0f));
        program.getUniform("projMatrix").setMatrix4(m);
        float r = fontSize / texFontSize;
        float left = loc.x + ci.xOffset * fontSize;
        float right = left + ci.w * r;
        float bottom = loc.y - ci.yOffset * fontSize;
        float top = bottom + ci.h * r;
        drawQuad(gl, program, left, right, bottom, top, ci.x, ci.y, ci.w, ci.h);
        program.unuse();
        tex.unuse();
        loc.x += ci.advanceWidth * fontSize;
	}

    PosTexNorTanData quadMeshVertices = new PosTexNorTanData();
    IndexData quadMeshIndices;
    
    // l, r, b, t is the rectangle in object space; x, y, w, h is the rectangle in texture space.
    private void drawQuad(GL2 gl, Program program, float l, float r, float b, float t, float x, float y, float w, float h) {
    	quadMeshVertices.startBuild()
                .addPosition(l, b, 0).addTexCoord(x, y)
                .addPosition(r, b, 0).addTexCoord(x+w, y)
                .addPosition(r, t, 0).addTexCoord(x+w, y+h)
                .addPosition(l, t, 0).addTexCoord(x, y+h)
                .endBuild();
        Vbo vertexBuffer = quadMeshVertices.getGLResource(gl);
        vertexBuffer.bind();
        program.getAttribute("vert_position").setup(gl, quadMeshVertices.getAttributeSpec("vert_position")).enable();
        program.getAttribute("vert_texCoord").setup(gl, quadMeshVertices.getAttributeSpec("vert_texCoord")).enable();
        vertexBuffer.unbind();
        if (quadMeshIndices == null)
            quadMeshIndices = new IndexData().startBuild().add(0).add(1).add(2).add(0).add(2).add(3).endBuild();
        Vbo indexBuffer = quadMeshIndices.getGLResource(gl);
        indexBuffer.bind();
        gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_INT, 0);
        indexBuffer.unbind();
    }

	/**
	 * Convert character information from the top-left based pixel units used in the input to 
	 * texture-relative and font-size-relative units.
	 * @param ci
	 */
	private void convertCharInfo(CharInfo ci) {
		// First flip the y coordinate
		ci.y = glyphSDData.getHeight() - ci.y - ci.h;
		ci.yOffset = ci.h - ci.yOffset;
		
		// Convert bbox to [0,1] texture coordinates
		ci.x = ci.x / glyphSDData.getWidth();
		ci.w = ci.w / glyphSDData.getWidth();
		ci.y = ci.y / glyphSDData.getHeight();
		ci.h = ci.h / glyphSDData.getHeight();
		
		// Convert offsets and width to be font-size relative
		ci.xOffset = ci.xOffset / rasterSize;
		ci.yOffset = ci.yOffset / rasterSize;
		ci.advanceWidth = ci.advanceWidth / rasterSize;
	}
	
	/**
	 * Read the font metric information from a file generated by the SDFont tool.  This information
	 * tells where to find each glyph in the signed distance texture and where the reference point for
	 * each character is.
	 * @param file
	 */
	private void readMetrics(File file) {
		String linePat = 
				"char id=(\\d+)\\s+x=(\\d+)\\s+y=(\\d+)\\s+width=(\\d+)\\s+height=(\\d+)\\s+" +
				"xoffset=(-?\\d*\\.\\d*)\\s+yoffset=(-?\\d*\\.\\d*)\\s+xadvance=(-?\\d*\\.\\d*)\\s+" +
				"page=(\\d+)\\s+chnl=(\\d+)\\s*";
		
		Matcher m;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String line = in.readLine();
			m = Pattern.compile("info face=\"(.*)\"").matcher(line);
			if (!m.matches()) {
				logger.error("Failure reading " + file + " at line 1");
				in.close();
				return;
			}
			name = m.group(1);
			line = in.readLine();
			m = Pattern.compile("info size=(\\d*)").matcher(line);
			if (!m.matches()) {
				logger.error("Failure reading " + file + " at line 2");
				in.close();
				return;
			}
			rasterSize = Integer.parseInt(m.group(1));
			texFontSize = rasterSize / (float) glyphSDData.getHeight();
			line = in.readLine();
			m = Pattern.compile("chars count=(\\d*)").matcher(line);
			if (!m.matches()) {
				logger.error("Failure reading " + file + " at line 3");
				in.close();
				return;
			}
			charInfo = new HashMap<Integer, CharInfo>();
			int numChars = Integer.parseInt(m.group(1));
			Pattern p = Pattern.compile(linePat);
			for (int i = 0; i < numChars; i++) {
				line = in.readLine();
				m = p.matcher(line);
				if (!m.matches()) {
					logger.error("Failure reading " + file + " at line " + (i+4));
					in.close();
					return;
				}
				int charID = Integer.parseInt(m.group(1));
				CharInfo ci = new CharInfo();
				ci.x = Integer.parseInt(m.group(2));
				ci.y = Integer.parseInt(m.group(3));
				ci.w = Integer.parseInt(m.group(4));
				ci.h = Integer.parseInt(m.group(5));
				ci.xOffset = Float.parseFloat(m.group(6));
				ci.yOffset = Float.parseFloat(m.group(7));
				ci.advanceWidth = Float.parseFloat(m.group(8));
				convertCharInfo(ci);
				charInfo.put(charID, ci);
			}
			logger.debug("read font " + name + " containing " + numChars + " characters.");
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
