package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Item;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class BlockTilledEvent extends Patcher {

	public BlockTilledEvent() {
		super("net.minecraft.item.ItemHoe", "ada");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77648_a", "onItemUse", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z");

		MethodInsnNode ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", FMLForgePlugin.RUNTIME_DEOBF ? "func_147449_b" : "setBlock", "(IIILnet/minecraft/block/Block;)Z");
		AbstractInsnNode ref = ain.getPrevious().getPrevious();
		m.instructions.remove(ain.getNext());
		m.instructions.remove(ain.getPrevious());
		m.instructions.remove(ain);

		m.instructions.insert(ref, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTillEvent", "fire", "(Lnet/minecraft/world/World;III)V", false));
	}

}
