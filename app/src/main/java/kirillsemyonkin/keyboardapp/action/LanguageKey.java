package kirillsemyonkin.keyboardapp.action;

import kirillsemyonkin.keyboardapp.KeyboardService;
import kirillsemyonkin.keyboardapp.icon.KeyIcon;
import kirillsemyonkin.keyboardapp.util.Highlight;

public class LanguageKey extends KeyboardKey {
    public LanguageKey(KeyIcon icon, float growthFactor, Highlight highlight) {
        super(icon, growthFactor, highlight);
    }

    public void tap(KeyboardService service) {
        service.switchToNextLocale();
    }

    public void hold(KeyboardService service, int pointerID) {
        // Switch language on long hold
        tap(service);
    }
}
