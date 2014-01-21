package Reika.DragonAPI.Base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public abstract class BlockCustomLeaf extends BlockLeaves {

	/** For fast/fancy graphics */
	protected Icon[] icon = new Icon[2];

	protected final Random rand = new Random();

	protected BlockCustomLeaf(int ID) {
		super(ID);
		this.setCreativeTab(this.showInCreative() ? this.getCreativeTab() : null);
		this.setStepSound(Block.soundGrassFootstep);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			this.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.setTickRandomly(this.decays());
	}

	public abstract boolean decays();

	public abstract boolean showInCreative();

	public abstract CreativeTabs getCreativeTab();

	@Override
	public final Icon getIcon(int par1, int par2)
	{
		return icon[this.getOpacityIndex()];
	}

	private final int getOpacityIndex() {
		graphicsLevel = Minecraft.getMinecraft().gameSettings.fancyGraphics;
		return graphicsLevel ? 0 : 1;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return 60;
	}

	@Override
	public final void updateTick(World world, int x, int y, int z, Random par5Random)
	{
		if (this.decays() && this.shouldTryDecay(world, x, y, z, world.getBlockMetadata(x, y, z))) {
			this.decay(world, x, y, z, par5Random);
		}
	}

	public abstract boolean shouldTryDecay(World world, int x, int y, int z, int meta);

	protected void decay(World world, int x, int y, int z, Random par5Random) {
		int r = 4;
		boolean decay = true;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int id = world.getBlockId(x+i, y+j, z+k);
					int meta = world.getBlockMetadata(x+i, y+j, z+k);
					if (id == Block.wood.blockID || ModWoodList.isModWood(id, meta)) {
						decay = false;
						i = j = k = r+1;
					}
				}
			}
		}
		int meta = world.getBlockMetadata(x, y, z);
		if (decay) {
			this.dropBlockAsItemWithChance(world, x, y, z, meta, 1, 0);
			world.setBlock(x, y, z, 0);
		}
	}

	@Override
	public final void beginLeavesDecay(World world, int x, int y, int z)
	{
		if (this.decays()) {

		}
	}

	@Override
	public final void registerIcons(IconRegister ico)
	{
		icon[0] = ico.registerIcon(this.getFancyGraphicsIcon());
		icon[1] = ico.registerIcon(this.getFastGraphicsIcon());
	}

	public abstract String getFastGraphicsIcon();
	public abstract String getFancyGraphicsIcon();

}
