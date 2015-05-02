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

package cs5625.gfx.gldata;

import cs5625.gfx.glcache.GLResourceCache;
import cs5625.gfx.glcache.GLResourceProvider;
import cs5625.gfx.glcache.GLResourceRecord;
import cs5625.gfx.objcache.LoadableByKey;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.objcache.Value;
import cs5625.jogl.Program;
import cs5625.util.IOUtil;

import javax.media.opengl.GL2;

public class FileProgramData implements ProgramData {
    private int version = 0;
    private String vertexShaderSource;
    private String fragmentShaderSource;
    private String vertexSourceFile;
    private String fragmentSourceFile;

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        boolean needUpdate = record.resource == null || record.version != this.version;
        if (needUpdate) {
            record.sizeInBytes = 0;
            record.resource = new Program(gl,
                    vertexShaderSource, vertexSourceFile,
                    fragmentShaderSource, fragmentSourceFile);
            record.version = this.version;
        }
    }

    @Override
    public Program getGLResource(GL2 gl) {
        return (Program) GLResourceCache.v().getGLResource(gl, this);
    }

    public int getVersion() {
        return version;
    }

    public void bumpVersion() {
        version++;
    }

    public String getVertexShaderSource() {
        return vertexShaderSource;
    }

    public String getFragmentShaderSource() {
        return fragmentShaderSource;
    }

    private void setSourceFromFiles(String vertexShaderFile, String fragmentShaderFile) {
        try {
            String vertexShaderSource = IOUtil.readTextFile(vertexShaderFile);
            String fragmentShaderSource = IOUtil.readTextFile(fragmentShaderFile);
            this.vertexShaderSource = vertexShaderSource;
            this.fragmentShaderSource = fragmentShaderSource;
            this.vertexSourceFile = vertexShaderFile;
            this.fragmentSourceFile = fragmentShaderFile;
            bumpVersion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FileProgramData createFromFile(String vertexShaderFile, String fragmentShaderFile) {
        FileProgramData result = new FileProgramData();
        result.setSourceFromFiles(vertexShaderFile, fragmentShaderFile);
        return result;
}

    public void loadByKey(String key) {
        String programFileNames = ObjectCacheKey.getKeyData(key);
        String[] comps = programFileNames.split(ObjectCacheKey.PATH_SEPARATOR);
        setSourceFromFiles(comps[0], comps[1]);
    }

    public <T extends LoadableByKey> Value<T> wrap(Class<T> klass) {
        return new Value<T>((T) this);
    }

    public static Reference<ProgramData> makeReference(String vertexSourceFile, String fragmentSourceFile) {
        return new Reference<ProgramData>(ObjectCacheKey.makeKey(
                FileProgramData.class,
                vertexSourceFile + ObjectCacheKey.PATH_SEPARATOR + fragmentSourceFile));
    }

}
