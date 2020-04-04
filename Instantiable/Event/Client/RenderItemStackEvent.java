package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;


public class RenderItemStackEvent extends Event {

	public static boolean runningItemRender = false;

	public final int posX;
	public final int posY;
	private final ItemStack item;

	public RenderItemStackEvent(ItemStack is, int x, int y) {
		item = is;
		posX = x;
		posY = y;
	}

	public static void firePre(ItemStack is, int x, int y) {
		runningItemRender = true;
		MinecraftForge.EVENT_BUS.post(new Pre(is, x, y));
	}

	public static void firePost(ItemStack is, int x, int y) {
		MinecraftForge.EVENT_BUS.post(new Post(is, x, y));
		runningItemRender = false;
	}

	public static class Pre extends RenderItemStackEvent {

		public Pre(ItemStack is, int x, int y) {
			super(is, x, y);
		}

	}

	public static class Post extends RenderItemStackEvent {

		public Post(ItemStack is, int x, int y) {
			super(is, x, y);
		}

	}

}
