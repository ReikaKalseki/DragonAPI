/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import cpw.mods.fml.relauncher.Side;

@Deprecated
public class ParticleEventPhaseReset extends Patcher {

	public ParticleEventPhaseReset() {
		super("Reika.DragonAPI.Instantiable.Event.Client.AddParticleEvent#");
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "getForParticle", "(Lnet/minecraft/client/particle/EntityFX;)LReika/DragonAPI/Instantiable/Event/Client/AddParticleEvent;");
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.ARETURN);
		InsnList li = new InsnList();
		li.add(new InsnNode(Opcodes.ACONST_NULL));
		li.add(new FieldInsnNode(Opcodes.PUTFIELD, "cpw/mods/fml/common/eventhandler/Event", "phase", "Lcpw/mods/fml/common/eventhandler/EventPriority;"));
		li.add(ReikaASMHelper.copyInstruction(ain.getPrevious()));
		m.instructions.insertBefore(ain, li);
	}

}
