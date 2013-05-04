package Reika.DragonAPI;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

public final class ReikaBlockRenderer extends RenderBlocks {
	
	public static ReikaBlockRenderer instance = new ReikaBlockRenderer();
	
	private ReikaBlockRenderer() {}
	
	 public void renderBlockInInventory(Block par1Block, int par2, float par3, int[] indices) {
	        Tessellator v5 = Tessellator.instance;
	        boolean flag = par1Block.blockID == Block.grass.blockID;
	        if (par1Block == Block.dispenser || par1Block == Block.dropper || par1Block == Block.furnaceIdle)
	            par2 = 3;
	        int j;
	        float f1;
	        float f2;
	        float f3;
	        if (this.useInventoryTint) {
	            j = par1Block.getRenderColor(par2);
	            if (flag)
	                j = 16777215;
	            f1 = (float)(j >> 16 & 255) / 255.0F;
	            f2 = (float)(j >> 8 & 255) / 255.0F;
	            f3 = (float)(j & 255) / 255.0F;
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
	            if (flag && this.useInventoryTint) {
	                k = par1Block.getRenderColor(par2);
	                f2 = (float)(k >> 16 & 255) / 255.0F;
	                f3 = (float)(k >> 8 & 255) / 255.0F;
	                float f7 = (float)(k & 255) / 255.0F;
	                GL11.glColor4f(f2 * par3, f3 * par3, f7 * par3, 1.0F);
	            }
	            v5.startDrawingQuads();
	            v5.setNormal(0.0F, 1.0F, 0.0F);
	            this.renderTopFace(par1Block, 0.0D, 0.0D, 0.0D, indices[1], false);
	            v5.draw();
	            if (flag && this.useInventoryTint)
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
	
    /**
     * Renders a falling custom block
     *//*
    public void renderBlockCustomFalling(Block par1Block, World par2World, int par3, int par4, int par5, int par6, int[] color)
    {
        float var7 = 0.5F;
        float var8 = 1.0F;
        float var9 = 0.8F;
        float var10 = 0.6F;
        Tessellator var11 = new Tessellator();
        var11.startDrawingQuads();
        var11.setBrightness(par1Block.getMixedBrightnessForBlock(par2World, par3, par4, par5));
        float var12 = 1.0F;
        float var13 = 1.0F;

        if (var13 < var12)
        {
            var13 = var12;
        }

        var11.setColorOpaque_F(var7 * var13, var7 * var13, var7 * var13);
        this.renderBottomFace(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getBlockTextureFromSideAndMetadata(0, par6));
        var13 = 1.0F;

        if (var13 < var12)
        {
            var13 = var12;
        }

        var11.setColorOpaque_F(var8 * var13, var8 * var13, var8 * var13);
        this.renderTopFace(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getBlockTextureFromSideAndMetadata(1, par6));
        var13 = 1.0F;

        if (var13 < var12)
        {
            var13 = var12;
        }

        var11.setColorOpaque_F(var9 * var13, var9 * var13, var9 * var13);
        this.renderEastFace(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getBlockTextureFromSideAndMetadata(2, par6));
        var13 = 1.0F;

        if (var13 < var12)
        {
            var13 = var12;
        }

        var11.setColorOpaque_F(var9 * var13, var9 * var13, var9 * var13);
        this.renderWestFace(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getBlockTextureFromSideAndMetadata(3, par6));
        var13 = 1.0F;

        if (var13 < var12)
        {
            var13 = var12;
        }

        var11.setColorOpaque_F(var10 * var13, var10 * var13, var10 * var13);
        this.renderNorthFace(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getBlockTextureFromSideAndMetadata(4, par6));
        var13 = 1.0F;

        if (var13 < var12)
        {
            var13 = var12;
        }

        var11.setColorOpaque_F(var10 * var13, var10 * var13, var10 * var13);
        this.renderSouthFace(par1Block, -0.5D, -0.5D, -0.5D, par1Block.getBlockTextureFromSideAndMetadata(5, par6));
        var11.draw();
    }*/
	 
	 
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

	        double d11 = par2 + this.renderMinX;
	        double d12 = par2 + this.renderMaxX;
	        double d13 = par4 + this.renderMinY;
	        double d14 = par6 + this.renderMinZ;
	        double d15 = par6 + this.renderMaxZ;

	        if (this.enableAO)
	        {
	            v5.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
	            v5.setBrightness(this.brightnessTopLeft);
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	            v5.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
	            v5.setBrightness(this.brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
	            v5.setBrightness(this.brightnessBottomRight);
	            v5.addVertexWithUV(d12, d13, d14, d7, d9);
	            v5.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
	            v5.setBrightness(this.brightnessTopRight);
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

	        double d11 = par2 + this.renderMinX;
	        double d12 = par2 + this.renderMaxX;
	        double d13 = par4 + this.renderMaxY;
	        double d14 = par6 + this.renderMinZ;
	        double d15 = par6 + this.renderMaxZ;

	        if (this.enableAO)
	        {
	            v5.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
	            v5.setBrightness(this.brightnessTopLeft);
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	            v5.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
	            v5.setBrightness(this.brightnessBottomLeft);
	            v5.addVertexWithUV(d12, d13, d14, d7, d9);
	            v5.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
	            v5.setBrightness(this.brightnessBottomRight);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
	            v5.setBrightness(this.brightnessTopRight);
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

	        double d11 = par2 + this.renderMinX;
	        double d12 = par2 + this.renderMaxX;
	        double d13 = par4 + this.renderMinY;
	        double d14 = par4 + this.renderMaxY;
	        double d15 = par6 + this.renderMinZ;

	        if (this.enableAO)
	        {
	            v5.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
	            v5.setBrightness(this.brightnessTopLeft);
	            v5.addVertexWithUV(d11, d14, d15, d7, d9);
	            v5.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
	            v5.setBrightness(this.brightnessBottomLeft);
	            v5.addVertexWithUV(d12, d14, d15, d3, d5);
	            v5.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
	            v5.setBrightness(this.brightnessBottomRight);
	            v5.addVertexWithUV(d12, d13, d15, d8, d10);
	            v5.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
	            v5.setBrightness(this.brightnessTopRight);
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

	        double d11 = par2 + this.renderMinX;
	        double d12 = par2 + this.renderMaxX;
	        double d13 = par4 + this.renderMinY;
	        double d14 = par4 + this.renderMaxY;
	        double d15 = par6 + this.renderMaxZ;

	        if (this.enableAO)
	        {
	            v5.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
	            v5.setBrightness(this.brightnessTopLeft);
	            v5.addVertexWithUV(d11, d14, d15, d3, d5);
	            v5.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
	            v5.setBrightness(this.brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d13, d15, d8, d10);
	            v5.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
	            v5.setBrightness(this.brightnessBottomRight);
	            v5.addVertexWithUV(d12, d13, d15, d4, d6);
	            v5.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
	            v5.setBrightness(this.brightnessTopRight);
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

	        double d11 = par2 + this.renderMinX;
	        double d12 = par4 + this.renderMinY;
	        double d13 = par4 + this.renderMaxY;
	        double d14 = par6 + this.renderMinZ;
	        double d15 = par6 + this.renderMaxZ;

	        if (this.enableAO)
	        {
	            v5.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
	            v5.setBrightness(this.brightnessTopLeft);
	            v5.addVertexWithUV(d11, d13, d15, d7, d9);
	            v5.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
	            v5.setBrightness(this.brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d13, d14, d3, d5);
	            v5.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
	            v5.setBrightness(this.brightnessBottomRight);
	            v5.addVertexWithUV(d11, d12, d14, d8, d10);
	            v5.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
	            v5.setBrightness(this.brightnessTopRight);
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

	        double d11 = par2 + this.renderMaxX;
	        double d12 = par4 + this.renderMinY;
	        double d13 = par4 + this.renderMaxY;
	        double d14 = par6 + this.renderMinZ;
	        double d15 = par6 + this.renderMaxZ;
	        
	        if (this.enableAO)
	        {
	            v5.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
	            v5.setBrightness(this.brightnessTopLeft);
	            v5.addVertexWithUV(d11, d12, d15, d8, d10);
	            v5.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
	            v5.setBrightness(this.brightnessBottomLeft);
	            v5.addVertexWithUV(d11, d12, d14, d4, d6);
	            v5.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
	            v5.setBrightness(this.brightnessBottomRight);
	            v5.addVertexWithUV(d11, d13, d14, d7, d9);
	            v5.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
	            v5.setBrightness(this.brightnessTopRight);
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
	        this.enableAO = true;
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
	        if (this.renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3 - 1, par4, 0)) {
	            if (this.renderMinY <= 0.0D)
	                --par3;
	            this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 - 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 - 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 - 1)];
	            if (!flag4 && !flag2) {
	                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
	                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
	            }
	            else {
	                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 - 1);
	                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 - 1);
	            }
	            if (!flag5 && !flag2) {
	                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
	                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
	            }
	            else {
	                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 + 1);
	                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 + 1);
	            }
	            if (!flag4 && !flag3) {
	                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
	                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
	            }
	            else
	            {
	                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 - 1);
	                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 - 1);
	            }
	            if (!flag5 && !flag3) {
	                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
	                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
	            }
	            else {
	                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 + 1);
	                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 + 1);
	            }
	            if (this.renderMinY <= 0.0D) {
	                ++par3;
	            }
	            i1 = l;
	            if (this.renderMinY <= 0.0D || !world.isBlockOpaqueCube(par2, par3 - 1, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7) / 4.0F;
	            f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
	            f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
	            f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN) / 4.0F;
	            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i1);
	            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i1);
	            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i1);
	            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i1);
	            if (flag1) {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.5F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.5F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.5F;
	            }
	            else {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
	            }
	            this.colorRedTopLeft *= f3;
	            this.colorGreenTopLeft *= f3;
	            this.colorBlueTopLeft *= f3;
	            this.colorRedBottomLeft *= f4;
	            this.colorGreenBottomLeft *= f4;
	            this.colorBlueBottomLeft *= f4;
	            this.colorRedBottomRight *= f5;
	            this.colorGreenBottomRight *= f5;
	            this.colorBlueBottomRight *= f5;
	            this.colorRedTopRight *= f6;
	            this.colorGreenTopRight *= f6;
	            this.colorBlueTopRight *= f6;
	            this.renderBottomFace(par1Block, (double)par2, (double)par3, (double)par4, indices[0], true);
	            flag = true;
	        }
	        if (this.renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3 + 1, par4, 1)) {
	            if (this.renderMaxY >= 1.0D)
	                ++par3;
	            this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 + 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 + 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 - 1)];
	            if (!flag4 && !flag2) {
	                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
	                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
	            }
	            else {
	                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 - 1);
	                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 - 1);
	            }
	            if (!flag4 && !flag3) {
	                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
	                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
	            }
	            else {
	                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 - 1);
	                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 - 1);
	            }
	            if (!flag5 && !flag2) {
	                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
	                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
	            }
	            else {
	                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4 + 1);
	                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4 + 1);
	            }
	            if (!flag5 && !flag3) {
	                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
	                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
	            }
	            else {
	                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4 + 1);
	                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4 + 1);
	            }
	            if (this.renderMaxY >= 1.0D)
	                --par3;
	            i1 = l;
	            if (this.renderMaxY >= 1.0D || !world.isBlockOpaqueCube(par2, par3 + 1, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7) / 4.0F;
	            f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
	            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
	            f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
	            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i1);
	            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i1);
	            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i1);
	            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
	            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5;
	            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6;
	            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7;
	            this.colorRedTopLeft *= f3;
	            this.colorGreenTopLeft *= f3;
	            this.colorBlueTopLeft *= f3;
	            this.colorRedBottomLeft *= f4;
	            this.colorGreenBottomLeft *= f4;
	            this.colorBlueBottomLeft *= f4;
	            this.colorRedBottomRight *= f5;
	            this.colorGreenBottomRight *= f5;
	            this.colorBlueBottomRight *= f5;
	            this.colorRedTopRight *= f6;
	            this.colorGreenTopRight *= f6;
	            this.colorBlueTopRight *= f6;
	            this.renderTopFace(par1Block, (double)par2, (double)par3, (double)par4, indices[1], true);
	            flag = true;
	        }
	        if (this.renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3, par4 - 1, 2)) {
	            if (this.renderMinZ <= 0.0D)
	                --par4;
	            this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            this.aoLightValueScratchYZNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            this.aoLightValueScratchYZPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            this.aoBrightnessYZNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            this.aoBrightnessYZPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 - 1)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 - 1)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 - 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 - 1)];

	            if (!flag2 && !flag4)  {
	                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
	                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
	            }
	            else {
	                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 - 1, par4);
	                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 - 1, par4);
	            }
	            if (!flag2 && !flag5) {
	                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
	                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
	            }
	            else
	            {
	                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 + 1, par4);
	                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 + 1, par4);
	            }
	            if (!flag3 && !flag4) {
	                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
	                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
	            }
	            else {
	                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 - 1, par4);
	                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 - 1, par4);
	            }
	            if (!flag3 && !flag5) {
	                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
	                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
	            }
	            else {
	                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 + 1, par4);
	                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 + 1, par4);
	            }
	            if (this.renderMinZ <= 0.0D)
	                ++par4;
	            i1 = l;
	            if (this.renderMinZ <= 0.0D || !world.isBlockOpaqueCube(par2, par3, par4 - 1))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            f3 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
	            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
	            f5 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
	            f6 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7) / 4.0F;
	            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
	            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i1);
	            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i1);
	            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i1);
	            if (flag1) {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
	            }
	            else {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
	            }
	            this.colorRedTopLeft *= f3;
	            this.colorGreenTopLeft *= f3;
	            this.colorBlueTopLeft *= f3;
	            this.colorRedBottomLeft *= f4;
	            this.colorGreenBottomLeft *= f4;
	            this.colorBlueBottomLeft *= f4;
	            this.colorRedBottomRight *= f5;
	            this.colorGreenBottomRight *= f5;
	            this.colorBlueBottomRight *= f5;
	            this.colorRedTopRight *= f6;
	            this.colorGreenTopRight *= f6;
	            this.colorBlueTopRight *= f6;
	            this.renderEastFace(par1Block, (double)par2, (double)par3, (double)par4, indices[2], true);
	            flag = true;
	        }
	        if (this.renderAllFaces || par1Block.shouldSideBeRendered(world, par2, par3, par4 + 1, 3)) {
	            if (this.renderMaxZ >= 1.0D)
	                ++par4;
	            this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            this.aoLightValueScratchYZNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            this.aoLightValueScratchYZPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            this.aoBrightnessYZNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            this.aoBrightnessYZPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 + 1)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 + 1)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2, par3 + 1, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2, par3 - 1, par4 + 1)];
	            if (!flag2 && !flag4) {
	                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
	                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
	            }
	            else {
	                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 - 1, par4);
	                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 - 1, par4);
	            }
	            if (!flag2 && !flag5) {
	                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
	                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
	            }
	            else {
	                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3 + 1, par4);
	                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3 + 1, par4);
	            }
	            if (!flag3 && !flag4) {
	                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
	                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
	            }
	            else {
	                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 - 1, par4);
	                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 - 1, par4);
	            }
	            if (!flag3 && !flag5) {
	                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
	                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
	            }
	            else {
	                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3 + 1, par4);
	                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3 + 1, par4);
	            }
	            if (this.renderMaxZ >= 1.0D)
	                --par4;
	            i1 = l;
	            if (this.renderMaxZ >= 1.0D || !world.isBlockOpaqueCube(par2, par3, par4 + 1))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            f3 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP) / 4.0F;
	            f6 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
	            f5 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
	            f4 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7) / 4.0F;
	            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i1);
	            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i1);
	            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
	            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i1);
	            if (flag1) {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.8F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.8F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.8F;
	            }
	            else {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
	            }
	            this.colorRedTopLeft *= f3;
	            this.colorGreenTopLeft *= f3;
	            this.colorBlueTopLeft *= f3;
	            this.colorRedBottomLeft *= f4;
	            this.colorGreenBottomLeft *= f4;
	            this.colorBlueBottomLeft *= f4;
	            this.colorRedBottomRight *= f5;
	            this.colorGreenBottomRight *= f5;
	            this.colorBlueBottomRight *= f5;
	            this.colorRedTopRight *= f6;
	            this.colorGreenTopRight *= f6;
	            this.colorBlueTopRight *= f6;
	            this.renderWestFace(par1Block, (double)par2, (double)par3, (double)par4, indices[3], true);
	            flag = true;
	        }
	        if (this.renderAllFaces || par1Block.shouldSideBeRendered(world, par2 - 1, par3, par4, 4)) {
	            if (this.renderMinX <= 0.0D)
	                --par2;
	            this.aoLightValueScratchXYNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            this.aoLightValueScratchXZNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            this.aoLightValueScratchXZNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            this.aoLightValueScratchXYNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            this.aoBrightnessXYNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            this.aoBrightnessXZNN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            this.aoBrightnessXZNP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            this.aoBrightnessXYNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 + 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3 - 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 - 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2 - 1, par3, par4 + 1)];
	            if (!flag5 && !flag2) {
	                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
	                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
	            }
	            else {
	                this.aoLightValueScratchXYZNNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 - 1);
	                this.aoBrightnessXYZNNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 - 1);
	            }
	            if (!flag4 && !flag2) {
	                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
	                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
	            }
	            else {
	                this.aoLightValueScratchXYZNNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 + 1);
	                this.aoBrightnessXYZNNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 + 1);
	            }
	            if (!flag5 && !flag3) {
	                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
	                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
	            }
	            else {
	                this.aoLightValueScratchXYZNPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 - 1);
	                this.aoBrightnessXYZNPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 - 1);
	            }
	            if (!flag4 && !flag3) {
	                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
	                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
	            }
	            else {
	                this.aoLightValueScratchXYZNPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 + 1);
	                this.aoBrightnessXYZNPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 + 1);
	            }
	            if (this.renderMinX <= 0.0D)
	                ++par2;
	            i1 = l;
	            if (this.renderMinX <= 0.0D || !world.isBlockOpaqueCube(par2 - 1, par3, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2 - 1, par3, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2 - 1, par3, par4);
	            f6 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP) / 4.0F;
	            f3 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
	            f4 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
	            f5 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7) / 4.0F;
	            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i1);
	            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i1);
	            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i1);
	            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i1);
	            if (flag1) {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
	            }
	            else {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
	            }
	            this.colorRedTopLeft *= f3;
	            this.colorGreenTopLeft *= f3;
	            this.colorBlueTopLeft *= f3;
	            this.colorRedBottomLeft *= f4;
	            this.colorGreenBottomLeft *= f4;
	            this.colorBlueBottomLeft *= f4;
	            this.colorRedBottomRight *= f5;
	            this.colorGreenBottomRight *= f5;
	            this.colorBlueBottomRight *= f5;
	            this.colorRedTopRight *= f6;
	            this.colorGreenTopRight *= f6;
	            this.colorBlueTopRight *= f6;
	            this.renderNorthFace(par1Block, (double)par2, (double)par3, (double)par4, indices[4], true);
	            flag = true;
	        }

	        if (this.renderAllFaces || par1Block.shouldSideBeRendered(world, par2 + 1, par3, par4, 5)) {
	            if (this.renderMaxX >= 1.0D)
	                ++par2;
	            this.aoLightValueScratchXYPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4);
	            this.aoLightValueScratchXZPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 - 1);
	            this.aoLightValueScratchXZPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3, par4 + 1);
	            this.aoLightValueScratchXYPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4);
	            this.aoBrightnessXYPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4);
	            this.aoBrightnessXZPN = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 - 1);
	            this.aoBrightnessXZPP = par1Block.getMixedBrightnessForBlock(world, par2, par3, par4 + 1);
	            this.aoBrightnessXYPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4);
	            flag3 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 + 1, par4)];
	            flag2 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3 - 1, par4)];
	            flag5 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 + 1)];
	            flag4 = Block.canBlockGrass[world.getBlockId(par2 + 1, par3, par4 - 1)];
	            if (!flag2 && !flag4) {
	                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
	                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
	            }
	            else {
	                this.aoLightValueScratchXYZPNN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 - 1);
	                this.aoBrightnessXYZPNN = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 - 1);
	            }
	            if (!flag2 && !flag5) {
	                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
	                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
	            }
	            else {
	                this.aoLightValueScratchXYZPNP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 - 1, par4 + 1);
	                this.aoBrightnessXYZPNP = par1Block.getMixedBrightnessForBlock(world, par2, par3 - 1, par4 + 1);
	            }

	            if (!flag3 && !flag4)
	            {
	                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
	                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
	            }
	            else {
	                this.aoLightValueScratchXYZPPN = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 - 1);
	                this.aoBrightnessXYZPPN = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 - 1);
	            }
	            if (!flag3 && !flag5) {
	                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
	                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
	            }
	            else {
	                this.aoLightValueScratchXYZPPP = par1Block.getAmbientOcclusionLightValue(world, par2, par3 + 1, par4 + 1);
	                this.aoBrightnessXYZPPP = par1Block.getMixedBrightnessForBlock(world, par2, par3 + 1, par4 + 1);
	            }
	            if (this.renderMaxX >= 1.0D)
	                --par2;
	            i1 = l;
	            if (this.renderMaxX >= 1.0D || !world.isBlockOpaqueCube(par2 + 1, par3, par4))
	                i1 = par1Block.getMixedBrightnessForBlock(world, par2 + 1, par3, par4);
	            f7 = par1Block.getAmbientOcclusionLightValue(world, par2 + 1, par3, par4);
	            f3 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP) / 4.0F;
	            f4 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7) / 4.0F;
	            f5 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
	            f6 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
	            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
	            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, i1);
	            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, i1);
	            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, i1);
	            if (flag1) {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = par5 * 0.6F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = par6 * 0.6F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = par7 * 0.6F;
	            }
	            else {
	                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
	                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
	                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
	            }
	            this.colorRedTopLeft *= f3;
	            this.colorGreenTopLeft *= f3;
	            this.colorBlueTopLeft *= f3;
	            this.colorRedBottomLeft *= f4;
	            this.colorGreenBottomLeft *= f4;
	            this.colorBlueBottomLeft *= f4;
	            this.colorRedBottomRight *= f5;
	            this.colorGreenBottomRight *= f5;
	            this.colorBlueBottomRight *= f5;
	            this.colorRedTopRight *= f6;
	            this.colorGreenTopRight *= f6;
	            this.colorBlueTopRight *= f6;
	            this.renderSouthFace(par1Block, (double)par2, (double)par3, (double)par4, indices[5], true);
	            flag = true;
	        }
	        this.enableAO = false;
	        v5.draw();
	        v5.startDrawingQuads();
	        return flag;
	    }
}
