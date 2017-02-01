package io.faucette.zombierampage;


import java.util.Random;

import io.faucette.math.Vec2;


public class Utils {
    private static Random random = new Random();


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

    public static int attack(int pp) {
        int base = (int) (pp * 0.5f);
        return base + ((int) (random.nextFloat() * (pp - base)));
    }
}
