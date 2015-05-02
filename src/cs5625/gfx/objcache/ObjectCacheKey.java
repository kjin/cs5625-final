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

import cs5625.util.PathUtil;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Paths;

public class ObjectCacheKey {
    public static final String SEPARATOR = "|||||";
    public static final String PATH_SEPARATOR = ":::::";

    public static String getProtocol(String key) {
        int index = key.indexOf(SEPARATOR);
        if (index == -1)
            throw new RuntimeException("invalid key format (" + key + ")");
        else {
            return key.substring(0, index);
        }
    }

    public static String getKeyData(String key) {
        int index = key.indexOf(SEPARATOR);
        if (index == -1)
            throw new RuntimeException("invalid key format (" + key + ")");
        else {
            return key.substring(index + SEPARATOR.length(), key.length());
        }
    }

    public static String makeKey(Class klass, String keyData) {
        return klass.getName() + SEPARATOR + keyData;
    }

    public static String makeKey(String klass, String keyData) {
        return klass + SEPARATOR + keyData;
    }

    public static String makeKeyFromRelativeFileName(Class klass, String fileName, String directory) {
        return klass.getName() + SEPARATOR + PathUtil.relativizeSecondToFirst(directory, fileName);
    }

    public static String makeKeyFromRelativeFileNames(Class klass, String[] fileNames, String directory) {
        String result = klass.getName() + SEPARATOR + PathUtil.relativizeSecondToFirst(directory, fileNames[0]);;
        for (int i = 1; i < fileNames.length; i++) {
            result += PATH_SEPARATOR + PathUtil.relativizeSecondToFirst(directory, fileNames[i]);
        }
        return result;
    }

    public static String convertToRelativePathKey(String key, String directory) {
        String protocol = getProtocol(key);
        String fileName = getKeyData(key);
        String[] fileNames = fileName.split(PATH_SEPARATOR);
        String newData = PathUtil.relativizeSecondToFirst(directory, fileNames[0]);
        for (int i = 1; i < fileNames.length; i++) {
            newData += PATH_SEPARATOR + PathUtil.relativizeSecondToFirst(directory, fileNames[i]);
        }
        return makeKey(protocol, newData);
    }

    public static String convertToAbsolutePathKey(String key, String directory) {
        String protocol = getProtocol(key);
        String fileName = getKeyData(key);
        String[] fileNames = fileName.split(PATH_SEPARATOR);
        String newData = FilenameUtils.separatorsToUnix(
                Paths.get(directory + "/" + fileNames[0]).toAbsolutePath().toString());
        for (int i = 1; i < fileNames.length; i++) {
            newData += PATH_SEPARATOR + FilenameUtils.separatorsToUnix(
                    Paths.get(directory + "/" + fileNames[i]).toAbsolutePath().toString());
        }
        return makeKey(protocol, newData);
    }
}
