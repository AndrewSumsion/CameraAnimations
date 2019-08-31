package io.github.AndrewSumsion.cameraanimations.animation;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VectorProperty extends AnimatableProperty<Vector> {
    public VectorProperty() {
        super("vector", null);
    }

    public VectorProperty(Vector value) {
        super("vector", value);
    }

    public Vector get(PropertyContext ctx, Vector startValue, double percent) {
        double x = getPoint(startValue.getX(), value.getX(), percent);
        double y = getPoint(startValue.getY(), value.getY(), percent);
        double z = getPoint(startValue.getZ(), value.getZ(), percent);
        return new Vector(x, y, z);
    }

    public void apply(PropertyContext ctx, Vector startValue, double percent, Player player) {
    }

    private double getPoint(double start, double end, double percent) {
        return (end - start) * percent + start;
    }

    @Override
    public boolean useAnimationCurve() {
        return false;
    }

    public void serialize(ConfigurationSection section) {
        section.set("x", value.getX());
        section.set("y", value.getY());
        section.set("z", value.getZ());
    }

    public void deserialize(ConfigurationSection section) {
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        value = new Vector(x, y, z);
    }
}
