package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class EventPrePost extends Patcher {

	public EventPrePost() {
		super("cpw.mods.fml.common.eventhandler.EventBus");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z");
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, "invoke");

		m.instructions.insertBefore(min, this.createCall(true));
		m.instructions.insert(min, this.createCall(false));
	}

	private InsnList createCall(boolean pre) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1)); //Event
		li.add(new VarInsnNode(Opcodes.ALOAD, 2)); //Listeners[index]
		li.add(new VarInsnNode(Opcodes.ILOAD, 3)); //also
		li.add(new InsnNode(Opcodes.AALOAD));	   //also
		String sig = "(Lcpw/mods/fml/common/eventhandler/Event;Lcpw/mods/fml/common/eventhandler/IEventListener;)V";
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/EventProfiler", pre ? "firePre" : "firePost", sig, false));
		return li;
	}

}
