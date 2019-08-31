package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.CameraAnimations;

public class ExplicitBezierCurve extends AnimationCurve {
    private double control1x;
    private double control1y;
    private double control2x;
    private double control2y;
    private double accuracy;

    public static ExplicitBezierCurve ease() {
        return new ExplicitBezierCurve(0.25, 0.1, 0.25, 1) {
            @Override
            public String toString() {
                return "ease";
            }
        };
    }
    public static ExplicitBezierCurve easeIn() {
        return new ExplicitBezierCurve(0.42, 0, 1, 1) {
            @Override
            public String toString() {
                return "ease-in";
            }
        };
    }
    public static ExplicitBezierCurve easeOut() {
        return new ExplicitBezierCurve(0, 0, 0.58, 1) {
            @Override
            public String toString() {
                return "ease-out";
            }
        };
    }
    public static ExplicitBezierCurve easeInOut() {
        return new ExplicitBezierCurve(0.42, 0, 0.58, 1) {
            @Override
            public String toString() {
                return "ease-in-out";
            }
        };
    }

    public ExplicitBezierCurve(double control1x, double control1y, double control2x, double control2y) {
        this.control1x = control1x;
        this.control1y = control1y;
        this.control2x = control2x;
        this.control2y = control2y;
        this.accuracy = CameraAnimations.getInstance().getConfig().getDouble("bezier-accuracy");
    }

    public double get(double x) {
        double t = 0D;
        double lastx = 0D;
        double lasty = 0D;
        while(true) {
            double currentx = 3 * ((1 - t) * (1 - t)) * t * control1x + 3 * (1 - t) * (t*t) * control2x + t * t * t;
            double currenty = 3 * ((1 - t) * (1 - t)) * t * control1y + 3 * (1 - t) * (t*t) * control2y + t * t * t;
            if(currentx > x) {
                double slope = (currenty - lasty) / (currentx - lastx);
                double y = slope * (x - lastx) + lasty;
                return y;
            }
            lastx = currentx;
            lasty = currenty;
            t += accuracy;
        }
    }

    @Override
    public String toString() {
        return String.format("cubicBezier(%s,%s,%s,%s)", control1x, control1y, control2x, control2y);
    }
}
