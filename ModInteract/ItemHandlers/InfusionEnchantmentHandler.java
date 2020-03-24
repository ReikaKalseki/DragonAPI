package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.enchantment.Enchantment;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;

import thaumcraft.api.ThaumcraftApi;

public class InfusionEnchantmentHandler {

	public static final InfusionEnchantmentHandler instance = new InfusionEnchantmentHandler();

	private final MultiMap<ModEntry, Enchantment> enchantments = new MultiMap();

	private InfusionEnchantmentHandler() {
		this.loadThaumcraft();
		if (ModList.THAUMICTINKER.isLoaded())
			this.loadTTinkerer();
	}

	private void loadThaumcraft() {
		enchantments.addValue(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantFrugal]);
		enchantments.addValue(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantHaste]);
		enchantments.addValue(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantPotency]);
		enchantments.addValue(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantRepair]);
		enchantments.addValue(ModList.THAUMCRAFT, Enchantment.enchantmentsList[ThaumcraftApi.enchantWandFortune]);
	}

	@ModDependent(ModList.THAUMICTINKER)
	private void loadTTinkerer() {
		Class c;
		try {
			c = Class.forName("thaumic.tinkerer.common.enchantment.ModEnchantments");
			Field[] fd = c.getDeclaredFields();
			for (Field f : fd) {
				if ((f.getModifiers() & Modifier.STATIC) != 0 && f.getType() == Enchantment.class) {
					enchantments.addValue(ModList.THAUMICTINKER, (Enchantment)f.get(null));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isInfusionEnchantment(Enchantment e) {
		return enchantments.containsValue(e);
	}

	public Collection<Enchantment> getEnchantmentsFor(ModEntry mod) {
		return Collections.unmodifiableCollection(enchantments.get(mod));
	}

}
