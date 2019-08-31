package io.github.AndrewSumsion.cameraanimations.animation;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DirectionProperty extends AnimatableProperty<DirectionProperty.Direction> {
    public DirectionProperty() {
        super("direction",null);
    }

    public DirectionProperty(Direction value) {
        super("direction", value);
    }

    private float getYawDifference(float start, float end) {
        float result = end - start;
        result = (result + 180) % 360 - 180;
        float a = result;
        float b = (result + 360) % 360;
        if(Math.abs(a) < Math.abs(b)) {
            return a;
        } else {
            return b;
        }
    }

    public Direction get(PropertyContext ctx, Direction startValue, double percent) {
        float pitchDifference = value.pitch() - startValue.pitch();
        float yawDifference = getYawDifference(startValue.yaw(), value.yaw());
        return new Direction(startValue.pitch() + pitchDifference * (float)percent, startValue.yaw() + yawDifference * (float)percent);
    }

    public void apply(PropertyContext ctx, Direction startingValue, double percent, Player player) {
    }

    public void serialize(ConfigurationSection section) {
        section.set("pitch", value.pitch());
        section.set("yaw", value.yaw());
    }

    public void deserialize(ConfigurationSection section) {
        float pitch = (float)section.getDouble("pitch");
        float yaw = (float)section.getDouble("yaw");
        value = new Direction(pitch, yaw);
    }

    public static class Direction {
        private float pitch;
        private float yaw;

        public Direction(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = simplify(yaw);
        }

        public float pitch() {
            return pitch;
        }

        public float yaw() {
            return yaw;
        }

        public Direction clone() {
            return new Direction(pitch, yaw);
        }

        public Direction multiply(float factor) {
            pitch *= factor;
            yaw *= factor;
            return this;
        }

        public Direction add(Direction other) {
            pitch += other.pitch;
            yaw += other.yaw;
            return this;
        }

        public void normalize() {
            yaw = (yaw + 360) % 360;
            yaw -= yaw > 180 ? 360 : 0;
        }
    }
    private static float simplify(float angle) {
        return (360 + angle) % 360;
    }
}
