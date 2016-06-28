/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerRegisterEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

import org.lwjgl.opengl.GL11;

import paulscode.sound.SoundSystemConfig;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.DragonAPIInit;
import reika.dragonapi.DragonOptions;
import reika.dragonapi.ModList;
import reika.dragonapi.APIPacketHandler.PacketIDs;
import reika.dragonapi.auxiliary.trackers.ReflectiveFailureTracker;
import reika.dragonapi.auxiliary.trackers.RemoteAssetLoader;
import reika.dragonapi.command.ClearItemsCommand;
import reika.dragonapi.exception.WTFException;
import reika.dragonapi.instantiable.event.AddRecipeEvent;
import reika.dragonapi.instantiable.event.AddSmeltingEvent;
import reika.dragonapi.instantiable.event.ItemUpdateEvent;
import reika.dragonapi.instantiable.event.XPUpdateEvent;
import reika.dragonapi.instantiable.event.client.GameFinishedLoadingEvent;
import reika.dragonapi.instantiable.event.client.HotbarKeyEvent;
import reika.dragonapi.instantiable.event.client.ChatEvent.ChatEventPost;
import reika.dragonapi.instantiable.io.PacketTarget;
import reika.dragonapi.interfaces.tileentity.PlayerBreakHook;
import reika.dragonapi.libraries.ReikaAABBHelper;
import reika.dragonapi.libraries.ReikaEntityHelper;
import reika.dragonapi.libraries.ReikaFluidHelper;
import reika.dragonapi.libraries.ReikaPlayerAPI;
import reika.dragonapi.libraries.ReikaRecipeHelper;
import reika.dragonapi.libraries.ReikaNBTHelper.NBTTypes;
import reika.dragonapi.libraries.io.ReikaChatHelper;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.java.ReikaObfuscationHelper;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.dragonapi.mod.interact.deepinteract.NEIIntercept;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DragonAPIEventWatcher {

	public static final DragonAPIEventWatcher instance = new DragonAPIEventWatcher();

	private DragonAPIEventWatcher() {

	}

	@SubscribeEvent
	public void trackBrokenBlocks(BlockEvent.BreakEvent evt) {
		TileEntity te = evt.world.getTileEntity(evt.x, evt.y, evt.z);
		if (te instanceof PlayerBreakHook) {
			if (!((PlayerBreakHook)te).breakByPlayer(evt.getPlayer())) {
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void catchNullOreDict(OreRegisterEvent evt) {
		if (evt.Ore == null || evt.Ore.getItem() == null)
			throw new WTFException("Someone registered null to the OreDictionary under the name '"+evt.Name+"'!", true);
		else if (evt.Name == null || evt.Name.isEmpty())
			throw new WTFException("Someone registered "+evt.Ore+" under a null or empty OreDict name!", true);
		else {
			DragonAPICore.log("Logged OreDict registration of "+evt.Ore+" as '"+evt.Name+"'.");
		}
	}

	@SubscribeEvent
	public void mapFluidContainers(FluidContainerRegisterEvent evt) {
		Fluid f = evt.data.fluid.getFluid();
		ItemStack fill = evt.data.filledContainer;
		ItemStack empty = evt.data.emptyContainer;
		StringBuilder sb = new StringBuilder();
		sb.append("Logged FluidContainer registration of ");
		sb.append(f.getName());
		sb.append(" with filled '");
		sb.append(fill != null ? fill.getDisplayName() : "[null]");
		sb.append("' and empty '");
		sb.append(empty != null ? empty.getDisplayName() : "[null]");
		sb.append("'.");
		DragonAPICore.log(sb.toString());
		ReikaFluidHelper.mapContainerToFluid(f, empty, fill);
	}

	@SubscribeEvent
	public void onClose(WorldEvent.Unload evt) {

	}

	@SubscribeEvent
	public void onLoad(WorldEvent.Load evt) {

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void increaseChannels(SoundSetupEvent evt) {
		if (DragonOptions.SOUNDCHANNELS.getState()) {
			SoundSystemConfig.setNumberNormalChannels(256);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGameLoaded(GameFinishedLoadingEvent evt) throws InterruptedException {
		this.checkRemoteAssetDownload();
		//if (ModList.liteLoaderInstalled())
		if (!ReikaObfuscationHelper.isDeObfEnvironment())
			Minecraft.getMinecraft().refreshResources();
		if (ModList.NEI.isLoaded()) {
			NEIIntercept.instance.register();
			//NEIFontRendererHandler.instance.register();
		}
		DragonAPIInit.proxy.registerSidedHandlersGameLoaded();
		ReflectiveFailureTracker.instance.print();
	}

	private void checkRemoteAssetDownload() throws InterruptedException {
		long time = 0;
		long d = 100;
		while (!RemoteAssetLoader.instance.isDownloadComplete()) {
			if (time%5000 == 0) {
				String p = String.format("%.2f", 100*RemoteAssetLoader.instance.getDownloadProgress());
				String s = "Remote asset downloads not yet complete (current = "+p+"%). Pausing game load. Total delay: "+time+" ms.";
				DragonAPICore.log(s);
			}
			Thread.sleep(d);
			time += d;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void stopHotbarSwap(HotbarKeyEvent evt) {
		if (DragonOptions.NOHOTBARSWAP.getState())
			evt.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void disableAlphaTest(RenderWorldEvent.Pre evt) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		if (DragonOptions.NOALPHATEST.getState())
			GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.01F);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void disableAlphaTest(RenderWorldEvent.Post evt) {
		GL11.glPopAttrib();
	}

	@SubscribeEvent
	public void sendInteractToClient(PlayerInteractEvent evt) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && !ReikaPlayerAPI.isFake(evt.entityPlayer)) {
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.PLAYERINTERACT.ordinal(), new PacketTarget.PlayerTarget((EntityPlayerMP)evt.entityPlayer), evt.x, evt.y, evt.z, evt.face, evt.action.ordinal());
		}
	}

	@SubscribeEvent
	public void clearItems(ItemUpdateEvent evt) {
		if (ClearItemsCommand.clearItem(evt.entityItem)) {
			evt.entityItem.setDead();
		}
	}

	@SubscribeEvent
	public void tagDroppedItems(ItemTossEvent evt) {
		if (evt.player != null) {
			String s = evt.player.getUniqueID().toString();
			evt.entityItem.getEntityData().setString("dropper", s);
			//ReikaPacketHelper.sendStringIntPacket(packetChannel, PacketIDs.ITEMDROPPER.ordinal(), new PacketTarget.DimensionTarget(evt.entityItem.worldObj), s, evt.entityItem.getEntityId());
		}
	}

	@SubscribeEvent
	public void tagDroppedItems(EntityJoinWorldEvent evt) {
		if (evt.entity instanceof EntityItem && evt.world.isRemote) {
			//ReikaJavaLibrary.pConsole("Sending clientside request for Entity ID "+evt.entity.getEntityId());
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.ITEMDROPPERREQUEST.ordinal(), new PacketTarget.ServerTarget(), evt.entity.getEntityId());
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void confirmNumericIDs(ChatEventPost evt) {
		if (evt.chatMessage.startsWith("Warning: Using numeric IDs will not be supported in the future")) {
			String item1 = EnumChatFormatting.GOLD+"/give item.forestry.apiculture.bee.template.root3";
			String item2 = EnumChatFormatting.GOLD+"/give item.gregtech.machine.primary.transformer.hv.ruby";
			String c = EnumChatFormatting.LIGHT_PURPLE.toString();
			ReikaChatHelper.writeString(c+"Numeric IDs will remain functional as long as I am here,");
			ReikaChatHelper.writeString(c+"because not everyone wants to type");
			ReikaChatHelper.writeString(c+"'"+item1+c+"'");
			ReikaChatHelper.writeString(c+"or");
			ReikaChatHelper.writeString(c+"'"+item2+c+"'.");
			ReikaChatHelper.writeString(c+"-DragonAPI");
		}
	}

	@SubscribeEvent
	public void verifyCraftingRecipe(AddRecipeEvent evt) {
		if (!evt.isVanillaPass) {
			try {
				if (!ReikaRecipeHelper.verifyRecipe(evt.recipe)) {
					String msg = "Class="+evt.recipe.getClass();
					if (evt.recipe.getRecipeOutput() != null && evt.recipe.getRecipeOutput().getItem() != null)
						msg += ", Output="+evt.recipe.getRecipeOutput();
					else if (evt.recipe.getRecipeOutput() != null)
						msg += ", Output is a null-item ItemStack";
					DragonAPICore.log("Invalid recipe, such as with nulled inputs, found. Removing to prevent crashes. "+msg+".");
					evt.setCanceled(true);
				}
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not parse crafting recipe");
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void verifySmeltingRecipe(AddSmeltingEvent evt) {
		if (!evt.isVanillaPass) {
			try {
				ItemStack in = evt.getInput();
				ItemStack out = evt.getOutput();
				if (in == null || in.getItem() == null) {
					DragonAPICore.logError("Found a null-input (or null-item input) smelting recipe! "+null+" > "+out+"! This is invalid!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
				else if (out == null || out.getItem() == null) {
					DragonAPICore.logError("Found a null-output (or null-item output) smelting recipe! "+in+" > "+null+"! This is invalid!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
				else if (!ReikaItemHelper.verifyItemStack(in, true)) {
					DragonAPICore.logError("Found a smelting recipe with an invalid input!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
				else if (!ReikaItemHelper.verifyItemStack(out, true)) {
					DragonAPICore.logError("Found a smelting recipe with an invalid output!");
					Thread.dumpStack();
					evt.setCanceled(true);
				}
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not parse smelting recipe");
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void addGuideGUI(PlayerInteractEvent evt) {
		EntityPlayer ep = evt.entityPlayer;
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && is.getItem() == Items.enchanted_book) {
			if (is.stackTagCompound != null) {
				NBTTagCompound disp = is.stackTagCompound.getCompoundTag("display");
				if (disp != null) {
					NBTTagList list = disp.getTagList("Lore", NBTTypes.STRING.ID);
					if (list != null && list.tagCount() > 0) {
						String sg = list.getStringTagAt(0);
						if (sg != null && sg.equals("Reika's Mods Guide")) {
							ep.openGui(DragonAPIInit.instance, 0, ep.worldObj, 0, 0, 0);
							evt.setResult(Result.ALLOW);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void collateXP(XPUpdateEvent evt) {
		if (DragonOptions.XPMERGE.getState()) {
			//ReikaJavaLibrary.pConsole(evt.xp.worldObj.loadedEntityList.size(), Side.SERVER);
			if (!evt.xp.isDead) {
				if (!evt.xp.worldObj.isRemote && evt.xp.xpOrbAge%16 == 0) {
					AxisAlignedBB box = ReikaAABBHelper.getEntityCenteredAABB(evt.xp, 1);
					List<EntityXPOrb> li = evt.xp.worldObj.getEntitiesWithinAABB(EntityXPOrb.class, box);
					//ReikaJavaLibrary.pConsole(li.size()+":"+li);
					if (li.size() > 1) {
						EntityXPOrb xp = ReikaEntityHelper.mergeXPOrbs(evt.xp.worldObj, li);
					}
				}
			}
		}
	}

}
