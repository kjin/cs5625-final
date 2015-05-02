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

import cs5625.gfx.camera.Camera;

import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.awt.event.*;

public class CameraController implements GLController {
    public static final int NO_MODE = 0;
    public static final int ROTATE_MODE = 1;
    public static final int TRANSLATE_MODE = 2;
    public static final int ZOOM_MODE = 3;
    protected Camera camera;
    protected int screenWidth;
    protected int screenHeight;
    protected final Vector2f lastMousePosition = new Vector2f();
    protected final Vector2f currentMousePosition = new Vector2f();
    protected final Vector2f mouseDelta = new Vector2f();
    protected final Vector3f worldMotion = new Vector3f();
    protected int mode;

    public CameraController(Camera camera) {
        this.camera = camera;
        initializeCameraController();
    }

    protected void initializeCameraController() {
        camera.updateFrame();
        mode = NO_MODE;
    }

    public void windowToViewport(Tuple2f p) {
        int w = screenWidth;
        int h = screenHeight;
        p.set((2 * p.x - w) / w, (2 * (h - p.y - 1) - h) / h);
    }

    protected boolean isFlagSet(MouseEvent e, int flag) {
        return (e.getModifiersEx() & flag) == flag;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastMousePosition.set(e.getX(), e.getY());
        windowToViewport(lastMousePosition);

        if (isFlagSet(e, MouseEvent.BUTTON1_DOWN_MASK)
                && !isFlagSet(e, MouseEvent.BUTTON2_DOWN_MASK)
                && !isFlagSet(e, MouseEvent.BUTTON3_DOWN_MASK)) {
            if (isFlagSet(e, MouseEvent.ALT_DOWN_MASK)) {
                mode = TRANSLATE_MODE;
            } else if (isFlagSet(e, MouseEvent.CTRL_DOWN_MASK)) {
                mode = ZOOM_MODE;
            } else if (isFlagSet(e, MouseEvent.SHIFT_DOWN_MASK)) {
                mode = ROTATE_MODE;
            } else {
                mode = ROTATE_MODE;
            }
        } else {
            mode = NO_MODE;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mode = NO_MODE;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currentMousePosition.set(e.getX(), e.getY());
        windowToViewport(currentMousePosition);
        mouseDelta.sub(currentMousePosition, lastMousePosition);

        if (mode == TRANSLATE_MODE) {
            camera.convertMotion(mouseDelta, worldMotion);
            camera.translate(worldMotion);
        } else if (mode == ZOOM_MODE) {
            camera.zoom(mouseDelta.y);
        } else if (mode == ROTATE_MODE) {
            camera.orbit(mouseDelta);
        }

        lastMousePosition.set(e.getX(), e.getY());
        windowToViewport(lastMousePosition);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheel) {
		/* Zoom in and out by the scroll wheel. */
        camera.zoom(mouseWheel.getUnitsToScroll());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // NOP
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector2f getCurrentMousePosition() {
        return currentMousePosition;
    }

    public Vector2f getMouseDelta() {
        return mouseDelta;
    }

    public Vector2f getLastMousePosition() {
        return lastMousePosition;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        camera.setAspect(width * 1.0f / height);
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
