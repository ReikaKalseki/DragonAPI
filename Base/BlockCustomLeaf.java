/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.Search.TerminationCondition;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public abstract class BlockCustomLeaf extends BlockLeaves {

	/** For fast/fancy graphics */
	protected IIcon[][] icon = new IIcon[16][2];

	protected final Random rand = new Random();

	protected BlockCustomLeaf() {
		this(false);
	}

	protected BlockCustomLeaf(boolean tick) {
		super();
		this.setCreativeTab(this.showInCreative() ? this.getCreativeTab() : null);
		this.setStepSound(soundTypeGrass);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			this.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.setTickRandomly(tick || this.decays() || this.shouldRandomTick());
	}

	@Override
	public final boolean getCanBlockGrass() {
		return false;
	}

	/** Overridden to allow conditional disabling of mod leaf control hacks, like the one in RandomThings. */
	@Override
	public final void onNeighborBlockChange(World world, int x, int y, int z, Block neighborID) {
		this.onBlockUpdate(world, x, y, z);
		if (this.allowModDecayControl()) {
			super.onNeighborBlockChange(world, x, y, z, neighborID);
		}
		else {

		}
	}

	@Override
	public String[] func_150125_e() {
		return new String[]{this.getUnlocalizedName()};
	}

	protected void onBlockUpdate(World world, int x, int y, int z) {

	}

	public abstract boolean shouldRandomTick();

	public abstract boolean decays();

	public abstract boolean isNatural();

	public abstract boolean allowModDecayControl();

	public abstract boolean showInCreative();

	public abstract CreativeTabs getCreativeTab();

	@Override
	public IIcon getIcon(int par1, int par2) {
		return icon[par2][this.getOpacityIndex()];
	}

	protected final int getOpacityIndex() {
		field_150121_P = Minecraft.getMinecraft().gameSettings.fancyGraphics;
		return field_150121_P ? 0 : 1;
	}

	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		return 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 60;
	}

	@Override
	public final void updateTick(World world, int x, int y, int z, Random par5Random) {
		int meta = world.getBlockMetadata(x, y, z);
		//ReikaJavaLibrary.pConsole(Block.getIdFromBlock(this)+" @ "+x+", "+y+", "+z+" : "+this.decays()+"&"+this.shouldTryDecay(world, x, y, z, meta));
		boolean flag = false;
		if (this.decays() && this.shouldTryDecay(world, x, y, z, meta)) {
			flag = this.decay(world, x, y, z, par5Random);
		}
		if (!flag)
			this.onRandomUpdate(world, x, y, z, par5Random);
	}

	protected void onRandomUpdate(World world, int x, int y, int z, Random r) {

	}

	public abstract boolean shouldTryDecay(World world, int x, int y, int z, int meta);

	protected boolean decay(World world, final int x, final int y, final int z, Random par5Random) {
		TerminationCondition t = new TerminationCondition(){

			@Override
			public boolean isValidTerminus(World world, int dx, int dy, int dz) {
				return BlockCustomLeaf.this.isValidLog(world, x, y, z, dx, dy, dz);
			}
		};

		PropagationCondition c = new PropagationCondition(){

			@Override
			public boolean isValidLocation(IBlockAccess world, int dx, int dy, int dz) {
				return BlockCustomLeaf.this.isMatchingLeaf(world, x, y, z, dx, dy, dz) || BlockCustomLeaf.this.isValidLog(world, x, y, z, dx, dy, dz);
			}

		};

		Search s = new Search(x, y, z);
		s.limit = BlockBox.block(x, y, z).expand(this.getMaximumLogSearchRadius());
		s.depthLimit = this.getMaximumLogSearchDepth();
		s.complete(world, c, t);
		boolean decay = s.getResult().isEmpty();
		if (decay) {
			this.dropBlockAsItemWithChance(world, x, y, z, world.getBlockMetadata(x, y, z), 1, 0);
			world.setBlockToAir(x, y, z);
		}
		return decay;
	}

	@Override
	public final void beginLeavesDecay(World world, int x, int y, int z) {
		if (this.decays()) {

		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			icon[i][0] = ico.registerIcon(this.getFancyGraphicsIcon(i));
			icon[i][1] = ico.registerIcon(this.getFastGraphicsIcon(i));
		}
	}

	public abstract String getFastGraphicsIcon(int meta);
	public abstract String getFancyGraphicsIcon(int meta);

	public abstract boolean isMatchingLeaf(IBlockAccess iba, int thisX, int thisY, int thisZ, int lookX, int lookY, int lookZ);
	public abstract boolean isValidLog(IBlockAccess iba, int thisX, int thisY, int thisZ, int lookX, int lookY, int lookZ);

	public abstract int getMaximumLogSearchRadius();
	public abstract int getMaximumLogSearchDepth();

}
