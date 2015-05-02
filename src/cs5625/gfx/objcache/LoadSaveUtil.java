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

package cs5625.gfx.objcache;

public class LoadSaveUtil {
    public static LoadableByKey loadByKey(String key) {
        String className = ObjectCacheKey.getProtocol(key);
        Class<?> klass = null;
        try {
            klass = Class.forName(className);
            if (!LoadableByKey.class.isAssignableFrom(klass))
                throw new RuntimeException("Attempting to load a class not implementing " +
                        LoadableByKey.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("the class named '" + className + "' does not exist");
        }

        try {
            LoadableByKey obj = (LoadableByKey)klass.newInstance();
            obj.loadByKey(key);
            return obj;
        } catch (InstantiationException e) {
            throw new RuntimeException("could not instantiate '" + className +
                    "'. Does the class has a default constructor?");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("illegal access to the static method 'loadByKey' of class '" +
                    className + "'");
        }
    }
}
