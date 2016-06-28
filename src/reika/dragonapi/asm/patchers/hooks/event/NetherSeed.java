package reika.dragonapi.asm.patchers.hooks.event;

import java.lang.reflect.Modifier;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class NetherSeed extends Patcher {

	public NetherSeed() {
		super("net.minecraft.world.WorldProviderHell", "aqp");
	}

	@Override
	protected void apply(ClassNode cn) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/DimensionSeedEvent", "fire", "(Lnet/minecraft/world/WorldProvider;)J", false));
		li.add(new InsnNode(Opcodes.LRETURN));
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "getSeed" : "getSeed"; //Forge
		ReikaASMHelper.addMethod(cn, li, name, "()J", Modifier.PUBLIC);
	}
}
