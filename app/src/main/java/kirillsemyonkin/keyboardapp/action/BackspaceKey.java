package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class BackspaceKey extends KeyboardKey {
    public BackspaceKey(KeyIcon icon, float growthFactor, boolean highlight) {
        super(icon, growthFactor, highlight);
    }

    public void action(KeyboardService service) {
        var text = service.currentComposingText();
        if (!text.isEmpty())
            service.backspaceComposingText();
        else
            service.backspaceText();
    }
}
