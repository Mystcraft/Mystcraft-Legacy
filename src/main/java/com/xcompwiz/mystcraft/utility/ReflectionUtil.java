package com.xcompwiz.mystcraft.utility;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtil {

    public static Field biomeNameField;

    public static String getBiomeName(Biome b) {
        try {
            return (String) biomeNameField.get(b);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("");
        }
    }

    public static void setFinalStaticField(Class<?> clazz, Object value, String... possibleNames) {
        setFinalInstanceField(clazz, null, value, possibleNames);
    }

    public static <E> void setFinalInstanceField(Class<E> clazz, E instance, Object value, String... possibleNames) {
        try {
            for (String s : possibleNames) {
                Field f = clazz.getDeclaredField(s);
                f.setAccessible(true);
                if(Modifier.isFinal(f.getModifiers())) {
                    Field modifiers = Field.class.getDeclaredField("modifiers");
                    modifiers.setAccessible(true);
                    modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                }
                f.setAccessible(true);
                f.set(instance, value);
            }
        } catch (Exception ignored) {}
    }

    public static void init() {
        biomeNameField = ReflectionHelper.findField(Biome.class, "biomeName", "field_76791_y");
        if(biomeNameField == null) {
            throw new IllegalStateException("Couldn't find biomeName field! Is your forge & minecraft version compatible with this version of Mystcraft? Searched field names: biomeName, field_76791_y");
        }
    }

}
