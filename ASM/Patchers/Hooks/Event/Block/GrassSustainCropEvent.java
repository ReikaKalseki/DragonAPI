package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Block;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class GrassSustainCropEvent extends Patcher {

	public GrassSustainCropEvent() {
		super("net.minecraft.block.BlockGrass", "alh");
	}

	@Override
	protected void apply(ClassNode cn) {
		String sig = "(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;Lnet/minecraftforge/common/IPlantable;)Z";
		String sig2 = ReikaASMHelper.addTrailingArgument(sig, "Z");
		String name = "canSustainPlant"; //no srg, is forge
		String name2 = "fireSustain";
		this.injectMethod(cn, name, name2, sig, sig2, 6);

		sig = "(Lnet/minecraft/world/World;III)Z";
		sig2 = ReikaASMHelper.addTrailingArgument(sig, "Z");
		name = "isFertile"; //no srg, is forge
		name2 = "fireFertility";
		this.injectMethod(cn, name, name2, sig, sig2, 4);

		//if (/*ModList.AGRICRAFT.isLoaded()*/ReikaASMHelper.checkForClass("com/InfinityRaider/AgriCraft/api/v1/ISoilContainer")) { //too early for fml ref
		ReikaASMHelper.log("Adding AgriCraft module for "+this);
		cn.interfaces.add("com/InfinityRaider/AgriCraft/api/v1/ISoilContainer"); //have to ship

		sig = "(Lnet/minecraft/world/World;III)Lnet/minecraft/block/Block;";
		sig2 = ReikaASMHelper.addLeadingArgument(sig, "Lnet/minecraft/block/Block;");
		name = "getSoil";
		name2 = "fireAgricraft_Block";
		InsnList li = new InsnList();
		//li.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/init/Blocks", "farmland", "Lnet/minecraft/block/Block;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GrassSustainCropEvent", name2, sig2, false));
		li.add(new InsnNode(Opcodes.ARETURN));
		ReikaASMHelper.addMethod(cn, li, name, sig, Modifier.PUBLIC);

		sig = "(Lnet/minecraft/world/World;III)I";
		sig2 = sig;
		name = "getSoilMeta";
		name2 = "fireAgricraft_Meta";
		li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GrassSustainCropEvent", name2, sig2, false));
		li.add(new InsnNode(Opcodes.IRETURN));
		ReikaASMHelper.addMethod(cn, li, name, sig, Modifier.PUBLIC);
		//}
	}

	private void injectMethod(ClassNode cn, String name, String name2, String sig, String sig2, int varCount) {
		InsnList li = new InsnList();
		/*
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new VarInsnNode(Opcodes.ALOAD, 5));
		li.add(new VarInsnNode(Opcodes.ALOAD, 6));
		 */
		for (int i = 1; i <= varCount; i++) {
			int op = i == 2 || i == 3 || i == 4 ? Opcodes.ILOAD : Opcodes.ALOAD;
			li.add(new VarInsnNode(op, i));
		}
		for (int i = 0; i <= varCount; i++) {
			int op = i == 2 || i == 3 || i == 4 ? Opcodes.ILOAD : Opcodes.ALOAD;
			li.add(new VarInsnNode(op, i));
		}

		/*
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new VarInsnNode(Opcodes.ALOAD, 5));
		li.add(new VarInsnNode(Opcodes.ALOAD, 6));
		 */
		li.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/block/Block", name, sig, false));

		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/GrassSustainCropEvent", name2, sig2, false));
		li.add(new InsnNode(Opcodes.IRETURN));
		ReikaASMHelper.addMethod(cn, li, name, sig, Modifier.PUBLIC);
	}

}
