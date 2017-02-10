package io.faucette.zombierampage;


import io.faucette.math.Vec2;


public class Utils {
    public static float circleToPoint(Vec2 center, float radias, Vec2 point) {
        Vec2 distance = Vec2.sub(new Vec2(), center, point);
        float lengthSq = distance.lengthSq();
        float radiasSq = radias * radias;

        if (lengthSq < radiasSq) {
            return (float) (Math.sqrt(radiasSq) - Math.sqrt(lengthSq));
        } else {
            return 0;
        }
    }
}
