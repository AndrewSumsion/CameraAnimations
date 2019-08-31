package io.github.AndrewSumsion.cameraanimations;

import io.github.AndrewSumsion.cameraanimations.animation.Animation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnimationHandler {
    private static Map<String, Animation> animations = new HashMap<String, Animation>();
    private static File configFile;

    void init(File configFile) {
        AnimationHandler.configFile = configFile;
    }

    public static void loadAnimations(File configFile) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if(!config.isConfigurationSection("animations")) return;
        ConfigurationSection animationsSection = config.getConfigurationSection("animations");
        for(String name : animationsSection.getKeys(false)) {
            Animation animation = new Animation();
            ConfigurationSection section = animationsSection.getConfigurationSection(name);
            animation.deserialize(section);
            animations.put(name, animation);
        }
    }

    public static void saveAnimations(File configFile) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection animationsSection = getSection(config, "animations");
        for(Map.Entry<String, Animation> entry : animations.entrySet()) {
            saveAnimation(entry.getKey());
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveAnimation(String name) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection animationsSection = getSection(config, "animations");
        ConfigurationSection section = animationsSection.createSection(name);
        Animation animation = animations.get(name);
        animation.serialize(section);
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConfigurationSection getSection(ConfigurationSection section, String name) {
        return section.isConfigurationSection(name) ? section.getConfigurationSection(name) : section.createSection(name);
    }

    public static Animation getAnimation(String name) {
        return animations.get(name);
    }

    public static void addAnimation(String name, Animation animation) {
        animations.put(name, animation);
    }

    public static void deleteAnimation(String name) {
        animations.remove(name);
    }

    public static Map<String, Animation> getAnimations() {
        return animations;
    }
}
