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

import com.jogamp.opengl.util.texture.TextureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.media.opengl.GL2;
import java.nio.Buffer;

public class TextureCubeMap extends Texture {
    private static Logger logger = LoggerFactory.getLogger(TextureCubeMap.class);
    private int size = 0;
    private boolean allocated = false;

    public TextureCubeMap(GL2 gl, int internalFormat) {
        super(gl, GL2.GL_TEXTURE_CUBE_MAP, internalFormat);
        wrapS = GL2.GL_CLAMP_TO_EDGE;
        wrapT = GL2.GL_CLAMP_TO_EDGE;
        wrapR = GL2.GL_CLAMP_TO_EDGE;
        minFilter = GL2.GL_LINEAR;
        magFilter = GL2.GL_LINEAR;
    }

    public int getSize() {
        return size;
    }

    public void allocate(int size, int format, int type) {
        this.size = size;
        Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
        if (oldTexture != this) {
            bind();
        }
        bind();
        /* Allocate space for all cube map faces. */
        for (int i=0; i<6; i++) {
            gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, internalFormat,
                    size, size, 0, format, type, null);
        }
        if (oldTexture == null) {
            unbind();
        } else if (oldTexture != this) {
            oldTexture.bind();
        }
        allocated = true;
    }

    public void allocate(int size) {
        allocate(size, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE);
    }

    public void setImages(int size, int format, int type, Buffer[] buffers) {
        this.size = size;

        Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
        if (oldTexture != this) {
            bind();
        }

        for (int i = 0; i < 6; i++) {
            setImage(i, size, format, type, buffers[i]);
        }

        if (oldTexture == null) {
            unbind();
        } else if (oldTexture != this) {
            oldTexture.bind();
        }

        allocated = true;
    }

    private void setImage(int side, int size, int format, int type, Buffer buffer) {
        logger.debug("side = " + side);
        logger.debug("size = " + size);
        logger.debug("format = " + format);
        logger.debug("type = " + type);

        if (buffer != null) {
            buffer.rewind();
            logger.debug("buffer size = " + buffer.capacity());
        }
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, internalFormat, size, size, 0,
                format, type, buffer);
    }

    public void setImages(TextureData[] datas) {
        this.size = datas[0].getWidth();
        for (int i = 0; i < 6; i++) {
            if (datas[i].getWidth() != size || datas[i].getHeight() != size) {
                throw new RuntimeException("texture data not of the right size");
            }
        }

        Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
        if (oldTexture != this) {
            bind();
        }

        for (int i = 0; i < 6; i++) {
            setImage(i, datas[i].getHeight(), datas[i].getPixelFormat(), datas[i].getPixelType(), datas[i].getBuffer());
        }

        if (oldTexture == null) {
            unbind();
        } else if (oldTexture != this) {
            oldTexture.bind();
        }
        allocated = true;
    }
}
