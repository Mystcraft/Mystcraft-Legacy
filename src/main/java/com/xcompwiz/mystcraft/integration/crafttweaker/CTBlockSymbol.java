package com.xcompwiz.mystcraft.integration.crafttweaker;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.data.ModSymbolsModifiers;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.mystcraft.symbol.CTBlockSymbol")
@ZenRegister
public class CTBlockSymbol extends CTAgeSymbol {

    private ModSymbolsModifiers.BlockModifierContainerObject container;
    private boolean registered = false;

    CTBlockSymbol(ModSymbolsModifiers.BlockModifierContainerObject container) {
        this.container = container;
    }

    //For categories, all myst default ones have modidCategory "mystcraft"
    //Categories are listed in com.xcompwiz.mystcraft.api.symbol.BlockCategory. Refer to the API for further information
    @ZenMethod
    public void addCategory(String modidCategory, String category, int categoryRank) {
        if(registered) return;
        BlockCategory cat = BlockCategory.getBlockCategory(new ResourceLocation(modidCategory, category));
        if(cat == null) {
            CraftTweakerAPI.logError("[Mystcraft] Unknown block category: '" + modidCategory + ":" + category + "'");
            return;
        }
        container.add(cat, categoryRank);
    }

    @ZenMethod
    public void register() {
        if(container.getSymbol() != null && !registered) {
            registered = true;
            SymbolManager.tryAddSymbol(container.getSymbol());
        }
    }

}
