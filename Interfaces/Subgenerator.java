package Reika.DragonAPI.Interfaces;


/** Implement this on WorldGenerator objects that are called as part of a parent generator (usually an IWG).
 * 
 *  This prevents it from being treated as its own generator in various trackers (eg profilers). */
public interface Subgenerator {

	/** The parent generator this is a submodule of. */
	public Object getParentGenerator();

}
