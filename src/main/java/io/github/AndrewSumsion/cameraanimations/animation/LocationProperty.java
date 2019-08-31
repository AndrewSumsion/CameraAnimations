package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.interpolation.Interpolator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LocationProperty extends AnimatableProperty<Location> {
    private Interpolator interpolator;

    public LocationProperty() {
        super("location", null);
    }

    public LocationProperty(Location value, Interpolator interpolator) {
        super("location", value);
        this.interpolator = interpolator;
    }

    public LocationProperty(LocationProperty other, Interpolator interpolator) {
        this(other.getValue(), interpolator);
    }

    public LocationProperty(Location value) {
        this(value, Interpolator.LINEAR);
    }

    @Override
    public Location get(PropertyContext ctx, Location startValue, double percent) {
        Location p1 = startValue.clone();
        Location p0 = p1.clone();
        Location p2 = value.clone();
        Location p3 = p2.clone();


        if(!(ctx.getKeyframeIndex() < 2)) {
            p0 = ctx.getAnimation().getKeyframe(ctx.getKeyframeIndex() - 2).getLocationProperty().getValue();
        }

        if(!(ctx.getKeyframeIndex() + 1 >= ctx.getAnimation().totalKeyframes())) {
            p3 = ctx.getAnimation().getKeyframe(ctx.getKeyframeIndex() + 1).getLocationProperty().getValue();
        }

        double x = interpolator.interpolate(p0.getX(), p1.getX(), p2.getX(), p3.getX(), percent);
        double y = interpolator.interpolate(p0.getY(), p1.getY(), p2.getY(), p3.getY(), percent);
        double z = interpolator.interpolate(p0.getZ(), p1.getZ(), p2.getZ(), p3.getZ(), percent);
        float pitch = (float)interpolator.interpolate(p0.getPitch(), p1.getPitch(), p2.getPitch(), p3.getPitch(), percent);
        float yaw = interpolator.interpolateYaw(p0.getYaw(), p1.getYaw(), p2.getYaw(), p3.getYaw(), (float)percent);
        return new Location(value.getWorld(), x, y, z, yaw, pitch);
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public void apply(PropertyContext ctx, Location lastValue, double percent, Player player) {
        player.teleport(get(ctx, lastValue, percent));
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("world", value.getWorld().getName());
        section.set("x", value.getX());
        section.set("y", value.getY());
        section.set("z", value.getZ());
        section.set("pitch", value.getPitch());
        section.set("yaw", value.getYaw());
    }

    @Override
    public void deserialize(ConfigurationSection section) {
        World world = Bukkit.getWorld(section.getString("world"));
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float pitch = (float)section.getDouble("pitch");
        float yaw = (float)section.getDouble("yaw");
        value = new Location(world, x, y, z, yaw, pitch);
    }
}
