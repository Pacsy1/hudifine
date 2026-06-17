package com.hudifine.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class MinecraftCompat {
    private MinecraftCompat() {
    }

    public static Screen getCurrentScreen(Minecraft client) {
        if (client == null) {
            return null;
        }

        Object byMethod = invokeFirstNoArg(client, "getScreen", "currentScreen", "screen");
        if (byMethod instanceof Screen screen) {
            return screen;
        }

        Object byField = readFirstField(client, "screen", "currentScreen");
        if (byField instanceof Screen screen) {
            return screen;
        }

        return null;
    }

    public static void setScreen(Minecraft client, Screen screen) {
        if (client == null) {
            return;
        }

        if (invokeFirst(client, screen, "setScreenAndShow", "setScreen", "setCurrentScreen")) {
            return;
        }

        writeFirstField(client, screen, "screen", "currentScreen");
    }

    public static boolean isSingleplayer(Minecraft client) {
        if (client == null) {
            return false;
        }

        Object value = invokeFirstNoArg(client, "hasSingleplayerServer", "isSingleplayer", "isInSingleplayer");
        if (value instanceof Boolean bool) {
            return bool;
        }

        return false;
    }

    private static Object invokeFirstNoArg(Object target, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                return method.invoke(target);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    private static boolean invokeFirst(Object target, Screen screen, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName, Screen.class);
                method.invoke(target, screen);
                return true;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return false;
    }

    private static Object readFirstField(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                Field field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    private static void writeFirstField(Object target, Screen screen, String... fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                Field field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, screen);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }
}
