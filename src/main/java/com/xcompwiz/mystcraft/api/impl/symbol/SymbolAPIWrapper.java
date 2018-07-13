package com.xcompwiz.mystcraft.api.impl.symbol;

import java.util.List;

import com.xcompwiz.mystcraft.api.hook.SymbolAPI;
import com.xcompwiz.mystcraft.api.impl.APIWrapper;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.logging.LoggerUtils;

import net.minecraft.util.ResourceLocation;

public class SymbolAPIWrapper extends APIWrapper implements SymbolAPI {

	public SymbolAPIWrapper(String modname) {
		super(modname);
	}

	@Override
	public void blacklistIdentifier(ResourceLocation identifier) {
		InternalAPI.symbol.blacklistIdentifier(identifier);
		LoggerUtils.info(String.format("[%s] has blacklisted symbol identifier %s", this.getOwnerMod(), identifier));
	}

	@Override
	public List<IAgeSymbol> getAllRegisteredSymbols() {
		return InternalAPI.symbol.getAllRegisteredSymbols();
	}

	@Override
	public IAgeSymbol getSymbol(ResourceLocation identifier) {
		return InternalAPI.symbol.getSymbol(identifier);
	}

	@Override
	public String getSymbolOwner(ResourceLocation identifier) {
		return InternalAPI.symbol.getSymbolOwner(identifier);
	}

}
