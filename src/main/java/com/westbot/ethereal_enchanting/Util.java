package com.westbot.ethereal_enchanting;

import net.minecraft.util.math.Vec3d;

public class Util {

    public static Vec3d[] drawCircle(Vec3d center, Vec3d center_axis, double radius, int numPoints, double angleOffset) {
        Vec3d[] points = new Vec3d[numPoints];
//        double angleIncrement = 2 * Math.PI / numPoints;
//        for (int i = 0; i < numPoints; ++i) {
//            double angle = i * angleIncrement;
//            double x = center.getX() + Math.cos(angle) * radius;
//            double y = center.getY() + Math.sin(angle) * radius;
//            points[i] = new Vec3d(x, y, center.getZ());
//        }
        Vec3d normalized_axis = center_axis.normalize();


        Vec3d arbitrary_vector = new Vec3d(1, 0, 0);
        if (Math.abs(normalized_axis.x) > 0.99) {
            arbitrary_vector = new Vec3d(0, 1, 0);
        }


        Vec3d perp1 = normalized_axis.crossProduct(arbitrary_vector).normalize();
        Vec3d perp2 = normalized_axis.crossProduct(perp1).normalize();

        for (int i = 0; i < numPoints; i++) {
            double angle = (2 * Math.PI * i / numPoints) + Math.toRadians(angleOffset);
            Vec3d point_on_circle = perp1.multiply(radius * Math.cos(angle))
                .add(perp2.multiply(radius * Math.sin(angle)));
            points[i] = center.add(point_on_circle);
        }


        return points;
    }

    public static double easeInOutSine(double x) {
        return -(Math.cos(Math.PI * x) - 1) / 2;
    }

    public static double easeInOutCircular(double x) {
        if (x < 0.5) {
            return (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2;
        } else {
            return (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
        }
    }

    public static double easeOutBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);

    }

    public static double easeInBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return c3 * x * x * x - c1 * x * x;
    }

    public static Vec3d interpolate(Vec3d a, Vec3d b, double t) {
        return new Vec3d(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t);
    }

}
