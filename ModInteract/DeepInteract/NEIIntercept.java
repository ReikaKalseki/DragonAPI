/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.DeepInteract;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.Event.NEIRecipeCheckEvent;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

import codechicken.nei.ItemPanel;
import codechicken.nei.ItemPanel.ItemPanelSlot;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIController;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

/** Allows for loading custom GUIs on NEI recipe checks. */
public class NEIIntercept implements IContainerInputHandler {

	public static final NEIIntercept instance = new NEIIntercept();

	private KeyBinding wailaNEILookup;

	private NEIIntercept() {

	}

	public void register() {
		GuiContainerManager.inputHandlers.add(this);
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		if (ModList.WAILA.isLoaded()) {
			try {
				Class c = Class.forName("mcp.mobius.waila.client.KeyEvent");
				Field f = c.getDeclaredField("key_recipe");
				f.setAccessible(true);
				wailaNEILookup = (KeyBinding)f.get(null);
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not interface with WAILA recipe lookup: "+e);
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.WAILA, e);
			}
		}
	}

	@Override
	public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {return false;}

	@Override
	public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {}

	@Override
	public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		return keyID == NEIClientConfig.getKeyBinding("gui.recipe") && !NEIClientConfig.isHidden() && MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui));
	}

	@Override
	public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
		if (button == 0) {
			if (gui instanceof GuiRecipe && !NEIClientConfig.isHidden())
				return MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui));
			else {
				ItemPanel panel = LayoutManager.itemPanel;
				if (panel != null) {
					ItemPanelSlot slot = panel.getSlotMouseOver(mousex, mousey);
					if (slot != null && panel.draggedStack == null) {
						ItemStack item = slot.item;
						if (NEIController.manager.window instanceof GuiRecipe || !NEIClientConfig.canCheatItem(item)) {
							if (button == 0) {
								return MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(gui, item));
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {}

	@Override
	public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {}

	@Override
	public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {return false;}

	@Override
	public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {}

	@Override
	public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {}

	@SubscribeEvent
	@ModDependent(ModList.WAILA)
	public void onKeyEvent(InputEvent.KeyInputEvent event) {
		int key = Keyboard.getEventKey();
		if (Keyboard.getEventKeyState()) {
			if (wailaNEILookup != null && key == wailaNEILookup.getKeyCode()) {
				MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlockClient(4.5, false);
				if (mov != null) {
					Minecraft mc = Minecraft.getMinecraft();
					World world = mc.theWorld;
					Block b = world.getBlock(mov.blockX, mov.blockY, mov.blockZ);
					int meta = world.getBlockMetadata(mov.blockX, mov.blockY, mov.blockZ);
					ItemStack is = new ItemStack(b, 1, meta);
					ArrayList<ItemStack> li = b.getDrops(world, mov.blockX, mov.blockY, mov.blockZ, meta, 0);
					if (li != null && !li.isEmpty()) {
						is = li.get(0);
					}
					GuiContainer prevscreen = mc.currentScreen instanceof GuiContainer ? (GuiContainer)mc.currentScreen : null;
					MinecraftForge.EVENT_BUS.post(new NEIRecipeCheckEvent(prevscreen, is));
				}
			}
		}
	}

}
