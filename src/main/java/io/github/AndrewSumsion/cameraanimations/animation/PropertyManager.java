package io.github.AndrewSumsion.cameraanimations.animation;

import java.util.HashMap;
import java.util.Map;

public class PropertyManager {
    private static Map<String, Class<? extends AnimatableProperty>> properties = new HashMap<String, Class<? extends AnimatableProperty>>();

    public static void registerProperty(String name, Class<? extends AnimatableProperty> propertyClass) {
        properties.put(name, propertyClass);
    }

    public static Class<? extends AnimatableProperty> getProperty(String name) {
        return properties.get(name);
    }
}
