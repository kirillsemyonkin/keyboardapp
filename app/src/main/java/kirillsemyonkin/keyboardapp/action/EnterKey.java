package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

public class EnterKey extends KeyboardKey {
    public EnterKey(KeyIcon icon, float growthFactor, Highlight highlight) {
        super(icon, growthFactor, highlight);
    }

    public void tap(KeyboardService service) {
        service.sendEnter();
    }
}
