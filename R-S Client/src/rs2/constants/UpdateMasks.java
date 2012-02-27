package rs2.constants;

public class UpdateMasks {

	/**
	 * The NPC instance.
	 */
	public static NPC npc = new NPC();

	/**
	 * Returns the NPC instance.
	 */
	public static NPC getNPCMasks() {
		return npc;
	}

	/**
	 * The NPC instance.
	 */
	public static Player player = new Player();

	/**
	 * Returns the NPC instance.
	 */
	public static Player getPlayerMasks() {
		return player;
	}

	public static class NPC {
		public final int FORCE_ANIMATION_MASK = 0x10;
		public final int FIRST_HIT_MASK = 8;
		public final int STILL_GRAPHICS_MASK = 0x80;
		public final int FACE_ENTITY_MASK = 0x20;
		public final int FORCE_TEXT_MASK = 1;
		public final int SECOND_HIT_MASK = 0x40;
		public final int TRANSFORM_MASK = 2;
		public final int FACE_UPDATE_MASK = 4;
	}

	public static class Player {
		public final int FORCE_MOVEMENT_MASK = 0x400;
		public final int STILL_GRAPHICS_MASK = 0x100;
		public final int FORCE_ANIMATION_MASK = 8;
		public final int FORCE_TEXT_MASK = 4;
		public final int CHAT_UPDATE_MASK = 0x80;
		public final int FACE_ENTITY_MASK = 1;
		public final int APPEARANCE_UPDATE_MASK = 0x10;
		public final int FACE_UPDATE_MASK = 2;
		public final int FIRST_HIT_MASK = 0x20;
		public final int SECOND_HIT_MASK = 0x200;
	}

}