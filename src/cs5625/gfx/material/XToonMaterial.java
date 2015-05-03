package cs5625.gfx.material;

import cs5625.gfx.objcache.Holder;
import cs5625.gfx.gldata.Texture2DData;

public class XToonMaterial extends AbstractMaterial {
	private Holder<Texture2DData> xtoonTexture = null;
	
	public Holder<Texture2DData> getXToonTexture()
	{
		return xtoonTexture;
	}
	
	public void setXToonTexture(Holder<Texture2DData> texture)
	{
		this.xtoonTexture = texture;
	}
}
