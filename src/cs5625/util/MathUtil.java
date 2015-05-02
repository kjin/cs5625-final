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

package cs5625.util;

public class MathUtil {
    public static int getClosestPowerOfTwo(int x) {
        if (x < 0)
            return 1;
        else {
            int y = 1;
            while (y < x) {
                y = y*2;
            }
            if (Math.abs(y-x) < Math.abs(y/2-x)) {
                return y;
            } else {
                return y/2;
            }
        }
    }

    public static int getCeilingPowerOfTwo(int x) {
        if (x < 0) {
            return 1;
        } else {
            int y = 1;
            while (y < x) {
                y *= 2;
            }
            return y;
        }
    }

    public static boolean isPowerOfTwo(int x) {
        x = Math.abs(x);
        if (x == 0)
            return false;
        else if (x == 1)
            return true;
        else {
            while (x > 1) {
                if ((x & 1) == 1)
                    return false;
                else
                    x = (x >> 1);
            }
            return x == 1;
        }
    }
}
