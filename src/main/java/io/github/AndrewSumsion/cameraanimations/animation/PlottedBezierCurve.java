package io.github.AndrewSumsion.cameraanimations.animation;

import io.github.AndrewSumsion.cameraanimations.CameraAnimations;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlottedBezierCurve extends AnimationCurve {
    private double control1x;
    private double control1y;
    private double control2x;
    private double control2y;
    private Map<Double, Double> points = new LinkedHashMap<Double, Double>();

    public static PlottedBezierCurve ease() {
        return new PlottedBezierCurve(0.25, 0.1, 0.25, 1, 0.05) {
            @Override
            public String toString() {
                return "ease";
            }
        };
    }
    public static PlottedBezierCurve easeIn() {
        return new PlottedBezierCurve(0.42, 0, 1, 1, 0.05) {
            @Override
            public String toString() {
                return "ease-in";
            }
        };
    }
    public static PlottedBezierCurve easeOut() {
        return new PlottedBezierCurve(0, 0, 0.58, 1, 0.05) {
            @Override
            public String toString() {
                return "ease-out";
            }
        };
    }
    public static PlottedBezierCurve easeInOut() {
        return new PlottedBezierCurve(0.42, 0, 0.58, 1, 0.05) {
            @Override
            public String toString() {
                return "ease-in-out";
            }
        };
    }

    public PlottedBezierCurve(double control1x, double control1y, double control2x, double control2y, double accuracy) {
        this.control1x = control1x;
        this.control1y = control1y;
        this.control2x = control2x;
        this.control2y = control2y;
        generate(CameraAnimations.getInstance().getConfig().getDouble("bezier-accuracy"));
    }

    private void generate(double accuracy) {
        double t = 0D;
        while(true) {
            double x = 3 * ((1 - t) * (1 - t)) * t * control1x + 3 * (1 - t) * (t*t) * control2x + Math.pow(t, 3);
            double y = 3 * ((1 - t) * (1 - t)) * t * control1y + 3 * (1 - t) * (t*t) * control2y + Math.pow(t, 3);
            points.put(x, y);
            t += accuracy;
            if(x > 1) {
                break;
            }
        }
    }

    public double get(double x) {
        double point1x = 0;
        double point1y = 0;
        double point2x = 0;
        double point2y = 0;
        double lastx = 0;
        double lasty = 0;
        for(Map.Entry<Double, Double> entry : points.entrySet()) {
            if(entry.getKey() > x) {
                point2x = entry.getKey();
                point2y = entry.getValue();
                point1x = lastx;
                point1y = lasty;
                break;
            }
            lastx = entry.getKey();
            lasty = entry.getValue();
        }
        double slope = (point2y - point1y) / (point2x - point1x);
        double y = slope * (x - point1x);
        return y + point1y;
    }

    @Override
    public String toString() {
        return String.format("cubicBezier(%s,%s,%s,%s)", control1x, control1y, control2x, control2y);
    }

    // do not attempt
    public static void main(String[] args) {
        PlottedBezierCurve bezier = new PlottedBezierCurve(0.04, -0.79, 0.91, 1.94, 0.000001);
        for(int i = 0; i < 999999; i++) {
            double x = ((double)i + 1D)/1000000D;
            System.out.println("(" + x + "," + bezier.get(x) + ")");
        }
    }
}
