package com.xcompwiz.mystcraft.integration.crafttweaker;

import com.xcompwiz.mystcraft.data.ModSymbolsModifiers;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.mystcraft.CTAgeSymbol")
@ZenRegister
public class CTAgeSymbol {

    //For words check com.xcompwiz.mystcraft.api.word.WordData in the API for informations and valid entries.
    @ZenMethod
    public static CTBlockSymbol createBlockSymbol(String word, IBlockState ctBlockState, int cardRank) {
        Object internal = ctBlockState.getInternal();
        if (internal == null || !(internal instanceof net.minecraft.block.state.IBlockState)) {
            CraftTweakerAPI.logError("[MystCraft] Tried to register new SymbolBlock with invalid/missing BlockState!");
            return null;
        }
        net.minecraft.block.state.IBlockState mcState = (net.minecraft.block.state.IBlockState) internal;
        return new CTBlockSymbol(ModSymbolsModifiers.BlockModifierContainerObject.create(word, cardRank, mcState));
    }

}
