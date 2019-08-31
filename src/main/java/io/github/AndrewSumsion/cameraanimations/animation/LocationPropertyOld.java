package io.github.AndrewSumsion.cameraanimations.animation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LocationPropertyOld extends AnimatableProperty<Location> {
    protected World world;
    protected VectorProperty vectorProperty;
    protected DirectionProperty directionProperty;

    public LocationPropertyOld() {
        super("location", null);
    }

    public LocationPropertyOld(Location value) {
        super("location", value);
        world = value.getWorld();
        vectorProperty = new VectorProperty(value.toVector());
        directionProperty = new DirectionProperty(new DirectionProperty.Direction(value.getPitch(), value.getYaw()));
    }

    public Location get(PropertyContext ctx, Location startValue, double percent) {
        if(startValue.equals(value)) {
            return startValue;
        }
        Vector vector = vectorProperty.get(ctx, startValue.toVector(), percent);
        DirectionProperty.Direction direction = directionProperty.get(ctx, new DirectionProperty.Direction(startValue.getPitch(), startValue.getYaw()), percent);
        return new Location(world, vector.getX(), vector.getY(), vector.getZ(), direction.yaw(), direction.pitch());
    }

    public void apply(PropertyContext ctx, Location startValue, double percent, Player player) {
        player.teleport(get(ctx, startValue, percent));
    }


    @Override
    public void setValue(Location endValue) {
        super.setValue(endValue);
        directionProperty.setValue(new DirectionProperty.Direction(endValue.getPitch(), endValue.getYaw()));
        vectorProperty.setValue(endValue.toVector());
    }

    public void serialize(ConfigurationSection section) {
        ConfigurationSection positionSection = section.createSection("position");
        positionSection.set("world", world.getName());
        vectorProperty.serialize(positionSection);
        ConfigurationSection directionSection = section.createSection("direction");
        directionProperty.serialize(directionSection);
    }

    public void deserialize(ConfigurationSection section) {
        vectorProperty = new VectorProperty();
        directionProperty = new DirectionProperty();
        vectorProperty.deserialize(section.getConfigurationSection("position"));
        directionProperty.deserialize(section.getConfigurationSection("direction"));
        world = Bukkit.getWorld(section.getString("position.world"));
        value = vectorProperty.getValue().toLocation(world, directionProperty.getValue().yaw(), directionProperty.getValue().pitch());
    }
}
