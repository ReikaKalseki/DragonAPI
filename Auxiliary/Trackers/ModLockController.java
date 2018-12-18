package Reika.DragonAPI.Auxiliary.Trackers;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ModLockController {

	public static final ModLockController instance = new ModLockController();

	private final HashMap<String, String> data = new HashMap();

	private ModLockController() {

	}

	public void registerMod(DragonAPIMod mod) {
		data.put(mod.getTechnicalName(), this.hash(mod));
	}

	private String hash(DragonAPIMod mod) {
		return "";
	}

	public boolean verify(DragonAPIMod mod) {
		return this.hash(mod).equals(data.get(mod.getTechnicalName()));
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
