package com.westbot.ethereal_enchanting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Util {

    public enum XPLevel {
        LEVEL1(1, 7),
        LEVEL2(2, 16),
        LEVEL3(3, 27),
        LEVEL4(4, 40),
        LEVEL5(5, 55),
        LEVEL6(6, 72),
        LEVEL7(7, 91),
        LEVEL8(8, 112),
        LEVEL9(9, 135),
        LEVEL10(10, 160),
        LEVEL11(11, 187),
        LEVEL12(12, 216),
        LEVEL13(13, 247),
        LEVEL14(14, 280),
        LEVEL15(15, 315),
        LEVEL16(16, 352),
        LEVEL17(17, 394),
        LEVEL18(18, 441),
        LEVEL19(19, 493),
        LEVEL20(20, 550),
        LEVEL21(21, 612),
        LEVEL22(22, 679),
        LEVEL23(23, 751),
        LEVEL24(24, 828),
        LEVEL25(25, 910),
        LEVEL26(26, 997),
        LEVEL27(27, 1083),
        LEVEL28(28, 1186),
        LEVEL29(29, 1288),
        LEVEL30(30, 1395),
        LEVEL31(31, 1507),
        LEVEL32(32, 1628);

        private final int level;
        private final int points;

        XPLevel(int level, int points) {
            this.level = level;
            this.points = points;
        }

        public int getPoints() {
            return points;
        }

        public int getLevel() {
            return level;
        }

    }

    public static int getXPLevelFromPoints(int points) {
        int lvl = 0;

        if (points < 352) {
            lvl = (int) Math.sqrt(points+9)-3;
        } else if (points < 1507) {
            lvl = (int) ((81.0/10.0) + Math.sqrt((2.0/5.0)*(points - (7839.0/40.0))));
        } else {
            lvl = 32;
        }
        return lvl;
    }

    public static Vec3d[] drawCircle(Vec3d center, Vec3d center_axis, double radius, int numPoints, double angleOffset) {
        Vec3d[] points = new Vec3d[numPoints];

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

    @Nullable
    public static Vec3d[][] orbitItems(ItemStack stack1, ItemStack stack2, ItemStack stack3, ItemStack stack4, World world) {
        if (stack1.isEmpty()) {
            return null;
        }
        if (stack2.isEmpty()) { // single item
            return new Vec3d[][]{new Vec3d[]{new Vec3d(0, 0, 0), new Vec3d(0, 0, 0)}};
        }
        if (stack3.isEmpty()) { // dual orbit
            float yaw = (world.getTime() * 5f) % 360;
            Vec3d[] points = drawCircle(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), 0.8, 2, yaw);

            return new Vec3d[][]{
                new Vec3d[]{points[0], new Vec3d(0, -yaw*2, 0)},
                new Vec3d[]{points[1], new Vec3d(0, -yaw*2, 0)}
            };
        }
        if (stack4.isEmpty()) { // triple orbit
            float yaw = (world.getTime() * 5f) % 360;
            Vec3d[] points = drawCircle(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), 0.9, 3, yaw);

            return new Vec3d[][]{
                new Vec3d[]{points[0], new Vec3d(0, -yaw*2, 0)},
                new Vec3d[]{points[1], new Vec3d(0, -yaw*2, 0)},
                new Vec3d[]{points[2], new Vec3d(0, -yaw*2, 0)}
            };
        } else { // quad orbit
            float yaw = (world.getTime() * 5f) % 360;
            Vec3d[] points = drawCircle(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), 1, 4, yaw);

            return new Vec3d[][]{
                new Vec3d[]{points[0], new Vec3d(0, -yaw*2, 0)},
                new Vec3d[]{points[1], new Vec3d(0, -yaw*2, 0)},
                new Vec3d[]{points[2], new Vec3d(0, -yaw*2, 0)},
                new Vec3d[]{points[3], new Vec3d(0, -yaw*2, 0)}
            };
        }

    }

}
