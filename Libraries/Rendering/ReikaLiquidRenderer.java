/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Rendering;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.BlockModelRenderer;
import Reika.DragonAPI.Auxiliary.BlockModelRenderer.ModelBlockInterface;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class ReikaLiquidRenderer {

	private static Map<Fluid, int[]> flowingRenderCache = new HashMap<Fluid, int[]>();
	private static Map<Fluid, int[]> stillRenderCache = new HashMap<Fluid, int[]>();
	public static final int LEVELS = 100;
	private static final ModelBlockInterface liquidBlock = new ModelBlockInterface();

	/*
	public static IIcon getFluidTexture(FluidStack fluidStack, boolean flowing) {
		if (fluidStack == null) {
			return null;
		}
		return getFluidTexture(fluidStack.getFluid(), flowing);
	}

	public static IIcon getFluidTexture(Fluid fluid, boolean flowing) {
		if (fluid == null) {
			return null;
		}
		IIcon icon = flowing ? fluid.getFlowingIcon() : fluid.getStillIcon();
		if (icon == null) {
			icon = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
		}
		return icon;
	}*/

	public static void setFluidColor(FluidStack fluidstack) {
		if (fluidstack == null)
			return;

		int color = fluidstack.getFluid().getColor(fluidstack);
		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;
		//GL11.glColor4f(red, green, blue, 1);
	}

	public static void bindFluidTexture(Fluid fluid) {
		ReikaTextureHelper.bindTerrainTexture();
	}

	public static int[] getGLLists(FluidStack fluidStack, World world, boolean flowing) {
		if (fluidStack == null) {
			return null;
		}
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) {
			return null;
		}
		Map<Fluid, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;
		int[] diplayLists = cache.get(fluid);
		if (diplayLists != null) {
			return diplayLists;
		}

		diplayLists = new int[LEVELS];

		if (fluid.getBlock() != null) {
			liquidBlock.baseBlock = fluid.getBlock();
			liquidBlock.texture = getFluidIconSafe(fluid, flowing);
		}
		else {
			liquidBlock.baseBlock = Blocks.water;
			liquidBlock.texture = getFluidIconSafe(fluid, flowing);
		}

		cache.put(fluid, diplayLists);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);

		for (int s = 0; s < LEVELS; ++s) {
			diplayLists[s] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(diplayLists[s], GL11.GL_COMPILE);

			liquidBlock.minX = 0.01f;
			liquidBlock.minY = 0;
			liquidBlock.minZ = 0.01f;

			liquidBlock.maxX = 0.99f;
			liquidBlock.maxY = (float) s / (float) LEVELS;
			liquidBlock.maxZ = 0.99f;

			BlockModelRenderer.renderBlock(liquidBlock, world, 0, 0, 0, false, true);

			GL11.glEndList();
		}

		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);

		return diplayLists;
	}

	public static IIcon getFluidIconSafe(Fluid f) {
		return getFluidIconSafe(f, false);
	}

	public static IIcon getFluidIconSafe(Fluid f, boolean flowing) {
		IIcon ico = flowing ? f.getFlowingIcon() : f.getStillIcon();
		if (ico == null) {
			DragonAPICore.logError("Fluid "+f.getID()+" ("+f.getLocalizedName()+") exists (block ID "+f.getBlock()+") but has no icon! Registering missingtex as a placeholder!");
			//ico = Blocks.bedrock.getIcon(0, 0);
			ico = ReikaTextureHelper.getMissingIcon();
			f.setIcons(ico);
		}
		return ico;
	}
}
