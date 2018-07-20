/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Fixes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Extras.ReplacementSmeltingHandler;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class FurnaceRecipeRewrite extends Patcher {

	private final HashMap<String, String> redirects = new HashMap();

	public FurnaceRecipeRewrite() {
		super("net.minecraft.item.crafting.FurnaceRecipes", "afa");

		redirects.put("getSmeltingResult", "getResult");
		redirects.put("func_151395_a", "getResult");

		redirects.put("getSmeltingList", "getList");
		redirects.put("func_77599_b", "getList");

		redirects.put("func_151398_b", "getSmeltingXPByOutput");
	}

	@Override
	protected void apply(ClassNode cn) {
		//cn.fields.clear();
		Collection<MethodNode> c = new ArrayList(cn.methods);


		for (MethodNode m : c) {
			if (m.name.equals("<init>")) {
				AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
				m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Extras/ReplacementSmeltingHandler", "onSmeltingInit", "(Lnet/minecraft/item/crafting/FurnaceRecipes;)V", false));
				m.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 0));
				//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
				//ReikaJavaLibrary.pConsole("");
			}
			else if (m.name.equals("func_151394_a")) { //addRecipe
				String orig = m.name;
				m.name = orig+"_redirect";

				LabelNode L1 = new LabelNode();
				LabelNode L2 = new LabelNode();

				InsnList li = new InsnList();
				li.add(new VarInsnNode(Opcodes.ALOAD, 1));
				li.add(new VarInsnNode(Opcodes.ALOAD, 2));
				li.add(new VarInsnNode(Opcodes.FLOAD, 3));
				li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Extras/ReplacementSmeltingHandler", "checkRecipe", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;F)Z", false));
				li.add(new JumpInsnNode(Opcodes.IFEQ, L1));
				li.add(L2);
				li.add(new VarInsnNode(Opcodes.ALOAD, 0));
				li.add(new VarInsnNode(Opcodes.ALOAD, 1));
				li.add(new VarInsnNode(Opcodes.ALOAD, 2));
				li.add(new VarInsnNode(Opcodes.FLOAD, 3));
				li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, cn.name, m.name, "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;F)V", false));
				li.add(L1);
				li.add(new FrameNode(Opcodes.F_SAME, 0, new Object[0], 0, new Object[0]));
				li.add(new InsnNode(Opcodes.RETURN));
				ReikaASMHelper.addMethod(cn, li, orig, m.desc, m.access);
			}
			else {
				String redirect = redirects.get(m.name);
				if (redirect != null) {
					String orig = m.name;
					m.name = orig+"_redirect";

					InsnList args = new InsnList();
					int i = 1;
					for (String s : ReikaASMHelper.parseMethodArguments(m)) {
						args.add(new VarInsnNode(ReikaASMHelper.getLoadOpcodeForArgument(s), i));
						i++;
					}

					LabelNode L1 = new LabelNode();
					LabelNode L2 = new LabelNode();

					InsnList li = new InsnList();

					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Extras/ReplacementSmeltingHandler", "isCompiled", "()Z", false));
					li.add(new JumpInsnNode(Opcodes.IFEQ, L1));

					li.add(ReikaASMHelper.copyInsnList(args));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Extras/ReplacementSmeltingHandler", redirect, m.desc, false));

					li.add(new JumpInsnNode(Opcodes.GOTO, L2));
					li.add(L1);
					li.add(new FrameNode(Opcodes.F_SAME, 0, new Object[0], 0, new Object[0]));
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(ReikaASMHelper.copyInsnList(args));
					li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, cn.name, m.name, m.desc, false));
					li.add(L2);
					String frame = ReikaASMHelper.convertLPrefixToPlain(ReikaASMHelper.getMethodReturnType(m));
					if (frame.equals("F")) {
						frame = "float";
					}
					li.add(new FrameNode(Opcodes.F_SAME1, 0, new Object[0], 1, new Object[]{frame}));
					li.add(new InsnNode(ReikaASMHelper.getOpcodeForMethodReturn(m)));

					ReikaASMHelper.addMethod(cn, li, orig, m.desc, m.access);

					ReikaASMHelper.log("Redirecting furnace smelting recipes method '"+orig+"' to DragonAPI replacement '"+redirect+"' (delegating to '"+m.name+"') with signature '"+m.desc+"'");
				}
			}
		}
	}

	@Override
	public boolean computeFrames() {
		return super.computeFrames();//true;
	}

	public void addRecipe(ItemStack in, ItemStack out, float xp) {
		if (ReplacementSmeltingHandler.checkRecipe(in, out, xp)) {
			this.doAddRecipe(in, out, xp);
		}
	}

	public void doAddRecipe(ItemStack in, ItemStack out, float xp) {

	}

}
