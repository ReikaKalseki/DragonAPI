/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Rendering;

import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

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
			rb.renderBlockAsItem(Blocks.mob_spawner, 0, 1);
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			double d = 0.5;
			GL11.glTranslated(d, d, d);
			rb = (RenderBlocks)data[0];
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(Blocks.mob_spawner, 0, 1);
			break;
		case INVENTORY:
			rb = (RenderBlocks)data[0];
			ReikaTextureHelper.bindTerrainTexture();
			rb.renderBlockAsItem(Blocks.mob_spawner, 0, 1);

			if (item.stackTagCompound == null)
				return;
			String name = ReikaSpawnerHelper.getSpawnerFromItemNBT(item);
			Entity entity = EntityList.createEntityByName(name, Minecraft.getMinecraft().theWorld);
			if (entity != null)
			{
				//entity.setWorld(par0MobSpawnerBaseLogic.getSpawnerWorld());

				double x = 10;
				double y = 10;
				double z = 0;

				entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
				Render r = ReikaEntityHelper.getEntityRenderer(entity.getClass());
				if (r != null) {
					GL11.glPushMatrix();
					double sc = 0.5;
					double dy = -0.5;
					if (entity instanceof EntitySlime) {
						sc = 1.25/((EntitySlime)entity).getSlimeSize();
						dy = -0.25;
					}
					if (entity instanceof EntityGiantZombie)
						sc = 0.1;
					if (entity instanceof EntityGhast) {
						sc = 0.15;
						dy = -0.25;
					}
					if (entity instanceof EntityWither)
						sc = 0.3;
					if (entity instanceof EntityDragon)
						sc = 0.125;
					if (entity instanceof EntityEnderman)
						dy = -0.75;
					if (entity instanceof EntitySilverfish) {
						sc = 1;
						dy = 0;
					}
					if (entity instanceof EntitySquid)
						dy = 0;
					double ang = (System.currentTimeMillis()/5)%360;
					GL11.glTranslated(0, dy, 0);
					GL11.glScaled(sc, sc, sc);
					GL11.glRotated(ang, 0, 1, 0);
					GL11.glRotated(-20, 1, 0, 0);
					ReikaRenderHelper.disableEntityLighting();
					try {
						String boss = BossStatus.bossName;
						BossStatus.bossName = null;
						r.doRender(entity, 0, 0, 0, 0, 0);
						BossStatus.bossName = boss;
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					ReikaRenderHelper.disableEntityLighting();
					GL11.glPopMatrix();
				}
			}
			break;
		default:
			break;
		}
	}

}