package kirillsemyonkin.keyboardapp.action;

import static kirillsemyonkin.keyboardapp.util.ShiftCase.LOWERCASE;
import static kirillsemyonkin.keyboardapp.util.ShiftCase.UPPERCASE;
import static kirillsemyonkin.keyboardapp.util.ShiftCase.UPPERCASE_LOCK;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

public class ShiftKey extends KeyboardKey {
    public ShiftKey(KeyIcon icon, float growthFactor, Highlight highlight) {
        super(icon, growthFactor, highlight);
    }

    public void tap(KeyboardService service) {
        if (service.shift() != LOWERCASE) {
            service.shift(LOWERCASE);
            return;
        }

        service.shift(UPPERCASE);
    }

    public void hold(KeyboardService service, int pointerID) {
        service.shift(UPPERCASE_LOCK);
    }
}
