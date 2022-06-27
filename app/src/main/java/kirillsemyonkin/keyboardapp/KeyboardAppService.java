package kirillsemyonkin.keyboardapp;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_DEL;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;
import static kirillsemyonkin.keyboardapp.layout.KeyboardLocale.XMLNS_NULL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import kirillsemyonkin.keyboardapp.action.AltCharAppendKey;
import kirillsemyonkin.keyboardapp.layout.KeyboardLayout;
import kirillsemyonkin.keyboardapp.layout.KeyboardLocale;
import kirillsemyonkin.keyboardapp.layout.LayoutRenderer;
import kirillsemyonkin.keyboardapp.view.KeyboardAppView;

public class KeyboardAppService extends InputMethodService implements KeyboardService {
    public static void fail(Exception e) {
        throw new RuntimeException("Application crash", e);
    }

    //
    // Locales
    //

    private final String NUMBER_LOCALE = "number";

    private Map<String, Integer> locales;
    private KeyboardLocale locale;

    public static Map<String, Integer> parseLocaleList(Resources resources, XmlPullParser parser)
        throws XmlPullParserException,
        IOException,
        Resources.NotFoundException {
        parser.require(START_TAG, XMLNS_NULL, "locales");

        var locales = new LinkedHashMap<String, Integer>(); // preserve order
        while (parser.next() != END_TAG) {
            parser.require(START_TAG, XMLNS_NULL, "locale");
            parser.next();
            parser.require(TEXT, XMLNS_NULL, null);

            var locale = parser.getText().trim();
            locales.put(
                locale,
                resources
                    .getIdentifier(locale, "xml", "kirillsemyonkin.keyboardapp"));

            parser.next();
            parser.require(END_TAG, XMLNS_NULL, "locale");
        }

        parser.require(END_TAG, XMLNS_NULL, "locales");
        return locales;
    }

    public void selectLocale(String locale)
        throws XmlPullParserException,
        IOException,
        NullPointerException {
        var id = NUMBER_LOCALE.equals(locale)
            ? (Integer) R.xml.number
            : locales.get(locale);
        if (id == null)
            throw new NullPointerException("Unknown locale " + locale);
        try (var parser = getResources().getXml(id)) {
            while (parser.getEventType() != START_TAG
                || !parser.getName().equals("keyboardLocale"))
                parser.next();
            this.locale = KeyboardLocale.parseLocale(locale, parser);
            switchMode(this.locale.defaultMode());
        }
    }

    //
    // Events
    //

    public void onCreate() {
        super.onCreate();
        try (var parser = getResources().getXml(R.xml.locales)) {
            while (parser.getEventType() != START_TAG
                || !parser.getName().equals("locales"))
                parser.next();
            locales = parseLocaleList(getResources(), parser);
        } catch (XmlPullParserException | IOException | Resources.NotFoundException e) {
            fail(e);
        }
        defaultInit();
    }

    private void defaultInit() {
        composingText = "";
        try {
            selectLocale(DEFAULT_LOCALE);
        } catch (XmlPullParserException | IOException e) {
            fail(e);
        }
    }

    private KeyboardAppView view;

    @SuppressLint("InflateParams")
    public View onCreateInputView() {
        return view
            = ((KeyboardAppView) getLayoutInflater()
            .inflate(
                R.layout.keyboard_view,
                null))
            .service(this);
    }

    public void onStartInput(EditorInfo attribute, boolean restarting) {
        defaultInit();
        if ((attribute.inputType & InputType.TYPE_CLASS_NUMBER) != 0)
            try {
                System.out.println("selecting number locale");
                selectLocale(NUMBER_LOCALE);
                System.out.println("selected number locale");
            } catch (XmlPullParserException | IOException e) {
                fail(e);
            }
        if (view != null) view.service(this); // update renderer
    }

    public boolean onEvaluateFullscreenMode() {
        return super.onEvaluateFullscreenMode()
            || renderer().fullscreen();
    }

    //
    // State
    //

    private KeyboardLayout layout;

    public static final String DEFAULT_LOCALE = "en_us";

    public LayoutRenderer renderer() {
        return LayoutRenderer
            .valueOf(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("type", "standard")
                .toUpperCase());
    }

    public void switchMode(String mode) {
        layout = locale.layout(mode);
        if (view != null) {
            view.invalidate();
            view.service(this); // resets press state
        }
    }

    public void switchToNextLocale() {
        var prefs = PreferenceManager
            .getDefaultSharedPreferences(this);

        var locales = new ArrayList<String>();
        for (var locale : this.locales.keySet())
            if (prefs.getBoolean(locale, false))
                locales.add(locale);
        if (locales.isEmpty()) locales.add(DEFAULT_LOCALE);

        try {
            selectLocale(locales
                .get(locale == null
                    ? 0
                    : (locales.indexOf(locale.id()) + 1) % locales.size()));
        } catch (XmlPullParserException | IOException e) {
            fail(e);
        }
    }

    public KeyboardLayout layout() {
        return layout;
    }

    public void openSettings() {
        var intent = new Intent(this, KeyboardSettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void hideKeyboard() {
        requestHideSelf(0);
    }

    //
    // Composing text
    //

    private String composingText;

    public void sendCharacter(char character) {
        switch (character) {
            case '\b': {
                sendBackspace();
                return;
            }
            case '\n': {
                sendEnter();
                return;
            }
            case ' ': {
                sendSpace();
                return;
            }
        }

        var newComposingText = composingText + character;
        getCurrentInputConnection()
            .setComposingText(newComposingText, newComposingText.length());
        composingText = newComposingText;
    }

    private void sendDownUp(int keycode) {
        getCurrentInputConnection()
            .sendKeyEvent(new KeyEvent(ACTION_DOWN, keycode));
        getCurrentInputConnection()
            .sendKeyEvent(new KeyEvent(ACTION_UP, keycode));
    }

    public void sendSpace() {
        var newComposingText = composingText + ' ';
        getCurrentInputConnection()
            .commitText(newComposingText, newComposingText.length());
        composingText = "";
    }

    public void sendBackspace() {
        if (composingText.isEmpty()) {
            sendDownUp(KEYCODE_DEL);
            return;
        }

        var newComposingText = composingText.substring(0, composingText.length() - 1);
        getCurrentInputConnection()
            .setComposingText(newComposingText, newComposingText.length());
        composingText = newComposingText;
    }

    public void sendEnter() {
        sendDownUp(KEYCODE_ENTER);
        composingText = "";
    }

    public void showAltChars(int pointer, AltCharAppendKey key) {
        view.openAltMenu(pointer, key);
    }

    public void hideAltChars(int pointer, AltCharAppendKey key) {
        view.closeAltMenu(pointer, key);
    }
}