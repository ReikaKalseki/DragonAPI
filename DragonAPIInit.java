package Reika.DragonAPI;

import java.net.URL;

import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.BCMachineHandler;
import Reika.DragonAPI.ModInteract.DartItemHandler;
import Reika.DragonAPI.ModInteract.DartOreHandler;
import Reika.DragonAPI.ModInteract.ThaumBlockHandler;
import Reika.DragonAPI.ModInteract.ThaumOreHandler;
import Reika.DragonAPI.ModInteract.TinkerToolHandler;
import Reika.DragonAPI.ModInteract.TwilightBlockHandler;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.DragonAPI.ModRegistry.ModSpiderList;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod( modid = "DragonAPI", name="DragonAPI", version="release", certificateFingerprint = "@GET_FINGERPRINT@")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class DragonAPIInit extends DragonAPIMod {

	@SidedProxy(clientSide="Reika.DragonAPI.APIProxyClient", serverSide="Reika.DragonAPI.APIProxy")
	public static APIProxy proxy;

	@Override
	@PreInit
	public void preload(FMLPreInitializationEvent evt) {

	}

	@Override
	@Init
	public void load(FMLInitializationEvent event) {

	}

	@Override
	@PostInit
	public void postload(FMLPostInitializationEvent evt) {
		this.loadHandlers();
	}

	private static void loadHandlers() {
		ReikaJavaLibrary.initClass(APIRegistry.class);
		ReikaJavaLibrary.initClass(ModOreList.class);
		ReikaJavaLibrary.initClass(ModWoodList.class);
		ReikaJavaLibrary.initClass(ModSpiderList.class);

		if (APIRegistry.BUILDCRAFTFACTORY.conditionsMet()) {
			ReikaJavaLibrary.initClass(BCMachineHandler.class);
		}
		if (APIRegistry.THAUMCRAFT.conditionsMet()) {
			ReikaJavaLibrary.initClass(ThaumOreHandler.class);
			ReikaJavaLibrary.initClass(ThaumBlockHandler.class);
		}
		if (APIRegistry.DARTCRAFT.conditionsMet()) {
			ReikaJavaLibrary.initClass(DartOreHandler.class);
			ReikaJavaLibrary.initClass(DartItemHandler.class);
		}
		if (APIRegistry.TINKERER.conditionsMet()) {
			ReikaJavaLibrary.initClass(TinkerToolHandler.class);
		}
		if (APIRegistry.TWILIGHT.conditionsMet()) {
			ReikaJavaLibrary.initClass(TwilightBlockHandler.class);
		}
	}

	@Override
	public String getDisplayName() {
		return "DragonAPI";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return null;
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return "";
	}

}
