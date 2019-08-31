package io.github.AndrewSumsion.cameraanimations.util;

import org.bukkit.Bukkit;

public class ReflectionUtils {
    private static ReflectionUtils INSTANCE;

    private String versionString;

    private ReflectionUtils() {
        versionString = Bukkit.getServer().getClass().getName().split("\\.")[3];
    }

    public static ReflectionUtils getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ReflectionUtils();
        }
        return INSTANCE;
    }

    public static Class<?> getClass(String path) {
        String nmsString = "net.minecraft.server.";
        String craftbukkitString = "org.bukkit.craftbukkit.";
        try {
            if(path.startsWith(nmsString)) {
                return Class.forName(path.replace(nmsString, nmsString + getInstance().versionString + "."));
            } else if(path.startsWith(craftbukkitString)) {
                return Class.forName(path.replace(craftbukkitString, craftbukkitString + getInstance().versionString + "."));
            } else {
                return Class.forName(path);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVersionString() {
        return getInstance().versionString;
    }
}
