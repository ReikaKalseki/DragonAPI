/*******************************************************************************
 * @author Reika
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Auxiliary.ReikaBlockRenderer;
import Reika.DragonAPI.IO.ReikaPNGLoader;
import Reika.DragonAPI.Interfaces.SidedTextureIndex;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BlockSheetTexRenderer implements ISimpleBlockRenderingHandler {
	
	private int textureSheet;
	private boolean is3D;
	
	public BlockSheetTexRenderer(Class root, String file, String backup) {
		this.is3D = true;
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
		this.textureSheet = Minecraft.getMinecraft().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTextureImage(root, file, backup));
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		SidedTextureIndex s = (SidedTextureIndex)block;
		int[] indices = new int[6];
		for (int i = 0; i < 6; i++)
			indices[i] = s.getBlockTextureFromSideAndMetadata(i, metadata);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureSheet);
		ReikaBlockRenderer.instance.renderBlockInInventory(block, 0, 0F, indices);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 7);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
		int metadata = world.getBlockMetadata(x, y, z);
		ReikaBlockRenderer.instance.renderCube(block, x, y, z, 1F, 1F, 1F, metadata, world, this.textureSheet);
		//if (!Loader.isModLoaded("Optifine"))
			Minecraft.getMinecraft().renderEngine.bindTexture("/terrain.png");
        return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return is3D;
	}

	@Override
	public int getRenderId() {
		return 0;
	}

}
