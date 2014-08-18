/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import Reika.DragonAPI.Auxiliary.CustomSoundHandler;
import Reika.DragonAPI.Instantiable.EnumSound;
import Reika.DragonAPI.Interfaces.SoundEnum;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReikaSoundHelper {

	public static void playBreakSound(World world, int x, int y, int z, Block b) {
		SoundType s = b.stepSound;
		String f = s.getBreakSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playStepSound(World world, int x, int y, int z, Block b) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playPlaceSound(World world, int x, int y, int z, Block b) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();//s.getPlaceSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playBreakSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		SoundType s = b.stepSound;
		String f = s.getBreakSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playStepSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playPlaceSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();//s.getPlaceSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playSoundAtBlock(World world, int x, int y, int z, String snd, float vol, float pit) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, snd, vol, pit);
	}

	public static void playSoundAtBlock(World world, int x, int y, int z, String snd) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, snd, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSoundAtBlock(String file, TileEntity te) {
		playCustomSoundAtBlock(file, te.xCoord, te.yCoord, te.zCoord);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSoundAtBlock(String file, int x, int y, int z) {
		playCustomSoundAtBlock(file, x, y, z, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSoundAtBlock(String file, int x, int y, int z, float vol, float pitch) {
		playCustomSound(file, x+0.5, y+0.5, z+0.5, vol, pitch);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSound(String file, Entity e, float vol, float pitch) {
		playCustomSound(file, e.posX, e.posY, e.posZ, vol, pitch);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSound(String file, double x, double y, double z, float vol, float pitch) {
		ResourceLocation rl = CustomSoundHandler.instance.getSoundResource(file);
		FMLClientHandler.instance().getClient().getSoundHandler().playSound(new PositionedSoundRecord(rl, (float)x, (float)y, (float)z, vol, pitch));
	}

	@SideOnly(Side.CLIENT)
	public static void playSound(SoundEnum s, double x, double y, double z, float vol, float pitch) {
		FMLClientHandler.instance().getClient().getSoundHandler().playSound(new EnumSound(s, x, y, z, vol, pitch));
	}
}
