package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class NullEntityRender extends Render {

	public static final NullEntityRender instance = new NullEntityRender();

	private NullEntityRender() {

	}

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float f, float ptick) {

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

}
