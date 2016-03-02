package Reika.DragonAPI.Exception;



public class EnvironmentSanityException extends DragonAPIException {

	public EnvironmentSanityException(ErrorType type, Object... data) {
		message.append(type.getString(data));
		if (type == ErrorType.UNPARSEABLE)
			this.initCause((Exception)data[1]);
		this.crash();
	}

	public static enum ErrorType {
		NULLREG(),
		NULLENTRY(),
		IDMISMATCH(),
		INVALIDVALUE(),
		UNPARSEABLE();

		public String getString(Object... data) {
			switch(this) {
				case NULLREG:
					return data[0]+" ("+data[0].getClass()+") was assigned a null name in the GameRegistry!";
				case NULLENTRY:
					return "Null was registered to the GameRegistry as '"+data[0]+"'!";
				case IDMISMATCH:
					return data[0]+" ("+data[0].getClass()+") occupies an ID ("+data[1]+") that does not match its stored value ("+data[2]+")!";
				case INVALIDVALUE:
					return data[0]+" ("+data[0].getClass()+") returns an invalid ("+data[1]+") value for a critical field or function ('"+data[2]+"')!";
				case UNPARSEABLE:
					return data[0]+" ("+data[0].getClass()+") throws an exception ("+data[1]+") when trying to parse it for '"+data[2]+"'! This is almost certainly caused by an illegal internal state.";
			}
			return "";
		}
	}

}
