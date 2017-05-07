package com.xcompwiz.mystcraft.imc;

import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolManager;

import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCBlacklistSymbol implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isStringMessage()) return;
		String identifier = message.getStringValue();
		SymbolManager.blackListSymbol(identifier);
		LoggerUtils.info(String.format("Symbol blacklist request from [%s] successful on identifier %s", message.getSender(), identifier));
	}

}
