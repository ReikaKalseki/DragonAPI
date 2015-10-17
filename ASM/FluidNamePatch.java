/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class FluidNamePatch implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] data) {
		if (name.equals("net.minecraftforge.fluids.FluidStack")) {
			data = this.patchClass(data);
		}
		return data;
	}

	private byte[] patchClass(byte[] data) {

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(classNode, 0);

		InsnList call = new InsnList();

		call.add(new VarInsnNode(Opcodes.ALOAD, 1));
		call.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Libraries/ReikaFluidHelper", "getOldNameIfApplicable", "(Ljava/lang/String;)Ljava/lang/String;", false));
		call.add(new VarInsnNode(Opcodes.ASTORE, 1));

		MethodNode m = ReikaASMHelper.getMethodByName(classNode, "loadFluidStackFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraftforge/fluids/FluidStack;");

		AbstractInsnNode ain = ReikaASMHelper.getFirstMethodCall(classNode, m, "com/google/common/base/Strings", "isNullOrEmpty", "(Ljava/lang/String;)Z");

		ain = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ASTORE, 1);

		m.instructions.insert(ain, call);

		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));

		ReikaASMHelper.log("Successfully applied Fluid Name patch.");

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		/*
		try {
			FileOutputStream out = new FileOutputStream(new File("FluidStack.class"));
			out.write(writer.toByteArray());
			out.flush(); out.close();
		} catch (Exception ex) {}
		 */
		return writer.toByteArray();
	}

}
