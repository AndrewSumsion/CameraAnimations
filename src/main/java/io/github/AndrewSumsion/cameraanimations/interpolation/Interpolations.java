package io.github.AndrewSumsion.cameraanimations.interpolation;

/**
 * Interpolation methods
 *
 * This class is responsible for doing different kind of interpolations. Cubic
 * interpolation code was from website below, but BauerCam also uses this code.
 *
 * @author mchorse
 * @link http://paulbourke.net/miscellaneous/interpolation/
 * @link https://github.com/daipenger/BauerCam
 */
public class Interpolations
{
    /**
     * Linear interpolation
     */
    public static double lerp(double a, double b, double position)
    {
        return a + (b - a) * position;
    }

    /**
     * Special interpolation method for interpolating yaw. The problem with yaw,
     * is that it may go in the "wrong" direction when having, for example,
     * -170 (as a) and 170 (as b) degress or other way around (170 and -170).
     *
     * This interpolation method fixes this problem.
     */
    public static float lerpYaw(float a, float b, double position)
    {
        a = wrapDegrees(a);
        b = wrapDegrees(b);

        return (float)lerp(a, normalizeYaw(a, b), position);
    }

    /**
     * Cubic interpolation using Hermite between y1 and y2. Taken from paul's
     * website.
     *
     * @param y0 - points[x-1]
     * @param y1 - points[x]
     * @param y2 - points[x+1]
     * @param y3 - points[x+2]
     * @param x - step between 0 and 1
     */
    public static double cubicHermite(double y0, double y1, double y2, double y3, double x)
    {
        double a = -0.5 * y0 + 1.5 * y1 - 1.5 * y2 + 0.5 * y3;
        double b = y0 - 2.5 * y1 + 2 * y2 - 0.5 * y3;
        double c = -0.5 * y0 + 0.5 * y2;

        /* In original article, the return was:
         *
         * ax^3 + bx^2 + cx + y1
         *
         * But expression below is simply a refactored version of the
         * expression above which is more readable. If you'll decompose return
         * you'll get the same formula above:
         *
         * ax^3 + bx^2 + cx + y1
         * (ax^2 + b*x + c) * x + y1
         * ((ax + b) * x + c) * x + y1
         *
         * That's it folks.
         */
        return ((a * x + b) * x + c) * x + y1;
    }

    /**
     * Yaw normalization for cubic interpolation
     */
    public static float cubicHermiteYaw(float y0, float y1, float y2, float y3, float position)
    {
        y0 = wrapDegrees(y0);
        y1 = wrapDegrees(y1);
        y2 = wrapDegrees(y2);
        y3 = wrapDegrees(y3);

        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);

        return (float)cubicHermite(y0, y1, y2, y3, position);
    }

    /**
     * Cubic interpolation between y1 and y2. Taken from paul's website.
     *
     * @param y0 - points[x-1]
     * @param y1 - points[x]
     * @param y2 - points[x+1]
     * @param y3 - points[x+2]
     * @param x - step between 0 and 1
     */
    public static double cubic(double y0, double y1, double y2, double y3, double x)
    {
        double a = y3 - y2 - y0 + y1;
        double b = y0 - y1 - a;
        double c = y2 - y0;

        return ((a * x + b) * x + c) * x + y1;
    }

    /**
     * Yaw normalization for cubic interpolation
     */
    public static float cubicYaw(float y0, float y1, float y2, float y3, float position)
    {
        y0 = wrapDegrees(y0);
        y1 = wrapDegrees(y1);
        y2 = wrapDegrees(y2);
        y3 = wrapDegrees(y3);

        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);

        return (float) cubic(y0, y1, y2, y3, position);
    }

    public static double catmullRom(double y0, double y1, double y2, double y3, double x) {
        return 0.5f * ((2 * y1) +
                (y2 - y0) * x +
                (2* y0 - 5* y1 + 4* y2 - y3) * x * x +
                (3* y1 - y0 - 3 * y2 + y3) * x * x * x);
    }

    public static float catmullRomYaw(float y0, float y1, float y2, float y3, double position) {
        y0 = wrapDegrees(y0);
        y1 = wrapDegrees(y1);
        y2 = wrapDegrees(y2);
        y3 = wrapDegrees(y3);

        y1 = normalizeYaw(y0, y1);
        y2 = normalizeYaw(y1, y2);
        y3 = normalizeYaw(y2, y3);

        return (float) catmullRom(y0, y1, y2, y3, position);
    }

    /**
     * Normalize yaw rotation (argument {@code b}) based on the previous
     * yaw rotation.
     */
    public static float normalizeYaw(float a, float b)
    {
        float diff = a - b;

        if (diff > 180 || diff < -180)
        {
            diff = Math.copySign(360 - Math.abs(diff), diff);

            return a + diff;
        }

        return b;
    }

    private static float wrapDegrees(float f) {
        f %= 360F;
        if (f >= 180F) {
            f -= 360F;
        }

        if (f < -180F) {
            f += 360F;
        }

        return f;
    }
}