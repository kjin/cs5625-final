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

package cs5625.ui;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GLViewWindows extends GLCanvas implements GLView {
    private Timer timer;
    private float targetFrameRate;

    public GLViewWindows() {
        this(60);
    }

    private static GLCapabilities getDefaultCapabilities() {
        GLProfile glProfile = GLProfile.getDefault();
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        glCapabilities.setAlphaBits(8);
        glCapabilities.setSampleBuffers(true);
        glCapabilities.setNumSamples(1);
        return glCapabilities;
    }

    public GLViewWindows(float frameRate) {
        this(getDefaultCapabilities(), frameRate);
    }

    public GLViewWindows(GLCapabilities glCapabilities, float initialFrameRate) {
        super(glCapabilities);
        initializerTimer(initialFrameRate);
    }

    private void initializerTimer(float initialFrameRate) {
        this.targetFrameRate = initialFrameRate;
        if (targetFrameRate <= 0)
            throw new RuntimeException("invalid frame rate!");

        timer = new Timer((int)(1000 / targetFrameRate), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
    }

    public void addGLController(GLController controller) {
        addGLEventListener(controller);
        addMouseListener(controller);
        addMouseMotionListener(controller);
        addMouseWheelListener(controller);
        addKeyListener(controller);
    }

    public void removeGLController(GLController controller) {
        removeGLEventListener(controller);
        removeMouseListener(controller);
        removeMouseMotionListener(controller);
        removeMouseWheelListener(controller);
        removeKeyListener(controller);
    }

    public void startAnimation() {
        timer.start();
    }

    public void stopAnimation() {
        timer.stop();
    }

    public float getTargetFrameRate() {
        return targetFrameRate;
    }
}
