package kirillsemyonkin.keyboardapp.util;

public enum ShiftCase {
    LOWERCASE("lowercase"),
    UPPERCASE("uppercase"),
    UPPERCASE_LOCK("uppercase");

    private final String mode;

    ShiftCase(String mode) {
        this.mode = mode;
    }

    public String mode() {
        return mode;
    }

    public static ShiftCase fromMode(String mode) {
        return "lowercase".equals(mode)
            ? LOWERCASE
            : UPPERCASE;
    }
}
