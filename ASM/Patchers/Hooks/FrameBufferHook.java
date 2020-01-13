package Reika.DragonAPI.ASM.Patchers.Hooks;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class FrameBufferHook extends Patcher {

	public FrameBufferHook() {
		super("net.minecraft.client.Minecraft", "bao");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_71411_J", "runGameLoop", "()V");
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_147615_c" : "framebufferRender");
		min.owner = "Reika/DragonAPI/ASM/ASMCallsClient";
		min.name = "onRenderFrameBuffer";
		min.setOpcode(Opcodes.INVOKESTATIC);
		ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/client/shader/Framebuffer;");
	}

}
