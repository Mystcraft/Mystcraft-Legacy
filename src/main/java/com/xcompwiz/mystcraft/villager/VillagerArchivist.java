package com.xcompwiz.mystcraft.villager;

import java.util.*;

import com.google.common.collect.Lists;
import com.xcompwiz.mystcraft.api.MystObjects;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import javax.annotation.Nullable;

public class VillagerArchivist extends VillagerRegistry.VillagerProfession implements EntityVillager.ITradeList {

	private List<IMerchantRecipeProvider> items	= new ArrayList<>();

	private final ArchivistCareer career;

	//Hellfire> First param is registry name! modid:name all lowercase. *has* to be unique.
	public VillagerArchivist() {
		//This constructor is deprecated cause its zombie-villager textures will default out to the minecraft one.
		//Unless we wanna do a custom one, this is fine for now.
		super(MystObjects.MystcraftModId + ":archivist",
				"mystcraft:textures/villager/archivist.png",
				"minecraft:textures/entity/zombie_villager/zombie_villager.png");

        //Also hard-registers the career onto the profession. thus we only call it once.
		this.career = new ArchivistCareer(this, "archivist");
	}

	//Only 1 available. index always 0. don't do unnecessary stuffs, just returning that once instance.
	@Override
	public VillagerRegistry.VillagerCareer getCareer(int id) {
		return career;
	}

	@Override
	public int getRandomCareer(Random rand) {
		return 0;
	}

	@Override
    //Select recipes to add to the villager trades (the recipeList param)
	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
	    for (IMerchantRecipeProvider provider : items) {
	        recipeList.addAll(provider.createNewMerchantRecipes(merchant, random));
        }

        Iterator<MerchantRecipe> iterator = recipeList.iterator();
	    List<MerchantRecipeMyst> toadd = new LinkedList<>();
        while (iterator.hasNext()) {
            MerchantRecipe recipe = iterator.next();
            if (!(recipe instanceof MerchantRecipeMyst)) {
                iterator.remove();
                toadd.add(new MerchantRecipeMyst(recipe)); //Wrap and add later to avoid CMEs
            }
        }
        recipeList.addAll(toadd);
    }

	public void registerRecipe(IMerchantRecipeProvider recipe) {
		items.add(recipe);
	}

	public static class ArchivistCareer extends VillagerRegistry.VillagerCareer {

	    private final VillagerArchivist parent;

        private ArchivistCareer(VillagerArchivist parent, String name) {
			super(parent, name);
			this.parent = parent;
		}

		@Nullable
		@Override
		//Returns a List of a list of potential trades. in the end the villager-logic will call upon the
		//ITradeList in order to potentially have trades added to its MerchantRecipeList
		public List<EntityVillager.ITradeList> getTrades(int level) {
			return Lists.newArrayList(parent); //We don't really care about the level. are recipes are treated level independent for now.
		}
	}

}
