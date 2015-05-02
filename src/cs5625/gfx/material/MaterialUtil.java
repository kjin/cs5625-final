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

package cs5625.gfx.material;

import javax.media.opengl.GL2;
import java.util.HashMap;

public class MaterialUtil {
    private final static HashMap<Integer, String> blendFuncIntToString = new HashMap<Integer, String>();
    private final static HashMap<String, Integer> blendFuncStringToInt = new HashMap<String, Integer>();

    static {
        blendFuncIntToString.put(GL2.GL_ZERO, "GL_ZERO");
        blendFuncIntToString.put(GL2.GL_ONE, "GL_ONE");
        blendFuncIntToString.put(GL2.GL_SRC_COLOR, "GL_SRC_COLOR");
        blendFuncIntToString.put(GL2.GL_ONE_MINUS_SRC_COLOR, "GL_ONE_MINUS_SRC_COLOR");
        blendFuncIntToString.put(GL2.GL_DST_COLOR, "GL_DST_COLOR");
        blendFuncIntToString.put(GL2.GL_ONE_MINUS_DST_COLOR, "GL_ONE_MINUS_DST_COLOR");
        blendFuncIntToString.put(GL2.GL_SRC_ALPHA, "GL_SRC_ALPHA");
        blendFuncIntToString.put(GL2.GL_ONE_MINUS_SRC_ALPHA, "GL_ONE_MINUS_SRC_ALPHA");
        blendFuncIntToString.put(GL2.GL_DST_ALPHA, "GL_DST_ALPHA");
        blendFuncIntToString.put(GL2.GL_ONE_MINUS_DST_ALPHA, "GL_ONE_MINUS_DST_ALPHA");
        blendFuncIntToString.put(GL2.GL_CONSTANT_COLOR, "GL_CONSTANT_COLOR");
        blendFuncIntToString.put(GL2.GL_ONE_MINUS_CONSTANT_COLOR, "GL_ONE_MINUS_CONSTANT_COLOR");
        blendFuncIntToString.put(GL2.GL_CONSTANT_ALPHA, "GL_CONSTANT_ALPHA");
        blendFuncIntToString.put(GL2.GL_ONE_MINUS_CONSTANT_ALPHA, "GL_ONE_MINUS_CONSTANT_ALPHA");
        blendFuncIntToString.put(GL2.GL_SRC_ALPHA_SATURATE, "GL_SRC_ALPHA_SATURATE");

        blendFuncStringToInt.put("GL_ZERO", GL2.GL_ZERO);
        blendFuncStringToInt.put("GL_ONE", GL2.GL_ONE);
        blendFuncStringToInt.put("GL_SRC_COLOR", GL2.GL_SRC_COLOR);
        blendFuncStringToInt.put("GL_ONE_MINUS_SRC_COLOR", GL2.GL_ONE_MINUS_SRC_COLOR);
        blendFuncStringToInt.put("GL_DST_COLOR", GL2.GL_DST_COLOR);
        blendFuncStringToInt.put("GL_ONE_MINUS_DST_COLOR", GL2.GL_ONE_MINUS_DST_COLOR);
        blendFuncStringToInt.put("GL_SRC_ALPHA", GL2.GL_SRC_ALPHA);
        blendFuncStringToInt.put("GL_ONE_MINUS_SRC_ALPHA", GL2.GL_ONE_MINUS_SRC_ALPHA);
        blendFuncStringToInt.put("GL_DST_ALPHA", GL2.GL_DST_ALPHA);
        blendFuncStringToInt.put("GL_ONE_MINUS_DST_ALPHA", GL2.GL_ONE_MINUS_DST_ALPHA);
        blendFuncStringToInt.put("GL_CONSTANT_COLOR", GL2.GL_CONSTANT_COLOR);
        blendFuncStringToInt.put("GL_ONE_MINUS_CONSTANT_COLOR", GL2.GL_ONE_MINUS_CONSTANT_COLOR);
        blendFuncStringToInt.put("GL_CONSTANT_ALPHA", GL2.GL_CONSTANT_ALPHA);
        blendFuncStringToInt.put("GL_ONE_MINUS_CONSTANT_ALPHA", GL2.GL_ONE_MINUS_CONSTANT_ALPHA);
        blendFuncStringToInt.put("GL_SRC_ALPHA_SATURATE", GL2.GL_SRC_ALPHA_SATURATE);
    }

    public static int blendFuncConstant(String name) {
        if (!blendFuncStringToInt.containsKey(name))
            throw new RuntimeException("invalid blend function name '" + name + "'");
        else
            return blendFuncStringToInt.get(name);
    }

    public static String blendFuncName(int constant) {
        if (!blendFuncIntToString.containsKey(constant)) {
            throw new RuntimeException("unsupported blend function constant '" + constant + "'");
        } else {
            return blendFuncIntToString.get(constant);
        }
    }
}
