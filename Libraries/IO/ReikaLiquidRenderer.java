package Reika.DragonAPI.Libraries.IO;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.IO.BlockModelRenderer.ModelBlockInterface;

public class ReikaLiquidRenderer {

	private static Map<LiquidStack, int[]> flowingRenderCache = new HashMap<LiquidStack, int[]>();
	private static Map<LiquidStack, int[]> stillRenderCache = new HashMap<LiquidStack, int[]>();
	public static final int LEVELS = 100;
	private static final ModelBlockInterface liquidBlock = new ModelBlockInterface();

	public static Icon getLiquidTexture(LiquidStack liquid) {
		if (liquid == null || liquid.itemID <= 0) {
			return null;
		}
		LiquidStack canon = liquid.canonical();
		if (canon == null) {
			throw new MisuseException(liquid+" does not exist!");
		}
		Icon icon = canon.getRenderingIcon();
		if (icon == null) {
			throw new MisuseException(liquid+" has no icon!");
		}
		return icon;
	}

	public static String getLiquidSheet(LiquidStack liquid) {
		if (liquid == null || liquid.itemID <= 0) {
			return "/terrain.png";
		}
		LiquidStack canon = liquid.canonical();
		if (canon == null) {
			throw new MisuseException(liquid+" does not exist!");
		}
		return canon.getTextureSheet();
	}

	public static int[] getGLLists(LiquidStack liquid, World world, boolean flowing) {
		if (liquid == null) {
			return null;
		}
		liquid = liquid.canonical();
		if(liquid == null) {
			throw new MisuseException(liquid+" does not exist!");
		}
		Map<LiquidStack, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;
		int[] diplayLists = cache.get(liquid);
		if (diplayLists != null) {
			return diplayLists;
		}

		diplayLists = new int[LEVELS];

		if (liquid.itemID < Block.blocksList.length && Block.blocksList[liquid.itemID] != null) {
			liquidBlock.baseBlock = Block.blocksList[liquid.itemID];
			if (!flowing) {
				liquidBlock.texture = getLiquidTexture(liquid);
			}
		} else if (Item.itemsList[liquid.itemID] != null) {
			liquidBlock.baseBlock = Block.waterStill;
			liquidBlock.texture = getLiquidTexture(liquid);
		} else {
			return null;
		}

		cache.put(liquid, diplayLists);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		ItemStack stack = liquid.asItemStack();
		int color = stack.getItem().getColorFromItemStack(stack, 0);
		float c1 = (color >> 16 & 255) / 255.0F;
		float c2 = (color >> 8 & 255) / 255.0F;
		float c3 = (color & 255) / 255.0F;
		GL11.glColor4f(c1, c2, c3, 1);
		for (int s = 0; s < LEVELS; ++s) {
			diplayLists[s] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(diplayLists[s], 4864 /*GL_COMPILE*/);

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
}
