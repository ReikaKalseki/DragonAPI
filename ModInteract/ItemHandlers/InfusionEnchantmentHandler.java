package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.enchantment.Enchantment;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;

import thaumcraft.api.ThaumcraftApi;

public class InfusionEnchantmentHandler {

	public static final InfusionEnchantmentHandler instance = new InfusionEnchantmentHandler();

	private final MultiMap<ModEntry, Enchantment> enchantments = new MultiMap();

	private InfusionEnchantmentHandler() {
		if (ModList.THAUMCRAFT.isLoaded())
			this.loadThaumcraft();
		if (ModList.THAUMICTINKER.isLoaded())
			this.loadTTinkerer();
	}

	@ModDependent(ModList.THAUMCRAFT)
	private void loadThaumcraft() {
		this.registerEnchant(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantFrugal]);
		this.registerEnchant(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantHaste]);
		this.registerEnchant(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantPotency]);
		this.registerEnchant(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantRepair]);
		this.registerEnchant(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantWandFortune]);
	}

	@ModDependent(ModList.THAUMICTINKER)
	private void loadTTinkerer() {
		Class c;
		try {
			c = Class.forName("thaumic.tinkerer.common.enchantment.ModEnchantments");
			Field[] fd = c.getDeclaredFields();
			for (Field f : fd) {
				if ((f.getModifiers() & Modifier.STATIC) != 0 && f.getType() == Enchantment.class) {
					this.registerEnchant(ModList.THAUMICTINKER, (Enchantment)f.get(null));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerEnchant(ModList mod, Enchantment e) {
		if (e.effectId == 0)
			return;
		if (ReikaEnchantmentHelper.isVanillaEnchant(e)) {
			DragonAPICore.logError("Detected a ThaumCraft enchantment registered to ID "+e.effectId+", overwriting a vanilla ID!");
			return;
		}
		enchantments.addValue(mod, e);
	}

	public boolean isInfusionEnchantment(Enchantment e) {
		return enchantments.containsValue(e);
	}

	public Collection<Enchantment> getEnchantmentsFor(ModEntry mod) {
		return Collections.unmodifiableCollection(enchantments.get(mod));
	}

}
