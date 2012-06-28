package com.xcompwiz.mystcraft.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.xcompwiz.mystcraft.utility.WeightedItemSelector;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector.IWeightedItem;
import com.xcompwiz.util.CollectionUtils;

public class GrammarGenerator {
	public static final class Rule implements IWeightedItem {
		private final String		parent;
		private final List<String>	values;
		private final float			rarity;

		public Rule(String parent, List<String> values, float rarity) {
			this.parent = parent;
			this.values = Collections.unmodifiableList(values);
			this.rarity = rarity;
		}

		@Override
		public float getWeight() {
			return this.rarity;
		}

		public String getParent() {
			return parent;
		}

		public List<String> getValues() {
			if (values == null) return null;
			return values;
		}

		public int size() {
			if (values == null) return 0;
			return values.size();
		}
	}

	// Rules the string expands to
	private static HashMap<String, List<Rule>>							mappings			= new HashMap<String, List<Rule>>();
	// Rules that expand to include the string
	private static HashMap<String, List<Rule>>							reverseLookup		= new HashMap<String, List<Rule>>();
	private static HashMap<String, HashMap<String, List<List<Rule>>>>	shortestpaths;

	private static boolean												profile_pathbuilder	= false;

	public static void registerRule(Rule rule) {
		if (rule.parent == null) { throw new RuntimeException("Invalid CFG Rule.  Requires parent to expand."); }
		if (shortestpaths != null) { throw new RuntimeException("You must register your rules before the grammar is finalized! (before Mystcraft's post-init)"); }
		CollectionUtils.getOrCreateElement(rule.parent, mappings).add(rule);
		for (String value : rule.values) {
			CollectionUtils.getOrCreateElement(value, reverseLookup).add(rule);
		}
	}

	public static List<Rule> getParentRules(String str) {
		List<Rule> rules = CollectionUtils.getOrCreateElement(str, reverseLookup);
		return Collections.unmodifiableList(rules);
	}

	public static List<Rule> getAllRules(String str) {
		List<Rule> mapping = mappings.get(str);
		if (mapping == null) return null;
		return Collections.unmodifiableList(mapping);
	}

	public static Rule getRandomRule(String str, Random rand) {
		List<Rule> rules = mappings.get(str);
		if (rules == null || rules.size() == 0) return null;
		return WeightedItemSelector.getRandomItem(rand, rules);
	}

	public static List<String> explore(String str, Random rand) {
		List<String> list = new ArrayList<String>();
		Rule rule = getRandomRule(str, rand);
		if (rule == null) {
			list.add(str);
			return list;
		}
		if (rule.size() == 0) { return list; }
		List<String> result = rule.getValues();
		for (String t : result) {
			list.addAll(explore(t, rand));
		}
		return list;
	}

	/**
	 * Returns the shortest connecting paths of rules from the subroot to the given node. If there are no paths then
	 * this returns null or an empty list. If multiple paths have the same length, it returns all of them.
	 * 
	 * @param subtree_token The subtree to connect to node
	 * @param node_token The node to connect to via rules
	 * @return The list of rules that make up the path in order of subroot to node
	 */
	public static List<List<Rule>> getShortestPaths(String subtree_token, String node_token) {
		if (shortestpaths == null) buildShortestPaths();
		List<List<Rule>> paths = null;
		HashMap<String, List<List<Rule>>> allpaths = shortestpaths.get(subtree_token);
		if (allpaths != null) {
			paths = allpaths.get(node_token);
		}
		if (paths == null) return paths;
		return Collections.unmodifiableList(paths);
	}

	public static class VisitPair {
		public String		target;
		public List<Rule>	path;

		public VisitPair(String target, List<Rule> path) {
			this.target = target;
			this.path = path;
		}

	}

	private static HashMap<String, List<List<Rule>>> getOrCalculatePaths(HashMap<String, HashMap<String, List<List<Rule>>>> shortestpaths, String token) {
		HashMap<String, List<List<Rule>>> allpaths = shortestpaths.get(token);
		if (allpaths != null) return allpaths;
		allpaths = new HashMap<String, List<List<Rule>>>();

		List<Rule> producers = reverseLookup.get(token);
		List<VisitPair> tovisit = new LinkedList<VisitPair>();

		if (producers != null) for (Rule rule : producers) {
			tovisit.add(new VisitPair(rule.parent, Collections.unmodifiableList(CollectionUtils.buildList(rule))));
		}
		while (tovisit.size() > 0) {
			VisitPair elem = tovisit.remove(0);
			String target = elem.target;
			if (target.equals(token)) continue;
			List<Rule> path = elem.path;
			List<List<Rule>> paths_to_target = CollectionUtils.getOrCreateElement(target, allpaths);
			if (paths_to_target.size() > 0) {
				if (paths_to_target.get(0).size() > path.size()) { // All paths stored are the same length
					paths_to_target.clear();
				}
			}
			if (paths_to_target.size() == 0 || paths_to_target.get(0).size() == path.size()) {
				paths_to_target.add(path);
				List<Rule> target_producers = reverseLookup.get(target);
				if (target_producers != null) for (Rule producer : target_producers) {
					tovisit.add(new VisitPair(producer.parent, Collections.unmodifiableList(CollectionUtils.buildList(path, null, producer))));
				}
			}
		}
		shortestpaths.put(token, allpaths); // Put the paths in the lookup now that they are fully built

		return allpaths;
	}

	public static void buildShortestPaths() {
		long timestart = System.currentTimeMillis();
		if (profile_pathbuilder) System.out.println("Starting buildShortestPaths");
		shortestpaths = new HashMap<String, HashMap<String, List<List<Rule>>>>();

		for (Entry<String, List<Rule>> lookup : reverseLookup.entrySet()) {
			String token = lookup.getKey();
			if (shortestpaths.containsKey(token)) continue;
			getOrCalculatePaths(shortestpaths, token);
		}
		long timeend = System.currentTimeMillis();
		if (profile_pathbuilder) System.out.println("buildShortestPaths Exectution Time: " + (timeend - timestart));
	}

	public static String pathToString(List<Rule> path) {
		String out = "";
		for (Rule rule : path) {
			if (!out.equals("")) out += " - ";
			out += rule.parent;
		}
		return out;
	}

	public static void testShortestPaths(String token1, String token2) {
		System.out.println(token1 + " - " + token2);
		List<List<Rule>> paths = GrammarGenerator.getShortestPaths(token1, token2);
		if (paths == null) {
			System.out.println("no path");
			return;
		}
		for (List<Rule> path : paths) {
			System.out.println("path: " + GrammarGenerator.pathToString(path));
		}
	}
}
