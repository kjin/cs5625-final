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

import cs5625.gfx.camera.Camera;
import cs5625.gfx.camera.PerspectiveCamera;
import cs5625.gfx.gldata.*;
import cs5625.gfx.json.NamedObject;
import cs5625.gfx.light.PointLight;
import cs5625.gfx.light.ShadowingSpotLight;
import cs5625.gfx.material.*;
import cs5625.gfx.mesh.Mesh;
import cs5625.gfx.mesh.MeshPart;
import cs5625.gfx.objcache.Holder;
import cs5625.gfx.objcache.ObjectCache;
import cs5625.gfx.objcache.ObjectCacheKey;
import cs5625.gfx.objcache.Reference;
import cs5625.gfx.scenetree.SceneTreeNode;
import cs5625.gfx.scenetree.SceneTreeTraverser;
import cs5625.jogl.*;
import cs5625.util.VectorMathUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DeferredRenderer {
    private static Logger logger = LoggerFactory.getLogger(DeferredRenderer.class);
    private static int MAX_LIGHTS = 40;
    /**
     * Display modes
     */
    public static final int SCENE_RENDERERING = 0;
    public static final int SHADOW_MAP = 1;
    public static final int AMBIENT_OCCULSION_MAP = 2;
    public static final int GBUFFER_COLOR_BUFFER_0 = 3;
    public static final int GBUFFER_COLOR_BUFFER_1 = 4;
    public static final int GBUFFER_COLOR_BUFFER_2 = 5;
    public static final int GBUFFER_COLOR_BUFFER_3 = 6;

    private int displayMode = 0;
    private int shadowMapToDisplay = 0;
    private float shadowMapMinZ = 10;
    private float shadowMapMaxZ = 30;
    /**
     * Matrices
     */
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix3f normalMatrix = new Matrix3f();
    private Matrix4f modelViewMatrix = new Matrix4f();
    private Matrix4f inverseViewMatrix = new Matrix4f();
    /**
     * OpenGL and buffers
     */
    GL2 gl;
    Fbo fbo;
    TextureRectBufferCollection gBuffer = new TextureRectBufferCollection(4, true);
    TextureRectBufferCollection bBuffer = new TextureRectBufferCollection(1, true);
    TextureRectBufferCollection screenBuffer = new TextureRectBufferCollection(1, false);
    TextureRectBufferCollection spotLightBuffer = new TextureRectBufferCollection(1, false);
    TextureRectBufferCollection ambientOcclusionBuffer = new TextureRectBufferCollection(1, false);
    /**
     * Sub-renderers
     */
    ColorBufferRenderer colorBufferRenderer = new ColorBufferRenderer();
    ShadowMapRenderer shadowMapRenderer = new ShadowMapRenderer();
    /**
     * Point light related fields
     */
    FindPointLights findPointLights = new FindPointLights();
    ArrayList<Point3f> pointLightEyeSpacePosition = new ArrayList<Point3f>();
    ArrayList<Vector3f> pointLightAttentuations = new ArrayList<Vector3f>();
    ArrayList<Color3f> pointLightColors = new ArrayList<Color3f>();
    /**
     * The default white material.
     */
    SingleColorMaterial whiteMaterial = new SingleColorMaterial();
    /**
     * Background color (don't set here, set in FancyPlaneGame!)
     */
    Color3f backgroundColor = new Color3f(0.2f,0.2f,0.6f);

    /**
     * Shadowing Spot Light Info
     */
    private static class ShadowingSpotLightInfo {
        ShadowingSpotLight light;
        SceneTreeNode lightNode;
        TextureRectBufferCollection shadowBuffer;

        public ShadowingSpotLightInfo(SceneTreeNode lightNode) {
            this.lightNode = lightNode;
            this.light = (ShadowingSpotLight)lightNode.getData().get();
        }

        public void allocate(GL2 gl) {
            if (shadowBuffer == null) {
                shadowBuffer = new TextureRectBufferCollection(1, true);
            }
            shadowBuffer.allocate(gl, light.getShadowMapResolution(), light.getShadowMapResolution());
        }

        public void disposeGL() {
            if (shadowBuffer != null)
                shadowBuffer.disposeGL();
        }

        public void setupCamera(Matrix4f viewMatrix, Matrix4f inverseViewMatrix, Matrix4f projectionMatrix) {
            // Setting up the camera for the shadow map.
            PerspectiveCamera camera = light.getCamera();
            camera.getViewMatrix(viewMatrix);
            camera.getProjectionMatrix(projectionMatrix);

            Point3f position = lightNode.transformPointToWorldSpace(new Point3f(0, 0, 0));
            Vector3f negGaze = lightNode.transformVectorToWorldSpace(new Vector3f(0, 0, 1));
            Vector3f up = lightNode.transformVectorFromWorldSpace(new Vector3f(0, 1, 0));
            Matrix4f M = new Matrix4f();
            VectorMathUtil.makeLookAtMatrix(M, position.x, position.y, position.z,
                    position.x - negGaze.x, position.y - negGaze.y, position.z - negGaze.z,
                    up.x, up.y, up.z);
            viewMatrix.mul(M);
            inverseViewMatrix.invert(viewMatrix);
        }
    }

    private class ShadowingSpotLightCollector implements SceneTreeTraverser {
        @Override
        public void processNodeBeforeChildren(SceneTreeNode node) {
            if (node.getData() == null)
                return;
            NamedObject data = node.getData().get();
            if (data instanceof ShadowingSpotLight) {
                ShadowingSpotLight light = (ShadowingSpotLight)node.getData().get();
                if (!spotLightInfoMap.containsKey(light)) {
                    ShadowingSpotLightInfo info = new ShadowingSpotLightInfo(node);
                    spotLightInfoMap.put(light, info);
                }
                spotLightInfos.add(spotLightInfoMap.get(light));
            }
        }

        @Override
        public void processNodeAfterChildren(SceneTreeNode node) {

        }
    }

    /**
     * Shadowing spot lights.
     */
    private ArrayList<ShadowingSpotLightInfo> spotLightInfos =
            new ArrayList<ShadowingSpotLightInfo>();
    private HashMap<ShadowingSpotLight, ShadowingSpotLightInfo> spotLightInfoMap =
            new HashMap<ShadowingSpotLight, ShadowingSpotLightInfo>();
    ShadowingSpotLightCollector spotLightCollector = new ShadowingSpotLightCollector();
    Matrix4f spotLightViewMatrix = new Matrix4f();
    Matrix4f spotLightInverseViewMatrix = new Matrix4f();
    Matrix4f spotLightProjectionMatrix = new Matrix4f();
    private float pcfWindowWidth = 2.5f;
    private int pcfKernelSampleCount = 40;
    private int pcssBlockerKernelSampleCount = 40;
    private int pcssPenumbraKernelSampleCount = 40;
    private float ssaoRadius = 0.1f;
    private float ssaoDepthBias = 0.3f;
    private boolean ssaoEnabled = true;
    private int ssaoSampleCount = 16;

    public DeferredRenderer() {
        viewMatrix.setIdentity();
        inverseViewMatrix.setIdentity();
        projectionMatrix.setIdentity();
        normalMatrix.setIdentity();
        modelViewMatrix.setIdentity();
    }

    public void disposeGL() {
        gBuffer.disposeGL();
        fbo.disposeGL();
        for (ShadowingSpotLightInfo info : spotLightInfos) {
            info.disposeGL();
        }
        spotLightInfos.clear();
    }

    public void setDisplayMode(int mode) {
        if (mode < 0 || mode > 6) {
            mode = 0;
        }
        this.displayMode = mode;
    }

    public void setSsaoEnabled(boolean value) {
        this.ssaoEnabled = value;
    }

    public boolean getSsaoEnabled() {
        return ssaoEnabled;
    }

    public float getSsaoDepthBias() {
        return ssaoDepthBias;
    }

    public void setSsaoDepthBias(float ssaoDepthBias) {
        this.ssaoDepthBias = ssaoDepthBias;
    }

    public float getSsaoRadius() {
        return ssaoRadius;
    }

    public void setSsaoRadius(float ssaoRadius) {
        this.ssaoRadius = ssaoRadius;
    }

    public int getSsaoSampleCount() {
        return ssaoSampleCount;
    }

    public void setSsaoSampleCount(int value) {
        this.ssaoSampleCount = value;
    }

    public void setShadowMapMinZ(float value) {
        this.shadowMapMinZ = value;
    }

    public void setShadowMapMaxZ(float value) {
        this.shadowMapMaxZ = value;
    }

    public void setShadowMapToDisplay(int shadowMapToDisplay) {
        this.shadowMapToDisplay = shadowMapToDisplay;
    }

    public void setBackgroundColor(Tuple3f color) {
        backgroundColor.set(color);
    }

    Reference<ProgramData> srgbProgramRef = FileProgramData.makeReference(
            "src/shaders/deferred/srgb.vert", "src/shaders/deferred/srgb.frag");
    Reference<ProgramData> copyProgramRef = FileProgramData.makeReference(
            "src/shaders/copy.vert", "src/shaders/copy.frag");
    Reference<ProgramData> scaleProgramRef = FileProgramData.makeReference(
            "src/shaders/scale.vert", "src/shaders/scale.frag");
    Reference<ProgramData> uberShaderProgramRef = FileProgramData.makeReference(
            "src/shaders/deferred/ubershader.vert", "src/shaders/deferred/ubershader.frag");
    Reference<ProgramData> clearProgramRef = FileProgramData.makeReference(
            "src/shaders/clear.vert", "src/shaders/clear.frag");
    Reference<ProgramData> displayShadowMapProgramRef = FileProgramData.makeReference(
            "src/shaders/display_shadow_map.vert", "src/shaders/display_shadow_map.frag");
    Reference<ProgramData> ssaoProgramRef = FileProgramData.makeReference(
            "src/shaders/deferred/ssao.vert", "src/shaders/deferred/ssao.frag");
    Reference<ProgramData> productProgramRef = FileProgramData.makeReference(
            "src/shaders/product.vert", "src/shaders/product.frag");
    
    Reference<ProgramData> bloomThresholdProgramRef = FileProgramData.makeReference(
    		"src/shaders/bloom_threshold.vert", "src/shaders/bloom_threshold.frag");
    Reference<ProgramData> gaussianBlurProgramRef = FileProgramData.makeReference(
            "src/shaders/gaussian_blur.vert", "src/shaders/gaussian_blur.frag");
    Reference<ProgramData> sobelEdgeProgramRef = FileProgramData.makeReference(
    		"src/shaders/sobel_edge.vert", "src/shaders/sobel_edge.frag");


    public void render(GL2 gl, SceneTreeNode node, Camera camera, int width, int height) {
        this.gl = gl;

        if (fbo == null) fbo = new Fbo(gl);
        
        // Collect all the spot lights.
        spotLightInfos.clear();
        node.letTraverse(spotLightCollector);
	
        // Render the G-Buffer
        setupCamera(camera);
        gBuffer.allocate(gl, width, height);
        fbo.bind();
        gBuffer.attachTo(fbo);
        fbo.drawTo(0, 4);
        renderSceneToColorBuffer(gl, node, width, height);
        fbo.drawToNone();
        fbo.detachAll();
        fbo.unbind();
        gBuffer.swap();

        screenBuffer.allocate(gl, width, height);
        
        // Render the scene.
        Program program = uberShaderProgramRef.get().getGLResource(gl);
        program.use();
        computePointLightInfo(node);
        setPointLightUniforms(program);
        program.setUniform("spotLight_enabled", false);
        program.setUniform("backgroundColor", backgroundColor);
        for (int i = 0; i < 4; i++) {
            useTextureRect(gl, program,
                    String.format("gbuf_materialParams%d", i+1),
                    gBuffer.colorBuffers[i].getReadBuffer(),
                    i);
        }
        renderFulScreenQuadToTextureRectBuffer(gl, program, width, height, screenBuffer, true, false);
        screenBuffer.swap();
        for (int i = 0; i < 4; i++) {
            gBuffer.colorBuffers[i].getReadBuffer().unuse();
        }
        program.unuse();
        
        // Sobel edge post processing
        boolean edgeEnabled = true;
        if (edgeEnabled) {
            float threshold = 0.2f;
            float contrast = 10f;
            int offset = 1;
        	
        	Program sobelEdgeProgram = sobelEdgeProgramRef.get().getGLResource(gl);
        	sobelEdgeProgram.use();
        	
        	sobelEdgeProgram.getUniform("threshold").set1Float(threshold);
        	sobelEdgeProgram.getUniform("contrast").set1Float(contrast);
        	sobelEdgeProgram.getUniform("offset").set1Int(offset);
        	
        	useTextureRect(gl, sobelEdgeProgram, "texture", screenBuffer.colorBuffers[0].getReadBuffer(), 0);
        	renderFullScreenQuadToTextureRectBuffer(gl, sobelEdgeProgram,
    				screenBuffer.colorBuffers[0].getWidth(), screenBuffer.colorBuffers[0].getHeight(), 
    				screenBuffer, true, true);

        	sobelEdgeProgram.unuse();
        }
        
        // Bloom post processing
        boolean bloomEnabled = false;
        if (displayMode == SCENE_RENDERERING && bloomEnabled) {
            float brightnessThreshold = 0.5f;
            int bloomLayers = 4;
            int[] bloomFilterSizes = {2, 5, 10, 20};
            float[] bloomFilterStdev = {0.4f, 1, 2.5f, 4};   
            
        	// Allocate an extra texture to store the original image.
        	bBuffer.allocate(gl, width, height);
        	
        	Program bloomThresholdProgram = bloomThresholdProgramRef.get().getGLResource(gl);
        	Program gaussianBlurProgram = gaussianBlurProgramRef.get().getGLResource(gl);
        	
        	bloomThresholdProgram.use();
        	bloomThresholdProgram.getUniform("brightness").set1Float(brightnessThreshold);
        	
        	useTextureRect(gl, bloomThresholdProgram, "texture", screenBuffer.colorBuffers[0].getReadBuffer(), 0);
        	renderFullScreenQuadToTextureRectBuffer(gl, bloomThresholdProgram,
    				bBuffer.colorBuffers[0].getWidth(), bBuffer.colorBuffers[0].getHeight(), bBuffer, true, true);
        	bloomThresholdProgram.unuse();
        	screenBuffer.swap();

        	gaussianBlurProgram.use();
        	for (int i = 3; i < 4; i++)
        	{
        		gaussianBlurProgram.getUniform("size").set1Int(bloomFilterSizes[i]);
        		gaussianBlurProgram.getUniform("stdev").set1Float(bloomFilterStdev[i]);
        		
        		gaussianBlurProgram.getUniform("axis").set1Int(0);
	        	useTextureRect(gl, gaussianBlurProgram, "texture", bBuffer.colorBuffers[0].getReadBuffer(), 0);
	        	renderFullScreenQuadToTextureRectBuffer(gl, gaussianBlurProgram,
	        			bBuffer.colorBuffers[0].getWidth(), bBuffer.colorBuffers[0].getHeight(), bBuffer, true, true);
	        	
	        	gaussianBlurProgram.getUniform("axis").set1Int(1);
	        	useTextureRect(gl, gaussianBlurProgram, "texture", bBuffer.colorBuffers[0].getReadBuffer(), 0);
	            gl.glEnable(GL2.GL_BLEND);
	            gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);
	        	renderFullScreenQuadToTextureRectBuffer(gl, gaussianBlurProgram,
	    				screenBuffer.colorBuffers[0].getWidth(), screenBuffer.colorBuffers[0].getHeight(), screenBuffer, false, false);
	        	gl.glDisable(GL2.GL_BLEND);
	        	bBuffer.swap();
        	}
        	gaussianBlurProgram.unuse();
        	bBuffer.colorBuffers[0].getReadBuffer().unuse();
        	screenBuffer.swap();
        }

        // Compute the SRGB color and write to screen.
        program = srgbProgramRef.get().getGLResource(gl);
        program.use();
        useTextureRect(gl, program, "image", screenBuffer.colorBuffers[0].getReadBuffer(), 0);
        drawFullScreenQuad(program, width, height);
        screenBuffer.colorBuffers[0].getReadBuffer().unuse();
        program.unuse();          
    }

    private void useTextureRect(GL2 gl, Program program, String uniformName, TextureRect texture, int textureUnitId) {
        if (program.hasUniform(uniformName)) {
            texture.useWith(TextureUnit.getTextureUnit(gl, textureUnitId));
            program.getUniform(uniformName).set1Int(textureUnitId);
        }
    }

    private void useTexture2D(GL2 gl, Program program, String uniformName, Texture2D texture, int textureUnitId) {
        if (program.hasUniform(uniformName)) {
            texture.useWith(TextureUnit.getTextureUnit(gl, textureUnitId));
            program.getUniform(uniformName).set1Int(textureUnitId);
        }
    }

    private void renderSceneToTextureRectBufferAndSwap(GL2 gl, SceneTreeNode node, int width, int height,
                                                       TextureRectBufferCollection buffers) {
        renderSceneToTextureRectBuffer(gl, node, width, height, buffers, true);
    }

    private void renderSceneToTextureRectBuffer(GL2 gl, SceneTreeNode node, int width, int height,
                                                TextureRectBufferCollection buffers, boolean swap) {
        fbo.bind();
        buffers.attachTo(fbo);
        fbo.drawTo(0, 1);
        renderSceneToColorBuffer(gl, node, width, height);
        fbo.drawToNone();
        fbo.detachAll();
        fbo.unbind();
        if (swap)
            buffers.swap();
    }

    private void renderSceneToColorBuffer(GL2 gl, SceneTreeNode node, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        node.letTraverse(colorBufferRenderer);
        gl.glFlush();
    }

    private void renderFulScreenQuadToTextureRectBufferAndSwap(GL2 gl, Program program, int width, int height,
                                                               TextureRectBufferCollection buffers) {
        renderFulScreenQuadToTextureRectBuffer(gl, program, width, height, buffers, true, true);
    }

    private void renderFulScreenQuadToTextureRectBufferAndSwap(GL2 gl, Program program, int width, int height,
                                                               TextureRectBufferCollection buffers,
                                                               boolean clear) {
        renderFulScreenQuadToTextureRectBuffer(gl, program, width, height, buffers, clear, true);
    }

    private void renderFulScreenQuadToTextureRectBuffer(GL2 gl, Program program, int width, int height,
                                                        TextureRectBufferCollection buffers,
                                                        boolean clear, boolean swap) {
        fbo.bind();
        buffers.attachTo(fbo);
        fbo.drawTo(0, 1);
        drawFullScreenQuad(program, width, height, clear);
        gl.glFlush();
        fbo.drawToNone();
        fbo.detachAll();
        fbo.unbind();
        if (swap)
            buffers.swap();
    }

    private void renderFullScreenQuadToCubeMapSide(GL2 gl, Program program, int size,
                                                   TextureCubeMapBufferCollection buffers, int side) {
        fbo.bind();
        buffers.attachTo(fbo, side);
        fbo.drawTo(0, 1);
        drawFullScreenQuad(program, size, size, true);
        gl.glFlush();
        fbo.drawToNone();
        fbo.detachAll();
        fbo.unbind();
    }

    PosTexNorTanData quadMeshVertices = new PosTexNorTanData();
    IndexData quadMeshIndices;

    private void drawFullScreenQuad(Program program, int w, int h, boolean clear) {
        quadMeshVertices.startBuild()
                .addPosition(-1, -1, 0).addTexCoord(0, 0)
                .addPosition(1, -1, 0).addTexCoord(w, 0)
                .addPosition(1, 1, 0).addTexCoord(w, h)
                .addPosition(-1, 1, 0).addTexCoord(0, h)
                .endBuild();
        bindPosNorTexTanAttributes(program, quadMeshVertices);
        if (quadMeshIndices == null)
            quadMeshIndices = new IndexData().startBuild().add(0).add(1).add(2).add(0).add(2).add(3).endBuild();
        Vbo indexBuffer = quadMeshIndices.getGLResource(gl);
        indexBuffer.bind();
        gl.glViewport(0, 0, w, h);
        if (clear) {
            gl.glDisable(GL2.GL_BLEND);
            gl.glClearColor(0, 0, 0, 0);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        }
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_INT, 0);
        gl.glFlush();
        indexBuffer.unbind();
    }

    private void drawFullScreenQuad(Program program, int w, int h) {
        drawFullScreenQuad(program, w, h, true);
    }

    private void setupCamera(Camera camera) {
        camera.getProjectionMatrix(projectionMatrix);
        camera.getViewMatrix(viewMatrix);
        inverseViewMatrix.invert(viewMatrix);
    }

    private void computePointLightInfo(SceneTreeNode node) {
        pointLightAttentuations.clear();
        pointLightEyeSpacePosition.clear();
        pointLightColors.clear();
        node.letTraverse(findPointLights);
    }

    private void setPointLightUniforms(Program program) {
        int lightCount = Math.min(MAX_LIGHTS, pointLightEyeSpacePosition.size());
        program.setUniform("pointLight_count", lightCount);
        for (int i = 0; i < lightCount; i++) {
            String eyePositionName = "pointLight_eyePosition[" + i + "]";
            program.setUniform(eyePositionName, pointLightEyeSpacePosition.get(i));
            String attenuationName = "pointLight_attenuation[" + i + "]";
            program.setUniform(attenuationName, pointLightAttentuations.get(i));
            String colorName = "pointLight_color[" + i + "]";
            program.setUniform(colorName, pointLightColors.get(i));
        }
    }

    class FindPointLights implements SceneTreeTraverser {
        @Override
        public void processNodeBeforeChildren(SceneTreeNode node) {
            if (node.getData() == null)
                return;
            NamedObject data = node.getData().get();
            if (!(data instanceof PointLight))
                return;
            PointLight light = (PointLight) data;
            Point3f pos = new Point3f(light.getPosition());
            Point3f worldPos = node.transformPointToWorldSpace(pos);
            viewMatrix.transform(worldPos);

            pointLightEyeSpacePosition.add(worldPos);
            pointLightAttentuations.add(new Vector3f(
                    light.getConstantAttenuation(),
                    light.getLinearAttenuation(),
                    light.getQuadraticAttenuation()));
            pointLightColors.add(light.getColor());
        }

        @Override
        public void processNodeAfterChildren(SceneTreeNode node) {

        }
    }

    class ColorBufferRenderer implements SceneTreeTraverser {
        Matrix4f modelMatrix = new Matrix4f();
        ArrayList<Matrix4f> modelMatrices = new ArrayList<Matrix4f>();
        int stackTop = 0;
        Matrix4f M = new Matrix4f();

        public ColorBufferRenderer() {
            modelMatrix.setIdentity();
        }

        @Override
        public void processNodeBeforeChildren(SceneTreeNode node) {
            while (modelMatrices.size() <= stackTop) {
                modelMatrices.add(new Matrix4f());
            }
            modelMatrices.get(stackTop).set(modelMatrix);
            node.getToParentSpaceMatrix(M);
            modelMatrix.mul(M);

            stackTop++;

            if (node.getData() == null)
                return;

            Object data = node.getData().get();
            if (data instanceof Mesh) {
                Mesh mesh = (Mesh) data;
                renderMeshToColorBuffer(mesh, modelMatrix);
            }
        }

        @Override
        public void processNodeAfterChildren(SceneTreeNode node) {
            stackTop--;
            modelMatrix.set(modelMatrices.get(stackTop));
        }
    }

    private void renderMeshToColorBuffer(Mesh mesh, Matrix4f modelMatrix) {
        modelViewMatrix.set(viewMatrix);
        modelViewMatrix.mul(modelMatrix);
        VectorMathUtil.set3x3Part(normalMatrix, modelViewMatrix);
        normalMatrix.invert();
        normalMatrix.transpose();

        for (int i = 0; i < mesh.getPartCount(); i++) {
            MeshPart meshPart = mesh.getPart(i);
            renderMeshPartToColorBuffer(mesh, meshPart);
        }
    }

    private void bindAndEnableAttribute(Program program, VertexData vertexData, String name) {
        if (program.hasAttribute(name) && vertexData.hasAttribute(name)) {
            program.getAttribute(name).setup(gl, vertexData.getAttributeSpec(name)).enable();
        }
    }

    private void disableAttribute(Program program, String name) {
        if (program.hasAttribute(name)) {
            program.getAttribute(name).disable();
        }
    }

    private void setMatrixUniforms(Program program) {
        program.setUniform("sys_modelViewMatrix", modelViewMatrix);
        program.setUniform("sys_projectionMatrix", projectionMatrix);
        program.setUniform("sys_normalMatrix", normalMatrix);
        program.setUniform("sys_viewMatrix", viewMatrix);
        program.setUniform("sys_inverseViewMatrix", inverseViewMatrix);
    }

    private void useTexture(Program program,
                            Holder<Texture2DData> textureHolder,
                            String hasTextureUniformName,
                            String textureUniformName,
                            int textureUnit) {
        if (textureHolder != null) {
            Texture2D texture = textureHolder.get().getGLResource(gl);
            if (program.hasUniform(hasTextureUniformName))
                program.getUniform(hasTextureUniformName).set1Int(1);
            texture.useWith(TextureUnit.getTextureUnit(gl, textureUnit));
            if (program.hasUniform(textureUniformName))
                program.getUniform(textureUniformName).set1Int(textureUnit);
        } else {
            if (program.hasUniform(hasTextureUniformName))
                program.getUniform(hasTextureUniformName).set1Int(0);
        }
    }

    private void useTextureClamp(Program program,
            Holder<Texture2DData> textureHolder,
            String hasTextureUniformName,
            String textureUniformName,
            int textureUnit) {
		if (textureHolder != null) {
			Texture2D texture = textureHolder.get().getGLResource(gl);
			texture.wrapT = GL.GL_CLAMP_TO_EDGE;
			texture.wrapS = GL.GL_CLAMP_TO_EDGE;
			if (program.hasUniform(hasTextureUniformName))
				program.getUniform(hasTextureUniformName).set1Int(1);
			texture.useWith(TextureUnit.getTextureUnit(gl, textureUnit));
			if (program.hasUniform(textureUniformName))
				program.getUniform(textureUniformName).set1Int(textureUnit);
		} else {
			if (program.hasUniform(hasTextureUniformName))
				program.getUniform(hasTextureUniformName).set1Int(0);
		}
	}
    
    private void useCubeMap(Program program,
                            Holder<TextureCubeMapData> textureHolder,
                            String textureUniformName,
                            int textureUnit) {
        if (textureHolder != null) {
            TextureCubeMap texture = textureHolder.get().getGLResource(gl);
            texture.useWith(TextureUnit.getTextureUnit(gl, textureUnit));
            if (program.hasUniform(textureUniformName))
                program.getUniform(textureUniformName).set1Int(textureUnit);
        }
    }

    private void unuseTexture(Program program, Holder<Texture2DData> textureHolder) {
        if (textureHolder != null) {
            Texture2D texture = textureHolder.get().getGLResource(gl);
            texture.unuse();
        }
    }

    private void unuseCubeMap(Program program, Holder<TextureCubeMapData> textureHolder) {
        if (textureHolder != null) {
            TextureCubeMap texture = textureHolder.get().getGLResource(gl);
            texture.unuse();
        }
    }

    private boolean checkVertexAttribute(VertexData vertexData, String name, Material material) {
        if (!vertexData.hasAttribute(name)) {
            logger.info("vertex data does not have '" + name +
                    "' attribute, so cannot be used with " + material.getClass().getName());
            return false;
        } else {
            return true;
        }
    }

    public void drawElement(IndexData indexData, MeshPart meshPart) {
        Vbo indexBuffer = indexData.getGLResource(gl);
        indexBuffer.bind();
        gl.glDrawElements(meshPart.primitive,
                meshPart.vertexCount,
                GL2.GL_UNSIGNED_INT,
                meshPart.vertexStart * 4);
        indexBuffer.unbind();
    }

    private void bindVertexAttributes(Program program, Mesh mesh) {
        VertexData vertexData = mesh.getVertexData().get();
        if (vertexData instanceof PosColData) {
            bindPosColAttributes(program, vertexData);
        } else if (vertexData instanceof PosTexNorTanData) {
            bindPosNorTexTanAttributes(program, vertexData);
        } else if (vertexData instanceof SkeletalMeshVertexData) {
            bindSkeletalMeshAttributes(program, vertexData);
        } else {
            throw new RuntimeException("unsupported vertex data type: " + vertexData.getClass().getName());
        }
    }

    private int setupVertexShaderUniforms(Program program, Mesh mesh) {
    	return 0;        
    }

    private void tearDownVertexShaderUniforms(Program program, Mesh mesh) {
        // NOP
    }

    private void disableVertexAttributes(Program program, Mesh mesh) {
        VertexData vertexData = mesh.getVertexData().get();
        if (vertexData instanceof PosColData) {
            disablePosColAttributes(program);
        } else if (vertexData instanceof PosTexNorTanData) {
            disablePosNorTexTanAttributes(program);
        } else if (vertexData instanceof SkeletalMeshVertexData) {
            disableSkeletalMeshAttributes(program);
        } else {
            throw new RuntimeException("unsupported vertex data type: " + vertexData.getClass().getName());
        }
    }

    private void bindPosNorTexTanAttributes(Program program, VertexData vertexData) {
        // Bind vertex attributes.
        Vbo vertexBuffer = vertexData.getGLResource(gl);
        vertexBuffer.bind();
        bindAndEnableAttribute(program, vertexData, "vert_position");
        bindAndEnableAttribute(program, vertexData, "vert_normal");
        bindAndEnableAttribute(program, vertexData, "vert_texCoord");
        bindAndEnableAttribute(program, vertexData, "vert_tangent");
        vertexBuffer.unbind();
    }

    private void disablePosNorTexTanAttributes(Program program) {
        disableAttribute(program, "vert_position");
        disableAttribute(program, "vert_normal");
        disableAttribute(program, "vert_texCoord");
        disableAttribute(program, "vert_tangent");
    }


    private void bindSkeletalMeshAttributes(Program program, VertexData vertexData) {
        Vbo vertexBuffer = vertexData.getGLResource(gl);
        vertexBuffer.bind();
        bindAndEnableAttribute(program, vertexData, "vert_position");
        bindAndEnableAttribute(program, vertexData, "vert_normal");
        bindAndEnableAttribute(program, vertexData, "vert_texCoord");
        bindAndEnableAttribute(program, vertexData, "vert_tangent");
        bindAndEnableAttribute(program, vertexData, "vert_boneIndices");
        bindAndEnableAttribute(program, vertexData, "vert_boneWeights");
        bindAndEnableAttribute(program, vertexData, "vert_morphStart");
        bindAndEnableAttribute(program, vertexData, "vert_morphCount");
        vertexBuffer.unbind();
    }

    private void disableSkeletalMeshAttributes(Program program) {
        disableAttribute(program, "vert_position");
        disableAttribute(program, "vert_normal");
        disableAttribute(program, "vert_texCoord");
        disableAttribute(program, "vert_tangent");
        disableAttribute(program, "vert_boneIndices");
        disableAttribute(program, "vert_boneWeights");
        disableAttribute(program, "vert_morphStart");
        disableAttribute(program, "vert_morphCount");
    }

    private void bindPosColAttributes(Program program, VertexData vertexData) {
        Vbo vertexBuffer = vertexData.getGLResource(gl);
        vertexBuffer.bind();
        bindAndEnableAttribute(program, vertexData, "vert_position");
        bindAndEnableAttribute(program, vertexData, "vert_color");
        vertexBuffer.unbind();
    }

    private void disablePosColAttributes(Program program) {
        disableAttribute(program, "vert_position");
        disableAttribute(program, "vert_color");
    }

    private void renderMeshPartToColorBuffer(Mesh mesh, MeshPart meshPart) {
        Material material = meshPart.material.get();
        if (material instanceof SingleColorMaterial) {
            renderMeshPart(mesh, meshPart, (SingleColorMaterial) material);
        } else if (material instanceof LambertianMaterial) {
            renderMeshPart(mesh, meshPart, (LambertianMaterial) material);
        } else if (material instanceof BlinnPhongMaterial) {
            renderMeshPart(mesh, meshPart, (BlinnPhongMaterial) material);
        } else if (material instanceof XToonMaterial) {
        	renderMeshPart(mesh, meshPart, (XToonMaterial) material);
        } else if (material instanceof SmokeParticleMaterial) {
        	renderMeshPart(mesh, meshPart, (SmokeParticleMaterial) material);
        } else {
            renderMeshPart(mesh, meshPart, whiteMaterial);
        }
    }

    private String getVertexShaderFileName(VertexData vertexData) {
        if (vertexData instanceof PosColData) {
            return "src/shaders/deferred/pos_col.vert";
        } else if (vertexData instanceof PosTexNorTanData) {
            return "src/shaders/deferred/pos_tex_nor_tan.vert";
        } else if (vertexData instanceof SkeletalMeshVertexData) {
            return "src/shaders/deferred/skeletal_mesh.vert";
        } else {
            throw new RuntimeException("Unsupported vertex data type: " + vertexData.getClass().getName());
        }
    }

    Program getProgram(String vertexShaderFileName, String fragmentShaderFileName) {
        ProgramData programData = (ProgramData) ObjectCache.v().load(FileProgramData.class,
                vertexShaderFileName + ObjectCacheKey.PATH_SEPARATOR + fragmentShaderFileName);
        Program program = programData.getGLResource(gl);
        return program;
    }

    private void renderMeshPart(Mesh mesh, MeshPart meshPart, SingleColorMaterial material) {
        VertexData vertexData = mesh.getVertexData().get();
        IndexData indexData = mesh.getIndexData().get();

        Program program = getProgram(getVertexShaderFileName(vertexData), "src/shaders/deferred/single_color.frag");
        program.use();
        bindPosNorTexTanAttributes(program, vertexData);

        // Set uniforms.
        setMatrixUniforms(program);
        program.setUniform("color", material.getColor());

        // Draw the mesh part.
        drawElement(indexData, meshPart);

        tearDownVertexShaderUniforms(program, mesh);
        disableVertexAttributes(program, mesh);
        program.unuse();
    }

    private void renderMeshPart(Mesh mesh, MeshPart meshPart, LambertianMaterial material) {
        VertexData vertexData = mesh.getVertexData().get();
        IndexData indexData = mesh.getIndexData().get();

        if (!checkVertexAttribute(vertexData, "vert_position", material)) return;
        if (!checkVertexAttribute(vertexData, "vert_normal", material)) return;
        boolean useTexture = material.getDiffuseTexture() != null;
        if (useTexture && !checkVertexAttribute(vertexData, "vert_texCoord", material)) return;

        Program program = getProgram(getVertexShaderFileName(vertexData), "src/shaders/deferred/lambertian.frag");
        program.use();
        bindVertexAttributes(program, mesh);
        int texUnitStart = setupVertexShaderUniforms(program, mesh);

        // Set uniforms.
        setMatrixUniforms(program);
        program.setUniform("mat_diffuseColor", material.getDiffuseColor());
        useTexture(program, material.getDiffuseTexture(), "mat_hasDiffuseTexture", "mat_diffuseTexture", texUnitStart + 0);

        // Draw the mesh part.
        drawElement(indexData, meshPart);

        // Tear down the program.
        unuseTexture(program, material.getDiffuseTexture());

        tearDownVertexShaderUniforms(program, mesh);
        disableVertexAttributes(program, mesh);
        program.unuse();
    }

    private void renderMeshPart(Mesh mesh, MeshPart meshPart, BlinnPhongMaterial material) {
        VertexData vertexData = mesh.getVertexData().get();
        IndexData indexData = mesh.getIndexData().get();

        if (!checkVertexAttribute(vertexData, "vert_position", material)) return;
        if (!checkVertexAttribute(vertexData, "vert_normal", material)) return;
        boolean useTexture = material.getDiffuseTexture() != null ||
                material.getSpecularTexture() != null ||
                material.getExponentTexture() != null;
        if (useTexture && !checkVertexAttribute(vertexData, "vert_texCoord", material)) return;

        Program program = getProgram(getVertexShaderFileName(vertexData), "src/shaders/deferred/blinnphong.frag");
        program.use();
        bindVertexAttributes(program, mesh);
        int texUnitStart = setupVertexShaderUniforms(program, mesh);

        // Set uniforms.
        setMatrixUniforms(program);
        program.setUniform("mat_diffuseColor", material.getDiffuseColor())
            .setUniform("mat_specularColor", material.getSpecularColor())
            .setUniform("mat_exponent", material.getExponent());
        useTexture(program, material.getDiffuseTexture(), "mat_hasDiffuseTexture", "mat_diffuseTexture", texUnitStart + 0);
        useTexture(program, material.getSpecularTexture(), "mat_hasSpecularTexture", "mat_specularTexture", texUnitStart + 1);
        useTexture(program, material.getExponentTexture(), "mat_hasExponentTexture", "mat_exponentTexture", texUnitStart + 2);

        // Draw the mesh part.
        drawElement(indexData, meshPart);

        // Tear down the program.
        unuseTexture(program, material.getDiffuseTexture());
        unuseTexture(program, material.getSpecularTexture());
        unuseTexture(program, material.getExponentTexture());

        tearDownVertexShaderUniforms(program, mesh);
        disableVertexAttributes(program, mesh);
        program.unuse();
    }
    
    private void renderMeshPart(Mesh mesh, MeshPart meshPart, XToonMaterial material) {
        VertexData vertexData = mesh.getVertexData().get();
        IndexData indexData = mesh.getIndexData().get();

        if (!checkVertexAttribute(vertexData, "vert_position", material)) return;
        if (!checkVertexAttribute(vertexData, "vert_normal", material)) return;
        boolean useTexture = material.getXToonTexture() != null;
        if (useTexture && !checkVertexAttribute(vertexData, "vert_texCoord", material)) return;

        Program program = getProgram(getVertexShaderFileName(vertexData), "src/shaders/deferred/xtoon.frag");
        program.use();
        bindVertexAttributes(program, mesh);
        int texUnitStart = setupVertexShaderUniforms(program, mesh);

        // Set uniforms.   
        setMatrixUniforms(program);
        useTextureClamp(program, material.getXToonTexture(), "mat_hasXToonTexture", "mat_xtoonTexture", texUnitStart + 0);
        program.setUniform("orientationOrDepth", material.getOrientationBased());
        
        // XToon only works with point lights for now
        setPointLightUniforms(program);
        program.setUniform("spotLight_enabled", false);

        // Draw the mesh part.
        drawElement(indexData, meshPart);

        // Tear down the program.
        unuseTexture(program, material.getXToonTexture());

        tearDownVertexShaderUniforms(program, mesh);
        disableVertexAttributes(program, mesh);
        program.unuse();
    }
    
    private void renderMeshPart(Mesh mesh, MeshPart meshPart, SmokeParticleMaterial material) {
        VertexData vertexData = mesh.getVertexData().get();
        IndexData indexData = mesh.getIndexData().get();

        if (!checkVertexAttribute(vertexData, "vert_position", material)) return;
        if (!checkVertexAttribute(vertexData, "vert_normal", material)) return;
        boolean useTexture = material.getDiffuseTexture() != null ||
                material.getSpecularTexture() != null ||
                material.getExponentTexture() != null;
        if (useTexture && !checkVertexAttribute(vertexData, "vert_texCoord", material)) return;

        Program program = getProgram("src/shaders/deferred/smokeparticle.vert", "src/shaders/deferred/smokeparticle.frag");
        program.use();
        Vbo vertexBuffer = vertexData.getGLResource(gl);
        vertexBuffer.bind();
        bindAndEnableAttribute(program, vertexData, "vert_particle_index");
        bindAndEnableAttribute(program, vertexData, "vert_particle_corner");
        vertexBuffer.unbind();
        int texUnitStart = setupVertexShaderUniforms(program, mesh);

        // Set uniforms.
        setMatrixUniforms(program);
        program.setUniform("mat_diffuseColor", material.getDiffuseColor())
            .setUniform("mat_specularColor", material.getSpecularColor())
            .setUniform("mat_exponent", material.getExponent());
        useTexture(program, material.getDiffuseTexture(), "mat_hasDiffuseTexture", "mat_diffuseTexture", texUnitStart + 0);
        useTexture(program, material.getSpecularTexture(), "mat_hasSpecularTexture", "mat_specularTexture", texUnitStart + 1);
        useTexture(program, material.getExponentTexture(), "mat_hasExponentTexture", "mat_exponentTexture", texUnitStart + 2);

        // Draw the mesh part.
        drawElement(indexData, meshPart);

        // Tear down the program.
        unuseTexture(program, material.getDiffuseTexture());
        unuseTexture(program, material.getSpecularTexture());
        unuseTexture(program, material.getExponentTexture());

        tearDownVertexShaderUniforms(program, mesh);
        disableAttribute(program, "vert_particle_index");
        disableAttribute(program, "vert_particle_corner");
        program.unuse();
    }

    class ShadowMapRenderer implements SceneTreeTraverser {
        Matrix4f modelMatrix = new Matrix4f();
        ArrayList<Matrix4f> modelMatrices = new ArrayList<Matrix4f>();
        int stackTop = 0;
        Matrix4f M = new Matrix4f();
        public int shadowMapWidth;
        public int shadowMapHeight;

        public ShadowMapRenderer() {
            modelMatrix.setIdentity();
        }

        @Override
        public void processNodeBeforeChildren(SceneTreeNode node) {
            while (modelMatrices.size() <= stackTop) {
                modelMatrices.add(new Matrix4f());
            }
            modelMatrices.get(stackTop).set(modelMatrix);
            node.getToParentSpaceMatrix(M);
            modelMatrix.mul(M);

            stackTop++;

            if (node.getData() == null)
                return;

            Object data = node.getData().get();
            if (data instanceof Mesh) {
                Mesh mesh = (Mesh) data;
                renderMeshToShadowMap(mesh, modelMatrix, shadowMapWidth, shadowMapHeight);
            }
        }

        @Override
        public void processNodeAfterChildren(SceneTreeNode node) {
            stackTop--;
            modelMatrix.set(modelMatrices.get(stackTop));
        }
    }

    private void renderMeshToShadowMap(Mesh mesh, Matrix4f modelMatrix, int shadowMapWidth, int shadowMapHeight) {
        if (!mesh.castsShadow())
            return;

        modelViewMatrix.set(viewMatrix);
        modelViewMatrix.mul(modelMatrix);
        VectorMathUtil.set3x3Part(normalMatrix, modelViewMatrix);
        normalMatrix.invert();
        normalMatrix.transpose();

        for (int i = 0; i < mesh.getPartCount(); i++) {
            MeshPart meshPart = mesh.getPart(i);
            renderMeshPartToShadowMap(mesh, meshPart, shadowMapWidth, shadowMapHeight);
        }
    }

    private void renderMeshPartToShadowMap(Mesh mesh, MeshPart meshPart, int shadowMapWidth, int shadowMapHeight) {
        VertexData vertexData = mesh.getVertexData().get();
        IndexData indexData = mesh.getIndexData().get();

        Program program = getProgram(getVertexShaderFileName(vertexData), "src/shaders/deferred/shadow_map.frag");
        program.use();
        program.setUniform("shadowMapWidth", shadowMapWidth);
        program.setUniform("shadowMapHeight", shadowMapHeight);
        bindVertexAttributes(program, mesh);
        int texUnitStart = setupVertexShaderUniforms(program, mesh);

        // Set uniforms.
        setMatrixUniforms(program);

        // Draw the mesh part.
        drawElement(indexData, meshPart);

        // Tear down the program.
        tearDownVertexShaderUniforms(program, mesh);
        disableVertexAttributes(program, mesh);
        program.unuse();
    }

    private void renderSceneToShadowMap(GL2 gl, SceneTreeNode node, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        shadowMapRenderer.shadowMapWidth = width;
        shadowMapRenderer.shadowMapHeight = height;
        node.letTraverse(shadowMapRenderer);
        gl.glFlush();
    }
    
    private void renderFullScreenQuadToTextureRectBuffer(GL2 gl, Program program, int width, int height,
            TextureRectBufferCollection buffers,
            boolean clear, boolean swap) {
		fbo.bind();
		buffers.attachTo(fbo);
		fbo.drawTo(0, 1);
		drawFullScreenQuad(program, width, height, clear);
		gl.glFlush();
		fbo.drawToNone();
		fbo.detachAll();
		fbo.unbind();
		if (swap)
			buffers.swap();
	}
}
