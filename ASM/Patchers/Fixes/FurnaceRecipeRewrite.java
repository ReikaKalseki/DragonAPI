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

import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class FurnaceRecipeRewrite extends Patcher {

	public FurnaceRecipeRewrite() {
		super("net.minecraft.item.crafting.FurnaceRecipes", "afa");
	}

	@Override
	protected void apply(ClassNode cn) {
		//cn.fields.clear();
		Iterator<MethodNode> it = cn.methods.iterator();
		while (it.hasNext()) {
			MethodNode m = it.next();
			if (m.name.contains("init")) { //leave constructor empty
				m.instructions.add(new InsnNode(Opcodes.RETURN));
			}
			else {
				if ((m.access & Modifier.STATIC) != 0) {
					//skip
				}
				else if ((m.access & Modifier.PRIVATE) != 0) {
					it.remove();
				}
				else {
					m.instructions.clear();
					int i = 1;
					for (String s : ReikaASMHelper.parseMethodArguments(m)) {
						m.instructions.add(new VarInsnNode(ReikaASMHelper.getLoadOpcodeForArgument(s), i));
						i++;
					}
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Extras/ReplacementSmeltingHandler", m.name, m.desc, false));
					m.instructions.add(new InsnNode(ReikaASMHelper.getOpcodeForMethodReturn(m)));
					ReikaASMHelper.log("Redirecting furnace smelting recipes method '"+m.name+"' with desc '"+m.desc+"'");
				}
			}
		}
	}

}
