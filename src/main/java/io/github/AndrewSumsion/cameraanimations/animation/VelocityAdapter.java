package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.CameraAnimations;
import io.github.AndrewSumsion.cameraanimations.crossversion.CrossVersion;
import io.github.AndrewSumsion.cameraanimations.interpolation.Interpolator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Map;

public class VelocityAdapter extends LocationProperty {
    private LocationProperty property;
    private Location lastLocation;

    public VelocityAdapter(LocationProperty property) {
        this.property = property;
    }

    @Override
    public void start(Location lastValue, Player player) {
        player.teleport(lastValue);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0);
    }

    @Override
    public void apply(PropertyContext ctx, Location startValue, double percent, Player player) {
        if(!CameraAnimations.getInstance().useVelocity) {
            property.apply(ctx, startValue, percent, player);
            return;
        }
        if(lastLocation == null) {
            lastLocation = startValue;
            return;
        }
        Location newLocation = property.get(ctx, startValue, percent);
        Vector velocity = newLocation.toVector().subtract(lastLocation.toVector());
        System.out.println(velocity.length());
        player.setVelocity(velocity);
        lastLocation = newLocation;
        CrossVersion.getInstance().relativeTeleport(player, newLocation.getYaw(), newLocation.getPitch());
    }

    @Override
    public Location get(PropertyContext ctx, Location startValue, double percent) {
        return property.get(ctx, startValue, percent);
    }

    @Override
    public boolean useAnimationCurve() {
        return property.useAnimationCurve();
    }

    @Override
    public Location getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(Location endValue) {
        property.setValue(endValue);
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        property.setInterpolator(interpolator);
    }

    @Override
    public Interpolator getInterpolator() {
        return property.getInterpolator();
    }

    @Override
    public void saveState(Player player, Map<String, String> data) {
        System.out.println("state saved");
        data.put("allowflight", String.valueOf(player.getAllowFlight()));
        data.put("flying", String.valueOf(player.isFlying()));
        data.put("flyspeed", String.valueOf(player.getFlySpeed()));
    }

    @Override
    public void restoreState(Player player, Map<String, String> data) {
        System.out.println("state restored");
        player.setAllowFlight(Boolean.valueOf(data.get("allowflight")));
        player.setFlying(Boolean.valueOf(data.get("flying")));
        player.setFlySpeed(Float.valueOf(data.get("flyspeed")));
    }

    @Override
    public void serialize(ConfigurationSection section) {
        property.serialize(section);
    }

    @Override
    public void deserialize(ConfigurationSection section) {
        property.deserialize(section);
    }
}
