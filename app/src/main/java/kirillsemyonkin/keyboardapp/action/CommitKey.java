package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class CommitKey extends KeyboardKey {
    private final String append;

    public CommitKey(KeyIcon icon, float growthFactor, String append) {
        super(icon, growthFactor);
        this.append = append;
    }

    public void action(KeyboardService service) {
        service.commitComposingText(
            service.currentComposingText() + append);
    }
}
