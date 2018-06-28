package Reika.DragonAPI.ASM.Patchers.Fixes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

/** Fixes Forge only checking for BlockFluidBase, which neither vanilla, CoFH's overwrites, nor some mod fluids extend. Also
 * allows location-specific density, which would be the only way to implement temperature-specific density (as real fluids have).*/
public class FluidDensityCheck extends Patcher {

	public FluidDensityCheck() {
		super("net.minecraftforge.fluids.BlockFluidBase");
	}

	@Override
	protected void apply(ClassNode cn) {
		String sig = "(Lnet/minecraft/world/IBlockAccess;III)I";
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "getDensity", sig);
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/Patchers/Fixes/FluidDensityCheck", "getDensityOverride", sig, false));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));
	}

	public static int getDensityOverride(IBlockAccess world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b instanceof IFluidBlock || b instanceof BlockLiquid) {
			Fluid f = FluidRegistry.lookupFluidForBlock(b);
			if (f == null && b.getMaterial() == Material.water)
				f = FluidRegistry.WATER;
			if (f == null && b.getMaterial() == Material.lava)
				f = FluidRegistry.LAVA;
			if (f != null) {
				return world instanceof World ? f.getDensity((World)world, x, y, z) : f.getDensity();
			}
		}
		return Integer.MAX_VALUE;
	}

}
