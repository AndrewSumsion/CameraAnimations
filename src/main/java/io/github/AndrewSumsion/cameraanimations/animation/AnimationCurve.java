package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.CameraAnimations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimationCurve {
    public double get(double x) {
        return x;
    }

    public static AnimationCurve getAnimationCurve(String name) {
        if(name.equalsIgnoreCase("linear")) return new AnimationCurve();
        String calculationType = CameraAnimations.getInstance().getConfig().getString("bezier-calculation", "explicit");;
        if(calculationType.equalsIgnoreCase("explicit")) {
            if (name.equalsIgnoreCase("ease")) return ExplicitBezierCurve.ease();
            if (name.equalsIgnoreCase("ease-in")) return ExplicitBezierCurve.easeIn();
            if (name.equalsIgnoreCase("ease-out")) return ExplicitBezierCurve.easeOut();
            if (name.equalsIgnoreCase("ease-in-out")) return ExplicitBezierCurve.easeInOut();
        } else if(calculationType.equalsIgnoreCase("plotted")) {
            if (name.equalsIgnoreCase("ease")) return PlottedBezierCurve.ease();
            if (name.equalsIgnoreCase("ease-in")) return PlottedBezierCurve.easeIn();
            if (name.equalsIgnoreCase("ease-out")) return PlottedBezierCurve.easeOut();
            if (name.equalsIgnoreCase("ease-in-out")) return PlottedBezierCurve.easeInOut();
        }
        Pattern cubicBezierPattern = Pattern.compile("^cubic-bezier\\((.*),(.*),(.*),(.*)\\)$");
        Matcher matcher = cubicBezierPattern.matcher(name);
        if(matcher.find()) {
            try {
                if(calculationType.equalsIgnoreCase("explicit")) {
                    return new ExplicitBezierCurve(
                            Double.valueOf(matcher.group(1)),
                            Double.valueOf(matcher.group(2)),
                            Double.valueOf(matcher.group(3)),
                            Double.valueOf(matcher.group(4))
                    );
                } else if(calculationType.equalsIgnoreCase("plotted")) {
                    return new PlottedBezierCurve(
                            Double.valueOf(matcher.group(1)),
                            Double.valueOf(matcher.group(2)),
                            Double.valueOf(matcher.group(3)),
                            Double.valueOf(matcher.group(4)),
                            0.05
                    );
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "linear";
    }
}
