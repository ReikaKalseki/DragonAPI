package Reika.DragonAPI.Exception;


public class WorldSanityException extends DragonAPIException {

	public WorldSanityException(String msg) {
		super();
		message.append("The world state has become seriously invalid, such that continuing the game is not feasible.\n");
		message.append(msg);
	}

}
