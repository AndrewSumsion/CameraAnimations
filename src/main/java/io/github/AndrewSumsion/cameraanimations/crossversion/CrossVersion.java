package io.github.AndrewSumsion.cameraanimations.crossversion;

import io.github.AndrewSumsion.cameraanimations.util.ReflectionUtils;
import org.bukkit.entity.Player;

public abstract class CrossVersion {
    private static CrossVersion INSTANCE;

    public static CrossVersion getInstance() {
        if(INSTANCE == null) {
            INSTANCE = createInstance();
        }
        return INSTANCE;
    }

    private static CrossVersion createInstance() {
        try {
            return (CrossVersion) Class.forName("io.github.AndrewSumsion.cameraanimations.crossversion.CrossVersion" + ReflectionUtils.getVersionString()).newInstance();
        } catch (ClassNotFoundException e) {
            return new CrossVersionReflection();
        } catch (IllegalAccessException e) {
            return new CrossVersionReflection();
        } catch (InstantiationException e) {
            return new CrossVersionReflection();
        }
    }

    protected CrossVersion() {}

    public abstract void relativeTeleport(Player player, float yaw, float pitch);
}
