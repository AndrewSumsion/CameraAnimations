package io.github.AndrewSumsion.cameraanimations.crossversion;

import net.minecraft.server.v1_12_R1.PacketPlayOutPosition;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.Set;

public class CrossVersionv1_12_R1 extends CrossVersion {

    @Override
    public void relativeTeleport(Player player, float yaw, float pitch) {
        Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> flagSet = new HashSet<PacketPlayOutPosition.EnumPlayerTeleportFlags>();
        flagSet.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X);
        flagSet.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y);
        flagSet.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z);
        ((CraftPlayer) player).getHandle().playerConnection.a(0, 0, 0, yaw, pitch, flagSet, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}
