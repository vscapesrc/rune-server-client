package rs2.constants;

public class Constants {

	/**
	 * The server address you're connecting to.
	 */
	public final static String HOST_ADDRESS = "127.0.0.1";

	/**
	 * The port you're connecting to.
	 */
	public final static int PORT = 43594;
	
	/**
	 * Are we connecting to an update server?
	 */
	public final static boolean UPDATE_SERVER_ENABLED = false;
	
	/**
	 * Is the text censor on?
	 */
	public final static boolean CENSOR_TEXT = false;

	/**
	 * The time it takes to idle logout.
	 * Set to -1 to never idle logout.
	 */
	public final static int IDLE_LOGOUT_TIME = -1;

	/**
	 * The client version (default: 317).
	 */
	public final static int CLIENT_VERSION = 317;

	/**
	 * Should we check for lent variables?
	 */
	public final static boolean LENT_ITEMS = false;

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
