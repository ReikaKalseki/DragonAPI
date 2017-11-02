package Reika.DragonAPI.Interfaces.Entity;


/** Ensure your entity writes isDead to NBT! */
public interface DestroyOnUnload {

	/** Usually calls setDead */
	public void destroy();

}
