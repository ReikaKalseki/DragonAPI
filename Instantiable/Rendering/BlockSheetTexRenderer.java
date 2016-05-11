/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Auxiliary.ReikaBlockRenderer;
import Reika.DragonAPI.Interfaces.Block.SidedTextureIndex;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BlockSheetTexRenderer implements ISimpleBlockRenderingHandler {

	private String textureSheet;
	private boolean is3D;
	private final Class modClass;

	public BlockSheetTexRenderer(Class root, String file) {
		is3D = true;
		modClass = root;
		//textureSheet = ReikaSpriteSheets.setupTextures(root, path);
		String filename;/*
		if (backup == null)
			backup = "";
		if (file == null || root == null)
			return;
		//if (root.getResource(file) == null && root.getResource(backup) == null)
			//return;
		if (root.getResource(file) == null)
			filename = backup;
		else
			filename = root.getResource(file).getPath();*/
		if (root == null)
			return;
		if (root.getResource(".") == null)
			filename = "";
		else {
			String base = root.getResource(".").getPath();
			String path = base.substring(1, base.length()-1);
			filename = path+file;
		}
		//ReikaJavaLibrary.pConsole("BLOCK @ "+filename+" from "+file+" Exists:");
		textureSheet = filename;//Minecraft.getMinecraft().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTextureImage(root, file, backup));

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		SidedTextureIndex s = (SidedTextureIndex)block;
		int[] indices = new int[6];
		for (int i = 0; i < 6; i++)
			indices[i] = s.getBlockTextureFromSideAndMetadata(i, metadata);
		ReikaTextureHelper.bindTexture(modClass, textureSheet);
		ReikaBlockRenderer.instance.renderBlockInInventory(block, 0, 0F, indices);
		ReikaTextureHelper.bindTerrainTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
		int metadata = world.getBlockMetadata(x, y, z);
		//ReikaBlockRenderer.instance.renderCube(block, x, y, z, 1F, 1F, 1F, metadata, world, textureSheet, modClass);
		//if (!Loader.isModLoaded("Optifine"))
		ReikaTextureHelper.bindTerrainTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int model) {
		return is3D;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
