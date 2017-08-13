package com.xcompwiz.mystcraft.data;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModRegistryPrimer {

    public static ModRegistryPrimer INSTANCE = new ModRegistryPrimer();
    private Map<Class<?>, List<IForgeRegistryEntry<?>>> bufferedRegistry = new HashMap<>();

    private ModRegistryPrimer() {}

    public static void queueForRegistration(IForgeRegistryEntry<?> entry) {
        Class<?> registryType = entry.getRegistryType();
        INSTANCE.bufferedRegistry.computeIfAbsent(registryType, k -> new LinkedList<>()).add(entry);
    }

    private <V extends IForgeRegistryEntry<V>> void addToRegistry(IForgeRegistry<V> registry) {
        List<V> entries = (List<V>) bufferedRegistry.get(registry.getRegistrySuperType());
        if(entries != null) {
            for (V entry : entries) {
                registry.register(entry);
            }
        }
    }

    @SubscribeEvent
    public void onRegistryEventBlocks(RegistryEvent.Register<Block> event) {
        addToRegistry(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegistryEventItems(RegistryEvent.Register<Item> event) {
        addToRegistry(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegistryEventRecipes(RegistryEvent.Register<IRecipe> event) {
        addToRegistry(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegistryEventSounds(RegistryEvent.Register<SoundEvent> event) {
        addToRegistry(event.getRegistry());
    }

}
