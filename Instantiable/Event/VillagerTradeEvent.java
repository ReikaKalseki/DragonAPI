package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;



public class VillagerTradeEvent extends PlayerEvent {

	public final EntityVillager villager;
	public final MerchantRecipe trade;

	public VillagerTradeEvent(EntityVillager ev, MerchantRecipe r, EntityPlayer ep) {
		super(ep);
		villager = ev;
		trade = r;
	}

	public static void fire(EntityVillager ev, MerchantRecipe r) {
		MinecraftForge.EVENT_BUS.post(new VillagerTradeEvent(ev, r, ev.getCustomer()));
	}

}
