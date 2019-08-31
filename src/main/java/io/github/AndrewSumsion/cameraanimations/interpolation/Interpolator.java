package io.github.AndrewSumsion.cameraanimations.interpolation;

public enum Interpolator {
    LINEAR {
        @Override
        public double interpolate(double p0, double p1, double p2, double p3, double x) {
            return Interpolations.lerp(p1, p2, x);
        }

        @Override
        public float interpolateYaw(float p0, float p1, float p2, float p3, double x) {
            return Interpolations.lerpYaw(p1, p2, x);
        }
    },
    CUBIC_HERMITE {
        @Override
        public double interpolate(double p0, double p1, double p2, double p3, double x) {
            return Interpolations.cubicHermite(p0, p1, p2, p3, x);
        }

        @Override
        public float interpolateYaw(float p0, float p1, float p2, float p3, double x) {
            return Interpolations.cubicHermiteYaw(p0, p1, p2, p3, (float) x);
        }
    },
    CUBIC {
        @Override
        public double interpolate(double p0, double p1, double p2, double p3, double x) {
            return Interpolations.cubic(p0, p1, p2, p3, x);
        }

        @Override
        public float interpolateYaw(float p0, float p1, float p2, float p3, double x) {
            return Interpolations.cubicYaw(p0, p1, p2, p3, (float) x);
        }
    },
    CATMULL_ROM {
        @Override
        public double interpolate(double p0, double p1, double p2, double p3, double x) {
            return Interpolations.catmullRom(p0, p1, p2, p3, x);
        }

        @Override
        public float interpolateYaw(float p0, float p1, float p2, float p3, double x) {
            return Interpolations.catmullRomYaw(p0, p1, p2, p3, x);
        }
    }
    ;
    public abstract double interpolate(double p0, double p1, double p2, double p3, double x);
    public abstract float interpolateYaw(float p0, float p1, float p2, float p3, double x);
    public String getUserFriendlyName() {
        return name().replace("_", "-").toLowerCase();
    }
    public static Interpolator fromUserFriendlyName(String name) {
        return valueOf(name.replace("-", "_").toUpperCase());
    }
}
