package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.BasicModEntry;

public class MatterOverdriveHandler {

	private static final String name = "addToBlacklist";

	private static Method itemCall;
	private static Method blockCall;
	private static Method stackCall;

	public static void blacklist(Item item) {
		try {
			itemCall.invoke(null, item);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void blacklist(Block item) {
		try {
			blockCall.invoke(null, item);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void blacklist(ItemStack item) {
		try {
			stackCall.invoke(null, item);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			Class c = Class.forName("matteroverdrive.handler.MatterRegistry");

			itemCall = c.getMethod(name, Item.class);
			blockCall = c.getMethod(name, Block.class);
			stackCall = c.getMethod(name, ItemStack.class);
		}
		catch (Exception e) {
			DragonAPICore.logError("Could not load Matter Overdrive blacklisting!");
			e.printStackTrace();
			ReflectiveFailureTracker.instance.logModReflectiveFailure(new BasicModEntry("mo"), e);
		}
	}

}
