package io.github.AndrewSumsion.cameraanimations;

import io.github.AndrewSumsion.cameraanimations.animation.Animation;
import io.github.AndrewSumsion.cameraanimations.animation.AnimationCurve;
import io.github.AndrewSumsion.cameraanimations.animation.RunningAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private static Map<UUID, PlayerData> data = new HashMap<UUID, PlayerData>();

    public static PlayerData getData(Player player) {
        return data.get(player.getUniqueId());
    }

    private Player player;
    private Animation queuedAnimation = new Animation();
    private RunningAnimation runningAnimation;
    private String editing = null;

    private PlayerData(Player player) {
        this.player = player;
    }

    public Animation getQueuedAnimation() {
        return queuedAnimation;
    }

    public void addKeyframe(Location location, float duration) {
        queuedAnimation.addKeyframe(new AnimationCurve(), duration, location);
    }

    public String getEditing() {
        return editing;
    }

    public void setEditing(String editing) {
        this.editing = editing;
    }

    public RunningAnimation getRunningAnimation() {
        return runningAnimation;
    }

    public void setRunningAnimation(RunningAnimation runningAnimation) {
        this.runningAnimation = runningAnimation;
    }

    public void resetQueuedAnimation() {
        queuedAnimation = new Animation();
    }

    public void setQueuedAnimation(Animation queuedAnimation) {
        this.queuedAnimation = queuedAnimation;
    }

    static Listener getEventListener() {
        return new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                data.put(event.getPlayer().getUniqueId(), new PlayerData(event.getPlayer()));
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                data.remove(event.getPlayer().getUniqueId());
            }

            @EventHandler
            public void onPlayerKick(PlayerKickEvent event) {
                data.remove(event.getPlayer().getUniqueId());
            }
        };
    }

    static void initialize() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            data.put(player.getUniqueId(), new PlayerData(player));
        }
    }
}
