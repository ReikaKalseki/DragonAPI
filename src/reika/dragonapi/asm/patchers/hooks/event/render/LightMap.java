package reika.dragonapi.asm.patchers.hooks.event.render;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class LightMap extends Patcher {

	public LightMap() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78472_g", "updateLightmap", "(F)V");
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_110564_a" : "updateDynamicTexture";
		AbstractInsnNode loc = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/client/renderer/texture/DynamicTexture", func, "()V");
		loc = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(loc), Opcodes.ALOAD, 0);
		m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/LightmapEvent", "fire", "()V", false));
	}
}
