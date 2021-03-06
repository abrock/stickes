package de.xonibo.stickes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StichData extends ArrayList<Stich> implements StickesSource {

    static public Color createRandomColor() {
        Random r = new Random();
        return new Color(r.nextInt(0xff), r.nextInt(0xff), r.nextInt(0xff));
    }
    List<Color> colorInternal = new ArrayList<>();

    public String getInfo() {
        StichData tmp = this.normalize();
        final int size = tmp.size();
        final int x = tmp.getMaxCornerX() / 10;
        final int y = tmp.getMaxCornerY() / 10;
        return String.format("Stiches: %d, x: %dmm, y: %dmm", size, x, y);
    }

    public StichData normalize() {
        int nx = Math.min(getInitCornerX(), 0);
        int ny = Math.min(getInitCornerY(), 0);
        StichData sd = (StichData) this.clone();
        for (Stich stich : sd) {
            stich.move(-nx, -ny);
        }
        return sd;
    }

    public StichData insertCenterStichAtStart() {
        normalize();
        int x = getMaxCornerX() / 2;
        int y = getMaxCornerY() / 2;
        Stich s = new Stich(x, y, true);
        add(0, s);
        return this;
    }

    @Override
    public boolean add(Stich e) {
        add(this.size(), e);
        return true;
    }

    @Override
    public void add(int index, Stich e) {
        if (e.isColorChange()) {
            colorInternal.add(e.getColor());
        }
        super.add(index, e);
    }

    public List<Color> getColors() {
        return colorInternal;
    }

    public int getInitCornerX() {
        int x = 0;
        for (Stich stich : this) {
            if (stich.getX() < x) {
                x = stich.getX();
            }
        }
        return x;
    }

    public int getInitCornerY() {
        int y = 0;
        for (Stich stich : this) {
            if (stich.getY() < y) {
                y = stich.getY();
            }
        }
        return y;
    }

    public int getMaxCornerX() {
        int x = 1;
        for (Stich stich : this) {
            if (stich.getX() > x) {
                x = stich.getX();
            }
        }
        return x;
    }

    public int getMaxCornerY() {
        int y = 1;
        for (Stich stich : this) {
            if (stich.getY() > y) {
                y = stich.getY();
            }
        }
        return y;
    }

    public int getJumps() {
        int count = 0;
        for (Stich stich : this) {
            if (stich.isJump()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String getSource() {
        //TODO getClass().getConstructors()[0]
        return "new StichData()";

    }

    public boolean checkAllRange(int maxDistance) {
        Stich previous = null;
        for (Stich stich : this) {
            if (previous != null && stich.isOverRange(previous, maxDistance)) {
                return false;
            }
            previous = stich;
        }
        return true;
    }

    public void addIntermediateStichesIfNessessary(int maxDistance) {
        int skip = 1;
        if (get(size() - 1).isEOF()) {
            skip++;
        }
        for (int i = size() - skip; i > 0; i--) {
            Stich s = get(i - 1);
            Stich n = get(i);
            addAll(i, createIntermediateStiches(s, n, maxDistance));

        }
    }

    static public StichData createIntermediateStiches(Stich firstStich, Stich lastStich, int maxDistance) {
        StichData sd = new StichData();
        if (firstStich.isOverRange(lastStich, maxDistance)) {
            int cx = (firstStich.getX() + lastStich.getX()) / 2;
            int cy = (firstStich.getY() + lastStich.getY()) / 2;
            Stich newstich = new Stich(cx, cy, true);
            sd.addAll(createIntermediateStiches(firstStich, newstich, maxDistance));
            sd.add(newstich);
            sd.addAll(createIntermediateStiches(newstich, lastStich, maxDistance));
        }
        return sd;
    }

}
