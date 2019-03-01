package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWaySet;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ModLockController {

	public static final ModLockController instance = new ModLockController();

	private final HashMap<String, String> data = new HashMap();
	private final OneWaySet<String> wasEverRegistered = new OneWaySet();

	private ModLockController() {

	}

	public void registerMod(DragonAPIMod mod) {
		if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION))
			throw new MisuseException(mod, "Mods can only be registered at the beginning of preinit!");
		this.doRegisterMod(mod);
	}

	private void doRegisterMod(DragonAPIMod mod) {
		data.put(mod.getTechnicalName(), this.hash(mod));
		wasEverRegistered.add(mod.getTechnicalName());
	}

	private String hash(DragonAPIMod mod) {
		return "";
	}

	public boolean verify(DragonAPIMod mod) {
		String n = mod.getTechnicalName();
		if (!wasEverRegistered.contains(n)) {
			if (ReikaObfuscationHelper.isDeObfEnvironment())
				DragonAPICore.logError("Cannot verify an unregistered mod "+n+"!");
			this.doRegisterMod(mod);
			return true;
		}
		return this.hash(mod).equals(data.get(n));
	}

	public void unverify(DragonAPIMod mod) {
		data.remove(mod.getTechnicalName());
	}

	public void syncPlayer(EntityPlayerMP ep) {
		StringBuilder raw = new StringBuilder();
		for (String s : data.keySet()) {
			String val = data.get(s);
			for (char ch = 'a'; ch <= 'z'; ch++) {
				s = s.replace(ch, (char)(450+ch));
			}
			for (char ch = 'A'; ch <= 'Z'; ch++) {
				s = s.replace(ch, (char)(450+ch));
			}
			raw.append(s+"/"+val+":");
		}
		ReikaPacketHelper.sendStringPacket(DragonAPIInit.packetChannel, PacketIDs.MODLOCK.ordinal(), raw.toString(), new PacketTarget.PlayerTarget(ep));
	}

	@SideOnly(Side.CLIENT)
	public void readSync(EntityPlayer ep, String raw) {
		data.clear();
		String[] parts = raw.split("\\:");
		for (String s : parts) {
			String[] vals = s.split("\\/");
			for (char ch = 'a'; ch <= 'z'; ch++) {
				vals[0] = vals[0].replace((char)(450+ch), ch);
			}
			for (char ch = 'A'; ch <= 'Z'; ch++) {
				vals[0] = vals[0].replace((char)(450+ch), ch);
			}
			data.put(vals[0], vals[1]);
		}
		MinecraftForge.EVENT_BUS.post(new ModReVerifyEvent());
	}

	@SideOnly(Side.CLIENT)
	public static class ModReVerifyEvent extends Event {

	}

}
