package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.tree.ClassNode;

public class LiquidIcon extends BlockIconPatch {

	public LiquidIcon() {
		super("net.minecraft.block.BlockLiquid", "alw");
	}

	@Override
	protected void addSimpleCall(ClassNode cn) {
		//NO-OP
	}

}
