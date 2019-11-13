package Reika.DragonAPI.Exception;


public class UnreachableCodeException extends DragonAPIException {

	public UnreachableCodeException() {
		this(null);
	}

	public UnreachableCodeException(String msg) {
		message.append("A block of code that was supposed to be unreachable has been executed!\n");
		message.append("This is indicative of some sort of error, with possible causes ranging from version mismatches to programming oversights.\n");
		if (msg != null)
			message.append(msg);
		this.crash();
	}

}
