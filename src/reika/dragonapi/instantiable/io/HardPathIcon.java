/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.io;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import reika.dragonapi.io.DirectResourceManager;


public class HardPathIcon extends TextureAtlasSprite {

	public HardPathIcon(String s) {
		super(s);
	}

	public ResourceLocation getResource() {
		return DirectResourceManager.getResource(this.getIconName());
	}

	public HardPathIcon register(TextureMap m) {
		ResourceLocation loc = this.getResource();
		m.mapRegisteredSprites.put(loc.toString(), this);
		return this;
	}

}
