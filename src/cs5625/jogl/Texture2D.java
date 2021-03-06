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

package cs5625.jogl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import org.apache.commons.io.FilenameUtils;

public class Texture2D extends TextureTwoDim {

    public Texture2D(GL2 gl) {
        super(gl, GL2.GL_TEXTURE_2D, GL2.GL_RGBA8);
    }

    public Texture2D(GL2 gl, int internalFormat) {
        super(gl, GL2.GL_TEXTURE_2D, internalFormat);
    }

    public Texture2D(GL2 gl, int internalFormat, boolean hasMipmap) {
        super(gl, GL2.GL_TEXTURE_2D, internalFormat, hasMipmap);
    }

    public Texture2D(GL2 gl, String filename) throws IOException {
        this(gl, filename, GL2.GL_RGBA8);
    }

    public Texture2D(GL2 gl, String filename, int internalFormat) throws IOException {
        this(gl, new File(filename), internalFormat);
    }

    public Texture2D(GL2 gl, File file) throws IOException {
        this(gl, file, GL2.GL_RGBA8);
    }

    public Texture2D(GL2 gl, File file, int internalFormat) throws IOException {
        super(gl, GL2.GL_TEXTURE_2D, internalFormat);
        TextureData data = null;
        if (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase().equals("png")) {
            BufferedImage image = ImageIO.read(file);
            ImageUtil.flipImageVertically(image);
            data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
        } else {
            data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
            if (data.getMustFlipVertically()) {
                BufferedImage image = ImageIO.read(file);
                ImageUtil.flipImageVertically(image);
                data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
            }
        }
        setImage(data);
    }

    public Texture2D(GL2 gl, BufferedImage image, int internalFormat) {
        super(gl, GL2.GL_TEXTURE_2D, internalFormat);
        TextureData data = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, false);
        setImage(data);
    }

    public Texture2D(GL2 gl, BufferedImage image) {
        this(gl, image, GL2.GL_RGBA8);
    }

    @Override
    public void allocate(int width, int height, int format, int type) {
        super.allocate(width, height, format, type);
    }

    @Override
    public void allocate(int width, int height) { super.allocate(width, height); }
}
