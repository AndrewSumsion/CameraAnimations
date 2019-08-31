package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.CameraAnimations;
import io.github.AndrewSumsion.cameraanimations.Serializable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Keyframe implements Serializable {
    private AnimationCurve animationCurve;
    private LocationProperty locationProperty;
    private LinkedHashMap<String, AnimatableProperty> properties;
    private int length;

    public Keyframe(){}

    public Keyframe(AnimationCurve animationCurve, double length, LocationProperty locationProperty, LinkedHashMap<String, AnimatableProperty> additionalProperties) {
        this.animationCurve = animationCurve;
        this.locationProperty = new VelocityAdapter(locationProperty);
        //this.locationProperty = locationProperty;
        this.properties = additionalProperties;
        this.length = (int)(length * 20D);
    }

    public Keyframe(AnimationCurve animationCurve, double length, LocationProperty locationProperty) {
        this(animationCurve, length, locationProperty, new LinkedHashMap<String, AnimatableProperty>());
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setAnimationCurve(AnimationCurve animationCurve) {
        this.animationCurve = animationCurve;
    }

    public LinkedHashMap<String, AnimatableProperty> getProperties() {
        return properties;
    }

    public LocationProperty getLocationProperty() {
        return locationProperty;
    }

    public AnimationCurve getAnimationCurve() {
        return animationCurve;
    }

    public AnimatableProperty getProperty(String key) {
        return properties.get(key);
    }

    public int[] run(Animation animation, Keyframe lastKeyframe, Player player, Runnable callback) {
        return run(animation, lastKeyframe, player, length, callback);
    }

    public int[] run(final Animation animation, final Keyframe lastKeyframe, final Player player, final int length, final Runnable callback) {
        locationProperty.start(lastKeyframe.getLocationProperty().getValue(), player);
        for(Map.Entry<String, AnimatableProperty> entry : properties.entrySet()) {
            entry.getValue().start(lastKeyframe.getProperty(entry.getKey()).getValue(), player);
        }
        final AtomicInteger count = new AtomicInteger(0);
        final int repeatingTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CameraAnimations.getInstance(), new Runnable() {
            public void run() {
                double percent = (double)count.get() / (double)length;
                if(length == 0) percent = 1;
                applyProperty(animation, locationProperty, lastKeyframe.getLocationProperty().getValue(), player, percent);
                for(Map.Entry<String, AnimatableProperty> entry : properties.entrySet()) {
                    AnimatableProperty lastValue = lastKeyframe.getProperty(entry.getKey());
                    applyProperty(animation, entry.getValue(), lastValue.getValue(), player, percent);
                }
                count.getAndIncrement();
            }
        }, 0, 0);
        int callbackTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(CameraAnimations.getInstance(), new Runnable() {
            public void run() {
                Bukkit.getScheduler().cancelTask(repeatingTaskId);
                locationProperty.end(lastKeyframe.getLocationProperty().getValue(), player);
                for(Map.Entry<String, AnimatableProperty> entry : properties.entrySet()) {
                    entry.getValue().end(lastKeyframe.getProperty(entry.getKey()).getValue(), player);
                }
                if(callback != null) {
                    callback.run();
                }
            }
        }, length);
        return new int[] {repeatingTaskId, callbackTaskId};
    }
    private <T> void applyProperty(Animation animation, AnimatableProperty<T> property, T lastValue, Player player, double percent) {
        double actualPercent = percent;
        if(property.useAnimationCurve()) {
            actualPercent = animationCurve.get(percent);
        }
        property.apply(new AnimatableProperty.PropertyContext(animation, this), lastValue, actualPercent, player);
    }

    public void deserialize(ConfigurationSection section) {
        properties = new LinkedHashMap<String, AnimatableProperty>();
        ConfigurationSection propertiesSection = section.getConfigurationSection("properties");
        for(String name : propertiesSection.getKeys(false)) {
            AnimatableProperty property;
            try {
                property = PropertyManager.getProperty(name).newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            property.deserialize(propertiesSection.getConfigurationSection(name));
            if(name.equals("location")) {
                locationProperty = new VelocityAdapter((LocationProperty) property);
                //locationProperty = (LocationProperty) property;
            } else {
                properties.put(name, property);
            }
        }
        length = (int)(section.getDouble("length") * 20D);
        animationCurve = AnimationCurve.getAnimationCurve(section.getString("animation-curve", "linear"));
        if(animationCurve == null) animationCurve = new AnimationCurve();
    }

    public void serialize(ConfigurationSection section) {
        section.set("length", (double)length / 20);
        ConfigurationSection propertiesSection = section.createSection("properties");
        locationProperty.serialize(propertiesSection.createSection("location"));
        for(Map.Entry<String, AnimatableProperty> entry : properties.entrySet()) {
            ConfigurationSection propertySection = propertiesSection.createSection(entry.getKey());
            entry.getValue().serialize(propertySection);
        }
        section.set("animation-curve", animationCurve != null ? animationCurve.toString() : "linear");
    }
}
