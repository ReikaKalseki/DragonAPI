package Reika.DragonAPI.Command;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

public class ClearItemsCommand extends DragonCommandBase {

	private static long clearTime = -1;
	private static boolean clearAll = false;
	private static Collection<Integer> clearIDs = new ArrayList();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		clearIDs.clear();
		clearAll = false;
		if (args.length == 0) {
			this.sendChatToSender(ics, EnumChatFormatting.RED.toString()+"You must specify at least one ID, or a '*'!");
			return;
		}
		clearTime = System.currentTimeMillis();
		if (args[0].equals("*")) {
			clearAll = true;
		}
		else {
			for (int i = 0; i < args.length; i++) {
				clearIDs.add(Integer.parseInt(args[i]));
			}
			this.sendChatToSender(ics, EnumChatFormatting.GREEN.toString()+"Cleared all items with IDs '"+args+"'.");
		}
	}

	@Override
	public String getCommandString() {
		return "clearitems";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	public static boolean clearItem(EntityItem ei) {
		return System.currentTimeMillis()-clearTime < 1000 && (clearAll || clearIDs.contains(Item.getIdFromItem(ei.getEntityItem().getItem())));
	}

}
