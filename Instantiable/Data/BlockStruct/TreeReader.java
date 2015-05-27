/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import Reika.ChromatiCraft.API.TreeGetter;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.TwilightForestHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public final class TreeReader extends BlockArray {

	private int leafCount;
	private int logCount;

	private ReikaTreeHelper vanilla;
	private ModWoodList wood;
	private ReikaDyeHelper dyeTree;

	private final Block dyeLeafID;
	private final Block rainbowLeafID;
	private boolean isDyeTree = false;
	private boolean isRainbowTree = false;
	private int dyeMeta = -1;

	public TreeReader() {
		super();
		if (ModList.CHROMATICRAFT.isLoaded()) {
			dyeLeafID = TreeGetter.getNaturalDyeLeafID();
			rainbowLeafID = TreeGetter.getRainbowLeafID();
		}
		else {
			dyeLeafID = null;
			rainbowLeafID = null;
		}
	}

	private void checkAndAddDyeTree(World world, int x, int y, int z, int ox, int oy, int oz, int depth) {
		if (Math.abs(x-ox) > 6 || Math.abs(y-oy) > 14 || Math.abs(z-oz) > 6)
			return;
		if (depth > 150)
			return;
		Block id = world.getBlock(x, y, z);
		if (id == Blocks.air)
			return;
		int meta = world.getBlockMetadata(x, y, z);
		if (this.hasBlock(x, y, z))
			return;
		//ReikaJavaLibrary.pConsole(id+":"+meta);
		if (id != Blocks.log && id != Blocks.log2 && id != dyeLeafID && !ModWoodList.isModWood(id, meta))
			return;
		if (id == dyeLeafID && dyeMeta != -1 && dyeMeta != meta)
			return;

		if (id != dyeLeafID) {
			ModWoodList wood = ModWoodList.getModWood(id, meta);
			ReikaTreeHelper van = ReikaTreeHelper.getTree(id, meta);
			//ReikaJavaLibrary.pConsole(wood+"/"+this.wood+"  :  "+van+"/"+vanilla);
			if (this.wood == null && vanilla == null) {
				//ReikaJavaLibrary.pConsole(this);
				this.setModTree(wood);
				this.setTree(van);
			}
			else {
				if (wood != this.wood || van != vanilla)
					return;
			}
		}
		if (vanilla == null && wood == null)
			return;

		if (id == dyeLeafID) {
			isDyeTree = true;
			dyeMeta = meta;
			leafCount++;
		}
		else
			logCount++;
		this.addBlockCoordinate(x, y, z);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					this.checkAndAddDyeTree(world, x+i, y+j, z+k, ox, oy, oz, depth+1);
				}
			}
		}
	}

	public void checkAndAddDyeTree(World world, int x, int y, int z) {
		this.checkAndAddDyeTree(world, x, y, z, x, y, z, 0);
	}

	public void checkAndAddRainbowTree(World world, int x, int y, int z) {
		this.checkAndAddRainbowTree(world, x, y, z, 0);
	}

	private void checkAndAddRainbowTree(World world, int x, int y, int z, int depth) {
		if (this.hasBlock(x, y, z))
			return;
		if (depth > 120)
			return;
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		if (id != Blocks.log && id != Blocks.log2 && id != rainbowLeafID && !ModWoodList.isModWood(id, meta))
			return;

		this.addBlockCoordinate(x, y, z);

		if (id == Blocks.log || id == Blocks.log2 || ModWoodList.isModWood(id, meta))
			logCount++;
		else {
			leafCount++;
			isRainbowTree = true;
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					this.checkAndAddRainbowTree(world, x+i, y+j, z+k, depth+1);
				}
			}
		}
	}

	public boolean isDyeTree() {
		return isDyeTree;
	}

	public boolean isRainbowTree() {
		return isRainbowTree;
	}

	public int getDyeTreeMeta() {
		return dyeMeta;
	}

	public void addTree(World world, int x, int y, int z, Block blockID, int blockMeta) {
		this.addTree(world, x, y, z, blockID, blockMeta, 0);
	}

	private void addTree(World world, int x, int y, int z, Block blockID, int blockMeta, int depth) {
		Block id = world.getBlock(x, y, z);
		if (id == Blocks.air)
			return;
		int meta = world.getBlockMetadata(x, y, z);
		if (id != blockID)
			return;
		if (meta != blockMeta)
			return;
		if (id != Blocks.log || id != Blocks.log2 && !ModWoodList.isModWood(id, meta))
			return;
		if (this.hasBlock(x, y, z))
			return;
		this.addBlockCoordinate(x, y, z);

		try {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						if (i != 0 || j != 0 || k != 0)
							this.addTree(world, x+i, y+j, z+k, blockID, blockMeta, depth+1);
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void addModTree(World world, int x, int y, int z) {
		if (wood == ModWoodList.SEQUOIA) {
			ReikaJavaLibrary.pConsole("Use sequoia handler!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		if (wood == ModWoodList.DARKWOOD) {
			ReikaJavaLibrary.pConsole("Use darkwood handler!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		if (wood == ModWoodList.IRONWOOD) {
			ReikaJavaLibrary.pConsole("Use ironwood handler!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		if (wood == null) {
			throw new MisuseException("You must set the mod tree type!");
		}
		this.addModTree(world, x, y, z, x, y, z, 0);
	}

	private void addModTree(World world, int x, int y, int z, int x0, int y0, int z0, int depth) {
		if (Math.abs(x-x0) > 24 || Math.abs(z-z0) > 24)
			return;
		if (this.hasBlock(x, y, z))
			return;
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ModWoodList get = ModWoodList.getModWood(id, meta);
		ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);

		//ReikaJavaLibrary.pConsole("ID:"+id+"  GET: "+get+"   WOOD: "+wood+"    LEAF: "+leaf);

		if (get != wood && leaf != wood)
			return;

		this.addBlockCoordinate(x, y, z);
		//ReikaJavaLibrary.pConsole(id+":"+get+":"+leaf);
		if (get == wood)
			logCount++;
		else if (leaf == wood)
			leafCount++;

		try {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						this.addModTree(world, x+i, y+j, z+k, x0, y0, z0, depth+1);
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	private void addTree(World world, int x, int y, int z, int x0, int y0, int z0, int depth) {
		if (vanilla == null) {
			throw new MisuseException("You must set the tree type!");
		}
		if (Math.abs(x-x0) > 24) //For magic forests
			return;
		if (Math.abs(z-z0) > 24)
			return;
		if (this.hasBlock(x, y, z))
			return;
		Block id = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ReikaTreeHelper get = ReikaTreeHelper.getTree(id, meta);
		ReikaTreeHelper leaf = ReikaTreeHelper.getTreeFromLeaf(id, meta);
		if (get != vanilla && leaf != vanilla)
			return;

		//ReikaJavaLibrary.pConsoleSideOnly("GET: "+get+"     LEAF: "+leaf+"    ## LOG: "+logCount+"    LEAVES: "+leafCount, Side.SERVER);

		this.addBlockCoordinate(x, y, z);
		if (get == vanilla)
			logCount++;
		else if (leaf == vanilla)
			leafCount++;

		//ReikaJavaLibrary.pConsole(depth+":"+maxDepth, Side.SERVER);
		try {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						this.addTree(world, x+i, y+j, z+k, x0, y0, z0, depth+1);
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(depth);
			e.printStackTrace();
		}
	}

	public void addTree(World world, int x, int y, int z) {
		this.addTree(world, x, y, z, x, y, z, 0);
	}

	/** For Natura's massive redwood trees. Warning: may lag-spike! */
	public void addSequoia(World world, int x, int y, int z, boolean debug) {
		this.setModTree(ModWoodList.SEQUOIA);
		int r = 24;
		int minx = x-r;
		int maxx = x+r;
		int minz = z-r;
		int maxz = z+r;

		int yr = 16;

		for (int j = y; j <= y+yr; j++) {
			for (int i = minx; i <= maxx; i++) {
				for (int k = minz; k <= maxz; k++) {
					Block id = world.getBlock(i, j, k);
					int meta = world.getBlockMetadata(i, j, k);
					ModWoodList get = ModWoodList.getModWood(id, meta);
					ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);

					if (get == ModWoodList.SEQUOIA) {
						logCount++;
						this.addBlockCoordinate(i, j, k);
					}
					else if (leaf == ModWoodList.SEQUOIA) {
						leafCount++;
						this.addBlockCoordinate(i, j, k);
					}
				}
			}
		}
	}

	/** For Highlands's ironwood trees. Warning: may lag-spike! */
	public void addIronwood(World world, int x, int y, int z, boolean debug) {
		this.setModTree(ModWoodList.IRONWOOD);
		int r = 24;
		int minx = x-r;
		int maxx = x+r;
		int minz = z-r;
		int maxz = z+r;

		int yr = 16;

		for (int j = y; j <= y+yr; j++) {
			for (int i = minx; i <= maxx; i++) {
				for (int k = minz; k <= maxz; k++) {
					Block id = world.getBlock(i, j, k);
					int meta = world.getBlockMetadata(i, j, k);
					ModWoodList get = ModWoodList.getModWood(id, meta);
					ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);

					if (get == ModWoodList.IRONWOOD) {
						logCount++;
						this.addBlockCoordinate(i, j, k);
					}
					else if (leaf == ModWoodList.IRONWOOD) {
						leafCount++;
						this.addBlockCoordinate(i, j, k);
					}
				}
			}
		}
	}

	/** For Twilight's dark forests. */
	public void addDarkForest(World world, int x, int y, int z, int minx, int maxx, int minz, int maxz, boolean debug) {
		this.setModTree(ModWoodList.DARKWOOD);
		for (int j = y-24; j <= y+24; j++) {
			for (int i = minx; i <= maxx; i++) {
				for (int k = minz; k <= maxz; k++) {
					Block id = world.getBlock(i, j, k);
					int meta = world.getBlockMetadata(i, j, k);
					ModWoodList get = ModWoodList.getModWood(id, meta);
					ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);

					if (get == ModWoodList.DARKWOOD) {
						logCount++;
						this.addBlockCoordinate(i, j, k);
					}
					else if (leaf == ModWoodList.DARKWOOD) {
						leafCount++;
						this.addBlockCoordinate(i, j, k);
					}
				}
			}
		}
	}

	@Deprecated
	public void addGenerousTree(World world, int x, int y, int z, int dw) {
		if (this.hasBlock(x, y, z))
			return;
		try {
			Block id = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			Material mat = ReikaWorldHelper.getMaterial(world, x, y, z);
			ModWoodList wood = ModWoodList.getModWood(id, meta);
			if (wood == ModWoodList.SEQUOIA) {
				ReikaJavaLibrary.pConsole("Use sequoia handler for "+id+":"+meta+"!");
				ReikaJavaLibrary.dumpStack();
				return;
			}
			//ItemStack leaf = wood.getCorrespondingLeaf();
			if (id == Blocks.log || id == Blocks.log2 || wood != null || id == TwilightForestHandler.BlockEntry.TREECORE.getBlock()) {
				this.addBlockCoordinate(x, y, z);
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							if (!this.hasBlock(x+i, y+j, z+k)) {
								Material read = ReikaWorldHelper.getMaterial(world, x+i, y+j, z+k);
								Block readid = world.getBlock(x+i, y+j, z+k);
								int readmeta = world.getBlockMetadata(x+i, y+j, z+k);
								if (read == Material.leaves) {
									Block leafID = readid;
									int leafMeta = readmeta;
									ModWoodList leaf = ModWoodList.getModWoodFromLeaf(leafID, leafMeta);
									if (leaf == wood)
										//this.recursiveAddWithBoundsMetadata(world, x+i, y+j, z+k, leafID, leafMeta, x-dw, 0, z-dw, x+dw, 256, z+dw);
										this.addGenerousTree(world, x+i, y+j, z+k, dw);
									else {
										ReikaTreeHelper tree = ReikaTreeHelper.getTreeFromLeaf(leafID, leafMeta);
										if (tree != null) {
											this.addGenerousTree(world, x+i, y+j, z+k, dw);
										}
									}
								}
								else if (readid == Blocks.log || readid == Blocks.log2 || ModWoodList.getModWood(readid, readmeta) == wood)
									this.addGenerousTree(world, x+i, y+j, z+k, dw);
							}
						}
					}
				}
			}
			else if (mat == Material.leaves) {
				ModWoodList leaf = ModWoodList.getModWoodFromLeaf(id, meta);
				ReikaTreeHelper tree = ReikaTreeHelper.getTreeFromLeaf(id, meta);
				if (leaf != null) {
					this.addBlockCoordinate(x, y, z);
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							for (int k = -1; k <= 1; k++) {
								this.addGenerousTree(world, x+i, y+j, z+k, dw);
							}
						}
					}
				}
				else if (tree != null) {
					this.addBlockCoordinate(x, y, z);
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							for (int k = -1; k <= 1; k++) {
								this.addGenerousTree(world, x+i, y+j, z+k, dw);
							}
						}
					}
				}
			}
		}
		catch (StackOverflowError e) {
			this.throwOverflow(0);
			e.printStackTrace();
		}
	}

	public int getNumberLeaves() {
		return leafCount;
	}

	public int getNumberLogs() {
		return logCount;
	}

	public void reset() {
		logCount = 0;
		leafCount = 0;
		vanilla = null;
		wood = null;
		isDyeTree = false;
		dyeMeta = -1;
	}

	public void setTree(ReikaTreeHelper tree) {
		vanilla = tree;
	}

	public void setModTree(ModWoodList tree) {
		wood = tree;
	}

	@Override
	public Coordinate getNextAndMoveOn() {
		Coordinate next = super.getNextAndMoveOn();
		if (this.isEmpty())
			this.reset();
		return next;
	}

	public boolean isValidTree() {
		if (wood == ModWoodList.SEQUOIA)
			return true;
		return this.getNumberLeaves() >= ReikaTreeHelper.TREE_MIN_LEAF && this.getNumberLogs() >= ReikaTreeHelper.TREE_MIN_LOG;
	}

	public ReikaTreeHelper getVanillaTree() {
		return vanilla;
	}

	public ModWoodList getModTree() {
		return wood;
	}

	public boolean isModTree() {
		return this.getModTree() != null;
	}

	public boolean isVanillaTree() {
		return this.getVanillaTree() != null;
	}

	@Override
	public TreeReader copy() {
		TreeReader copy = new TreeReader();

		copy.refWorld = refWorld;
		copy.liquidMat = liquidMat;
		copy.overflow = overflow;
		copy.blocks.clear();
		copy.blocks.addAll(blocks);

		copy.leafCount = leafCount;
		copy.logCount = logCount;

		copy.vanilla = vanilla;
		copy.wood = wood;
		copy.dyeTree = dyeTree;

		copy.isDyeTree = isDyeTree;
		copy.dyeMeta = dyeMeta;

		return copy;
	}

}
