package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;

public class BackspaceKey extends KeyboardKey {
    public BackspaceKey(KeyIcon icon, float growthFactor) {
        super(icon, growthFactor);
    }

    public void action(KeyboardService service) {
        var text = service.currentComposingText();
        if (!text.isEmpty())
            service.updateComposingText(text.substring(0, text.length() - 1));
        else
            service.deleteSurroundingText(1, 0);
    }
}
