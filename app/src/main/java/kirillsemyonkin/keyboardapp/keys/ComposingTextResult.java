package kirillsemyonkin.keyboardapp.keys;

public class ComposingTextResult {
    private final String newComposingText;
    private final boolean commit;

    public ComposingTextResult(String newComposingText, boolean commit) {
        this.newComposingText = newComposingText;
        this.commit = commit;
    }

    public static ComposingTextResult composingUpdate(String newText) {
        return new ComposingTextResult(newText, false);
    }

    public static ComposingTextResult composingCommit(String finalText) {
        return new ComposingTextResult(finalText, true);
    }

    public String newComposingText() {
        return newComposingText;
    }

    public boolean commit() {
        return commit;
    }
}
