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

package cs5625.gfx.gldata;

import cs5625.gfx.glcache.GLResourceProvider;
import cs5625.gfx.objcache.LoadableByKey;
import cs5625.jogl.Texture2D;

public interface Texture2DData extends GLResourceProvider<Texture2D>, LoadableByKey {
	public int getWidth();
	public int getHeight();
	public void updateTexture2D(Texture2D texture);
}
