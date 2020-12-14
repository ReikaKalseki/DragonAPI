package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.tree.ClassNode;

import Reika.DragonAPI.ASM.Patchers.BlockRenderBrightness;

public class ChunkCacheRenderBrightness extends BlockRenderBrightness {

	public ChunkCacheRenderBrightness() {
		super("net.minecraft.world.ChunkCache", "ahr");
	}

	@Override
	protected void apply(ClassNode cn) {
		this.patchBlockLight(cn);
	}

}
