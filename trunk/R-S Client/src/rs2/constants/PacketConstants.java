package rs2.constants;

/**
 * This class contains all the names for the documented packets to easily reference them when needed.
 * @author Galkon
 */
public class PacketConstants {
	
	/**
	 * The sent instance.
	 */
	public static Sent sent = new Sent();

	/**
	 * Returns the sent instance.
	 * @return
	 */
	public static Sent getSent() {
		return sent;
	}

	/**
	 * The received instance.
	 */
	public static Received received = new Received();

	/**
	 * Returns the received instance.
	 * @return
	 */
	public static Received getReceived() {
		return received;
	}

	/**
	 * Contains all constants for packets that are received from the server.
	 * @author Galkon
	 */
	public static class Received {

		public final int INTERFACE_MEDIA_UPDATE = 8;
		public final int PACKET_24 = 24;
		public final int SHOW_NUMERIC_INPUT = 27;
		public final int SINGLE_ITEM_UPDATE = 34;
		public final int ALL_ITEMS_UPDATE = 53;
		public final int NPC_UPDATE = 65;
		public final int PACKET_68 = 68;
		public final int INTERFACE_SCROLLPOS_UPDATE = 79;
		public final int PLAYER_UPDATE = 81;
		public final int PACKET_85 = 85;
		public final int SET_OPEN_INTERFACE = 97;
		public final int INTERFACE_TEXT_COLOR_UPDATE = 122;
		public final int INTERFACE_TEXT_UPDATE = 126;
		public final int SET_OVERLAY_INTERFACE = 142;
		public final int PACKET_171 = 171;
		public final int SHOW_STRING_INPUT = 187;
		public final int PACKET_196 = 196;
		public final int SET_CHAT_MODES = 206;
		public final int SET_DIALOG_ID = 218;
		public final int CLOSE_INTERFACES = 219;
		public final int PACKET_221 = 221;
		public final int INTERFACE_MODEL_UPDATE = 230;
		public final int WEIGHT_UPDATE = 240;
		public final int UPDATE_INTERFACE_MODEL_ZOOM = 246;
		public final int SET_OPEN_AND_OVERLAY_INTERFACES = 248;
		public final int PACKET_249 = 249;
		public final int PACKET_254 = 254;

	}

	public static class Sent {
	
		public final int SEND_CHAT_MODES = 95;
		public final int IDLE_LOGOUT = 202;
		public final int DELETE_FRIEND = 215;

	}

}
