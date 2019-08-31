package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RunningAnimation {
    private Player player;
    private Animation animation;
    private int length;
    private double totalDistance = 0D;
    private int runningTaskID = -1;
    private int callbackTaskID = -1;
    private int currentKeyframe;
    private Map<String, String> savedState = new HashMap<String, String>();

    public RunningAnimation(Player player, Animation animation, int length) {
        this.player = player;
        this.animation = animation;
        this.length = length;
        if(animation.totalKeyframes() < 1) return;
        Keyframe lastKeyframe = animation.getKeyframe(0);
        for(Keyframe keyframe : animation.getKeyframes()) {
            totalDistance += lastKeyframe.getLocationProperty().getValue().distance(keyframe.getLocationProperty().getValue());
            lastKeyframe = keyframe;
        }
    }

    public RunningAnimation(Player player, Animation animation) {
        this(player, animation, -1);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(runningTaskID);
        Bukkit.getScheduler().cancelTask(callbackTaskID);
        restoreState(animation.getKeyframe(currentKeyframe));
    }

    public void run() {
        PlayerData.getData(player).setRunningAnimation(this);
        playKeyframe(0);
    }

    private void playKeyframe(final int index) {
        /*
        Teleports the player up to the next open space when the animation finishes
         */
        if(index >= animation.totalKeyframes()) {
            int y = player.getLocation().getBlockY();
            while(player.getWorld().getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType() != Material.AIR) {
                y++;
            }
            Location newLocation = player.getLocation().clone();
            newLocation.setY(y);
            if(y != player.getLocation().getBlockY()) {
                player.teleport(newLocation);
            }
            PlayerData.getData(player).setRunningAnimation(null);
            return;
        }
        final Keyframe keyframe = animation.getKeyframe(index);
        saveState(keyframe);

        Keyframe lastKeyframe = keyframe;
        if(index > 0) {
            lastKeyframe = animation.getKeyframe(index - 1);
        }
        int length = keyframe.getLength();
        if(this.length >= 0) {
            //length = length / this.length;
            length = (int)(this.length / (float) (animation.totalKeyframes() - 1));
        }
        keyframe.getLocationProperty().setInterpolator(animation.getInterpolator());
        if(index == 0) {
            length = 0;
        }
        currentKeyframe = index;
        int[] taskIds = keyframe.run(animation, lastKeyframe, player, length, new Runnable() {
            public void run() {
                restoreState(keyframe);
                playKeyframe(index + 1);
            }
        });
        runningTaskID = taskIds[0];
        callbackTaskID = taskIds[1];
    }

    private void saveState(Keyframe keyframe) {
        keyframe.getLocationProperty().saveState(player, savedState);
        for(AnimatableProperty property : keyframe.getProperties().values()) {
            property.saveState(player, savedState);
        }
    }

    private void restoreState(Keyframe keyframe) {
        keyframe.getLocationProperty().restoreState(player, savedState);
        for(AnimatableProperty property : keyframe.getProperties().values()) {
            property.restoreState(player, savedState);
        }
    }
}
