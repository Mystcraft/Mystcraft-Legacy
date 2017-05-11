package com.xcompwiz.mystcraft.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtil {

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

}
