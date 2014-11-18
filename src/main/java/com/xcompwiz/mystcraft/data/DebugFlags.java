package com.xcompwiz.mystcraft.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import com.xcompwiz.mystcraft.core.DebugDataTracker;
import com.xcompwiz.mystcraft.core.DebugDataTracker.Callback;
import com.xcompwiz.mystcraft.grammar.GrammarTree;

public final class DebugFlags {

	public static boolean	instability	= false;
	public static boolean	profiler	= false;
	public static boolean	grammar		= false;

	static {
		DebugDataTracker.register("grammar.generate", new Callback() {

			@Override
			public void setState(ICommandSender agent, boolean state) {
				Random rand = new Random();
				GrammarTree tree = new GrammarTree(GrammarRules.ROOT);
				List<String> symbols = new ArrayList<String>();
				tree.parseTerminals(symbols, rand);
				symbols = tree.getExpanded(rand);
				if (DebugFlags.grammar) {
					System.out.println(" == Produced Tree ==");
					tree.print();
				}
				agent.addChatMessage(new ChatComponentText(symbols.toString()));
			}
		});
	}
}
