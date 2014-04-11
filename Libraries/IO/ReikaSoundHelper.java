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

import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.world.World;

public class ReikaSoundHelper {

	public static void playBreakSound(World world, int x, int y, int z, Block b) {
		StepSound s = b.stepSound;
		String f = s.getBreakSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playStepSound(World world, int x, int y, int z, Block b) {
		StepSound s = b.stepSound;
		String f = s.getStepSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playPlaceSound(World world, int x, int y, int z, Block b) {
		StepSound s = b.stepSound;
		String f = s.getPlaceSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playBreakSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		StepSound s = b.stepSound;
		String f = s.getBreakSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playStepSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		StepSound s = b.stepSound;
		String f = s.getStepSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playPlaceSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		StepSound s = b.stepSound;
		String f = s.getPlaceSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playSoundAtBlock(World world, int x, int y, int z, String snd, float vol, float pit) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, snd, vol, pit);
	}

	public static void playSoundAtBlock(World world, int x, int y, int z, String snd) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, snd, 1, 1);
	}
}
