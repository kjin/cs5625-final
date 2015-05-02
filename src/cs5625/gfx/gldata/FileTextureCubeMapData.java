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

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import cs5625.gfx.glcache.GLResourceCache;
import cs5625.gfx.glcache.GLResourceRecord;
import cs5625.gfx.objcache.LoadableByKey;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.objcache.Value;
import cs5625.jogl.TextureCubeMap;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import java.awt.image.BufferedImage;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTextureCubeMapData implements TextureCubeMapData {
    private static Logger logger = LoggerFactory.getLogger(TextureCubeMapData.class);
    private TextureData[] data = new TextureData[6];
    private int version = 0;
    private String[] fileNames;

    public FileTextureCubeMapData() {
        // NOP
    }

    private TextureData loadFromFile(String fileName) {
        try {
            TextureData data;
            File file = new File(fileName);
            BufferedImage image = ImageIO.read(file);
            if (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase().equals("png")) {
                //ImageUtil.flipImageVertically(image);
                data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
            } else {
                data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
                if (data.getMustFlipVertically()) {
                    image = ImageIO.read(file);
                    ImageUtil.flipImageVertically(image);
                    data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
                }
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getVersion() {
        return version;
    }

    public void bumpVersion() {
        version++;
    }

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        TextureCubeMap texture = null;
        boolean needUpdate = false;
        if (record.resource == null) {
            texture = new TextureCubeMap(gl, GL2.GL_RGBA);
            record.resource = texture;
            needUpdate = true;
        } else if (record.version != this.version) {
            texture = (TextureCubeMap) record.resource;
            needUpdate = true;
        }
        if (needUpdate) {
            for (int i = 0; i < 6; i++) {
                logger.debug("texture file name (" + i + ") = " + fileNames[i]);
            }
            texture.setImages(data);
            record.version = version;
            record.sizeInBytes = data[0].getWidth() * data[0].getHeight() * 4 * 6;
        }
    }

    @Override
    public TextureCubeMap getGLResource(GL2 gl) {
        return (TextureCubeMap) GLResourceCache.v().getGLResource(gl, this);
    }

    @Override
    public void loadByKey(String key) {
        String keyData  = ObjectCacheKey.getKeyData(key);
        fileNames = keyData.split(ObjectCacheKey.PATH_SEPARATOR);
        if (fileNames.length != 6) {
            throw new RuntimeException("key does not contains at least 6 file names");
        }
        for (int i = 0; i < 6; i++) {
            data[i] = loadFromFile(fileNames[i]);
        }
        bumpVersion();
    }

    @Override
    public <T extends LoadableByKey> Value<T> wrap(Class<T> klass) {
        return new Value<T>((T)this);
    }

    public static Reference<TextureCubeMapData> makeReference(String f0, String f1, String f2,
                                                       String f3, String f4, String f5) {
        String s = ObjectCacheKey.PATH_SEPARATOR;
        String key = ObjectCacheKey.makeKey(FileTextureCubeMapData.class,
                f0 + s + f1 + s + f2 + s + f3 + s + f4 + s + f5);
        return new Reference<TextureCubeMapData>(key);
    }
}
