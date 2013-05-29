/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Interfaces.SidedTextureIndex;

public final class ReikaBlockRenderer extends RenderBlocks {

	public static ReikaBlockRenderer instance = new ReikaBlockRenderer();

	private ReikaBlockRenderer() {}

	/** Renders a block entity (like TNT) Args: Entity, par2, par4, par6 */
	 public void renderBlockEntityAt(Entity e, double p2, double p4, double p6) {
		 Tessellator v5 = Tessellator.instance;
		 double x = 0.25;
		 double y = 0;
		 GL11.glTranslated(-0.5, -0.5, -0.5);
		 v5.startDrawingQuads();
		 v5.addVertexWithUV(0, 1, 1, x, y+0.5);
		 v5.addVertexWithUV(1, 1, 1, x+0.25, y+0.5);
		 v5.addVertexWithUV(1, 1, 0, x+0.25, y);
		 v5.addVertexWithUV(0, 1, 0, x, y);
		 v5.draw();
		 x = 0.5;
		 v5.startDrawingQuads();
		 v5.addVertexWithUV(0, 0, 0, x, y+0.5);
		 v5.addVertexWithUV(1, 0, 0, x+0.25, y+0.5);
		 v5.addVertexWithUV(1, 0, 1, x+0.25, y);
		 v5.addVertexWithUV(0, 0, 1, x, y);
		 v5.draw();
		 x = 0;
		 y = 0.5;
		 v5.startDrawingQuads();
		 v5.addVertexWithUV(1, 0, 0, x, y+0.5);
		 v5.addVertexWithUV(0, 0, 0, x+0.25, y+0.5);
		 v5.addVertexWithUV(0, 1, 0, x+0.25, y);
		 v5.addVertexWithUV(1, 1, 0, x, y);
		 v5.draw();
		 x = 0.25;
		 v5.startDrawingQuads();
		 v5.addVertexWithUV(0, 0, 1, x, y+0.5);
		 v5.addVertexWithUV(1, 0, 1, x+0.25, y+0.5);
		 v5.addVertexWithUV(1, 1, 1, x+0.25, y);
		 v5.addVertexWithUV(0, 1, 1, x, y);
		 v5.draw();
		 x = 0.5;
		 v5.startDrawingQuads();
		 v5.addVertexWithUV(1, 0, 1, x, y+0.5);
		 v5.addVertexWithUV(1, 0, 0, x+0.25, y+0.5);
		 v5.addVertexWithUV(1, 1, 0, x+0.25, y);
		 v5.addVertexWithUV(1, 1, 1, x, y);
		 v5.draw();
		 x = 0.75;
		 v5.startDrawingQuads();
		 v5.addVertexWithUV(0, 0, 0, x, y+0.5);
		 v5.addVertexWithUV(0, 0, 1, x+0.25, y+0.5);
		 v5.addVertexWithUV(0, 1, 1, x+0.25, y);
		 v5.addVertexWithUV(0, 1, 0, x, y);
		 v5.draw();

		 GL11.glTranslated(0.5, 0.5, 0.5);
	 }

	 public void renderBlockInInventory(Block par1Block, int par2, float par3, int[] indices) {
	        Tessellator v5 = Tessellator.instance;
	        boolean flag = par1Block.blockID == Block.grass.blockID;
	        if (par1Block == Block.dispenser || par1Block == Block.dropper || par1Block == Block.furnaceIdle)
	            par2 = 3;
	        int j;
	        float f1;
	        float f2;
	        float f3;
	        if (useInventoryTint) {
	            j = par1Block.getRenderColor(par2);
	            if (flag)
	                j = 16777215;
	            f1 = (j >> 16 & 255) / 255.0F;
	            f2 = (j >> 8 & 255) / 255.0F;
	            f3 = (j & 255) / 255.0F;
	            GL11.glColor4f(f1 * par3, f2 * par3, f3 * par3, 1.0F);
	        }
	        j = par1Block.getRenderType();
	        this.setRenderBoundsFromBlock(par1Block);
	        int k;
	            par1Block.setBlockBoundsForItemRender();
	            this.setRenderBoundsFromBlock(par1Block);
	            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
	            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	            GL11.glColor3d(1, 1, 1);
				if (v5.isDrawing)
					v5.draw();
	            v5.startDrawingQuads();
	            v5.setNormal(0.0F, -1.0F, 0.0F);
	            this.renderBottomFace(par1Block, 0.0D, 0.0D, 0.0D, indices[0], false);
	            v5.draw();
	            if (flag && useInventoryTint) {
	                k = par1Block.getRenderColor(par2);
	                f2 = (k >> 16 & 255) / 255.0F;
	                f3 = (k >> 8 & 255) / 255.0F;
	                float f7 = (k & 255) / 255.0F;
	                GL11.glColor4f(f2 * par3, f3 * par3, f7 * par3, 1.0F);
	            }
	            v5.startDrawingQuads();
	            v5.setNormal(0.0F, 1.0F, 0.0F);
	            this.renderTopFace(par1Block, 0.0D, 0.0D, 0.0D, indices[1], false);
	            v5.draw();
	            if (flag && useInventoryTint)
	                GL11.glColor4f(par3, par3, par3, 1.0F);
	            v5.startDrawingQuads();
	            v5.setNormal(0.0F, 0.0F, -1.0F);
	            this.renderEastFace(par1Block, 0.0D, 0.0D, 0.0D, indices[2], false);
	            v5.draw();
	            v5.startDrawingQuads();
	            v5.setNormal(0.0F, 0.0F, 1.0F);
	            this.renderWestFace(par1Block, 0.0D, 0.0D, 0.0D, indices[3], false);
	            v5.draw();
	            v5.startDrawingQuads();
	            v5.setNormal(-1.0F, 0.0F, 0.0F);
	            this.renderNorthFace(par1Block, 0.0D, 0.0D, 0.0D, indices[4], false);
	            v5.draw();
	            v5.startDrawingQuads();
	            v5.setNormal(1.0F, 0.0F, 0.0F);
	            this.renderSouthFace(par1Block, 0.0D, 0.0D, 0.0D, indices[5], false);
	            v5.draw();
	            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	    }

	    public void renderBottomFace(Block par1Block, double par2, double par4, double par6, int index, boolean inWorld)
	    {
	        Tessellator v5 = Tessellator.instance;
	        int row = index/16;
	        int col = index-16*row;

	        double d3 = col/16D;
	        double d4 = col/16D+0.0625;
	        double d5 = row/16D;
	        double d6 = row/16D+0.0625;

	        double d7 = d4;
	        double d8 = d3;
	        double d9 = d5;
	        double d10 = d6;

	        double d11 = par2 + renderMinX;
	        double d12 = par2 + renderMaxX;
	        double d13 = par4 + renderMinY;
	        double d14 = par6 + renderMinZ;
	        double d15 = par6 + renderMaxZ;

	        if (enableAO)
	        {
	            v5.setColorOpaque_F(colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft);
	            v5.setBrightness(brightnessTopLeft);
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	            v5.setColorOpaque_F(colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft);
	            v5.setBrightness(brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.setColorOpaque_F(colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight);
	            v5.setBrightness(brightnessBottomRight);
	            v5.addVertexWithUV(d12, d13, d14, d7, d9);
	            v5.setColorOpaque_F(colorRedTopRight, colorGreenTopRight, colorBlueTopRight);
	            v5.setBrightness(brightnessTopRight);
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	        }
	        else
	        {
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.addVertexWithUV(d12, d13, d14, d7, d9);
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	        }
	    }

	    public void renderTopFace(Block par1Block, double par2, double par4, double par6, int index, boolean inWorld)
	    {
	        Tessellator v5 = Tessellator.instance;
	        //ReikaJavaLibrary.pConsole(index);
	        int row = index/16;
	        int col = index-16*row;


	        double d3 = col/16D;
	        double d4 = col/16D+0.0625;
	        double d5 = row/16D;
	        double d6 = row/16D+0.0625;

	        double d7 = d4;
	        double d8 = d3;
	        double d9 = d5;
	        double d10 = d6;

	        double d11 = par2 + renderMinX;
	        double d12 = par2 + renderMaxX;
	        double d13 = par4 + renderMaxY;
	        double d14 = par6 + renderMinZ;
	        double d15 = par6 + renderMaxZ;

	        if (enableAO)
	        {
	            v5.setColorOpaque_F(colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft);
	            v5.setBrightness(brightnessTopLeft);
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	            v5.setColorOpaque_F(colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft);
	            v5.setBrightness(brightnessBottomLeft);
	            v5.addVertexWithUV(d12, d13, d14, d7, d9);
	            v5.setColorOpaque_F(colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight);
	            v5.setBrightness(brightnessBottomRight);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.setColorOpaque_F(colorRedTopRight, colorGreenTopRight, colorBlueTopRight);
	            v5.setBrightness(brightnessTopRight);
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	        }
	        else
	        {
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	            v5.addVertexWithUV(d12, d13, d14, d7, d9);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	        }
	    }

	    public void renderEastFace(Block par1Block, double par2, double par4, double par6, int index, boolean inWorld)
	    {
	        Tessellator v5 = Tessellator.instance;

	        int row = index/16;
	        int col = index-16*row;

	        double d3 = col/16D;
	        double d4 = col/16D+0.0625;
	        double d5 = row/16D;
	        double d6 = row/16D+0.0625;

	        double d7 = d4;
	        double d8 = d3;
	        double d9 = d5;
	        double d10 = d6;

	        double d11 = par2 + renderMinX;
	        double d12 = par2 + renderMaxX;
	        double d13 = par4 + renderMinY;
	        double d14 = par4 + renderMaxY;
	        double d15 = par6 + renderMinZ;

	        if (enableAO)
	        {
	            v5.setColorOpaque_F(colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft);
	            v5.setBrightness(brightnessTopLeft);
	            v5.addVertexWithUV(d11, d14, d15, d7, d9);
	            v5.setColorOpaque_F(colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft);
	            v5.setBrightness(brightnessBottomLeft);
	            v5.addVertexWithUV(d12, d14, d15, d3, d5);
	            v5.setColorOpaque_F(colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight);
	            v5.setBrightness(brightnessBottomRight);
	            v5.addVertexWithUV(d12, d13, d15, d8, d10);
	            v5.setColorOpaque_F(colorRedTopRight, colorGreenTopRight, colorBlueTopRight);
	            v5.setBrightness(brightnessTopRight);
	            v5.addVertexWithUV(d11, d13, d15, d4, d6);
	        }
	        else
	        {
	            v5.addVertexWithUV(d11, d14, d15, d7, d9);
	            v5.addVertexWithUV(d12, d14, d15, d3, d5);
	            v5.addVertexWithUV(d12, d13, d15, d8, d10);
	            v5.addVertexWithUV(d11, d13, d15, d4, d6);
	        }
	    }

	    public void renderWestFace(Block par1Block, double par2, double par4, double par6, int index, boolean inWorld)
	    {
	        Tessellator v5 = Tessellator.instance;

	        int row = index/16;
	        int col = index-16*row;

	        double d3 = col/16D;
	        double d4 = col/16D+0.0625;
	        double d5 = row/16D;
	        double d6 = row/16D+0.0625;

	        double d7 = d4;
	        double d8 = d3;
	        double d9 = d5;
	        double d10 = d6;

	        double d11 = par2 + renderMinX;
	        double d12 = par2 + renderMaxX;
	        double d13 = par4 + renderMinY;
	        double d14 = par4 + renderMaxY;
	        double d15 = par6 + renderMaxZ;

	        if (enableAO)
	        {
	            v5.setColorOpaque_F(colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft);
	            v5.setBrightness(brightnessTopLeft);
	            v5.addVertexWithUV(d11, d14, d15, d3, d5);
	            v5.setColorOpaque_F(colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft);
	            v5.setBrightness(brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	            v5.setColorOpaque_F(colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight);
	            v5.setBrightness(brightnessBottomRight);
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	            v5.setColorOpaque_F(colorRedTopRight, colorGreenTopRight, colorBlueTopRight);
	            v5.setBrightness(brightnessTopRight);
	            v5.addVertexWithUV(d12, d14, d15, d7, d9);
	        }
	        else
	        {
	            v5.addVertexWithUV(d11, d14, d15, d3, d5);
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	            v5.addVertexWithUV(d12, d14, d15, d7, d9);
	        }
	    }

	    public void renderNorthFace(Block par1Block, double par2, double par4, double par6, int index, boolean inWorld)
	    {
	        Tessellator v5 = Tessellator.instance;

	        int row = index/16;
	        int col = index-16*row;

	        double d3 = col/16D;
	        double d4 = col/16D+0.0625;
	        double d5 = row/16D;
	        double d6 = row/16D+0.0625;

	        double d7 = d4;
	        double d8 = d3;
	        double d9 = d5;
	        double d10 = d6;

	        double d11 = par2 + renderMinX;
	        double d12 = par4 + renderMinY;
	        double d13 = par4 + renderMaxY;
	        double d14 = par6 + renderMinZ;
	        double d15 = par6 + renderMaxZ;

	        if (enableAO)
	        {
	            v5.setColorOpaque_F(colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft);
	            v5.setBrightness(brightnessTopLeft);
	            v5.addVertexWithUV(d11, d13, d15, d7, d9);
	            v5.setColorOpaque_F(colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft);
	            v5.setBrightness(brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.setColorOpaque_F(colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight);
	            v5.setBrightness(brightnessBottomRight);
	            v5.addVertexWithUV(d11, d12, d14, d8, d10);
	            v5.setColorOpaque_F(colorRedTopRight, colorGreenTopRight, colorBlueTopRight);
	            v5.setBrightness(brightnessTopRight);
	            v5.addVertexWithUV(d11, d12, d15, d4, d6);
	        }
	        else
	        {
	            v5.addVertexWithUV(d11, d13, d15, d7, d9);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.addVertexWithUV(d11, d12, d14, d8, d10);
	            v5.addVertexWithUV(d11, d12, d15, d4, d6);
	        }
	    }

	    public void renderSouthFace(Block par1Block, double par2, double par4, double par6, int index, boolean inWorld)
	    {
	        Tessellator v5 = Tessellator.instance;

	        int row = index/16;
	        int col = index-16*row;

	        double d3 = col/16D;
	        double d4 = col/16D+0.0625;
	        double d5 = row/16D;
	        double d6 = row/16D+0.0625;

	        double d7 = d4;
	        double d8 = d3;
	        double d9 = d5;
	        double d10 = d6;

	        double d11 = par2 + renderMaxX;
	        double d12 = par4 + renderMinY;
	        double d13 = par4 + renderMaxY;
	        double d14 = par6 + renderMinZ;
	        double d15 = par6 + renderMaxZ;

	        if (enableAO)
	        {
	            v5.setColorOpaque_F(colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft);
	            v5.setBrightness(brightnessTopLeft);
	            v5.addVertexWithUV(d11, d12, d15, d8, d10);
	            v5.setColorOpaque_F(colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft);
	            v5.setBrightness(brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d12, d14, d4, d6);
	            v5.setColorOpaque_F(colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight);
	            v5.setBrightness(brightnessBottomRight);
	            v5.addVertexWithUV(d11, d13, d14, d7, d9);
	            v5.setColorOpaque_F(colorRedTopRight, colorGreenTopRight, colorBlueTopRight);
	            v5.setBrightness(brightnessTopRight);
	            v5.addVertexWithUV(d11, d13, d15, d3, d5);
	        }
	        else
	        {
	            v5.addVertexWithUV(d11, d12, d15, d8, d10);
	            v5.addVertexWithUV(d11, d12, d14, d4, d6);
	            v5.addVertexWithUV(d11, d13, d14, d7, d9);
	            v5.addVertexWithUV(d11, d13, d15, d3, d5);
	        }
	    }

	    public boolean renderCube(Block par1Block, int par2, int par3, int par4, float par5, float par6, float par7, int meta, IBlockAccess world, int tex)
	    {
	    	GL11.glDisable(GL11.GL_LIGHTING);
	    	GL11.glColor3d(1, 1, 1);
	    	SidedTextureIndex s = (SidedTextureIndex)par1Block;
			int[] indices = new int[6];
			for (int i = 0; i < 6; i++)
				indices[i] = s.getBlockTextureFromSideAndMetadata(i, meta);
	        enableAO = true;
	        boolean flag = false;
	        float f3 = 0.0F;
	        float f4 = 0.0F;
	        float f5 = 0.0F;
	        float f6 = 0.0F;
	        boolean flag1 = true;
	        int l = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4);
	        Tessellator v5 = Tessellator.instance;
			if (v5.isDrawing)
				v5.draw();
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
	        //ModLoader.getMinecraftInstance().renderEngine.bindTexture("/Reika/RotaryCraft/Textures/Terrain/textures.png");
	        v5.startDrawingQuads();
	        v5.setBrightness(983055);
	        boolean flag2;
	        boolean flag3;
	        boolean flag4;
	        boolean flag5;
	        float f7;
	        int i1;
	        if (renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3 - 1, par4, 0)) {
	            if (renderMinY <= 0.0D)
	                --par3;
	            aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 - 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 - 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 - 1)];
	            if (!flag4 && !flag2) {
	                aoLightValueScratchXYZNNN = aoLightValueScratchXYNN;
	                aoBrightnessXYZNNN = aoBrightnessXYNN;
	            }
	            else {
	                aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 - 1);
	                aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 - 1);
	            }
	            if (!flag5 && !flag2) {
	                aoLightValueScratchXYZNNP = aoLightValueScratchXYNN;
	                aoBrightnessXYZNNP = aoBrightnessXYNN;
	            }
	            else {
	                aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 + 1);
	                aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 + 1);
	            }
	            if (!flag4 && !flag3) {
	                aoLightValueScratchXYZPNN = aoLightValueScratchXYPN;
	                aoBrightnessXYZPNN = aoBrightnessXYPN;
	            }
	            else
	            {
	                aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 - 1);
	                aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 - 1);
	            }
	            if (!flag5 && !flag3) {
	                aoLightValueScratchXYZPNP = aoLightValueScratchXYPN;
	                aoBrightnessXYZPNP = aoBrightnessXYPN;
	            }
	            else {
	                aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 + 1);
	                aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 + 1);
	            }
	            if (renderMinY <= 0.0D) {
	                ++par3;
	            }
	            i1 = l;
	            if (renderMinY <= 0.0D || !world.isBlockOpaqueCube(par2, par3 - 1, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            f3 = (aoLightValueScratchXYZNNP + aoLightValueScratchXYNN + aoLightValueScratchYZNP + f7) / 4.0F;
	            f6 = (aoLightValueScratchYZNP + f7 + aoLightValueScratchXYZPNP + aoLightValueScratchXYPN) / 4.0F;
	            f5 = (f7 + aoLightValueScratchYZNN + aoLightValueScratchXYPN + aoLightValueScratchXYZPNN) / 4.0F;
	            f4 = (aoLightValueScratchXYNN + aoLightValueScratchXYZNNN + f7 + aoLightValueScratchYZNN) / 4.0F;
	            brightnessTopLeft = this.getAoBrightness(aoBrightnessXYZNNP, aoBrightnessXYNN, aoBrightnessYZNP, i1);
	            brightnessTopRight = this.getAoBrightness(aoBrightnessYZNP, aoBrightnessXYZPNP, aoBrightnessXYPN, i1);
	            brightnessBottomRight = this.getAoBrightness(aoBrightnessYZNN, aoBrightnessXYPN, aoBrightnessXYZPNN, i1);
	            brightnessBottomLeft = this.getAoBrightness(aoBrightnessXYNN, aoBrightnessXYZNNN, aoBrightnessYZNN, i1);
	            if (flag1) {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = par5 * 0.5F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = par6 * 0.5F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = par7 * 0.5F;
	            }
	            else {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = 0.5F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = 0.5F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = 0.5F;
	            }
	            colorRedTopLeft *= f3;
	            colorGreenTopLeft *= f3;
	            colorBlueTopLeft *= f3;
	            colorRedBottomLeft *= f4;
	            colorGreenBottomLeft *= f4;
	            colorBlueBottomLeft *= f4;
	            colorRedBottomRight *= f5;
	            colorGreenBottomRight *= f5;
	            colorBlueBottomRight *= f5;
	            colorRedTopRight *= f6;
	            colorGreenTopRight *= f6;
	            colorBlueTopRight *= f6;
	            this.renderBottomFace(par1Block, par2, par3, par4, indices[0], true);
	            flag = true;
	        }
	        if (renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3 + 1, par4, 1)) {
	            if (renderMaxY >= 1.0D)
	                ++par3;
	            aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 + 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 + 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 - 1)];
	            if (!flag4 && !flag2) {
	                aoLightValueScratchXYZNPN = aoLightValueScratchXYNP;
	                aoBrightnessXYZNPN = aoBrightnessXYNP;
	            }
	            else {
	                aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 - 1);
	                aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 - 1);
	            }
	            if (!flag4 && !flag3) {
	                aoLightValueScratchXYZPPN = aoLightValueScratchXYPP;
	                aoBrightnessXYZPPN = aoBrightnessXYPP;
	            }
	            else {
	                aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 - 1);
	                aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 - 1);
	            }
	            if (!flag5 && !flag2) {
	                aoLightValueScratchXYZNPP = aoLightValueScratchXYNP;
	                aoBrightnessXYZNPP = aoBrightnessXYNP;
	            }
	            else {
	                aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 + 1);
	                aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 + 1);
	            }
	            if (!flag5 && !flag3) {
	                aoLightValueScratchXYZPPP = aoLightValueScratchXYPP;
	                aoBrightnessXYZPPP = aoBrightnessXYPP;
	            }
	            else {
	                aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 + 1);
	                aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 + 1);
	            }
	            if (renderMaxY >= 1.0D)
	                --par3;
	            i1 = l;
	            if (renderMaxY >= 1.0D || !world.isBlockOpaqueCube(par2, par3 + 1, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            f6 = (aoLightValueScratchXYZNPP + aoLightValueScratchXYNP + aoLightValueScratchYZPP + f7) / 4.0F;
	            f3 = (aoLightValueScratchYZPP + f7 + aoLightValueScratchXYZPPP + aoLightValueScratchXYPP) / 4.0F;
	            f4 = (f7 + aoLightValueScratchYZPN + aoLightValueScratchXYPP + aoLightValueScratchXYZPPN) / 4.0F;
	            f5 = (aoLightValueScratchXYNP + aoLightValueScratchXYZNPN + f7 + aoLightValueScratchYZPN) / 4.0F;
	            brightnessTopRight = this.getAoBrightness(aoBrightnessXYZNPP, aoBrightnessXYNP, aoBrightnessYZPP, i1);
	            brightnessTopLeft = this.getAoBrightness(aoBrightnessYZPP, aoBrightnessXYZPPP, aoBrightnessXYPP, i1);
	            brightnessBottomLeft = this.getAoBrightness(aoBrightnessYZPN, aoBrightnessXYPP, aoBrightnessXYZPPN, i1);
	            brightnessBottomRight = this.getAoBrightness(aoBrightnessXYNP, aoBrightnessXYZNPN, aoBrightnessYZPN, i1);
	            colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = par5;
	            colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = par6;
	            colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = par7;
	            colorRedTopLeft *= f3;
	            colorGreenTopLeft *= f3;
	            colorBlueTopLeft *= f3;
	            colorRedBottomLeft *= f4;
	            colorGreenBottomLeft *= f4;
	            colorBlueBottomLeft *= f4;
	            colorRedBottomRight *= f5;
	            colorGreenBottomRight *= f5;
	            colorBlueBottomRight *= f5;
	            colorRedTopRight *= f6;
	            colorGreenTopRight *= f6;
	            colorBlueTopRight *= f6;
	            this.renderTopFace(par1Block, par2, par3, par4, indices[1], true);
	            flag = true;
	        }
	        if (renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3, par4 - 1, 2)) {
	            if (renderMinZ <= 0.0D)
	                --par4;
	            aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 - 1)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 - 1)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 - 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 - 1)];

	            if (!flag2 && !flag4)  {
	                aoLightValueScratchXYZNNN = aoLightValueScratchXZNN;
	                aoBrightnessXYZNNN = aoBrightnessXZNN;
	            }
	            else {
	                aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 - 1, par4);
	                aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 - 1, par4);
	            }
	            if (!flag2 && !flag5) {
	                aoLightValueScratchXYZNPN = aoLightValueScratchXZNN;
	                aoBrightnessXYZNPN = aoBrightnessXZNN;
	            }
	            else
	            {
	                aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 + 1, par4);
	                aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 + 1, par4);
	            }
	            if (!flag3 && !flag4) {
	                aoLightValueScratchXYZPNN = aoLightValueScratchXZPN;
	                aoBrightnessXYZPNN = aoBrightnessXZPN;
	            }
	            else {
	                aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 - 1, par4);
	                aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 - 1, par4);
	            }
	            if (!flag3 && !flag5) {
	                aoLightValueScratchXYZPPN = aoLightValueScratchXZPN;
	                aoBrightnessXYZPPN = aoBrightnessXZPN;
	            }
	            else {
	                aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 + 1, par4);
	                aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 + 1, par4);
	            }
	            if (renderMinZ <= 0.0D)
	                ++par4;
	            i1 = l;
	            if (renderMinZ <= 0.0D || !world.isBlockOpaqueCube(par2, par3, par4 - 1))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            f3 = (aoLightValueScratchXZNN + aoLightValueScratchXYZNPN + f7 + aoLightValueScratchYZPN) / 4.0F;
	            f4 = (f7 + aoLightValueScratchYZPN + aoLightValueScratchXZPN + aoLightValueScratchXYZPPN) / 4.0F;
	            f5 = (aoLightValueScratchYZNN + f7 + aoLightValueScratchXYZPNN + aoLightValueScratchXZPN) / 4.0F;
	            f6 = (aoLightValueScratchXYZNNN + aoLightValueScratchXZNN + aoLightValueScratchYZNN + f7) / 4.0F;
	            brightnessTopLeft = this.getAoBrightness(aoBrightnessXZNN, aoBrightnessXYZNPN, aoBrightnessYZPN, i1);
	            brightnessBottomLeft = this.getAoBrightness(aoBrightnessYZPN, aoBrightnessXZPN, aoBrightnessXYZPPN, i1);
	            brightnessBottomRight = this.getAoBrightness(aoBrightnessYZNN, aoBrightnessXYZPNN, aoBrightnessXZPN, i1);
	            brightnessTopRight = this.getAoBrightness(aoBrightnessXYZNNN, aoBrightnessXZNN, aoBrightnessYZNN, i1);
	            if (flag1) {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = par5 * 0.8F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = par6 * 0.8F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = par7 * 0.8F;
	            }
	            else {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = 0.8F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = 0.8F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = 0.8F;
	            }
	            colorRedTopLeft *= f3;
	            colorGreenTopLeft *= f3;
	            colorBlueTopLeft *= f3;
	            colorRedBottomLeft *= f4;
	            colorGreenBottomLeft *= f4;
	            colorBlueBottomLeft *= f4;
	            colorRedBottomRight *= f5;
	            colorGreenBottomRight *= f5;
	            colorBlueBottomRight *= f5;
	            colorRedTopRight *= f6;
	            colorGreenTopRight *= f6;
	            colorBlueTopRight *= f6;
	            this.renderEastFace(par1Block, par2, par3, par4, indices[2], true);
	            flag = true;
	        }
	        if (renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3, par4 + 1, 3)) {
	            if (renderMaxZ >= 1.0D)
	                ++par4;
	            aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 + 1)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 + 1)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 + 1)];
	            if (!flag2 && !flag4) {
	                aoLightValueScratchXYZNNP = aoLightValueScratchXZNP;
	                aoBrightnessXYZNNP = aoBrightnessXZNP;
	            }
	            else {
	                aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 - 1, par4);
	                aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 - 1, par4);
	            }
	            if (!flag2 && !flag5) {
	                aoLightValueScratchXYZNPP = aoLightValueScratchXZNP;
	                aoBrightnessXYZNPP = aoBrightnessXZNP;
	            }
	            else {
	                aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 + 1, par4);
	                aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 + 1, par4);
	            }
	            if (!flag3 && !flag4) {
	                aoLightValueScratchXYZPNP = aoLightValueScratchXZPP;
	                aoBrightnessXYZPNP = aoBrightnessXZPP;
	            }
	            else {
	                aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 - 1, par4);
	                aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 - 1, par4);
	            }
	            if (!flag3 && !flag5) {
	                aoLightValueScratchXYZPPP = aoLightValueScratchXZPP;
	                aoBrightnessXYZPPP = aoBrightnessXZPP;
	            }
	            else {
	                aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 + 1, par4);
	                aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 + 1, par4);
	            }
	            if (renderMaxZ >= 1.0D)
	                --par4;
	            i1 = l;
	            if (renderMaxZ >= 1.0D || !world.isBlockOpaqueCube(par2, par3, par4 + 1))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            f3 = (aoLightValueScratchXZNP + aoLightValueScratchXYZNPP + f7 + aoLightValueScratchYZPP) / 4.0F;
	            f6 = (f7 + aoLightValueScratchYZPP + aoLightValueScratchXZPP + aoLightValueScratchXYZPPP) / 4.0F;
	            f5 = (aoLightValueScratchYZNP + f7 + aoLightValueScratchXYZPNP + aoLightValueScratchXZPP) / 4.0F;
	            f4 = (aoLightValueScratchXYZNNP + aoLightValueScratchXZNP + aoLightValueScratchYZNP + f7) / 4.0F;
	            brightnessTopLeft = this.getAoBrightness(aoBrightnessXZNP, aoBrightnessXYZNPP, aoBrightnessYZPP, i1);
	            brightnessTopRight = this.getAoBrightness(aoBrightnessYZPP, aoBrightnessXZPP, aoBrightnessXYZPPP, i1);
	            brightnessBottomRight = this.getAoBrightness(aoBrightnessYZNP, aoBrightnessXYZPNP, aoBrightnessXZPP, i1);
	            brightnessBottomLeft = this.getAoBrightness(aoBrightnessXYZNNP, aoBrightnessXZNP, aoBrightnessYZNP, i1);
	            if (flag1) {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = par5 * 0.8F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = par6 * 0.8F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = par7 * 0.8F;
	            }
	            else {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = 0.8F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = 0.8F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = 0.8F;
	            }
	            colorRedTopLeft *= f3;
	            colorGreenTopLeft *= f3;
	            colorBlueTopLeft *= f3;
	            colorRedBottomLeft *= f4;
	            colorGreenBottomLeft *= f4;
	            colorBlueBottomLeft *= f4;
	            colorRedBottomRight *= f5;
	            colorGreenBottomRight *= f5;
	            colorBlueBottomRight *= f5;
	            colorRedTopRight *= f6;
	            colorGreenTopRight *= f6;
	            colorBlueTopRight *= f6;
	            this.renderWestFace(par1Block, par2, par3, par4, indices[3], true);
	            flag = true;
	        }
	        if (renderAllFaces || par1Block.shouldSideBeRendered(world, par2 - 1, par3, par4, 4)) {
	            if (renderMinX <= 0.0D)
	                --par2;
	            aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 + 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 - 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 - 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 + 1)];
	            if (!flag5 && !flag2) {
	                aoLightValueScratchXYZNNN = aoLightValueScratchXZNN;
	                aoBrightnessXYZNNN = aoBrightnessXZNN;
	            }
	            else {
	                aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 - 1);
	                aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 - 1);
	            }
	            if (!flag4 && !flag2) {
	                aoLightValueScratchXYZNNP = aoLightValueScratchXZNP;
	                aoBrightnessXYZNNP = aoBrightnessXZNP;
	            }
	            else {
	                aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 + 1);
	                aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 + 1);
	            }
	            if (!flag5 && !flag3) {
	                aoLightValueScratchXYZNPN = aoLightValueScratchXZNN;
	                aoBrightnessXYZNPN = aoBrightnessXZNN;
	            }
	            else {
	                aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 - 1);
	                aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 - 1);
	            }
	            if (!flag4 && !flag3) {
	                aoLightValueScratchXYZNPP = aoLightValueScratchXZNP;
	                aoBrightnessXYZNPP = aoBrightnessXZNP;
	            }
	            else {
	                aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 + 1);
	                aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 + 1);
	            }
	            if (renderMinX <= 0.0D)
	                ++par2;
	            i1 = l;
	            if (renderMinX <= 0.0D || !world.isBlockOpaqueCube(par2 - 1, par3, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            f6 = (aoLightValueScratchXYNN + aoLightValueScratchXYZNNP + f7 + aoLightValueScratchXZNP) / 4.0F;
	            f3 = (f7 + aoLightValueScratchXZNP + aoLightValueScratchXYNP + aoLightValueScratchXYZNPP) / 4.0F;
	            f4 = (aoLightValueScratchXZNN + f7 + aoLightValueScratchXYZNPN + aoLightValueScratchXYNP) / 4.0F;
	            f5 = (aoLightValueScratchXYZNNN + aoLightValueScratchXYNN + aoLightValueScratchXZNN + f7) / 4.0F;
	            brightnessTopRight = this.getAoBrightness(aoBrightnessXYNN, aoBrightnessXYZNNP, aoBrightnessXZNP, i1);
	            brightnessTopLeft = this.getAoBrightness(aoBrightnessXZNP, aoBrightnessXYNP, aoBrightnessXYZNPP, i1);
	            brightnessBottomLeft = this.getAoBrightness(aoBrightnessXZNN, aoBrightnessXYZNPN, aoBrightnessXYNP, i1);
	            brightnessBottomRight = this.getAoBrightness(aoBrightnessXYZNNN, aoBrightnessXYNN, aoBrightnessXZNN, i1);
	            if (flag1) {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = par5 * 0.6F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = par6 * 0.6F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = par7 * 0.6F;
	            }
	            else {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = 0.6F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = 0.6F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = 0.6F;
	            }
	            colorRedTopLeft *= f3;
	            colorGreenTopLeft *= f3;
	            colorBlueTopLeft *= f3;
	            colorRedBottomLeft *= f4;
	            colorGreenBottomLeft *= f4;
	            colorBlueBottomLeft *= f4;
	            colorRedBottomRight *= f5;
	            colorGreenBottomRight *= f5;
	            colorBlueBottomRight *= f5;
	            colorRedTopRight *= f6;
	            colorGreenTopRight *= f6;
	            colorBlueTopRight *= f6;
	            this.renderNorthFace(par1Block, par2, par3, par4, indices[4], true);
	            flag = true;
	        }

	        if (renderAllFaces || par1Block.shouldSideBeRendered(world, par2 + 1, par3, par4, 5)) {
	            if (renderMaxX >= 1.0D)
	                ++par2;
	            aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 + 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 - 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 - 1)];
	            if (!flag2 && !flag4) {
	                aoLightValueScratchXYZPNN = aoLightValueScratchXZPN;
	                aoBrightnessXYZPNN = aoBrightnessXZPN;
	            }
	            else {
	                aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 - 1);
	                aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 - 1);
	            }
	            if (!flag2 && !flag5) {
	                aoLightValueScratchXYZPNP = aoLightValueScratchXZPP;
	                aoBrightnessXYZPNP = aoBrightnessXZPP;
	            }
	            else {
	                aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 + 1);
	                aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 + 1);
	            }

	            if (!flag3 && !flag4)
	            {
	                aoLightValueScratchXYZPPN = aoLightValueScratchXZPN;
	                aoBrightnessXYZPPN = aoBrightnessXZPN;
	            }
	            else {
	                aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 - 1);
	                aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 - 1);
	            }
	            if (!flag3 && !flag5) {
	                aoLightValueScratchXYZPPP = aoLightValueScratchXZPP;
	                aoBrightnessXYZPPP = aoBrightnessXZPP;
	            }
	            else {
	                aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 + 1);
	                aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 + 1);
	            }
	            if (renderMaxX >= 1.0D)
	                --par2;
	            i1 = l;
	            if (renderMaxX >= 1.0D || !world.isBlockOpaqueCube(par2 + 1, par3, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            f3 = (aoLightValueScratchXYPN + aoLightValueScratchXYZPNP + f7 + aoLightValueScratchXZPP) / 4.0F;
	            f4 = (aoLightValueScratchXYZPNN + aoLightValueScratchXYPN + aoLightValueScratchXZPN + f7) / 4.0F;
	            f5 = (aoLightValueScratchXZPN + f7 + aoLightValueScratchXYZPPN + aoLightValueScratchXYPP) / 4.0F;
	            f6 = (f7 + aoLightValueScratchXZPP + aoLightValueScratchXYPP + aoLightValueScratchXYZPPP) / 4.0F;
	            brightnessTopLeft = this.getAoBrightness(aoBrightnessXYPN, aoBrightnessXYZPNP, aoBrightnessXZPP, i1);
	            brightnessTopRight = this.getAoBrightness(aoBrightnessXZPP, aoBrightnessXYPP, aoBrightnessXYZPPP, i1);
	            brightnessBottomRight = this.getAoBrightness(aoBrightnessXZPN, aoBrightnessXYZPPN, aoBrightnessXYPP, i1);
	            brightnessBottomLeft = this.getAoBrightness(aoBrightnessXYZPNN, aoBrightnessXYPN, aoBrightnessXZPN, i1);
	            if (flag1) {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = par5 * 0.6F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = par6 * 0.6F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = par7 * 0.6F;
	            }
	            else {
	                colorRedTopLeft = colorRedBottomLeft = colorRedBottomRight = colorRedTopRight = 0.6F;
	                colorGreenTopLeft = colorGreenBottomLeft = colorGreenBottomRight = colorGreenTopRight = 0.6F;
	                colorBlueTopLeft = colorBlueBottomLeft = colorBlueBottomRight = colorBlueTopRight = 0.6F;
	            }
	            colorRedTopLeft *= f3;
	            colorGreenTopLeft *= f3;
	            colorBlueTopLeft *= f3;
	            colorRedBottomLeft *= f4;
	            colorGreenBottomLeft *= f4;
	            colorBlueBottomLeft *= f4;
	            colorRedBottomRight *= f5;
	            colorGreenBottomRight *= f5;
	            colorBlueBottomRight *= f5;
	            colorRedTopRight *= f6;
	            colorGreenTopRight *= f6;
	            colorBlueTopRight *= f6;
	            this.renderSouthFace(par1Block, par2, par3, par4, indices[5], true);
	            flag = true;
	        }
	        enableAO = false;
	        v5.draw();
	        v5.startDrawingQuads();
	        return flag;
	    }
}
