/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class SpawnerRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		RenderBlocks rb;
		float spin = (System.nanoTime()/50000000)%360;
		switch(type) {
		case ENTITY:
			double s = 0.5;
			GL11.glScaled(s, s, s);
			rb = (RenderBlocks)data[0];
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(Block.mobSpawner, 0, 1);
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			double d = 0.5;
			GL11.glTranslated(d, d, d);
			rb = (RenderBlocks)data[0];
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(Block.mobSpawner, 0, 1);
			break;
		case INVENTORY:
			rb = (RenderBlocks)data[0];
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(Block.mobSpawner, 0, 1);

			if (item.stackTagCompound == null)
				return;
			String name = item.stackTagCompound.getString("Spawer");
			Entity entity = EntityList.createEntityByName(name, Minecraft.getMinecraft().theWorld);

			if (entity != null)
			{
				//entity.setWorld(par0MobSpawnerBaseLogic.getSpawnerWorld());

				double x = 10;
				double y = 10;
				double z = 0;

				float f1 = 0.4375F;
				//GL11.glTranslatef(0.0F, 0.4F, 0.0F);
				//GL11.glRotatef((float)(par0MobSpawnerBaseLogic.field_98284_d + (par0MobSpawnerBaseLogic.field_98287_c - par0MobSpawnerBaseLogic.field_98284_d) * (double)par7) * 10.0F, 0.0F, 1.0F, 0.0F);
				//GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
				//GL11.glTranslatef(0.0F, -0.4F, 0.0F);
				//GL11.glScalef(f1, f1, f1);
				entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
				//RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, spin);
			}
			break;
		default:
			break;
		}
	}

}
