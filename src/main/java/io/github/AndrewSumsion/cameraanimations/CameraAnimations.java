package io.github.AndrewSumsion.cameraanimations;

import io.github.AndrewSumsion.cameraanimations.animation.LocationProperty;
import io.github.AndrewSumsion.cameraanimations.animation.PropertyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CameraAnimations extends JavaPlugin implements Listener {
    private static CameraAnimations INSTANCE;

    public static CameraAnimations getInstance() {
        return INSTANCE;
    }

    private File animationsFile;
    public boolean useVelocity = false;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        INSTANCE = this;
        Bukkit.getPluginCommand("keyframe").setExecutor(new KeyframeCommandExecutor());
        Bukkit.getPluginCommand("animation").setExecutor(new AnimationCommandExecutor());
        PropertyManager.registerProperty("location", LocationProperty.class);
        PlayerData.initialize();
        Bukkit.getPluginManager().registerEvents(PlayerData.getEventListener(), this);
        saveDefaultConfig();
        animationsFile = new File(getDataFolder(), "animations.yml");
        try {
            animationsFile.createNewFile();
        } catch (IOException e) {
            getLogger().severe("Failed to load animations.yml file! This plugin will not work!");
            throw new RuntimeException(e);
        }
        AnimationHandler.loadAnimations(animationsFile);
    }

    @Override
    public void onDisable() {
        AnimationHandler.saveAnimations(animationsFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getLabel().equalsIgnoreCase("ctest")) {
            Player player = (Player) sender;
            player.setFlySpeed(0.1F);
            player.setWalkSpeed(0.2F);
            useVelocity = !useVelocity;
            player.sendMessage(""+useVelocity);
        }
        return true;
    }
}
