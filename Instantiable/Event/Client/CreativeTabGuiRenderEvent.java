/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Cancelable
@SideOnly(Side.CLIENT)
/** Cancel the event to disable the "vanilla" rendering */
public class CreativeTabGuiRenderEvent extends Event {

	public final GuiContainerCreative gui;
	public final CreativeTabs tab;
	public final GuiTextField textField;
	public final int tabPage;
	public final int guiXSize;
	public final int guiYSize;

	public CreativeTabGuiRenderEvent(GuiContainerCreative gui, CreativeTabs tab, GuiTextField search, int tabPage, int xSize, int ySize) {
		this.gui = gui;
		this.tab = tab;
		textField = search;
		this.tabPage = tabPage;
		guiXSize = xSize;
		guiYSize = ySize;
	}
}
