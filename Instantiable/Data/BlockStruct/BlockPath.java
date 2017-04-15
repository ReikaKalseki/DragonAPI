package Reika.DragonAPI.Instantiable.Data.BlockStruct;

import java.util.LinkedList;
import java.util.List;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class BlockPath {

	private final LinkedList<Coordinate> path = new LinkedList();

	public BlockPath() {

	}

	public BlockPath(List<Coordinate> li) {
		path.addAll(li);
	}

}
