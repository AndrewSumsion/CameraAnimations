package io.github.AndrewSumsion.cameraanimations.crossversion;

import io.github.AndrewSumsion.cameraanimations.util.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CrossVersionReflection extends CrossVersion {
    protected CrossVersionReflection() {
        super();
    }

    private Method tpMethod;

    @Override
    public void relativeTeleport(Player player, float yaw, float pitch) {
        System.out.println("Using Reflection");
        try {
            relativeTeleportInternal(player, yaw, pitch);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void relativeTeleportInternal(Player player, float yaw, float pitch) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> playerConnectionClass = ReflectionUtils.getClass("net.minecraft.server.PlayerConnection");
        if(tpMethod == null) {
            for(Method method : playerConnectionClass.getDeclaredMethods()) {
                if(Arrays.equals(method.getParameterTypes(),
                        new Class<?>[] {double.class, double.class, double.class, float.class, float.class, Set.class})) {
                    tpMethod = method;
                    break;
                }
            }
        }

        Set flags = new HashSet();
        Class<?> teleportFlagsClass = ReflectionUtils.getClass("net.minecraft.server.PacketPlayOutPosition$EnumPlayerTeleportFlags");
        flags.add(teleportFlagsClass.getDeclaredField("X").get(null));
        flags.add(teleportFlagsClass.getDeclaredField("Y").get(null));
        flags.add(teleportFlagsClass.getDeclaredField("Z").get(null));



        Class<?> craftPlayerClass;
        craftPlayerClass = ReflectionUtils.getClass("org.bukkit.craftbukkit.entity.CraftPlayer");
        Object craftPlayer = craftPlayerClass.cast(player);
        Object entityPlayer = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
        Class entityPlayerClass = ReflectionUtils.getClass("net.minecraft.server.EntityPlayer");
        Object playerConnection = entityPlayerClass.getDeclaredField("playerConnection").get(entityPlayer);
        tpMethod.invoke(playerConnection, 0D, 0D, 0D, yaw, pitch, flags);
    }

    private void fallback(Player player, float yaw, float pitch) {
        Location location = player.getLocation().clone();
        location.setYaw(yaw);
        location.setPitch(pitch);
        player.teleport(location);
    }
}
