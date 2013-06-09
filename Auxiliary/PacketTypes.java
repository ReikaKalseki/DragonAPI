package Reika.DragonAPI.Auxiliary;

public enum PacketTypes {

	DATA(),
	SOUND();

	private PacketTypes() {

	}

	public static PacketTypes getPacketType(int type) {
		return PacketTypes.values()[type];
	}

}
