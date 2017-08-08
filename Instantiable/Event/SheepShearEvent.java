/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import java.util.ArrayList;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class SheepShearEvent extends LivingEvent {

	public final int fortune;
	public final EntitySheep sheep;
	public final ItemStack shears;

	public final ArrayList<ItemStack> drops;

	public SheepShearEvent(EntitySheep e, ItemStack is, int f, ArrayList<ItemStack> li) {
		super(e);
		shears = is;
		fortune = f;
		sheep = e;
		drops = li;
	}

	public static ArrayList<ItemStack> fire(EntitySheep e, ItemStack is, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		int n = 1 + e.worldObj.rand.nextInt(3);
		for (int i = 0; i < n; i++) {
			ret.add(new ItemStack(Blocks.wool, 1, e.getFleeceColor()));
		}
		SheepShearEvent evt = new SheepShearEvent(e, is, fortune, ret);
		MinecraftForge.EVENT_BUS.post(evt);
		if (evt.isCanceled())
			return new ArrayList();
		e.setSheared(true);
		e.playSound("mob.sheep.shear", 1.0F, 1.0F);
		return ret;
	}

}
