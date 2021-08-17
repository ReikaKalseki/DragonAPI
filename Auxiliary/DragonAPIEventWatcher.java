/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerRegisterEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader;
import Reika.DragonAPI.Auxiliary.Trackers.SpecialDayTracker;
import Reika.DragonAPI.Auxiliary.Trackers.VersionTransitionTracker;
import Reika.DragonAPI.Command.ClearItemsCommand;
import Reika.DragonAPI.Exception.WTFException;
import Reika.DragonAPI.Extras.ChangePacketRenderer;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.Interpolation;
import Reika.DragonAPI.Instantiable.Event.AddRecipeEvent;
import Reika.DragonAPI.Instantiable.Event.AddSmeltingEvent;
import Reika.DragonAPI.Instantiable.Event.FireChanceEvent;
import Reika.DragonAPI.Instantiable.Event.ItemUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.MobTargetingEvent;
import Reika.DragonAPI.Instantiable.Event.ProfileEvent;
import Reika.DragonAPI.Instantiable.Event.ProfileEvent.ProfileEventWatcher;
import Reika.DragonAPI.Instantiable.Event.XPUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ChatEvent.ChatEventPost;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLoginEvent;
import Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent;
import Reika.DragonAPI.Instantiable.Event.Client.GameFinishedLoadingEvent;
import Reika.DragonAPI.Instantiable.Event.Client.HotbarKeyEvent;
import Reika.DragonAPI.Instantiable.Event.Client.RenderBlockAtPosEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SettingsEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SkyColorEvent;
import Reika.DragonAPI.Instantiable.Event.Client.SoundAttenuationDistanceEvent;
import Reika.DragonAPI.Instantiable.Event.Client.WinterColorsEvent;
import Reika.DragonAPI.Instantiable.IO.EnumSound;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Interfaces.Block.Submergeable;
import Reika.DragonAPI.Interfaces.Entity.DestroyOnUnload;
import Reika.DragonAPI.Interfaces.Registry.CustomDistanceSound;
import Reika.DragonAPI.Interfaces.TileEntity.PlayerBreakHook;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.NEIIntercept;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystemConfig;

public class DragonAPIEventWatcher implements ProfileEventWatcher {

	public static final DragonAPIEventWatcher instance = new DragonAPIEventWatcher();

	private long IDMsgCooldown = 0;

	private final Interpolation biomeHumidityFlammability = new Interpolation(false);

	private DragonAPIEventWatcher() {
		ProfileEvent.registerHandler("debug", this);

		biomeHumidityFlammability.addPoint(0, 3);
		biomeHumidityFlammability.addPoint(0.1, 2);
		biomeHumidityFlammability.addPoint(0.25, 1.5);
		biomeHumidityFlammability.addPoint(0.4, 1.1);
		biomeHumidityFlammability.addPoint(0.5, 1);
		biomeHumidityFlammability.addPoint(0.8, 0.9);
		biomeHumidityFlammability.addPoint(0.9, 0.75);
		biomeHumidityFlammability.addPoint(1, 0.5);
	}

	public void onCall(String tag) {
		if (tag.equals("debug")) {
			this.showF3Extras();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void soundEnumDistance(SoundAttenuationDistanceEvent evt) {
		if (evt.sound instanceof EnumSound) {
			EnumSound es = (EnumSound)evt.sound;
			if (es.sound instanceof CustomDistanceSound) {
				float dist = ((CustomDistanceSound)es.sound).getAudibleDistance();
				if (dist > 0)
					evt.distance = dist;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void renderSubmergeable(RenderBlockAtPosEvent evt) {
		if (evt.block instanceof Submergeable) {
			Submergeable s = (Submergeable)evt.block;
			int meta = evt.getMetadata();
			if (s.isSubmergeable(evt.access, evt.xCoord, evt.yCoord, evt.zCoord) && s.renderLiquid(meta)) {
				if (evt.renderPass == 1)
					this.renderWaterInBlock(evt.access, evt.xCoord, evt.yCoord, evt.zCoord, evt.block, meta, Tessellator.instance);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderWaterInBlock(IBlockAccess world, int x, int y, int z, Block block, int meta, Tessellator v5) {
		Block above = world.getBlock(x, y+1, z);
		if (above != Blocks.water && above != Blocks.flowing_water && !ReikaWorldHelper.hasAdjacentWater(world, x, y+1, z, false, false)) {
			boolean flag = ReikaWorldHelper.hasAdjacentWater(world, x, y, z, false, false);
			if (!flag) {
				for (int i = 2; i < 6 && !flag; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					int dx = x+dir.offsetX;
					int dy = y+dir.offsetY;
					int dz = z+dir.offsetZ;
					flag = flag || ReikaWorldHelper.hasAdjacentWater(world, dx, dy, dz, false, true);
				}
			}
			if (flag) {
				IIcon ico = Blocks.water.getIcon(world, x, y, z, 1);
				float u = ico.getMinU();
				float v = ico.getMinV();
				float du = ico.getMaxU();
				float dv = ico.getMaxV();
				//double h = 0.888;
				RenderBlocks.getInstance().blockAccess = world;
				double d2 = RenderBlocks.getInstance().getLiquidHeight(x, y, z, Material.water);
				double d3 = RenderBlocks.getInstance().getLiquidHeight(x, y, z + 1, Material.water);
				double d4 = RenderBlocks.getInstance().getLiquidHeight(x + 1, y, z + 1, Material.water);
				double d5 = RenderBlocks.getInstance().getLiquidHeight(x + 1, y, z, Material.water);
				v5.setColorOpaque_I(Blocks.water.colorMultiplier(world, x, y, z));
				v5.addVertexWithUV(x, y+d3, z+1, u, dv);
				v5.addVertexWithUV(x+1, y+d4, z+1, du, dv);
				v5.addVertexWithUV(x+1, y+d5, z, du, v);
				v5.addVertexWithUV(x, y+d2, z, u, v);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void monitorNewWorld(CreateSpawnPosition ev) {
		DragonAPICore.log("Registering creation of new world "+ev.world+" in "+ev.world.getSaveHandler().getWorldDirectory());
		ReikaWorldHelper.onWorldCreation(ev.world);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void tagDeathDrops(PlayerDropsEvent ev) {
		for (EntityItem ei : ev.drops) {
			ei.getEntityData().setString(ReikaItemHelper.PLAYER_DEATH_DROP_KEY, ev.entityPlayer.getUniqueID().toString());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void doCorrectBiomeFoliage(PlayerInteractEvent ev) {
		if (ev.action == Action.RIGHT_CLICK_BLOCK && DragonOptions.GRASSMEAL.getState()) {
			ItemStack is = ev.entityPlayer.getCurrentEquippedItem();
			if (ReikaItemHelper.matchStacks(is, ReikaItemHelper.bonemeal) && !ev.world.isRemote) {
				Block b = ev.world.getBlock(ev.x, ev.y, ev.z);
				int meta = ev.world.getBlockMetadata(ev.x, ev.y, ev.z);
				if (b == Blocks.grass) {
					BiomeGenBase biome = ev.world.getBiomeGenForCoords(ev.x, ev.z);
					WorldGenerator grass = biome.getRandomWorldGenForGrass(ev.world.rand);
					if (grass != null) {
						if (grass.generate(ev.world, ev.world.rand, ev.x, ev.y, ev.z)) {
							if (!ev.entityPlayer.capabilities.isCreativeMode)
								ev.entityPlayer.getCurrentEquippedItem().stackSize--;
							//ev.setCanceled(true);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void checkModChanges(WorldEvent.Load evt) {
		VersionTransitionTracker.instance.onWorldLoad(evt.world);
	}

	@SideOnly(Side.CLIENT)
	private void showF3Extras() {
		long amt = ReikaTextureHelper.binder.getTotalBytesLoaded();
		String pre = ReikaEngLibrary.getSIPrefix(amt);
		double base = ReikaMathLibrary.getThousandBase(amt);
		String sg = String.format("%.3f %sbytes in texture data", base, pre);
		Minecraft mc = Minecraft.getMinecraft();
		int len = FMLCommonHandler.instance().getBrandings(false).size();
		mc.ingameGUI.drawString(mc.fontRenderer, sg, mc.displayWidth/ReikaRenderHelper.getGUIScale()-10-mc.fontRenderer.getStringWidth(sg), 73+(len-4)*(2+mc.fontRenderer.FONT_HEIGHT), 0xffffff);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void xmasFog(EntityViewRenderEvent.FogColors evt) {
		World world = Minecraft.getMinecraft().theWorld;
		float f = SpecialDayTracker.instance.getXmasWeatherStrength(world);
		if (f > 0) {
			int c0 = ReikaColorAPI.RGBtoHex((int)(evt.red*255), (int)(evt.green*255), (int)(evt.blue*255));
			int c1 = WinterColorsEvent.getFogColor();
			f *= world.getSunBrightnessBody(ReikaRenderHelper.getPartialTickTime());
			int c = ReikaColorAPI.mixColors(c1, c0, f);

			evt.red = ReikaColorAPI.getRed(c)/255F;
			evt.green = ReikaColorAPI.getGreen(c)/255F;
			evt.blue = ReikaColorAPI.getBlue(c)/255F;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void xmasSky(SkyColorEvent evt) {
		World world = Minecraft.getMinecraft().theWorld;
		float f = SpecialDayTracker.instance.getXmasWeatherStrength(world);
		if (f > 0) {
			evt.color = ReikaColorAPI.mixColors(WinterColorsEvent.getSkyColor(), evt.color, f);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void forceFramebuffer(SettingsEvent.Save evt) {
		evt.settings.fboEnable = true;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void forceFramebuffer(SettingsEvent.Load evt) {
		evt.settings.fboEnable = true;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void collectAll(PlayerInteractEvent evt) {
		if (!evt.world.isRemote && evt.action == Action.LEFT_CLICK_BLOCK) {
			Key k = DragonOptions.getCollectKey();
			if (k != null && KeyWatcher.instance.isKeyDown(evt.entityPlayer, k)) {
				TileEntity te = evt.world.getTileEntity(evt.x, evt.y, evt.z);
				if (te instanceof IInventory) {
					boolean flag2 = false;
					ReikaChatHelper.clearChat((EntityPlayerMP)evt.entityPlayer);
					IInventory ii = (IInventory)te;
					for (int i = 0; i < 6; i++) {
						HashMap<Integer, ItemStack> map = ReikaInventoryHelper.getLocatedTransferrables(ForgeDirection.VALID_DIRECTIONS[i], ii);
						for (int slot : map.keySet()) {
							boolean flag = true;
							ItemStack is = map.get(slot);
							if (evt.entityPlayer.isSneaking())
								ReikaPlayerAPI.addOrDropItem(is, evt.entityPlayer);
							else
								flag = ReikaInventoryHelper.addToIInv(is, evt.entityPlayer.inventory);
							if (flag) {
								ii.setInventorySlotContents(slot, null);
								String s = "Collected "+is.stackSize+" "+is.getDisplayName();
								ReikaPacketHelper.sendStringPacket(DragonAPIInit.packetChannel, PacketIDs.STRINGPARTICLE.ordinal(), s, te);
								flag2 = true;
							}
						}
					}
					if (flag2) {
						ReikaSoundHelper.playSoundAtEntity(evt.world, evt.entityPlayer, "random.click", 1, 1.5F);
					}
					evt.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void modifyFireSpread(FireChanceEvent evt) {
		if (DragonOptions.BIOMEFIRE.getState()) {
			BiomeGenBase b = evt.getBiome();
			float humid = b.rainfall; //ranges from 0 (desert, nether, etc) to 1 (ocean) //ReikaBiomeHelper.getBiomeHumidity(b);
			evt.spreadChance *= biomeHumidityFlammability.getValue(humid); //*= 2-humid*1.5;  //1F/(0.5F+humid); //doubled for 0, unchanged for 0.5, halved for 1
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderUpdateHalos(EntityRenderingLoopEvent evt) {
		if (ChangePacketRenderer.isActive && evt.renderPass == 1) {
			ChangePacketRenderer.instance.render();
		}
	}

	@SubscribeEvent
	public void unloadDestroyableEntities(WorldEvent.Unload evt) {
		for (Entity e : ((List<Entity>)evt.world.loadedEntityList)) {
			if (e instanceof DestroyOnUnload) {
				((DestroyOnUnload)e).destroy();
			}
		}
	}

	@SubscribeEvent
	public void unloadChunkLightnings(ChunkEvent.Unload evt) {
		ReikaChunkHelper.clearUnloadableEntities(evt.getChunk());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fixRespirationFourPlusFog(EntityViewRenderEvent.FogColors evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		ItemStack helm = ep.getCurrentArmor(3);
		if (helm != null && ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.respiration, helm) > 3) {
			if (ep.isPotionActive(Potion.blindness)) {
				evt.red = evt.green = evt.blue = 0;
			}
			else if (ep.isInsideOfMaterial(Material.water)) {
				evt.blue = 1;
				evt.red = 0.6F;
				evt.green = 0.8F;
			}
			else if (ep.isInsideOfMaterial(Material.lava)) {
				evt.blue = 0.2F;
				evt.red = 1F;
				evt.green = 0.6F;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void fixRespirationFourPlusFog(EntityViewRenderEvent.FogDensity evt) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		ItemStack helm = ep.getCurrentArmor(3);
		if (helm != null && ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.respiration, helm) > 3) {
			if (ep.isInsideOfMaterial(Material.water) || ep.isInsideOfMaterial(Material.lava)) {
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
				evt.density = Math.min(evt.density, ep.isInsideOfMaterial(Material.lava) ? 0.1F : 0.0025F);
				if (ep.isPotionActive(Potion.blindness))
					evt.density = 0.9F;
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void protectNewPlayers(MobTargetingEvent.Pre evt) {
		if (evt.player.ticksExisted < 200 && DragonOptions.PROTECTNEW.getState()) { //10s
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void stillAllowPetTargeting(MobTargetingEvent.Pre evt) {
		if (ReikaEntityHelper.tameMobTargeting) {
			evt.setResult(Result.ALLOW);
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
			if (evt.Name.equals("transdimBlock") && evt.Ore.getItemDamage() > 0) { //prevent 4k lines from ender chests
				return;
			}
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
	public void onPlayerLogin(ClientLoginEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.resize(mc.displayWidth, mc.displayHeight); //shader pipeline fix
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGameLoaded(GameFinishedLoadingEvent evt) throws InterruptedException {
		this.checkRemoteAssetDownload();

		Minecraft mc = Minecraft.getMinecraft();

		//if (ModList.liteLoaderInstalled())
		if (!ReikaObfuscationHelper.isDeObfEnvironment())
			//if (ReikaJVMParser.isArgumentPresent("-DragonAPI_noAssetReload"))
			DirectResourceManager.getInstance().initToSoundRegistry();
		//else
		//	mc.refreshResources();

		if (ModList.NEI.isLoaded()) {
			NEIIntercept.instance.register();
			//NEIFontRendererHandler.instance.register();
		}
		DragonAPIInit.proxy.registerSidedHandlersGameLoaded();
		ReflectiveFailureTracker.instance.print();

		mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("note.harp"), 1, 1, 0, 0, 0));
		Thread.sleep(100);
		float f = (float)MusicKey.Cs5.getRatio(MusicKey.Fs4);
		mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("note.harp"), 1, 1, 0, 0, 0));
		mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("note.harp"), 1, f, 0, 0, 0));

		//move to load a world mc.resize(mc.displayWidth, mc.displayHeight);
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
		//GL11.glAlphaFunc(GL11.GL_GEQUAL, 1/255F);
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
			ReikaItemHelper.setDropper(evt.entityItem, evt.player);
			//ReikaPacketHelper.sendStringIntPacket(packetChannel, PacketIDs.ITEMDROPPER.ordinal(), new PacketTarget.DimensionTarget(evt.entityItem.worldObj), s, evt.entityItem.getEntityId());
		}
	}

	@SubscribeEvent
	public void tagDroppedItems(EntityJoinWorldEvent evt) {
		if (evt.entity instanceof EntityItem && evt.world.isRemote) {
			//ReikaJavaLibrary.pConsole("Sending clientside request for Entity ID "+evt.entity.getEntityId());
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.ITEMDROPPERREQUEST.ordinal(), PacketTarget.server, evt.entity.getEntityId());
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
					evt.markInvalid();
				}
				else if (out == null || out.getItem() == null) {
					DragonAPICore.logError("Found a null-output (or null-item output) smelting recipe! "+in+" > "+null+"! This is invalid!");
					Thread.dumpStack();
					evt.markInvalid();
				}
				else if (!ReikaItemHelper.verifyItemStack(in, true)) {
					DragonAPICore.logError("Found a smelting recipe with an invalid input!");
					Thread.dumpStack();
					evt.markInvalid();
				}
				else if (!ReikaItemHelper.verifyItemStack(out, true)) {
					DragonAPICore.logError("Found a smelting recipe with an invalid output!");
					Thread.dumpStack();
					evt.markInvalid();
				}
			}
			catch (Exception e) {
				DragonAPICore.logError("Could not parse smelting recipe: ");
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
