package rs2.constants;

public class Constants {

	/**
	 * The connection address.
	 */
	public final static String HOST_ADDRESS = "127.0.0.1";

	/**
	 * The connection port.
	 */
	public final static int PORT = 43594;
	
	/**
	 * Are we connecting to an update server?
	 */
	public final static boolean UPDATE_SERVER_ENABLED = false;

	/**
	 * Are we checking for CRC and version matching?
	 */
	public final static boolean CHECK_VERSION_AND_CRC = true;
	
	/**
	 * Is the text censor on?
	 */
	public final static boolean CENSOR_TEXT = false;

	/**
	 * The time it takes to idle logout (default: 5000).
	 * Set to -1 to never idle logout.
	 */
	public final static int IDLE_LOGOUT_TIME = 5000;

	/**
	 * The client version (default: 317).
	 */
	public final static int CLIENT_VERSION = 317;

	/**
	 * Should we check for lent variables?
	 */
	public final static boolean LENT_ITEMS = false;

	/**
	 * Does the client take antibot precautions?
	 * If set to true, floor colors will have a hue and
	 * lightness offset when the map region is built,
	 * and the camera position/rotation, minimap rotation/zoom
	 * will have an offset upon login and will be offset in
	 * mainGameProcessor().
	 */
	public final static boolean BOT_RANDOMIZATION = false;

	/**
	 * Prayer headicons.
	 */
	public final static int PROTECT_FROM_MELEE = 0;
	public final static int PROTECT_FROM_RANGED = 1;
	public final static int PROTECT_FROM_MAGIC = 2;
	public final static int RETRIBUTION = 3;
	public final static int SMITE = 4;
	public final static int REDEMPTION = 5;
	public final static int PROTECT_FROM_MAGIC_AND_RANGED = 6;

}
