package com.xcompwiz.mystcraft.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xcompwiz.mystcraft.data.GrammarRules;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugTaskCallback;
import com.xcompwiz.mystcraft.grammar.GrammarTree;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public final class DebugFlags {
	public static boolean instability = false;
	public static boolean grammar = false;

//	if (DebugFlags.instability) DebugDataTracker.register(worldprovider.getDimensionName() + ".effects", new DefaultCallback(providerlevels));

	static {
		DebugUtils.register("global.grammar.generate", new DebugTaskCallback() {
			@Override
			public void run(ICommandSender agent, Object... args) {
				Random rand = new Random();
				GrammarTree tree = new GrammarTree(GrammarRules.ROOT);
				List<ResourceLocation> symbols = new ArrayList<>();
				tree.parseTerminals(symbols, rand);
				symbols = tree.getExpanded(rand);
				if (DebugFlags.grammar) {
					System.out.println(" == Produced Tree ==");
					tree.print();
				}
				agent.sendMessage(new TextComponentString(symbols.toString()));
			}
		});
	}
}
