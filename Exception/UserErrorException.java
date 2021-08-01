package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class UserErrorException extends DragonAPIException {

	public UserErrorException() {
		super();
	}

	public UserErrorException(Exception e) {
		super(e);
	}

	protected final void applyDNP(DragonAPIMod mod) {
		String doc = mod.getDocumentationSite().toString();
		String bug = mod.getBugSite() == null ? "" : mod.getBugSite().toString();
		if (bug.isEmpty() || doc.equals(bug)) {
			message.append("This is not a "+mod.getDisplayName()+" bug. Do not post it to "+doc+" unless you are really stuck.");
		}
		else {
			message.append("This is not a "+mod.getDisplayName()+" bug. Do not post it to "+doc+" or "+bug+" unless you are really stuck.");
		}
	}

}
