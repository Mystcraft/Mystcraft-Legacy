package com.xcompwiz.mystcraft.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import com.xcompwiz.mystcraft.data.GrammarRules;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugValueCallback;
import com.xcompwiz.mystcraft.grammar.GrammarTree;

public final class DebugFlags {
	public static boolean	instability	= false;
	public static boolean	grammar		= false;

//	if (DebugFlags.instability) DebugDataTracker.register(agedata.getAgeName() + ".instability.symbols", new DefaultCallback(symbolinstability));
//	if (DebugFlags.instability) DebugDataTracker.register(agedata.getAgeName() + ".instability.book", new DefaultCallback(agedata.getBaseInstability()));
//	if (DebugFlags.instability) DebugDataTracker.register(agedata.getAgeName() + ".instability", new DefaultCallback(symbolinstability + blockinstability + agedata.getBaseInstability() + getInstabilityBonusManager().getResult()));
//	if (DebugFlags.instability) DebugDataTracker.register(agedata.getAgeName() + ".instability.bonus", new DefaultCallback(getInstabilityBonusManager().getResult()));
//	if (DebugFlags.instability) DebugDataTracker.register(agedata.getAgeName() + ".instability.blocks", new DefaultCallback(blockinstability));
//	if (DebugFlags.instability) DebugDataTracker.register(agedata.getAgeName() + ".profiled", new DefaultCallback(profiler.getCount()));

//	if (DebugFlags.instability) DebugDataTracker.register(worldprovider.getDimensionName() + ".effects", new DefaultCallback(providerlevels));

//	if (DebugFlags.profiler) DebugDataTracker.register((debugname == null ? "Unnamed" : debugname) + ".instability." + entry.getKey(), new DefaultCallback(entry.getValue()));

	static {
		DebugHierarchy.register("grammar.generate", new DebugValueCallback() {

			@Override
			public void set(ICommandSender agent, boolean state) {
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

			@Override
			public String get(ICommandSender agent) {
				throw new DefaultValueCallback.CallbackReadNotSupported();
			}
		});
	}
}
