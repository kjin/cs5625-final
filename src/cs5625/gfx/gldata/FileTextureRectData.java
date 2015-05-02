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
import cs5625.gfx.objcache.Value;
import cs5625.jogl.Texture2D;
import cs5625.jogl.TextureRect;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import java.awt.image.BufferedImage;
import java.io.File;

public class FileTextureRectData implements TextureRectData {
    private static Logger logger = LoggerFactory.getLogger(FileTextureRectData.class);
    private TextureData data;
    private int version = 0;
    private String fileName;
    private boolean srgb;

    public FileTextureRectData() {
        // NOP
    }

    public FileTextureRectData(String fileName, boolean srgb) {
        this.fileName = FilenameUtils.separatorsToUnix(new File(fileName).getAbsolutePath());
    	this.srgb = srgb;
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            File file = new File(fileName);
            BufferedImage image = ImageIO.read(file);
            if (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase().equals("png")) {
                ImageUtil.flipImageVertically(image);
                data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
            } else {
                data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
                if (data.getMustFlipVertically()) {
                    image = ImageIO.read(file);
                    ImageUtil.flipImageVertically(image);
                    data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
                }
            }
            bumpVersion();
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
    public void loadByKey(String key) {
        fileName = ObjectCacheKey.getKeyData(key);
        loadFromFile();
    }

    @Override
    public void updateGLResource(GL2 gl, GLResourceRecord record) {
        TextureRect texture = null;
        boolean needUpdate = false;
        if (record.resource == null) {
            texture = new TextureRect(gl, srgb ? GL2.GL_SRGB8_ALPHA8 : GL2.GL_RGBA8);
            record.resource = texture;
            needUpdate = true;
        } else if (record.version != this.version) {
            texture = (TextureRect) record.resource;
            needUpdate = true;
        }
        if (needUpdate) {
            logger.debug("rect texture file name = " + fileName);
            texture.setImage(data);
            record.version = version;
            record.sizeInBytes = data.getWidth() * data.getHeight() * 4;
        }
    }

    @Override
    public TextureRect getGLResource(GL2 gl) {
        return (TextureRect) GLResourceCache.v().getGLResource(gl, this);
    }

    public <T extends LoadableByKey> Value<T> wrap(Class<T> klass) {
        return new Value<T>((T)this);
    }
}
