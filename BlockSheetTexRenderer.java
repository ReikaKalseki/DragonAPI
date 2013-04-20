package Reika.DragonAPI;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.ModLoader;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockSheetTexRenderer implements ISimpleBlockRenderingHandler {
	
	private int textureSheet;
	private boolean is3D;
	private int[][] indices;
	
	public BlockSheetTexRenderer(Class root, String file, String backup) {
		this.is3D = true;
		//textureSheet = ReikaSpriteSheets.setupTextures(root, path);
		String filename;
		if (backup == null)
			backup = "";
		if (file == null || root == null)
			return;
		//if (root.getResource(file) == null && root.getResource(backup) == null)
			//return;
		if (root.getResource(file) == null)
			filename = backup;
		else
			filename = root.getResource(file).getPath();
		this.textureSheet = ModLoader.getMinecraftInstance().renderEngine.allocateAndSetupTexture(ReikaPNGLoader.readTextureImage(filename, backup));
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		SidedTextureIndex s = (SidedTextureIndex)block;
		int index = s.getBlockTextureFromSideAndMetadata(3, metadata);
		int row = index/16;
		int col = index-row*16; 
		GL11.glColor3d(0.6, 0.6, 0.6);
		GL11.glTranslated(0, -0.0625, 0);
		//GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureSheet);
		Tessellator v5 = new Tessellator();
		GL11.glTranslated(-1, -0.875, 0);
		v5.startDrawingQuads();
		v5.setBrightness(5);
        v5.addVertexWithUV(1, 0, 0, 0.0625F*col, 0.0625F*row);
        v5.addVertexWithUV(1, 1, 0, 0.0625F*col, 0.0625F+0.0625F*row);
        v5.addVertexWithUV(0, 1, 0, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
        v5.addVertexWithUV(0, 0, 0, 0.0625F+0.0625F*col, 0.0625F*row);
		v5.draw();
		GL11.glColor3d(0.95, 0.95, 0.95);
		index = s.getBlockTextureFromSideAndMetadata(1, metadata);
		row = index/16;
		col = index-row*16; 
		GL11.glTranslated(-0.03125, 1, 0);
		v5.startDrawingQuads();
        v5.addVertexWithUV(1, 0, 0, 0.0625F*col, 0.0625F*row);
        v5.addVertexWithUV(2, 0.82, 0, 0.0625F*col, 0.0625F+0.0625F*row);
        v5.addVertexWithUV(1, 0.8, 0, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
        v5.addVertexWithUV(0, 0, 0, 0.0625F+0.0625F*col, 0.0625F*row);
		v5.draw();
		index = s.getBlockTextureFromSideAndMetadata(4, metadata);
		row = index/16;
		col = index-row*16;
		GL11.glTranslated(0, -1, 0);
		GL11.glColor3d(0.4, 0.4, 0.4);
		v5.startDrawingQuads();
        v5.addVertexWithUV(1.0625, 0, 0, 0.0625F*col, 0.0625F+0.0625F*row);
        v5.addVertexWithUV(2, 0.78, 0, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
        v5.addVertexWithUV(2, 1.8, 0, 0.0625F+0.0625F*col, 0.0625F*row);
        v5.addVertexWithUV(1.0625, 1.0625, 0, 0.0625F*col, 0.0625F*row);
		v5.draw();
		//ReikaJavaLibrary.pConsole(4);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor3d(1, 1, 1);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {/*
		//renderblocks.overrideBlockTexture;
        renderblocks.renderStandardBlock(block, x, y, z);*/
		SidedTextureIndex s = (SidedTextureIndex)block;
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		double a = x%16;
		double b = y%16;
		double c = z%16;
		World w = ModLoader.getMinecraftInstance().theWorld;
		GL11.glTranslated(a, b, c);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		//GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureSheet);
		Tessellator v5 = new Tessellator();
		for (int i = 0; i < 6; i++) {
			int index = s.getBlockTextureFromSideAndMetadata(i, meta);
			int row = index/16;
			int col = index-row*16;   
			GL11.glColor3d(1, 1, 1);
	        v5.startDrawingQuads();
	        int light = 0;
	        double sky = w.getSunBrightness(0);
	        switch(i) {
	        case 0:
	    		//GL11.glColor3d(0.6, 0.6, 0.6);
	        	light =  w.getBlockLightValue(x, y-1, z);
	        	//v5.setBrightness(248+15-light);
	        	GL11.glColor3d(0.6, 0.6, 0.6);
		        v5.addVertexWithUV(1, 0, 1, 0.0625F*col, 0.0625F*row);
		        v5.addVertexWithUV(0, 0, 1, 0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 0, 0, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(1, 0, 0, 0.0625F+0.0625F*col, 0.0625F*row);
	        break;
	        case 1:
	    		//GL11.glColor3d(1, 1, 1);
	        	light =  w.getBlockLightValue(x, y+1, z);
	        	//v5.setBrightness(238+15-light);
	        	GL11.glColor3d(1, 1, 1);
		        v5.addVertexWithUV(1, 1, 0, 0.0625F*col, 0.0625F*row);
		        v5.addVertexWithUV(0, 1, 0, 0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 1, 1, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(1, 1, 1, 0.0625F+0.0625F*col, 0.0625F*row);
	        break;
	        case 2:
	    		//GL11.glColor3d(1, 1, 1);
	        	light =  w.getBlockLightValue(x, y, z-1);
	        	//v5.setBrightness(242+15-light);
		        v5.addVertexWithUV(1, 1, 0, 0.0625F*col, 0.0625F*row);
		        v5.addVertexWithUV(1, 0, 0, 0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 0, 0, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 1, 0, 0.0625F+0.0625F*col, 0.0625F*row);
	        break;
	        case 3:
	    		//GL11.glColor3d(0.9, 0.9, 0.9);
	        	light =  w.getBlockLightValue(x, y, z+1);
	        	//v5.setBrightness(242+15-light);
		        v5.addVertexWithUV(1, 0, 1, 0.0625F*col, 0.0625F*row);
		        v5.addVertexWithUV(1, 1, 1, 0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 1, 1, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 0, 1, 0.0625F+0.0625F*col, 0.0625F*row);
	        break;
	        case 4:
	    		//GL11.glColor3d(0.8, 0.8, 0.8);
	        	light =  w.getBlockLightValue(x-1, y, z);
	        	//v5.setBrightness(245+15-light);
		        v5.addVertexWithUV(0, 1, 0, 0.0625F*col, 0.0625F*row);
		        v5.addVertexWithUV(0, 0, 0, 0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 0, 1, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(0, 1, 1, 0.0625F+0.0625F*col, 0.0625F*row);
	        break;
	        case 5:
	    		//GL11.glColor3d(0.6, 0.6, 0.6);
	        	light =  w.getBlockLightValue(x+1, y, z);
	        	//v5.setBrightness(245+15-light);
		        v5.addVertexWithUV(1, 0, 0, 0.0625F*col, 0.0625F*row);
		        v5.addVertexWithUV(1, 1, 0, 0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(1, 1, 1, 0.0625F+0.0625F*col, 0.0625F+0.0625F*row);
		        v5.addVertexWithUV(1, 0, 1, 0.0625F+0.0625F*col, 0.0625F*row);
	        break;
	        case 6:
	        break;
	        }
	        v5.draw();
		}
		GL11.glColor3d(1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(-a, -b, -c);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 7);
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
