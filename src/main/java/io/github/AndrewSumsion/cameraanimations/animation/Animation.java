package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.Serializable;
import io.github.AndrewSumsion.cameraanimations.interpolation.Interpolator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Animation implements Serializable {
    private Interpolator interpolator = Interpolator.LINEAR;
    private List<Keyframe> keyframes;

    public Animation() {
        this(new ArrayList<Keyframe>(), Interpolator.LINEAR);
    }

    public Animation(Interpolator interpolator) {
        this(new ArrayList<Keyframe>(), interpolator);
    }

    public Animation(List<Keyframe> keyframes, Interpolator interpolator) {
        this.keyframes = keyframes;
        this.interpolator = interpolator;
    }

    public Animation(Animation animation) {
        YamlConfiguration configuration = new YamlConfiguration();
        animation.serialize(configuration);
        deserialize(configuration);
    }

    public void addKeyframe(Keyframe keyframe) {
        keyframes.add(keyframe);
    }

    public void deleteKeyframe(int index) {
        keyframes.remove(index);
    }

    public Keyframe getKeyframe(int index) {
        return keyframes.get(index);
    }

    public List<Keyframe> getKeyframes() {
        return keyframes;
    }

    public void addKeyframe(int index, Keyframe keyframe) {
        keyframes.add(index, keyframe);
    }

    public void addKeyframe(AnimationCurve animationCurve, double length, Location destination) {
        addKeyframe(new Keyframe(animationCurve, length, new LocationProperty(destination)));
    }

    public int totalKeyframes() {
        return keyframes.size();
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void serialize(ConfigurationSection section) {
        section.set("interpolation", interpolator.name().toLowerCase().replace("_", "-"));
        ConfigurationSection keyframesSection = section.createSection("keyframes");
        for(int i = 0; i < keyframes.size(); i++) {
            keyframes.get(i).serialize(keyframesSection.createSection("keyframe" + (i + 1)));
        }
    }

    public void deserialize(ConfigurationSection section) {
        interpolator = Interpolator.valueOf(section.getString("interpolation").toUpperCase().replace("-", "_"));
        keyframes = new ArrayList<Keyframe>();
        ConfigurationSection keyframesSection = section.getConfigurationSection("keyframes");
        for(int i = 0; i < keyframesSection.getKeys(false).size(); i++) {
            Keyframe keyframe = new Keyframe();
            keyframe.deserialize(keyframesSection.getConfigurationSection("keyframe" + (i + 1)));
            keyframes.add(keyframe);
        }
    }

    /////////////////////////////////////////////////////////////////////////////

    public RunningAnimation play(Player player, int length) {
        RunningAnimation runningAnimation = new RunningAnimation(player, this, length);
        runningAnimation.run();
        return runningAnimation;
    }

    public RunningAnimation play(Player player) {
        return play(player, -1);
    }
}
