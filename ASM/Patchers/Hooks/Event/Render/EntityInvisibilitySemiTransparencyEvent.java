package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.Side;


public class EntityInvisibilitySemiTransparencyEvent extends Patcher {

	public EntityInvisibilitySemiTransparencyEvent() {
		super("net.minecraft.client.renderer.entity.RendererLivingEntity", "boh");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77036_a", "renderModel", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V");
		AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, 0.15F);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/EntitySemitransparencyEvent", "fire", "(Lnet/minecraft/entity/Entity;)F", false));
		m.instructions.insert(ain, li);
		m.instructions.remove(ain);
	}

	@Override
	public final boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

}
