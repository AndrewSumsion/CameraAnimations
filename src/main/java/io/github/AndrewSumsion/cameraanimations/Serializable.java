package io.github.AndrewSumsion.cameraanimations;

import org.bukkit.configuration.ConfigurationSection;

public interface Serializable {
    void serialize(ConfigurationSection section);
    void deserialize(ConfigurationSection section);
}
