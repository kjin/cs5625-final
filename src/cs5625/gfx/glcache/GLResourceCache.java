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

package cs5625.gfx.glcache;

import cs5625.jogl.GLResource;

import javax.media.opengl.GL2;
import java.util.*;

/**
 * A cache of OpenGL resources.  It keeps track of instances of GLResources and corresponding GLResourceProviders that
 * can update them.  The function collectGarbage can be called to evict least recently used resources to lower memory
 * consumption below a certain capacity.
 */
public class GLResourceCache {
    private static class SingleHolder {
        private static final GLResourceCache instance = new GLResourceCache();
    }

    public static GLResourceCache v() {
        return SingleHolder.instance;
    }

    private GLResourceCache() {
        // NOP
    }

    private HashMap<GLResourceProvider, GLResourceRecord> records =
            new HashMap<GLResourceProvider, GLResourceRecord>();
    private int capacity = (1 << 20) * 256;

    public GLResource getGLResource(GL2 gl, GLResourceProvider provider) {
        GLResourceRecord record = null;
        if (!records.containsKey(provider)) {
            record = new GLResourceRecord();
            records.put(provider, record);
        } else {
            record = records.get(provider);
        }
        provider.updateGLResource(gl, record);
        record.lastUsage = System.nanoTime();
        return record.resource;
    }

    public void collectGarbage() {
        int size = 0;
        for (GLResourceRecord record : records.values()) {
            size += record.sizeInBytes;
        }

        //System.out.println("size = " + size + "; capacity = " + capacity);
        if (size > capacity) {
            ArrayList<Map.Entry<GLResourceProvider, GLResourceRecord>> recordList
                    = new ArrayList<Map.Entry<GLResourceProvider, GLResourceRecord>>(records.entrySet());
            Collections.sort(recordList, new Comparator<Map.Entry<GLResourceProvider, GLResourceRecord>>() {
                @Override
                public int compare(Map.Entry<GLResourceProvider, GLResourceRecord> o1, Map.Entry<GLResourceProvider, GLResourceRecord> o2) {
                    long t1 = o1.getValue().lastUsage;
                    long t2 = o2.getValue().lastUsage;
                    if (t1 < t2) {
                        return -1;
                    } else if (t1 == t2) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
            for (int i = 0; i < recordList.size(); i++) {
                Map.Entry<GLResourceProvider, GLResourceRecord> record = recordList.get(i);
                record.getValue().resource.disposeGL();
                records.remove(record.getKey());
                size -= record.getValue().sizeInBytes;
                if (size <= capacity/2) {
                    return;
                }
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void disposeGL() {
        for (GLResourceRecord record : records.values()) {
            record.resource.disposeGL();
        }
    }
}
