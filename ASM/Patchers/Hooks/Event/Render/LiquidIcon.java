package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.Side;

public class LiquidIcon extends Patcher {

	public LiquidIcon() {
		super("net.minecraft.block.BlockLiquid", "alw");
	}

	@Override
	protected void apply(ClassNode cn) {
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_149673_e" : "getIcon";

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new VarInsnNode(Opcodes.ILOAD, 4));
		li.add(new VarInsnNode(Opcodes.ILOAD, 5));
		//li.add(new VarInsnNode(Opcodes.ILOAD, 5));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/LiquidBlockIconEvent", "fire", "(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;", false));
		li.add(new InsnNode(Opcodes.ARETURN));

		MethodNode m = ReikaASMHelper.addMethod(cn, li, name, "(Lnet/minecraft/world/IBlockAccess;IIII)Lnet/minecraft/util/IIcon;", Modifier.PUBLIC);
		//m.visibleAnnotations.add(new AnnotationNode(Override.class.getName()));
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

}
