package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.Serializable;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class AnimatableProperty<T> implements Serializable {
    protected T value;
    protected String name;

    public AnimatableProperty(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public abstract T get(PropertyContext ctx, T lastValue, double percent);

    public abstract void apply(PropertyContext ctx, T lastValue, double percent, Player player);

    public boolean useAnimationCurve() {
        return true;
    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void start(T lastValue, Player player) {}

    public void end(T lastValue, Player player) {}

    public void saveState(Player player, Map<String, String> data) {}

    public void restoreState(Player player, Map<String, String> data) {}

    public static class PropertyContext {
        private Animation animation;
        private Keyframe keyframe;

        public PropertyContext(Animation animation, Keyframe keyframe) {
            this.animation = animation;
            this.keyframe = keyframe;
        }

        public Animation getAnimation() {
            return animation;
        }

        public Keyframe getKeyframe() {
            return keyframe;
        }

        public int getKeyframeIndex() {
            return animation.getKeyframes().indexOf(keyframe);
        }
    }
}
