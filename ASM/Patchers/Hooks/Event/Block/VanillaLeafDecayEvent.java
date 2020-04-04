package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;


public class VanillaLeafDecayEvent extends LeafDecayEvent {

	public VanillaLeafDecayEvent() {
		super("net.minecraft.block.BlockLeaves", "alt");
	}

}
