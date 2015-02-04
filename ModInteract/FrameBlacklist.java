package Reika.DragonAPI.ModInteract;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockKey;

import com.amadornes.framez.api.movement.BlockMovementType;
import com.amadornes.framez.api.movement.HandlingPriority;
import com.amadornes.framez.api.movement.HandlingPriority.Priority;
import com.amadornes.framez.api.movement.IMovementHandler;
import com.amadornes.framez.api.movement.IMovingBlock;

public class FrameBlacklist {

	private static final HashSet<BlockKey> blacklist = new HashSet();

	public static void blacklistBlock(Block b) {
		blacklistBlock(new BlockKey(b));
	}

	public static void blacklistBlock(Block b, int meta) {
		blacklistBlock(new BlockKey(b, meta));
	}

	private static void blacklistBlock(BlockKey bk) {
		blacklist.add(bk);
	}

	public static class FramezHandler implements IMovementHandler {

		@Override
		@HandlingPriority(Priority.HIGH)
		public boolean handleStartMoving(IMovingBlock block) {
			return blacklist.contains(new BlockKey(block.getBlock(), block.getMetadata()));
		}

		@Override
		@HandlingPriority(Priority.HIGH)
		public boolean handleFinishMoving(IMovingBlock block) {
			return blacklist.contains(new BlockKey(block.getBlock(), block.getMetadata()));
		}

		@Override
		@HandlingPriority(Priority.HIGH)
		public BlockMovementType getMovementType(World world, Integer x, Integer y, Integer z) {
			Block b = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			return blacklist.contains(new BlockKey(b, meta)) ? BlockMovementType.UNMOVABLE : null;
		}

	}

}
