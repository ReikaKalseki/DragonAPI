package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World.Gen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SnowIceGenEvent extends Patcher {

	public SnowIceGenEvent() {
		super("net.minecraft.world.gen.ChunkProviderGenerate", "aqz");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73153_a", "populate", "(Lnet/minecraft/world/chunk/IChunkProvider;II)V");
		this.redirect(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_72884_u" : "isBlockFreezable", "fireIce");
		this.redirect(cn, m, "func_147478_e", "fireSnow");
	}

	private void redirect(ClassNode cn, MethodNode m, String seek, String call) {
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, seek);
		min.owner = "Reika/DragonAPI/Instantiable/Event/SnowOrIceOnGenEvent";
		min.name = call;
		min.itf = false;
		min.setOpcode(Opcodes.INVOKESTATIC);
		ReikaASMHelper.addLeadingArgument(min, "Lnet/minecraft/world/World;");
	}

}
