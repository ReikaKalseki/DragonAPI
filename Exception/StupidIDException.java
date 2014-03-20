package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class StupidIDException extends DragonAPIException {

	public StupidIDException(DragonAPIMod mod, int ID) {
		message.append(mod.getDisplayName()+" was not installed correctly:\n");
		message.append("ID "+ID+" is invalid, as it is "+this.getError(ID)+".\n");
		message.append("This is NOT a mod bug. Do not post it to the mod website");
		message.append("or you will look extremely foolish.");
		this.crash();
	}

	private String getError(int id) {
		return id < 0 ? "negative" : id > 4095 ? "too large" : "";
	}

}
