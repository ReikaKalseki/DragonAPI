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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

@Deprecated
public class CraftingRecipeRewrite1 extends Patcher {

	public CraftingRecipeRewrite1() {
		super("net.minecraft.item.crafting.CraftingManager#", "afe#");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77592_b", "getRecipeList", "()Ljava/util/List;");
		this.redirectMethod(m, "getRecipeList");

		m = ReikaASMHelper.getMethodByName(cn, "func_82787_a", "findMatchingRecipe", "(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Lnet/minecraft/item/ItemStack;");
		this.redirectMethod(m, "getRecipe");

		m = ReikaASMHelper.getMethodByName(cn, "<init>", "()V"); //Redirect internal list
		TypeInsnNode type = (TypeInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.NEW);
		MethodInsnNode cons = (MethodInsnNode)type.getNext().getNext();
		String s = "Reika/DragonAPI/Extras/ReplacementCraftingHandler$RecipeList";
		type.desc = s;
		cons.owner = s;
	}

	private void redirectMethod(MethodNode m, String newname) {
		m.instructions.clear();
		int i = 1;
		for (String s : ReikaASMHelper.parseMethodArguments(m)) {
			m.instructions.add(new VarInsnNode(ReikaASMHelper.getLoadOpcodeForArgument(s), i));
			i++;
		}
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Extras/ReplacementCraftingHandler", newname, m.desc, false));
		m.instructions.add(new InsnNode(ReikaASMHelper.getOpcodeForMethodReturn(m)));
		ReikaASMHelper.log("Redirecting crafting recipe handler method '"+m.name+"' with desc '"+m.desc+"'");
	}

}
