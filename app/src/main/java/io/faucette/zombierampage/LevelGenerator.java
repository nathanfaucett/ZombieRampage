package io.faucette.zombierampage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class LevelGenerator implements Iterable<LevelGenerator.Section> {
    private float increment;
    private Map<String, Section> sections;


    public LevelGenerator() {
        increment = 0.25f;
        sections = new HashMap<>();
        generate();
    }

    private static String getSectionName(int x, int y) {
        return x + ":" + y;
    }

    private void generate() {
        Section root = getOrCreateSection(0, 0);
        root.chance = 1f;
        generatePaths(root, 1f);
        getPathTypes(root);
    }

    private void generatePaths(Section section, float chance) {
        int x = section.getX();
        int y = section.getY();
        List<int[]> available = new ArrayList<>();

        if (!hasSection(x, y + 1) && !hasSection(x + 1, y + 1) && !hasSection(x - 1, y + 1)) {
            available.add(new int[]{x, y + 1});
        }
        if (!hasSection(x + 1, y) && !hasSection(x + 1, y + 1) && !hasSection(x + 1, y - 1)) {
            available.add(new int[]{x + 1, y});
        }
        if (!hasSection(x, y - 1) && !hasSection(x + 1, y - 1) && !hasSection(x - 1, y - 1)) {
            available.add(new int[]{x, y - 1});
        }
        if (!hasSection(x - 1, y) && !hasSection(x - 1, y + 1) && !hasSection(x - 1, y - 1)) {
            available.add(new int[]{x - 1, y});
        }

        for (int[] xy : available) {
            float value = (float) Math.random();

            if (value < chance) {
                Section nextSection = getOrCreateSection(xy[0], xy[1]);
                nextSection.chance = value;
                generatePaths(nextSection, chance - increment);
            }
        }
    }

    private void getPathTypes(Section section) {
        if (section.type == null) {
            int x = section.getX();
            int y = section.getY();

            boolean top = hasSection(x, y + 1);
            boolean right = hasSection(x + 1, y);
            boolean bottom = hasSection(x, y - 1);
            boolean left = hasSection(x - 1, y);

            if (top && right && bottom && left) {

                section.type = Type.FourWay;

            } else if (top && right && bottom) {

                section.type = Type.VerticalRightThreeWay;

            } else if (top && left && bottom) {

                section.type = Type.VerticalLeftThreeWay;

            } else if (left && right && top) {

                section.type = Type.HorizontalTopThreeWay;

            } else if (left && right && bottom) {

                section.type = Type.HorizontalBottomThreeWay;

            } else if (top && right) {

                section.type = Type.TopRightTurn;

            } else if (top && left) {

                section.type = Type.TopLeftTurn;

            } else if (bottom && right) {

                section.type = Type.BottomRightTurn;

            } else if (bottom && left) {

                section.type = Type.BottomLeftTurn;

            } else if (top && bottom) {

                section.type = Type.Vertical;

            } else if (left && right) {

                section.type = Type.Horizontal;

            } else if (top) {

                section.type = Type.BottomEnd;

            } else if (right) {

                section.type = Type.LeftEnd;

            } else if (bottom) {

                section.type = Type.TopEnd;

            } else if (left) {

                section.type = Type.RightEnd;

            }

            if (top) {
                getPathTypes(getOrCreateSection(x, y + 1));
            }
            if (right) {
                getPathTypes(getOrCreateSection(x + 1, y));
            }
            if (bottom) {
                getPathTypes(getOrCreateSection(x, y - 1));
            }
            if (left) {
                getPathTypes(getOrCreateSection(x - 1, y));
            }
        }
    }

    private boolean hasSection(int x, int y) {
        return sections.containsKey(LevelGenerator.getSectionName(x, y));
    }

    private Section getOrCreateSection(int x, int y) {
        String name = LevelGenerator.getSectionName(x, y);

        if (sections.containsKey(name)) {
            return sections.get(name);
        } else {
            Section section = new Section(x, y);
            sections.put(name, section);
            return section;
        }
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.values().iterator();
    }

    public enum Type {
        TopEnd,
        RightEnd,
        BottomEnd,
        LeftEnd,

        TopRightTurn,
        TopLeftTurn,
        BottomRightTurn,
        BottomLeftTurn,

        VerticalLeftThreeWay,
        VerticalRightThreeWay,
        HorizontalTopThreeWay,
        HorizontalBottomThreeWay,

        Vertical,
        Horizontal,

        FourWay,
    }

    public class Section {
        private int x;
        private int y;
        private float chance;
        private Type type;
        private String name;

        public Section(int x, int y) {
            this.x = x;
            this.y = y;
            this.type = null;
            this.name = LevelGenerator.getSectionName(x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public float getChance() {
            return chance;
        }

        public Type getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return getType() + " " + getName();
        }
    }
}
