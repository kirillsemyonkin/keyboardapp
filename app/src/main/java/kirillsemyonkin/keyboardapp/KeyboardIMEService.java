package kirillsemyonkin.keyboardapp;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import kirillsemyonkin.keyboardapp.keys.ComposingTextResult;

public class KeyboardIMEService extends InputMethodService {
    //
    // Events
    //

    public void onCreate() {
        super.onCreate();
        android.os.Debug.waitForDebugger();
    }

    private KeyboardIMEView view;

    public View onCreateInputView() {
        return view
            = ((KeyboardIMEView) getLayoutInflater()
            .inflate(
                R.layout.keyboard_ime_view,
                null))
            .service(this);
    }

    public void onStartInput(EditorInfo attribute, boolean restarting) {
        // TODO detect keyboard type from text field type
    }

    //
    // Composing text
    //

    private String composingText = "";

    public String currentComposingText() {
        return composingText;
    }

    public void updateComposingText(ComposingTextResult composingTextResult) {
        var newText = composingTextResult.newComposingText();

        var ic = getCurrentInputConnection();
        if (composingTextResult.commit()) {
            ic.commitText(newText, 1);
            composingText = "";
        } else {
            ic.setComposingText(newText, 1);
            composingText = newText;
        }
    }
}