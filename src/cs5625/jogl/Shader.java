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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.media.opengl.GL2;

public abstract class Shader implements GLResource {
    // Check whether the GLSL vertex and fragment shaders are supported
    public static Boolean checkGlslSupport(GL2 gl) {
        if (!gl.isExtensionAvailable("GL_ARB_vertex_shader")
                || !gl.isExtensionAvailable("GL_ARB_fragment_shader")) {

            System.err.println("GLSL is not supported!");
            return false;

        } else {
            System.out.println("GLSL is supported!");
            return true;
        }
    }

    public static String getInfoLog(GL2 gl, int objectId) {
        int[] buf = new int[1];

        // Retrieve the log length
        gl.glGetObjectParameterivARB(objectId,
                GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB, buf, 0);

        int logLength = buf[0];

        if (logLength <= 1) {
            return "";
        } else {
            // Retrieve the log message
            byte[] content = new byte[logLength + 1];
            gl.glGetInfoLogARB(objectId, logLength, buf, 0, content, 0);

            return new String(content);
        }
    }

    // ************* Private variables *************
    private final int type; // GL2.GL_FRAGMENT_SHADER or GL2.GL_VERTEX_SHADER
    private int id;
    private GL2 gl;
    private boolean disposed;

    /**
     * Create a shader from a source code text.
     *
     * @param shaderType the type of the shader
     * @param gl         the OpenGL context
     * @param src        the source code
     * @param fileName   the name of the file that contains the source code.
     * @throws GlslException
     */
    public Shader(int shaderType, GL2 gl,
                  String src, String fileName) throws GlslException {
        this.type = shaderType;
        this.gl = gl;

        this.id = this.gl.glCreateShaderObjectARB(this.type);

        setSource(src);

        if (!compile()) {
            if (src != null) {
                throw new GlslException("Compiliation error in " + fileName + "\n"
                        + getInfoLog(this.gl, this.id));
            } else {
                throw new GlslException("Compilation error "
                        + getInfoLog(this.gl, this.id));
            }
        }
    }

    public Shader(int shaderType, GL2 gl, String src) {
        this(shaderType, gl, src, null);
    }

    public int getId() {
        return this.id;
    }

    public void disposeGL() {
        if (!disposed) {
            gl.glDeleteShader(this.id);
            disposed = true;
        }
    }

    private void setSource(String source) {
        // Attach the GLSL source code
        gl.glShaderSourceARB(this.id, 1,
                new String[]
                        {
                                source
                        },
                new int[]
                        {
                                source.length()
                        }, 0);
    }

    private Boolean compile() {
        // Try to compile the GLSL source code
        gl.glCompileShaderARB(this.id);

        // Check the compilation status
        int[] compileCheck = new int[1];
        this.gl.glGetObjectParameterivARB(this.id,
                GL2.GL_OBJECT_COMPILE_STATUS_ARB, compileCheck, 0);

        return compileCheck[0] == GL2.GL_TRUE;
    }
}
