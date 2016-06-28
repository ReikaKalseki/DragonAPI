package reika.dragonapi.asm.patchers;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class PlayerRenderPass extends Patcher {

	public PlayerRenderPass() {
		super("net.minecraft.entity.player.EntityPlayer", "yz");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList insns = new InsnList();
		insns.add(new InsnNode(Opcodes.ICONST_1));
		insns.add(new InsnNode(Opcodes.IRETURN));
		ReikaASMHelper.addMethod(cn, insns, "shouldRenderInPass", "(I)Z", Modifier.PUBLIC); //Forge method, no SRG
	}
}
