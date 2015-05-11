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

import java.nio.FloatBuffer;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.vecmath.*;

public class Uniform {
    // ************* Private variables *************
    public GL2 gl;
    public Program program;
    public String name;
    public int size;
    public int type;
    public int location;
    public Boolean isRowMajor;

    public Uniform(GL2 glContext, Program prog) {
        this.program = prog;
        this.gl = glContext;
    }

    // ************* Public interface *************
    public Uniform(GL2 glContext, Program prog, int index) {
        this.program = prog;
        this.gl = glContext;

        byte[] uniformName = new byte[512];

        int[] uniformLength = new int[1];
        int[] uniformSize = new int[1];
        int[] uniformType = new int[1];

        // Get the uniform info (name, type, size)
        this.gl.glGetActiveUniform(this.program.getId(), index,
                uniformName.length, uniformLength, 0, uniformSize, 0,
                uniformType, 0, uniformName, 0);

        this.name = new String(uniformName, 0, uniformLength[0]);
        this.size = uniformSize[0];
        this.type = uniformType[0];

        // Get the uniform location within the program
        this.location =
                this.gl.glGetUniformLocation(this.program.getId(), this.name);

        this.isRowMajor = false; // Default is column major format

        // Some Intel drivers return -1 for "gl_" variables.
        // Since we shouldn't be setting "gl_" variables anyway, just skip this part
        // and return an incomplete Uniform with a -1 location.
        if (this.location > -1) {
            // We should provide the index as an int[] buffer
            int[] uniformIndex = new int[]
                    {
                            index
                    };
            int[] uniformIsRowMajor = new int[1];

            // Get the uniform storage format (column VS row major)
            try {
                // Seems like some OpenGL drivers do not implement this function 
                this.gl.glGetActiveUniformsiv(this.program.getId(), 1, uniformIndex, 0,
                        GL2.GL_UNIFORM_IS_ROW_MAJOR, uniformIsRowMajor, 0);

                this.isRowMajor = (uniformIsRowMajor[0] != 0);
            } catch (GLException ex) {
                // NOP
            }
        }

    }

    public Boolean getIsRowMajor() {
        return isRowMajor;
    }

    public int getLocation() {
        return location;
    }

    public int getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
    
    public void set1FloatV(float[] x) {
        this.gl.glUniform1fv(this.location, x.length, x, 0);
    }

    public void set1Int(int x) {
        this.gl.glUniform1i(this.location, x);
    }

    public void set2Int(int x, int y) {
        this.gl.glUniform2i(this.location, x, y);
    }

    public void set3Int(int x, int y, int z) {
        this.gl.glUniform3i(this.location, x, y, z);
    }

    public void set4Int(int x, int y, int z, int w) {
        this.gl.glUniform4i(this.location, x, y, z, w);
    }

    public void set1Float(float x) {
        this.gl.glUniform1f(this.location, x);
    }

    public void set2Float(float x, float y) {
        this.gl.glUniform2f(this.location, x, y);
    }

    public void set3Float(float x, float y, float z) {
        this.gl.glUniform3f(this.location, x, y, z);
    }

    public void set4Float(float x, float y, float z, float w) {
        this.gl.glUniform4f(this.location, x, y, z, w);
    }

    public void setTuple2(Tuple2f v) {
        this.gl.glUniform2f(this.location, v.x, v.y);
    }

    public void setTuple3(Tuple3f v) {
        this.gl.glUniform3f(this.location, v.x, v.y, v.z);
    }

    public void setTuple4(Tuple4f v) {
        this.gl.glUniform4f(this.location, v.x, v.y, v.z, v.w);
    }

    public void setMatrix4(Matrix4f mat) {
        FloatBuffer buf = FloatBuffer.allocate(16);

        // We will pass the matrix elements in column major order
        for (int c = 0; c < 4; ++c) {
            for (int r = 0; r < 4; ++r) {
                buf.put(mat.getElement(r, c));
            }
        }

        buf.rewind();

        this.gl.glUniformMatrix4fv(this.location, 1, false, buf);
    }

    public void setMatrix3(Matrix3f mat) {
        FloatBuffer buf = FloatBuffer.allocate(9);

        // We will pass the matrix elements in column major order
        for (int c = 0; c < 3; ++c) {
            for (int r = 0; r < 3; ++r) {
                buf.put(mat.getElement(r, c));
            }
        }

        buf.rewind();

        this.gl.glUniformMatrix3fv(this.location, 1, false, buf);
    }
}
