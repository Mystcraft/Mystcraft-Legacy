package com.xcompwiz.mystcraft.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.utility.WeightedItemSelector;
import net.minecraft.util.ResourceLocation;

public class GrammarTree {

	private static class GrammarNode {
		public final ResourceLocation token;
		public final boolean isTerminal;
		public Rule selected = null;
		private GrammarNode parent;
		private List<GrammarNode> children = new ArrayList<GrammarNode>();
		private Integer leftPos;
		private Integer rightPos;

		public GrammarNode(ResourceLocation token) {
			this.token = token;
			this.leftPos = null;
			this.rightPos = null;
			this.isTerminal = false;
		}

		public GrammarNode(ResourceLocation token, int pos) {
			this.token = token;
			this.leftPos = pos;
			this.rightPos = pos;
			this.isTerminal = true;
		}

		public Integer getLeftPosition() {
			if (leftPos != null)
				return leftPos;
			for (int i = 0; i < children.size(); ++i) {
				Integer pos = children.get(i).getLeftPosition();
				if (pos == null)
					continue;
				if (this.leftPos == null || this.leftPos > pos) {
					this.leftPos = pos;
				}
			}
			if (leftPos != null)
				return leftPos;
			if (this.parent != null) {
				for (int i = 0; i < parent.children.size(); ++i) {
					if (parent.children.get(i).equals(this)) {
						if (parent.children.size() > i + 1) {
							this.leftPos = parent.children.get(i + 1).getLeftPosition();
						}
						break;
					}
				}
			}
			return leftPos;
		}

		public Integer getRightPosition() {
			if (rightPos != null)
				return rightPos;
			for (int i = 0; i < children.size(); ++i) {
				Integer pos = children.get(i).getRightPosition();
				if (pos == null)
					continue;
				if (this.rightPos == null || this.rightPos < pos) {
					this.rightPos = pos;
				}
			}
			if (rightPos != null)
				return rightPos;
			if (this.parent != null) {
				for (int i = parent.children.size() - 1; i >= 0; --i) {
					if (parent.children.get(i).equals(this)) {
						if (i > 0) {
							this.rightPos = parent.children.get(--i).getRightPosition();
						}
					} else if (this.rightPos != null && parent.children.get(i).getRightPosition() != null && this.rightPos < parent.children.get(i).getRightPosition()) {
						this.rightPos = parent.children.get(i).getRightPosition();
					}
				}
			}
			return rightPos;
		}

		private void addChild(GrammarNode child) {
			this.children.add(child);
			child.parent = this;
		}

		private void addChild(int index, GrammarNode child) {
			this.children.add(index, child);
			child.parent = this;
		}

		private static class NodePair {
			public GrammarNode original;
			public GrammarNode clone;

			public NodePair(GrammarNode o, GrammarNode c) {
				this.original = o;
				this.clone = c;
			}
		}

		@Override
		public GrammarNode clone() {
			List<NodePair> todo = new LinkedList<NodePair>();
			GrammarNode clone = flatClone(this);
			todo.add(new NodePair(this, clone));
			while (todo.size() > 0) {
				NodePair current = todo.remove(0);
				for (GrammarNode child : current.original.children) {
					GrammarNode childclone = flatClone(child);
					current.clone.addChild(childclone);
					todo.add(new NodePair(child, childclone));
				}
			}
			return clone;
		}

		private GrammarNode flatClone(GrammarNode grammarNode) {
			GrammarNode clone;
			if (grammarNode.isTerminal) {
				clone = new GrammarNode(grammarNode.token, grammarNode.leftPos);
			} else {
				clone = new GrammarNode(grammarNode.token);
			}
			clone.leftPos = grammarNode.leftPos;
			clone.rightPos = grammarNode.rightPos;
			clone.selected = grammarNode.selected;
			return clone;
		}

		@Override
		public String toString() {
			return String.format("%s%s (%d)", token, (selected != null ? ":" : "") + (isTerminal ? "*" : ""), children.size());
		}
	}

	/** The root node of the tree */
	private GrammarNode root;
	/** The currently unexpanded nodes in the tree, with more recently added nodes on the "top" */
	private List<GrammarNode> unexplored = new LinkedList<>();
	/** The roots of orphaned subtrees */
	private List<GrammarNode> subroots = new ArrayList<>();

	/** The symbol list before parsing */
	private List<ResourceLocation> terminals;

	public GrammarTree(ResourceLocation root) {
		this.root = new GrammarNode(root);
	}

	/**
	 * Builds the base tree
	 * @param terminals The original symbol list
	 * @param rand The random number generator to use to decide between ties in shortest path
	 */
	public void parseTerminals(List<ResourceLocation> terminals, Random rand) {
		this.terminals = Collections.unmodifiableList(terminals);
		for (int i = terminals.size(); i > 0; --i) {
			ResourceLocation terminal = terminals.get(i - 1);
			buildSubtree(new GrammarNode(terminal, i - 1), rand);
		}
	}

	/**
	 * Merges the subtrees into the tree and returns the list of symbols after expanding the remaining tokens
	 * @param rand The random number generator to use when choosing expansion paths
	 * @return the list of symbols after expanding the remaining tokens
	 */
	public List<ResourceLocation> getExpanded(Random rand) {
		//Init the out list
		List<ResourceLocation> out = new ArrayList<>();
		if (terminals != null) {
			out.addAll(terminals);
		}

		//If root node is unexplored, add it to the list.
		this.unexplored.clear();
		if (this.root.selected == null)
			this.unexplored.add(this.root);
		//Expand unexplored nodes while they only have one rule
		for (int i = 0; i < unexplored.size(); ++i) {
			List<Rule> rules = GrammarGenerator.getAllRules(unexplored.get(i).token);
			if (rules != null && rules.size() == 1) {
				expandUnexploredNode(i--, rules.get(0));
			}
		}
		//Attempt to connect all of the subroots to the main tree
		List<GrammarNode> failedsubs = new ArrayList<GrammarNode>();
		while (subroots.size() > 0) {
			if (unexplored.size() == 0) {
				failedsubs.addAll(subroots);
				break;
			}
			GrammarNode subroot = subroots.remove(0);
			if (!connectSubtreeShortest(subroot, rand)) {
				failedsubs.add(subroot);
			}
		}
		subroots = failedsubs;

		//Determine the insertion locations (into the output list) for the nodes of the tree
		HashMap<Integer, List<ResourceLocation>> insertLeft = new HashMap<>();
		HashMap<Integer, List<ResourceLocation>> insertRight = new HashMap<>();
		getInsertions(insertLeft, insertRight);

		//Insert all of the tokens into the list, expanding non-terminated symbols
		List<ResourceLocation> products;
		for (int i = out.size() + 1; i > 0; --i) {
			products = insertRight.get(i - 1);
			if (products != null) {
				for (int j = products.size(); j > 0; --j) {
					out.addAll(i, GrammarGenerator.explore(products.get(j - 1), rand));
				}
			}
			products = insertLeft.get(i - 1);
			if (products != null) {
				for (int j = products.size(); j > 0; --j) {
					out.addAll(i - 1, GrammarGenerator.explore(products.get(j - 1), rand));
				}
			}
		}
		unexplored.clear();
		return out;
	}

	/** Returns the insertions to make to the symbol list */
	private void getInsertions(GrammarNode node, HashMap<Integer, List<ResourceLocation>> insertLeft, HashMap<Integer, List<ResourceLocation>> insertRight) {
		for (GrammarNode child : node.children) {
			getInsertions(child, insertLeft, insertRight);
		}
		if (node.children.size() == 0 && node.selected == null && !node.isTerminal) {
			Integer left = node.getLeftPosition();
			if (left == null) {
				Integer right = node.getRightPosition();
				if (right == null) {
					left = terminals.size();
				} else {
					getOrCreateList(right, insertRight).add(node.token);
				}
			}
			if (left != null) {
				getOrCreateList(left, insertLeft).add(node.token);
			}
		}
	}

	/** Returns the insertions to make to the symbol list */
	private void getInsertions(HashMap<Integer, List<ResourceLocation>> left, HashMap<Integer, List<ResourceLocation>> right) {
		getInsertions(root, left, right);
	}

	/** Helper function for working with HashMap<Integer, List<String>> maps */
	private List<ResourceLocation> getOrCreateList(Integer key, HashMap<Integer, List<ResourceLocation>> map) {
		return map.computeIfAbsent(key, keyNew -> new ArrayList<>());
	}

	/** Prints the node and its children */
	private void printNode(GrammarNode node, String prefix) {
		System.out.println(prefix + node.token + (node.selected != null ? ":" : "") + (node.isTerminal ? "*" : "") + "-" + (node.getLeftPosition() == null ? "?" : node.getLeftPosition()) + "|" + (node.getRightPosition() == null ? "?" : node.getRightPosition()));
		for (GrammarNode child : node.children) {
			printNode(child, prefix + "  ");
		}
	}

	/** Prints the entire tree */
	public void print() {
		printNode(root, ">");
		System.out.println(String.format("With %d subtrees", subroots.size()));
		for (GrammarNode subroot : subroots) {
			printNode(subroot, "  >");
		}
	}

	/**
	 * Builds a subtree from the provided node. First it attempts to insert the node into any existing tree by looking for shortest path connections to all
	 * unexplored nodes. Failing that, it reverse expands the node upward for as long as it has exactly one producing rule and then adds the resultant subroot
	 * to the forest.
	 * @param subroot The node to reverse expand into a tree
	 * @param rand The random number generator to use to decide between ties in shortest path
	 */
	private void buildSubtree(GrammarNode subroot, Random rand) {
		for (GrammarNode node : unexplored) {
			List<Rule> path = getShortestPath(subroot, node, rand);
			if (path != null) {
				for (Rule rule : path) {
					subroot = reverseExpand(subroot, rule);
				}
				replaceNodeWithTree(node, subroot);
				return;
			}
		}
		List<Rule> rules = GrammarGenerator.getParentRules(subroot.token);
		while (rules != null && rules.size() == 1) {
			if (rules.get(0).getParent().equals(this.root.token))
				break; //Force no expansion to root (don't want to connect prematurely)
			subroot = reverseExpand(subroot, rules.get(0));
			rules = GrammarGenerator.getParentRules(subroot.token);
		}
		subroots.add(subroot);
		addUnexploredNodes(subroot);
	}

	/**
	 * Returns the shortest connecting path of rules from the subroot to the given node. If there are no paths then this returns null. If there is more than
	 * one, chooses one randomly.
	 * @param subroot The subtree to connect to node
	 * @param node The node to connect to via rules
	 * @return The list of rules that make up the path in order of subroot to node
	 */
	private List<Rule> getShortestPath(GrammarNode subroot, GrammarNode node, Random rand) {
		List<List<Rule>> paths = GrammarGenerator.getShortestPaths(subroot.token, node.token);
		if (paths == null || paths.size() == 0)
			return null;
		return WeightedItemSelector.getRandomItem(rand, paths);
	}

	/**
	 * Attempts to put the given subtree into the main tree. This is used to put the subtree forest built in the phrasal pass into the main tree. If the node
	 * has no producing rules (rules that produce this token) then this fails. For all unexplored nodes: It checks if this node matches that node, replacing it
	 * and returning on success. Otherwise it builds a list of all of the rules which can map from that node to this one. If there are any such options and they
	 * can be selected from randomly, it will randomly select such a rule. If there aren't any such direct connections then the function attempts to build a
	 * list of possible paths to the main tree. It then randomly selects one of these connections, if there are any. Each connection is considered equally
	 * likely.
	 * @param subroot The node to put into the tree
	 * @param rand The random number generator to use to make decisions
	 * @return success
	 */
	@SuppressWarnings("unused")
	//XXX: (re)move connectSubtreeRandomly
	private boolean connectSubtreeRandomly(GrammarNode subroot, Random rand) {
		List<Rule> rules = GrammarGenerator.getParentRules(subroot.token);
		if (rules == null || rules.size() == 0)
			return false;
		for (GrammarNode node : unexplored) {
			if (node.token.equals(subroot.token)) {
				replaceNodeWithTree(node, subroot);
				return true;
			}
			List<Rule> options = new ArrayList<Rule>();
			for (Rule rule : rules) {
				if (node.token.equals(rule.getParent())) {
					options.add(rule);
				}
			}
			if (options.size() > 0 && WeightedItemSelector.getTotalWeight(options) > 0) {
				subroot = reverseExpand(subroot, WeightedItemSelector.getRandomItem(rand, options));
				replaceNodeWithTree(node, subroot);
				return true;
			}
		}

		HashMap<GrammarNode, GrammarNode> connections = new HashMap<GrammarNode, GrammarNode>();

		for (Rule rule : rules) {
			if (!checkForLoop(rule.getParent(), subroot))
				producePaths(connections, reverseExpand(subroot.clone(), rule), rand);
		}

		if (connections.size() == 0)
			return false;
		int selected = rand.nextInt(connections.size());
		for (Entry<GrammarNode, GrammarNode> pair : connections.entrySet()) {
			if (selected == 0) {
				replaceNodeWithTree(pair.getValue(), pair.getKey());
				break;
			}
			--selected;
		}
		return true;
	}

	/**
	 * Attempts to put the given subtree into the main tree. This is used to put the subtree forest built in the phrasal pass into the main tree. If the node
	 * has no producing rules (rules that produce this token) then this fails. For all unexplored nodes: It checks if this node matches that node, replacing it
	 * and returning on success. Otherwise it builds a list of all of the rules which can map from that node to this one directly. If there are any such options
	 * and they can be selected from randomly, it will randomly select such a rule. If there aren't any such direct connections then the function attempts to
	 * find the shortest path to the node.
	 * @param subroot
	 * @param rand
	 * @return
	 */
	private boolean connectSubtreeShortest(GrammarNode subroot, Random rand) {
		List<Rule> rules = GrammarGenerator.getParentRules(subroot.token);
		if (rules == null || rules.size() == 0)
			return false;
		for (GrammarNode node : unexplored) {
			if (node.token.equals(subroot.token)) {
				replaceNodeWithTree(node, subroot);
				return true;
			}
			List<Rule> options = new ArrayList<Rule>();
			for (Rule rule : rules) {
				if (node.token.equals(rule.getParent())) {
					options.add(rule);
				}
			}
			if (options.size() > 0 && WeightedItemSelector.getTotalWeight(options) > 0) {
				subroot = reverseExpand(subroot, WeightedItemSelector.getRandomItem(rand, options));
				replaceNodeWithTree(node, subroot);
				return true;
			}
			List<Rule> path = getShortestPath(subroot, node, rand);
			if (path != null) {
				for (Rule rule : path) {
					subroot = reverseExpand(subroot, rule);
				}
				replaceNodeWithTree(node, subroot);
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if this token already exists in a tree
	 * @param parent The token to search for
	 * @param subroot The root of the tree to search
	 * @return If the token is the token of any node in the tree
	 */
	private boolean checkForLoop(ResourceLocation parent, GrammarNode subroot) {
		List<GrammarNode> nodes = new LinkedList<GrammarNode>();
		nodes.add(subroot);
		while (nodes.size() > 0) {
			GrammarNode node = nodes.remove(0);
			if (node.token.equals(parent))
				return true;
			nodes.addAll(node.children);
		}
		return false;
	}

	/**
	 * Used in building the possible connections to the main tree from a orphaned node For all unexplored nodes, build a list of direct options if any options,
	 * select one to reverse expand, insert to the connection results, and return Otherwise, recursively call this on all possible rules which produce this
	 * token, avoiding loops
	 * @param connections The shared connections list to which any found connections will be added (subroot, node to replace)
	 * @param subroot The root of the tree to put into the main tree
	 * @param rand RNG for decisions
	 */
	private void producePaths(HashMap<GrammarNode, GrammarNode> connections, GrammarNode subroot, Random rand) {
		List<Rule> rules = GrammarGenerator.getParentRules(subroot.token);
		if (rules == null || rules.size() == 0)
			return;
		for (GrammarNode node : unexplored) {
			List<Rule> options = new ArrayList<Rule>();
			for (Rule rule : rules) {
				if (node.token.equals(rule.getParent())) {
					options.add(rule);
				}
			}
			if (options.size() > 0 && WeightedItemSelector.getTotalWeight(options) > 0) {
				subroot = reverseExpand(subroot, WeightedItemSelector.getRandomItem(rand, options));
				connections.put(subroot, node);
				return;
			}
		}
		for (Rule rule : rules) {
			if (!checkForLoop(rule.getParent(), subroot))
				producePaths(connections, reverseExpand(subroot.clone(), rule), rand);
		}
	}

	/**
	 * Replaces one node with another one Used to replace unexplored nodes with fulfilling subtrees Adds the children of the subtree to the unexplored list
	 * @param node Node to replace
	 * @param subroot To to insert
	 */
	private void replaceNodeWithTree(GrammarNode node, GrammarNode subroot) {
		unexplored.remove(node);
		node.selected = subroot.selected;
		node.children = subroot.children;
		for (GrammarNode child : node.children) {
			child.parent = node;
		}
		node.leftPos = null;
		node.rightPos = null;
		addUnexploredNodes(node);
	}

	/**
	 * Adds the children of the passed subtree to the unexplored list Adds all children in order (left to right) to the front of the list (far right ends up on
	 * top) If any children are explored then they are queued to be processed (farther right and children of more recently added nodes get processed later than
	 * leftmost nodes, putting them higher on the list)
	 * @param subroot to add the children of
	 */
	private void addUnexploredNodes(GrammarNode subroot) {
		List<GrammarNode> nodes = new LinkedList<GrammarNode>();
		nodes.add(subroot);
		while (nodes.size() > 0) {
			GrammarNode node = nodes.remove(0);
			for (GrammarNode child : node.children) {
				if (child.selected != null) {
					nodes.add(child);
				} else if (!child.isTerminal) {
					unexplored.add(0, child);
				}
			}
		}
	}

	/**
	 * Expands a node in the unexplored list by the given rule, replacing it with any produced tokens
	 * @param index
	 * @param rule
	 */
	private void expandUnexploredNode(int index, Rule rule) {
		GrammarNode node = unexplored.remove(index);
		node.selected = rule;
		List<ResourceLocation> products = rule.getValues();
		for (int i = products.size(); i > 0; --i) {
			ResourceLocation product = products.get(i - 1);
			GrammarNode newnode = new GrammarNode(product);
			node.addChild(0, newnode);
			unexplored.add(index, newnode);
		}
	}

	/**
	 * Reverse expands a node by a rule. Basically uses the rule to build a new root node and sibling nodes for the passed in root node. The passed in node will
	 * be used in place of the token farthest right in the rule which matches.
	 * @param subroot The root of the subtree
	 * @param rule The rule to use
	 * @return The new root
	 */
	private GrammarNode reverseExpand(GrammarNode subroot, Rule rule) {
		GrammarNode newroot = new GrammarNode(rule.getParent());
		newroot.selected = rule;
		List<ResourceLocation> products = rule.getValues();
		for (int i = products.size(); i > 0; --i) {
			ResourceLocation product = products.get(i - 1);
			if (subroot != null && product.equals(subroot.token)) {
				newroot.addChild(0, subroot);
				subroot = null;
			} else {
				newroot.addChild(0, new GrammarNode(product));
			}
		}
		return newroot;
	}
}
