package kirillsemyonkin.keyboardapp.util;

public class KeyProjection {
    private final int x;
    private final int y;
    private final int width;

    public KeyProjection(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }
}
