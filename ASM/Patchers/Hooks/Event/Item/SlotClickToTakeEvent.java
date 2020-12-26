package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Item;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SlotClickToTakeEvent extends Patcher {

	public SlotClickToTakeEvent() {
		super("net.minecraft.inventory.Container", "zs");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75144_a", "slotClick", "(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
		MethodInsnNode min = ReikaASMHelper.getNthMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_82869_a" : "canTakeStack", 2);
		min.setOpcode(Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/Instantiable/Event/SlotEvent$ClickSlotEvent";
		min.name = "fire";
		min.desc = "(Lnet/minecraft/inventory/Slot;Lnet/minecraft/entity/player/EntityPlayer;I)Z";
		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 2));
	}

}
