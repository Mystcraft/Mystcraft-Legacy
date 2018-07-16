package com.xcompwiz.mystcraft.api.word;

import com.xcompwiz.mystcraft.api.hook.WordAPI;

/**
 * Includes the word data for the symbol poems. You can, of course, create your own words, but it is recommended that you use this list for the most part and
 * only create words as necessary. If you use a string in a poem but do not define the draw components for the string as a word then the system will
 * automatically deterministically generate components for the word. These symbols are based on the original Narayan from Myst III: Exile You can see them here:
 * http://fc07.deviantart.net/fs19/f/2007/277/5/7/Symbols_of_Narayan_by_Killberk.png http://isomerica.net/~mlah/narayan/symbols.html
 * @author xcompwiz
 */
public final class WordData {

	// Native Narayan Words
	public static final String Balance = "Balance";
	public static final String Believe = "Believe";
	public static final String Change = "Change";
	public static final String Chaos = "Chaos";
	public static final String Civilization = "Civilization";
	public static final String Constraint = "Constraint";
	public static final String Contradict = "Contradict";
	public static final String Control = "Control";
	public static final String Convey = "Convey";
	public static final String Creativity = "Creativity";
	public static final String Cycle = "Cycle";
	public static final String Dependence = "Dependence";
	public static final String Discover = "Discover";
	public static final String Dynamic = "Dynamic";
	public static final String Elevate = "Elevate";
	public static final String Encourage = "Encourage";
	public static final String Energy = "Energy";
	public static final String Entropy = "Entropy";
	public static final String Ethereal = "Ethereal";
	public static final String Exist = "Exist";
	public static final String Explore = "Explore";
	public static final String Flow = "Flow";
	public static final String Force = "Force";
	public static final String Form = "Form";
	public static final String Future = "Future";
	public static final String Growth = "Growth";
	public static final String Harmony = "Harmony";
	public static final String Honor = "Honor";
	public static final String Infinite = "Infinite";
	public static final String Inhibit = "Inhibit";
	public static final String Intelligence = "Intelligence";
	public static final String Love = "Love";
	public static final String Machine = "Machine";
	public static final String Merge = "Merge";
	public static final String Momentum = "Momentum";
	public static final String Motion = "Motion";
	public static final String Mutual = "Mutual";
	public static final String Nature = "Nature";
	public static final String Nurture = "Nurture";
	public static final String Possibility = "Possibility";
	public static final String Power = "Power";
	public static final String Question = "Question";
	public static final String Rebirth = "Rebirth";
	public static final String Remember = "Remember";
	public static final String Resilience = "Resilience";
	public static final String Resurrect = "Resurrect";
	public static final String Sacrifice = "Sacrifice";
	public static final String Society = "Society";
	public static final String Spur = "Spur";
	public static final String Static = "Static";
	public static final String Stimulate = "Stimulate";
	public static final String Survival = "Survival";
	public static final String Sustain = "Sustain";
	public static final String System = "System";
	public static final String Time = "Time";
	public static final String Tradition = "Tradition";
	public static final String Transform = "Transform";
	public static final String Weave = "Weave";
	public static final String Wisdom = "Wisdom";
	public static final String Void = "Void";

	// Additional
	public static final String Chain = "Chain";
	public static final String Celestial = "Celestial";
	public static final String Image = "Image";
	public static final String Terrain = "Terrain";
	public static final String Order = "Order";

	// Suggested mappings

	public static final String Modifier = Transform;
	public static final String Environment = Survival;

	public static final String Structure = Static;
	public static final String Ore = Machine;
	public static final String Sea = Flow;

	private static boolean initialized = false;

	/**
	 * Do not call this. This is called by Mystcraft itself.
	 */
	public static void init(WordAPI wordAPI) {
		if (wordAPI == null || initialized)
			return;
		initialized = true;
		wordAPI.registerWord(Nature, new Integer[] { 5, 6, 8, 10, 11, 12, 15, 16, 17, 22 }); // Narayan - Nature
		wordAPI.registerWord(Love, new Integer[] { 4, 6, 9, 10, 11, 14, 16, 17, 19, 20 }); // Narayan - Love
		wordAPI.registerWord(Force, new Integer[] { 4, 5, 8, 16, 17, 19 }); // Narayan - Force
		wordAPI.registerWord(Transform, new Integer[] { 4, 5, 6, 8, 11, 14, 18, 20, 21 }); // Narayan - Transform
		wordAPI.registerWord(Change, new Integer[] { 4, 7, 10, 11, 12, 16, 17, 18, 19, 21 }); // Narayan - Change
		wordAPI.registerWord(Machine, new Integer[] { 4, 6, 7, 8, 14, 17, 18, 20, 21 }); // Narayan - Machine
		wordAPI.registerWord(Future, new Integer[] { 4, 5, 8, 12, 14, 15, 18, 19, 20 }); // Narayan - Future
		wordAPI.registerWord(Cycle, new Integer[] { 4, 5, 7, 16, 17, 18 }); // Narayan - Cycle
		wordAPI.registerWord(Merge, new Integer[] { 4, 5, 6, 7, 16, 19, 20, 21 }); // Narayan - Merge
		wordAPI.registerWord(Dependence, new Integer[] { 4, 7, 8, 12, 13, 14, 15, 19, 20, 23 }); // Narayan - Dependence
		wordAPI.registerWord(Void, new Integer[] { 4, 5, 6, 16, 17, 18, 23 }); // Narayan - Void
		wordAPI.registerWord(Energy, new Integer[] { 4, 5, 9, 13, 16, 18, 19, 22, 23 }); // Narayan - Energy
		wordAPI.registerWord(Mutual, new Integer[] { 7, 8, 11, 15, 17, 18, 21 }); // Narayan - Mutual
		wordAPI.registerWord(Contradict, new Integer[] { 6, 12, 13, 17, 18, 20, 21, 22, 23 }); // Narayan - Contradict
		wordAPI.registerWord(Power, new Integer[] { 5, 8, 9, 10, 22, 23 }); // Narayan - Power
		wordAPI.registerWord(Possibility, new Integer[] { 4, 6, 8, 9, 10, 14, 16, 17, 19, 20, 23 }); // Narayan - Possibility
		wordAPI.registerWord(Convey, new Integer[] { 4, 6, 9, 14, 15, 19, 22, 23 }); // Narayan - Convey
		wordAPI.registerWord(Encourage, new Integer[] { 5, 6, 9, 13, 14, 18, 20, 21, 22, 23 }); // Narayan - Encourage
		wordAPI.registerWord(Wisdom, new Integer[] { 5, 7, 8, 9, 10, 13, 14, 19, 23 }); // Narayan - Wisdom
		wordAPI.registerWord(Dynamic, new Integer[] { 4, 5, 6, 12, 13, 17, 18, 22, 23 }); // Narayan - Dynamic
		wordAPI.registerWord(Intelligence, new Integer[] { 4, 9, 12, 13, 15, 18, 19, 20, 21 }); // Narayan - Intelligence
		wordAPI.registerWord(Entropy, new Integer[] { 4, 6, 9, 10, 13, 14, 15, 16, 17, 18, 23 }); // Narayan - Entropy
		wordAPI.registerWord(Society, new Integer[] { 4, 5, 6, 7, 8, 11, 14, 15, 17, 18, 20, 21 }); // Narayan - Society
		wordAPI.registerWord(Chaos, new Integer[] { 4, 6, 9, 11, 12, 13, 18, 23 }); // Narayan - Chaos
		wordAPI.registerWord(Growth, new Integer[] { 4, 5, 6, 12, 13, 15, 21, 22, 23 }); // Narayan - Growth
		wordAPI.registerWord(Civilization, new Integer[] { 4, 8, 9, 11, 14, 15, 17, 18, 20, 21, 23 }); // Narayan - Civilization
		wordAPI.registerWord(Spur, new Integer[] { 5, 9, 13, 14, 16, 20, 21, 22, 23 }); // Narayan - Spur
		wordAPI.registerWord(Infinite, new Integer[] { 5, 6, 8, 19, 20, 21, 22, 23 }); // Narayan - Infinite
		wordAPI.registerWord(Motion, new Integer[] { 6, 7, 9, 16, 19, 20, 21 }); // Narayan - Motion
		wordAPI.registerWord(Harmony, new Integer[] { 5, 7, 9, 10, 14, 15, 16, 19, 20, 21 }); // Narayan - Harmony
		wordAPI.registerWord(Resurrect, new Integer[] { 5, 7, 8, 9, 12, 13, 14, 15, 19, 23 }); // Narayan - Resurrect
		wordAPI.registerWord(Weave, new Integer[] { 8, 10, 13, 14, 20, 21, 22, 23 }); // Narayan - Weave
		wordAPI.registerWord(Rebirth, new Integer[] { 4, 5, 6, 9, 10, 14, 16, 17, 19, 23 }); // Narayan - Rebirth
		wordAPI.registerWord(Control, new Integer[] { 4, 6, 9, 10, 13, 19, 22, 23 }); // Narayan - Control
		wordAPI.registerWord(Sacrifice, new Integer[] { 5, 7, 9, 10, 14, 15, 17, 19 }); // Narayan - Sacrifice
		wordAPI.registerWord(Time, new Integer[] { 4, 5, 7, 12, 13, 19, 20, 21, 22, 23 }); // Narayan - Time
		wordAPI.registerWord(Constraint, new Integer[] { 5, 6, 9, 11, 13, 20, 22, 23 }); // Narayan - Constraint
		wordAPI.registerWord(Inhibit, new Integer[] { 10, 11, 12, 15, 18, 19, 20, 23 }); // Narayan - Inhibit
		wordAPI.registerWord(Creativity, new Integer[] { 6, 8, 9, 12, 13, 14, 18, 20, 21, 22, 23 }); // Narayan - Creativity
		wordAPI.registerWord(Stimulate, new Integer[] { 4, 5, 8, 9, 12, 13, 15, 20, 21, 22 }); // Narayan - Stimulate
		wordAPI.registerWord(Momentum, new Integer[] { 6, 7, 12, 13, 17, 18, 19, 20, 23 }); // Narayan - Momentum
		wordAPI.registerWord(Balance, new Integer[] { 4, 6, 9, 10, 16, 19 }); // Narayan - Balance
		wordAPI.registerWord(Resilience, new Integer[] { 5, 6, 16, 17, 18, 19, 20, 23 }); // Narayan - Resilience
		wordAPI.registerWord(Flow, new Integer[] { 4, 7, 12, 13, 17, 18 }); // Narayan - Flow
		wordAPI.registerWord(Believe, new Integer[] { 5, 6, 9, 10, 20, 22, 23 }); // Narayan - Believe
		wordAPI.registerWord(Tradition, new Integer[] { 4, 6, 7, 8, 9, 14, 17, 18, 20 }); // Narayan - Tradition
		wordAPI.registerWord(Nurture, new Integer[] { 4, 7, 8, 12, 13, 15, 19, 21, 22 }); // Narayan - Nurture
		wordAPI.registerWord(Honor, new Integer[] { 4, 6, 9, 10, 18, 20, 21 }); // Narayan - Honor
		wordAPI.registerWord(Form, new Integer[] { 4, 6, 8, 9, 10, 11, 13, 14, 16, 23 }); // Narayan - Form
		wordAPI.registerWord(Question, new Integer[] { 4, 11, 13, 19, 20, 21, 22, 23 }); // Narayan - Question
		wordAPI.registerWord(Static, new Integer[] { 4, 6, 8, 11, 13, 14, 15, 19, 23 }); // Narayan - Static
		wordAPI.registerWord(Exist, new Integer[] { 4, 5, 6, 9, 13, 16, 17, 19, 20, 23 }); // Narayan - Exist
		wordAPI.registerWord(Elevate, new Integer[] { 5, 6, 9, 10, 19, 20, 21, 22, 23 }); // Narayan - Elevate
		wordAPI.registerWord(Survival, new Integer[] { 5, 6, 8, 9, 16, 17, 18, 19, 23 }); // Narayan - Survival
		wordAPI.registerWord(System, new Integer[] { 10, 12, 14, 15, 16, 20, 21, 22 }); // Narayan - System
		wordAPI.registerWord(Remember, new Integer[] { 4, 6, 13, 16, 19, 20 }); // Narayan - Remember
		wordAPI.registerWord(Sustain, new Integer[] { 6, 16, 19, 20, 21, 23 }); // Narayan - Sustain
		wordAPI.registerWord(Ethereal, new Integer[] { 4, 6, 8, 13, 14, 16, 18, 21, 23 }); // Narayan - Ethereal
		wordAPI.registerWord(Discover, new Integer[] { 4, 5, 8, 10, 12, 13, 14, 16, 22 }); // Narayan - Discover
		wordAPI.registerWord(Explore, new Integer[] { 6, 8, 12, 13, 20, 21, 23 }); // Narayan - Explore

		wordAPI.registerWord(Chain, new Integer[] { 4, 5, 6, 7, 24, 25, 27, 33, 34, 35, 40, 41, 42, 43 }); // Veovis - Chain
		wordAPI.registerWord(Image, new Integer[] { 7, 8, 9, 10, 11, 19, 21, 23 }); // Veovis - Image
		wordAPI.registerWord(Celestial, new Integer[] { 6, 8, 9, 10, 14, 18, 20, 21, 22, 23, 24 }); // Veovis - Celestial
		wordAPI.registerWord(Terrain, new Integer[] { 6, 10, 12, 13, 16, 19, 24, 25, 27 }); // Veovis - Terrain
		wordAPI.registerWord(Order, new Integer[] { 11, 14, 15, 17, 20, 21 }); // Draco18s - Original Concept Art for new "System" word
	}
}
