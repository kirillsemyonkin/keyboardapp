package kirillsemyonkin.keyboardapp.util;

public class KeyProjection {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public KeyProjection(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public int height() {
        return height;
    }
}
