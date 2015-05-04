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

package cs5625.fancyplane;

import cs5625.gfx.camera.PerspectiveCamera;
import cs5625.gfx.glcache.GLResourceCache;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.light.ShadowingSpotLight;
import cs5625.gfx.mesh.TriMesh;
import cs5625.gfx.mesh.converter.WavefrontObjToTriMeshConverter;
import cs5625.gfx.objcache.Holder;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.objcache.Value;
import cs5625.gfx.scenetree.SceneTreeNode;
import cs5625.gfx.scenetree.SceneTreeTraverser;
import cs5625.ui.*;
import layout.TableLayout;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class FancyPlaneGame extends JFrame implements GLController, ActionListener {
    GLView glView;
    private int canvasWidth = 1024;
    private int canvasHeight = 768;
    CameraController cameraController;
    DeferredRenderer deferredRenderer = new DeferredRenderer();
    JComboBox sceneComboBox;
    JComboBox displayModeComboBox;
    JComboBox shadowMapComboBox;
    JComboBox shadowMapModeComboBox;
    JSpinnerSlider shadowMapMinZSpinnerSlider;
    JSpinnerSlider shadowMapMaxZSpinnerSlider;
    JSpinnerSlider shadowMapBiasScaleSpinnerSlider;
    JSpinnerSlider shadowMapConstantBiasSpinnerSlider;
    JSpinnerSlider lightHalfWidthSpinnerSlider;
    JSpinnerSlider lightFieldOfViewSpinnerSlider;
    ArrayList<Holder<SceneTreeNode>> scenes = new ArrayList<Holder<SceneTreeNode>>();
    ArrayList<Color3f> sceneBackgroundColors = new ArrayList<Color3f>();
    JCheckBox ssaoEnabledCheckBox;
    JSpinnerSlider ssaoRadiusSpinnerSlider;
    JSpinnerSlider ssaoDepthBiasSpinnerSlider;
    JSpinnerSlider ssaoSampleCountSpinnerSlider;
    JSpinnerSlider spotLightNearSpinnerSlider;
    JSpinnerSlider spotLightFarSpinnerSlider;


    JSpinnerSlider[] spotLightPositionSpinnerSliders = new JSpinnerSlider[3];
    JSpinnerSlider[] spotLightTargetSpinnerSliders = new JSpinnerSlider[3];

    ArrayList<ShadowingSpotLight> spotLights = new ArrayList<ShadowingSpotLight>();
    
    // the gameplay contoller!!!!
    FancyGameController fancyGameController;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // NOP
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FancyPlaneGame().run();
            }
        });
    }

    public FancyPlaneGame() {
        super("CS 5625 Fancy Plane: Final Project");
    }

    private void run() {
    	SceneTreeNode fancyScene = new SceneTreeNode();
    	fancyGameController = new FancyGameController(fancyScene);
        scenes.add(new Value<SceneTreeNode>(fancyScene));
        sceneBackgroundColors.add(new Color3f(1.0f,1.0f,1.0f));

    	cameraController = new CameraController(fancyGameController.getCamera());

        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeGLView();
        initializeControlPanel();
        pack();
        setVisible(true);
    }

    private void initializeGLView() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            GLViewWindows glViewWindows = new GLViewWindows();
            glViewWindows.addGLController(this);
            glViewWindows.addGLController(cameraController);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(BorderLayout.CENTER, glViewWindows);
            glViewWindows.requestFocusInWindow();
            glView = glViewWindows;
            glViewWindows.addKeyListener(this);
        } else {
            GLViewMac glViewMac = new GLViewMac();
            glViewMac.addGLController(this);
            glViewMac.addGLController(cameraController);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(BorderLayout.CENTER, glViewMac);
            glViewMac.requestFocusInWindow();
            glView = glViewMac;
            glViewMac.addKeyListener(this);
        }
    }

    private void initializeControlPanel() {
        JPanel controlPanel = new JPanel();
        getContentPane().add(BorderLayout.SOUTH, controlPanel);

        double[][] tableLayoutSizes =
                {
                        {
                                5, TableLayout.MINIMUM, 5, TableLayout.FILL,
                                5, TableLayout.MINIMUM, 5, TableLayout.FILL,
                                5, TableLayout.MINIMUM, 5, TableLayout.FILL,
                                5,
                        },
                        {
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5, TableLayout.MINIMUM,
                                5
                        }
                };
        TableLayout tableLayout = new TableLayout(tableLayoutSizes);
        controlPanel.setLayout(tableLayout);

        controlPanel.add(new Label("Scene:"), "1,1,1,1");
        sceneComboBox = new JComboBox();
        sceneComboBox.addItem("1. Fancy Plane");
        controlPanel.add(sceneComboBox, "3,1,3,1");
        //sceneComboBox.addActionListener(this);

        controlPanel.add(new Label("Display Mode:"), "1,3,1,3");
        displayModeComboBox = new JComboBox();
        displayModeComboBox.addItem("Scene Rendering");
        displayModeComboBox.addItem("Shadow Map");
        displayModeComboBox.addItem("Ambient Occlusion Map");
        displayModeComboBox.addItem("G-Buffer #1");
        displayModeComboBox.addItem("G-Buffer #2");
        displayModeComboBox.addItem("G-Buffer #3");
        displayModeComboBox.addItem("G-Buffer #4");
        //displayModeComboBox.addActionListener(this);
        controlPanel.add(displayModeComboBox, "3,3,3,3");

        controlPanel.add(new Label("Shadow Map Min Z (for display only):"), "1,5,1,5");
        shadowMapMinZSpinnerSlider = new JSpinnerSlider(0.01, 100, 9999, 20);
        controlPanel.add(shadowMapMinZSpinnerSlider, "3,5,3,5");
        shadowMapMinZSpinnerSlider.setEnabled(false);

        controlPanel.add(new Label("Shadow Map Max Z (for display only):"), "1,7,1,7");
        shadowMapMaxZSpinnerSlider = new JSpinnerSlider(0.01, 100, 9999, 50);
        controlPanel.add(shadowMapMaxZSpinnerSlider, "3,7,3,7");
        shadowMapMaxZSpinnerSlider.setEnabled(false);

        controlPanel.add(new JLabel("Spot Light:"), "5,1,5,1");
        shadowMapComboBox = new JComboBox();
        controlPanel.add(shadowMapComboBox, "7,1,7,1");
        //shadowMapComboBox.addActionListener(this);

        controlPanel.add(new Label("Shadow Map Mode:"), "5,3,5,3");
        shadowMapModeComboBox = new JComboBox();
        shadowMapModeComboBox.addItem("None");
        shadowMapModeComboBox.addItem("Simple");
        shadowMapModeComboBox.addItem("Percentage Closer Filtering (PCF)");
        shadowMapModeComboBox.addItem("Percentage Closer Soft Shadow (PCSS)");
        controlPanel.add(shadowMapModeComboBox, "7,3,5,3");

        controlPanel.add(new JLabel("Shadow Map Constant Bias:"), "5,5,5,5");
        shadowMapConstantBiasSpinnerSlider = new JSpinnerSlider(0, 0.01f, 100000, 0);
        controlPanel.add(shadowMapConstantBiasSpinnerSlider, "7,5,7,5");

        controlPanel.add(new JLabel("Shadow Map Bias Scale:"), "5,7,5,7");
        shadowMapBiasScaleSpinnerSlider = new JSpinnerSlider(0, 0.01f, 100000, 0);
        controlPanel.add(shadowMapBiasScaleSpinnerSlider, "7,7,7,7");

        controlPanel.add(new JLabel("Light Width:"), "5,9,5,9");
        lightHalfWidthSpinnerSlider = new JSpinnerSlider(0, 5.0f, 50000, 0.1f);
        controlPanel.add(lightHalfWidthSpinnerSlider, "7,9,7,9");

        controlPanel.add(new JLabel("Light Field of View"), "5,11,5,11");
        lightFieldOfViewSpinnerSlider = new JSpinnerSlider(1, 60, 590, 45.0f);
        controlPanel.add(lightFieldOfViewSpinnerSlider, "7,11,7,11");

        controlPanel.add(new JLabel("Spot Light Position X:"), "9,1,9,1");
        controlPanel.add(new JLabel("Spot Light Position Y:"), "9,3,9,3");
        controlPanel.add(new JLabel("Spot Light Position Z:"), "9,5,9,5");
        for (int i = 0; i < 3; i++) {
            spotLightPositionSpinnerSliders[i] = new JSpinnerSlider(-50, 50, 10000, 0);
            controlPanel.add(spotLightPositionSpinnerSliders[i], String.format("11,%d,11,%d", 1+2*i, 1+2*i));
        }
        controlPanel.add(new JLabel("Spot Light Target X:"), "9,7,9,7");
        controlPanel.add(new JLabel("Spot Light Target Y:"), "9,9,9,9");
        controlPanel.add(new JLabel("Spot Light Target Z:"), "9,11,9,11");
        for (int i = 0; i < 3; i++) {
            spotLightTargetSpinnerSliders[i] = new JSpinnerSlider(-50, 50, 10000, 0);
            controlPanel.add(spotLightTargetSpinnerSliders[i], String.format("11,%d,11,%d", 7+2*i, 7+2*i));
        }

        controlPanel.add(new JLabel("Spot Light Camera Near:"), "9,13,9,13");
        spotLightNearSpinnerSlider = new JSpinnerSlider(0.1, 100, 9999, 20);
        controlPanel.add(spotLightNearSpinnerSlider, "11,13,11,13");

        controlPanel.add(new JLabel("Spot Light Camera Far:"), "9,15,9,15");
        spotLightFarSpinnerSlider = new JSpinnerSlider(0.1, 100, 9999, 50);
        controlPanel.add(spotLightFarSpinnerSlider, "11,15,11,15");

        controlPanel.add(new JLabel("SSAO:"), "1,9,1,9");
        ssaoEnabledCheckBox = new JCheckBox("Enabled");
        ssaoEnabledCheckBox.setSelected(false);
        controlPanel.add(ssaoEnabledCheckBox, "3,9,3,9");

        controlPanel.add(new JLabel("SSAO Radius"), "1,11,1,11");
        ssaoRadiusSpinnerSlider = new JSpinnerSlider(0, 1.0f, 1000, 0.3);
        controlPanel.add(ssaoRadiusSpinnerSlider, "3,11,3,11");

        controlPanel.add(new JLabel("SSAO Depth Bias"), "1,13,1,13");
        ssaoDepthBiasSpinnerSlider = new JSpinnerSlider(0, 1.0f, 1000, 0.1f);
        controlPanel.add(ssaoDepthBiasSpinnerSlider, "3,13,3,13");

        controlPanel.add(new JLabel("SSAO Sample Count"), "1,15,1,15");
        ssaoSampleCountSpinnerSlider = new JSpinnerSlider(1, 40, 39, 16);
        controlPanel.add(ssaoSampleCountSpinnerSlider, "3,15,3,15");

        updateShadowMapComboBox();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        glView.startAnimation();
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
        glView.stopAnimation();
        GLResourceCache.v().disposeGL();
        deferredRenderer.disposeGL();
    }

    Point3f lightPosition = new Point3f();
    Point3f lightTarget = new Point3f();

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        SceneTreeNode sceneGraph = scenes.get(sceneComboBox.getSelectedIndex()).get();
        deferredRenderer.setDisplayMode(displayModeComboBox.getSelectedIndex());
        deferredRenderer.setShadowMapMinZ((float) shadowMapMinZSpinnerSlider.getValue());
        deferredRenderer.setShadowMapMaxZ((float) shadowMapMaxZSpinnerSlider.getValue());
        deferredRenderer.setSsaoEnabled(ssaoEnabledCheckBox.isSelected());
        deferredRenderer.setSsaoRadius((float)ssaoRadiusSpinnerSlider.getValue());
        deferredRenderer.setSsaoDepthBias((float)ssaoDepthBiasSpinnerSlider.getValue());
        deferredRenderer.setSsaoSampleCount((int)ssaoSampleCountSpinnerSlider.getValue());
        deferredRenderer.setBackgroundColor(sceneBackgroundColors.get(sceneComboBox.getSelectedIndex()));

        int shadowMapIndex = shadowMapComboBox.getSelectedIndex();
        deferredRenderer.setShadowMapToDisplay(shadowMapComboBox.getSelectedIndex());
        if (shadowMapIndex >= 0) {
            ShadowingSpotLight light = spotLights.get(shadowMapIndex);
            light.setShadowMapBiasScale((float)shadowMapBiasScaleSpinnerSlider.getValue());
            light.setShadowMapConstantBias((float)shadowMapConstantBiasSpinnerSlider.getValue());
            light.setShadowMapMode(shadowMapModeComboBox.getSelectedIndex());
            light.setLightWidth((float) lightHalfWidthSpinnerSlider.getValue());
            light.setFov((float) lightFieldOfViewSpinnerSlider.getValue());

            lightPosition.set((float)spotLightPositionSpinnerSliders[0].getValue(),
                    (float)spotLightPositionSpinnerSliders[1].getValue(),
                    (float)spotLightPositionSpinnerSliders[2].getValue());
            lightTarget.set((float)spotLightTargetSpinnerSliders[0].getValue(),
                    (float)spotLightTargetSpinnerSliders[1].getValue(),
                    (float)spotLightTargetSpinnerSliders[2].getValue());
            light.setPosition(lightPosition);
            light.setTarget(lightTarget);

            float nearValue = (float)spotLightNearSpinnerSlider.getValue();
            float farValue = (float)spotLightFarSpinnerSlider.getValue();
            light.setNear(Math.min(nearValue, farValue));
            light.setFar(Math.max(nearValue, farValue));
        }
        
    	fancyGameController.update();

        deferredRenderer.render(gl, sceneGraph, fancyGameController.getCamera(), canvasWidth, canvasHeight);

        GLResourceCache.v().collectGarbage();
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        fancyGameController.getCamera().setAspect(canvasWidth * 1.0f / canvasHeight);
    }

    private void updateShadowMapComboBox() {
        int index = sceneComboBox.getSelectedIndex();
        SceneTreeNode node = scenes.get(index).get();
        spotLights.clear();
        ShadowingSpotLightCollector collector = new ShadowingSpotLightCollector();
        node.letTraverse(collector);

        if (shadowMapComboBox.getItemCount() != spotLights.size()) {
            //shadowMapComboBox.removeActionListener(this);
            int currentIndex = shadowMapComboBox.getSelectedIndex();
            shadowMapComboBox.removeAllItems();
            for (int i = 0; i < spotLights.size(); i++) {
                shadowMapComboBox.addItem(String.format("%d", i + 1));
            }
            if (currentIndex < 0) currentIndex = 0;
            if (currentIndex >= shadowMapComboBox.getItemCount()) currentIndex = shadowMapComboBox.getItemCount()-1;
            shadowMapComboBox.setSelectedIndex(currentIndex);
            //shadowMapComboBox.addActionListener(this);
        }
        updateShadowMapControls();
    }

    private void updateDisplayShadowMapControls() {
        if (displayModeComboBox.getSelectedIndex() == 1) {
            shadowMapMinZSpinnerSlider.setEnabled(true);
            shadowMapMaxZSpinnerSlider.setEnabled(true);
        } else {

            shadowMapMinZSpinnerSlider.setEnabled(false);
            shadowMapMaxZSpinnerSlider.setEnabled(false);
        }
    }

    private void updateShadowMapControls() {
        int index = shadowMapComboBox.getSelectedIndex();
        if (index < 0) {
            shadowMapComboBox.setEnabled(false);
            shadowMapModeComboBox.setEnabled(false);
            shadowMapBiasScaleSpinnerSlider.setEnabled(false);
            shadowMapConstantBiasSpinnerSlider.setEnabled(false);
            lightHalfWidthSpinnerSlider.setEnabled(false);
            shadowMapBiasScaleSpinnerSlider.setEnabled(false);
            shadowMapConstantBiasSpinnerSlider.setEnabled(false);
            shadowMapModeComboBox.setEnabled(false);
            lightHalfWidthSpinnerSlider.setEnabled(false);
            lightFieldOfViewSpinnerSlider.setEnabled(false);
            for (int i = 0; i < 3; i++) {
                spotLightPositionSpinnerSliders[i].setEnabled(false);
                spotLightPositionSpinnerSliders[i].setEnabled(false);
            }
            spotLightNearSpinnerSlider.setEnabled(false);
            spotLightFarSpinnerSlider.setEnabled(false);
        } else {
            shadowMapComboBox.setEnabled(true);
            shadowMapModeComboBox.setEnabled(true);
            shadowMapBiasScaleSpinnerSlider.setEnabled(true);
            shadowMapConstantBiasSpinnerSlider.setEnabled(true);
            lightHalfWidthSpinnerSlider.setEnabled(true);
            shadowMapBiasScaleSpinnerSlider.setEnabled(true);
            shadowMapConstantBiasSpinnerSlider.setEnabled(true);
            shadowMapModeComboBox.setEnabled(true);
            lightHalfWidthSpinnerSlider.setEnabled(true);
            lightFieldOfViewSpinnerSlider.setEnabled(true);
            for (int i = 0; i < 3; i++) {
                spotLightPositionSpinnerSliders[i].setEnabled(true);
                spotLightPositionSpinnerSliders[i].setEnabled(true);
            }
            spotLightNearSpinnerSlider.setEnabled(true);
            spotLightFarSpinnerSlider.setEnabled(true);

            ShadowingSpotLight light = spotLights.get(index);
            shadowMapBiasScaleSpinnerSlider.setValue(light.getShadowMapBiasScale());
            shadowMapConstantBiasSpinnerSlider.setValue(light.getShadowMapConstantBias());
            shadowMapModeComboBox.setSelectedIndex(light.getShadowMapMode());
            lightHalfWidthSpinnerSlider.setValue(light.getLightWidth());
            lightFieldOfViewSpinnerSlider.setValue(light.getFov());

            Point3f position = light.getPosition();
            spotLightPositionSpinnerSliders[0].setValue(position.x);
            spotLightPositionSpinnerSliders[1].setValue(position.y);
            spotLightPositionSpinnerSliders[2].setValue(position.z);

            Point3f target = light.getTarget();
            spotLightTargetSpinnerSliders[0].setValue(target.x);
            spotLightTargetSpinnerSliders[1].setValue(target.y);
            spotLightTargetSpinnerSliders[2].setValue(target.z);

            spotLightNearSpinnerSlider.setValue(light.getNear());
            spotLightFarSpinnerSlider.setValue(light.getFar());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    	fancyGameController.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	fancyGameController.keyReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == displayModeComboBox) {
            updateDisplayShadowMapControls();
        } else if (e.getSource() == sceneComboBox) {
            updateShadowMapComboBox();
        } else if (e.getSource() == shadowMapComboBox) {
            updateShadowMapControls();
        }
    }

    private class ShadowingSpotLightCollector implements SceneTreeTraverser {
        @Override
        public void processNodeBeforeChildren(SceneTreeNode node) {
            if (node.getData() == null)
                return;
            NamedObject data = node.getData().get();
            if (data instanceof ShadowingSpotLight) {
                spotLights.add((ShadowingSpotLight)data);
            }
        }

        @Override
        public void processNodeAfterChildren(SceneTreeNode node) {

        }
    }
}
