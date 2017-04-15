/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
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
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader;
import Reika.DragonAPI.Command.ClearItemsCommand;
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Instantiable.Event.AddRecipeEvent;
import Reika.DragonAPI.Instantiable.Event.AddSmeltingEvent;
import Reika.DragonAPI.Instantiable.Event.ItemUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.MobTargetingEvent;
import Reika.DragonAPI.Instantiable.Event.XPUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ChatEvent.ChatEventPost;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import Reika.DragonAPI.Instantiable.Event.Client.HotbarKeyEvent;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.TileEntity.PlayerBreakHook;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.NEIIntercept;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DragonAPIEventWatcher {

	public static final DragonAPIEventWatcher instance = new DragonAPIEventWatcher();

	private long IDMsgCooldown = 0;

	private DragonAPIEventWatcher() {

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fixRespirationFourPlusFog(EntityViewRenderEvent.FogDensity evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (ep.isPotionActive(Potion.blindness))
			return;
		ItemStack helm = ep.getCurrentArmor(3);
		if (helm != null && ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.respiration, helm) > 3) {
			evt.density = 0.05F;
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void protectNewPlayers(MobTargetingEvent.Pre evt) {
		if (evt.player.ticksExisted < 200 && DragonOptions.PROTECTNEW.getState()) { //10s
			evt.setResult(Result.DENY);
		}
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
		ReikaFluidHelper.initEarlyRegistrations();
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
		if (System.currentTimeMillis()-IDMsgCooldown >= 30000 && evt.chatMessage.startsWith("Warning: Using numeric IDs will not be supported in the future")) {
			String item1 = EnumChatFormatting.GOLD+"/give item.forestry.apiculture.bee.template.root3";
			String item2 = EnumChatFormatting.GOLD+"/give item.gregtech.machine.primary.transformer.hv.ruby";
			String c = EnumChatFormatting.LIGHT_PURPLE.toString();
			ReikaChatHelper.writeString(c+"Numeric IDs will remain functional as long as I am here,");
			ReikaChatHelper.writeString(c+"because not everyone wants to type");
			ReikaChatHelper.writeString(c+"'"+item1+c+"'");
			ReikaChatHelper.writeString(c+"or");
			ReikaChatHelper.writeString(c+"'"+item2+c+"'.");
			ReikaChatHelper.writeString(c+"-DragonAPI");
			IDMsgCooldown = System.currentTimeMillis();
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
	/*
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
	 */

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
