package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.ArrayList;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Exception.EnvironmentSanityException;
import Reika.DragonAPI.Exception.EnvironmentSanityException.ErrorType;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class EnvironmentSanityChecker {

	public static final EnvironmentSanityChecker instance = new EnvironmentSanityChecker();

	private EnvironmentSanityChecker() {

	}

	public void check() {
		this.checkBlocks();
		this.checkItems();
		this.checkBiomes();
		this.checkEnchants();
		this.checkPotions();
		this.checkOreDict();
	}

	private void checkBlocks() {
		for (String s : ((Set<String>)Block.blockRegistry.getKeys())) {
			Block b = (Block)Block.blockRegistry.getObject(s);
			if (s == null) {
				throw new EnvironmentSanityException(ErrorType.NULLREG, b);
			}
			else if (b == null) {
				throw new EnvironmentSanityException(ErrorType.NULLENTRY, s);
			}
			else {
				this.verifyBlock(b);
			}
		}
	}

	private void checkItems() {
		for (String s : ((Set<String>)Item.itemRegistry.getKeys())) {
			Item b = (Item)Item.itemRegistry.getObject(s);
			if (s == null) {
				throw new EnvironmentSanityException(ErrorType.NULLREG, b);
			}
			else if (b == null) {
				throw new EnvironmentSanityException(ErrorType.NULLENTRY, s);
			}
			else {
				this.verifyItem(b);
			}
		}
	}

	private void checkBiomes() {
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b != null) {
				if (b.biomeID != i) {
					if (i != BiomeGenBase.megaTaigaHills.biomeID + 128 + 1) {
						//What the HELL, Mojang!?
					}
					else {
						throw new EnvironmentSanityException(ErrorType.IDMISMATCH, b, i, b.biomeID);
					}
				}
				else {
					this.verifyBiome(b);
				}
			}
		}
	}

	private void checkEnchants() {
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			Enchantment e = Enchantment.enchantmentsList[i];
			if (e != null) {
				if (e.effectId != i) {
					throw new EnvironmentSanityException(ErrorType.IDMISMATCH, e, i, e.effectId);
				}
				else {
					this.verifyEnchant(e);
				}
			}
		}
	}

	private void checkPotions() {
		for (int i = 0; i < Potion.potionTypes.length; i++) {
			Potion p = Potion.potionTypes[i];
			if (p != null) {
				if (p.id != i) {
					throw new EnvironmentSanityException(ErrorType.IDMISMATCH, p, i, p.id);
				}
				else {
					this.verifyPotion(p);
				}
			}
		}
	}

	private void checkOreDict() {
		String[] tags = OreDictionary.getOreNames();
		for (int i = 0; i < tags.length; i++) {
			String tag = tags[i];
			ArrayList<ItemStack> li = new ArrayList(OreDictionary.getOres(tag));
			for (ItemStack is : li) {
				try {
					this.verifyOreDictItem(tag, is);
				}
				catch (EnvironmentSanityException e) {
					if (DragonOptions.FIXSANITY.getState()) {
						try {
							DragonAPICore.logError("Found invalid item "+is+" registered to OreDict "+tag+": "+e);
							e.printStackTrace();
							ReikaItemHelper.removeOreDictEntry(tag, is);
						}
						catch (Exception ex) { //could not remove
							ex.initCause(e);
							throw new RuntimeException(ex);
						}
					}
					else {
						throw e;
					}
				}
			}
		}
	}

	private void verifyOreDictItem(String tag, ItemStack is) {
		try {
			is.getUnlocalizedName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Name");
		}
		try {
			is.getDisplayName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Display Name");
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.doClientOreDictVerification(tag, is);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doClientOreDictVerification(String tag, ItemStack is) {
		is.getItem().registerIcons(ReikaTextureHelper.dummyTextureMap);
		Block b = Block.getBlockFromItem(is.getItem());
		if (b != null) {
			b.registerBlockIcons(ReikaTextureHelper.dummyTextureMap);
		}
		try {
			is.getIconIndex();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Icon");
		}
		boolean draw = Tessellator.instance.isDrawing;
		try {
			ReikaGuiAPI.instance.drawItemStack(ReikaGuiAPI.itemRenderer, is, 0, 0);
		}
		catch (Exception e) {
			/*
			try {
				tessellatorReset.invoke(Tessellator.instance);
			}
			catch (Exception ex) {
				RuntimeException exc = new IllegalStateException("Could not reset tessellator when trying to recover parsing invalid OreDict entry");
				ex.initCause(e);
				exc.initCause(ex);
				throw exc;
			}*/
			Tessellator.instance.isDrawing = draw;
			throw new EnvironmentSanityException(ErrorType.OREDICT, is, tag, e, "Render");
		}
	}

	public void verifyBlock(Block b) throws EnvironmentSanityException {
		if (b.getMaterial() == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.getMaterial(), "Material");
		}
		if (b.stepSound == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.stepSound, "Step Sound");
		}
		try {
			b.getLocalizedName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, b, e, "Localization");
		}
	}

	public void verifyItem(Item i) throws EnvironmentSanityException {
		try {
			i.getUnlocalizedName();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, i, e, "Name");
		}
		try {
			i.getMaxDamage();
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, i, e, "Max Damage");
		}
		try {
			i.getItemStackLimit(new ItemStack(i));
		}
		catch (Exception e) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, i, e, "Stacksize Limit");
		}
	}

	public void verifyBiome(BiomeGenBase b) throws EnvironmentSanityException {
		if (b.biomeName == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.biomeName, "Name");
		}
		if (b.topBlock == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.topBlock, "Top Block");
		}
		if (b.fillerBlock == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.fillerBlock, "Filler Block");
		}
		if (b.theBiomeDecorator == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.theBiomeDecorator, "Decorator");
		}

		for (int i = 0; i < EnumCreatureType.values().length; i++) {
			EnumCreatureType e = EnumCreatureType.values()[i];
			if (b.getSpawnableList(e) == null) {
				throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, b, b.getSpawnableList(e), "Spawnable List");
			}
		}
	}

	public void verifyEnchant(Enchantment e) throws EnvironmentSanityException {
		if (e.type == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, e, e.type, "Type");
		}
		try {
			e.getName();
		}
		catch (Exception ex) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, e, ex, "Name");
		}
		try {
			e.getTranslatedName(1);
		}
		catch (Exception ex) {
			throw new EnvironmentSanityException(ErrorType.UNPARSEABLE, e, ex, "Localization");
		}
	}

	public void verifyPotion(Potion p) throws EnvironmentSanityException {
		if (p.getName() == null) {
			throw new EnvironmentSanityException(ErrorType.INVALIDVALUE, p, p.getName(), "Name");
		}
	}

}
