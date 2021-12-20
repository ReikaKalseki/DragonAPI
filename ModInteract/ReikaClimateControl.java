/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.world.biome.BiomeGenBase;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.ClassReparenter.Reparent;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

import climateControl.api.BiomeSettings;
import climateControl.api.ClimateControlRules;
import climateControl.utils.Mutable;

@Reparent(value = {"climateControl.api.BiomeSettings", "Reika.DragonAPI.ModInteract.DummyBiomeSettings"})
public class ReikaClimateControl extends BiomeSettings {

	public static final String biomeCategory = "ReikasBiome";
	public static final String reikasCategory = "ReikasSettings";
	private final Category reikasSettings = new Category(reikasCategory);

	private static final ArrayList<ElementDelegate> biomes = new ArrayList();

	/*
	public final Element enderForest = new Element("Ender Forest", 47, "MEDIUM");
	public final Element rainbowForest = new Element("Rainbow Forest", 48, "MEDIUM");
	public final Element glowcliffs = new Element("Luminous Cliffs", 49, "MEDIUM");
	 */

	static final String biomesOnName = "ReikasBiomesOn";


	static final String configName = "Reikas";

	private Object biomesFromConfig = climateControlCategory.booleanSetting(biomesOnName, "", false);
	private Object biomesInNewWorlds = climateControlCategory.booleanSetting(this.startBiomesName(configName), "Use biome in new worlds and dimensions", true);

	public ReikaClimateControl() {
		super(biomeCategory);
		/*
		enderForest.biomeIncidences().set(3);
		rainbowForest.biomeIncidences().set(3);
		glowcliffs.biomeIncidences().set(1);
		 */
		for (ElementDelegate e : biomes) {
			new Element(e.name, e.ID, e.incidence, e.hasVillages, e.climate);
		}
		if (ModList.CLIMATECONTROL.isLoaded()) {
			this.loadConfig();
		}
		DragonAPICore.log("Constructing updated ClimateControl config with "+biomes.size()+" entries: "+biomes);
	}

	@ModDependent(ModList.CLIMATECONTROL)
	private void loadConfig() {
		biomesFromConfig = climateControlCategory.booleanSetting(biomesOnName, "", false);
		biomesInNewWorlds = climateControlCategory.booleanSetting(this.startBiomesName(configName), "Use biome in new worlds and dimensions", true);
	}

	public static void registerBiome(BiomeGenBase b, int incidence, boolean hasVillages, String climate) {
		biomes.add(new ElementDelegate(b.biomeName, b.biomeID, incidence, hasVillages, climate));
		DragonAPICore.log("Registering ClimateControl biome registration of "+biomes.get(biomes.size()-1));
	}

	@Override
	public void setNativeBiomeIDs(File configDirectory) {/*
		try {
			UpdatedReikaSettings nativeSettings = this.nativeIDs(configDirectory);
			for (Element e : biomes) {
				e.biomeID().set(newValue);
			}
			/*
			enderForest.biomeID().set(nativeSettings.EnderForestID.value());
			rainbowForest.biomeID().set(nativeSettings.RainbowForestID.value());
			glowcliffs.biomeID().set(nativeSettings.GlowCliffsID.value());
	 *//*
		}
		catch (NoClassDefFoundError e) {
			// no highlands
		}*/
	}

	@Override
	public void setRules(ClimateControlRules rules) {

	}

	@Override
	@ModDependent(ModList.CLIMATECONTROL)
	public void onNewWorld() {
		((Mutable<Boolean>)biomesFromConfig).set((Mutable<Boolean>)biomesInNewWorlds);
	}

	@Override
	@ModDependent(ModList.CLIMATECONTROL)
	public boolean biomesAreActive() {
		return ((Mutable<Boolean>)biomesFromConfig).value();
	}
	/*
	private UpdatedReikaSettings nativeIDs(File configDirectory) {
		UpdatedReikaSettings result = new UpdatedReikaSettings();
		File reikaDirectory = new File(configDirectory, "Reika");
		File configFile = new File(reikaDirectory, "ChromatiCraft.cfg");
		result.readFrom(new Configuration(configFile));
		return result;
	}*/

	private static class ElementDelegate {

		private final String name;
		private final int ID;
		private final int incidence;
		private final boolean hasVillages;
		private final String climate;

		private ElementDelegate(String n, int id, int rate, boolean v, String c) {
			name = n;
			ID = id;
			incidence = rate;
			hasVillages = v;
			climate = c;
		}

		@Override
		public String toString() {
			return name+" @ "+ID+" = "+incidence+" / "+hasVillages+" / "+climate;
		}

	}
}
/*
class UpdatedReikaSettings extends Settings {

	public static final String biomeIDName = "biome ids";
	public final Category biomeIDs = new Category(biomeIDName);

	Mutable<Integer> EnderForestID = biomeIDs.intSetting("Ender Forest Biome ID", 47);
	Mutable<Integer> RainbowForestID = biomeIDs.intSetting("Rainbow Forest Biome ID", 48);
	Mutable<Integer> GlowCliffsID = biomeIDs.intSetting("Luminous Cliffs Biome ID", 49);

}*/
