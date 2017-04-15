package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import cpw.mods.fml.relauncher.Side;


public class WeatherSkyColorStrength extends Patcher {

	public WeatherSkyColorStrength() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "getSkyColorBody", "(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/Vec3;"); //Forge
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_72867_j" : "getRainStrength";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, cn.name, name, "(F)F");
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/WeatherSkyStrengthEvent";
		min.name = "fire_Rain";
		min.desc = "(Lnet/minecraft/world/World;F)F";
		min.setOpcode(Opcodes.INVOKESTATIC);

		name = FMLForgePlugin.RUNTIME_DEOBF ? "func_72819_i" : "getWeightedThunderStrength";
		min = ReikaASMHelper.getFirstMethodCall(cn, m, cn.name, name, "(F)F");
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/WeatherSkyStrengthEvent";
		min.name = "fire_Thunder";
		min.desc = "(Lnet/minecraft/world/World;F)F";
		min.setOpcode(Opcodes.INVOKESTATIC);
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

}
