package rs2.constants;

public final class SkillConstants {

	/**
	 * The total number of skills.
	 */
	public static final int total = 25;

	/**
	 * All the names of the skills.
	 */
	public static final String[] names = {
		"attack", "defence", "strength", "hitpoints", "ranged",
		"prayer", "magic", "cooking", "woodcutting", "fletching",
		"fishing", "firemaking", "crafting", "smithing", "mining",
		"herblore", "agility", "thieving", "slayer", "farming",
		"runecraft", "construction", "hunting", "summoning", "-unused-"
	};

	/**
	 * An array containing whether or not the given skillId is enabled.
	 */
	public static final boolean[] enabled = {
		true, true, true, true, true,
		true, true, true, true, true,
		true, true, true, true, true,
		true, true, true, true, false,
		true, false, false, false, false
	};

}
