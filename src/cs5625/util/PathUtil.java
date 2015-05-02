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

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
    public static String relativizeSecondToFirst(String firstDir, String secondFile) {
        Path firstPath = Paths.get(firstDir).toAbsolutePath();
        Path secondPath = Paths.get(secondFile).toAbsolutePath();
        Path result = firstPath.relativize(secondPath);
        return FilenameUtils.separatorsToUnix(result.toString());
    }

    public static String relativizeSecondToFirstDir(String firstFile, String secondFile) {
        Path firstPath = Paths.get(firstFile).toAbsolutePath();
        Path secondPath = Paths.get(secondFile).toAbsolutePath();
        Path result = firstPath.getParent().relativize(secondPath);
        return FilenameUtils.separatorsToUnix(result.toString());
    }

    public static String getNormalizedAbsolutePath(String path) {
        path = FilenameUtils.normalize(new File(path).getAbsolutePath(), true);
        return path;
    }

}
