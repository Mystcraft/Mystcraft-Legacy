package com.xcompwiz.mystcraft.imc;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCHandler {
	public interface IMCProcessor {
		public void process(IMCMessage message);
	}

	private static Map<String, IMCProcessor>	processors	= new HashMap<String, IMCProcessor>();

	static {
		processors.put("blockmodifier", new IMCBlockModifier());
		processors.put("blacklistfluid", new IMCBlacklistFluid());
		processors.put("blacklist", new IMCBlacklistSymbol());
		processors.put("fluidsymboldata", new IMCFluidData());
		processors.put("register", new IMCAPIRegister());
	}

	public static void process(ImmutableList<IMCMessage> messages) {
		for (IMCMessage message : messages) {
			String key = message.key.toLowerCase();
			IMCProcessor process = processors.get(key);
			try {
				process.process(message);
			} catch (Exception e) {
				LoggerUtils.error(e.getLocalizedMessage());
			}
		}
	}
}
