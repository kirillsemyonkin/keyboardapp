package kirillsemyonkin.keyboardapp;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;
import static kirillsemyonkin.keyboardapp.KeyboardLocale.XMLNS_NULL;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KeyboardAppService extends InputMethodService implements KeyboardService {
    //
    // Locales
    //

    private Map<String, Integer> locales;
    private KeyboardLocale locale;

    private Map<String, Integer> parseLocaleList(XmlPullParser parser)
        throws XmlPullParserException,
        IOException,
        Resources.NotFoundException {
        parser.require(START_TAG, XMLNS_NULL, "locales");

        var locales = new HashMap<String, Integer>();
        while (parser.next() != END_TAG) {
            parser.require(START_TAG, XMLNS_NULL, "locale");
            parser.next();
            parser.require(TEXT, XMLNS_NULL, null);

            var locale = parser.getText().trim();
            locales.put(
                locale,
                getResources()
                    .getIdentifier(locale, "xml", this.getPackageName()));

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
        var id = locales.get(locale);
        if (id == null)
            throw new NullPointerException("Unknown locale " + locale);
        try (var parser = getResources().getXml(id)) {
            while (parser.getEventType() != START_TAG
                || !parser.getName().equals("keyboardLocale"))
                parser.next();
            this.locale = KeyboardLocale.parseLocale(parser);
            switchMode(this.locale.defaultMode());
        }
    }

    //
    // Events
    //

    public void onCreate() {
        super.onCreate();
        //android.os.Debug.waitForDebugger();
        try (var parser = getResources().getXml(R.xml.locales)) {
            while (parser.getEventType() != START_TAG
                || !parser.getName().equals("locales"))
                parser.next();
            locales = parseLocaleList(parser);
        } catch (XmlPullParserException | IOException e) { // FIXME temp
            e.printStackTrace();
        }
        defaultInit();
    }

    private void defaultInit() {
        composingText = "";
        try {
            selectLocale("en_us"); // TODO last lang setting
        } catch (XmlPullParserException | IOException | NullPointerException e) { // FIXME temp
            e.printStackTrace();
        }
    }

    private KeyboardAppView view;

    @SuppressLint("InflateParams")
    public View onCreateInputView() {
        return view
            = ((KeyboardAppView) getLayoutInflater()
            .inflate(
                R.layout.keyboard_ime_view,
                null))
            .service(this);
    }

    public void onStartInput(EditorInfo attribute, boolean restarting) {
        defaultInit();
        // TODO detect keyboard type from text field type
    }

    //
    // State
    //

    private KeyboardLayout layout;

    public void switchMode(String mode) {
        layout = locale.layout(mode);
        if (view != null) view.invalidate();
    }

    public KeyboardLayout layout() {
        return layout;
    }

    //
    // Composing text
    //

    private String composingText;

    public String currentComposingText() {
        return composingText;
    }

    public void updateComposingText(String composingText) {
        getCurrentInputConnection()
            .setComposingText(this.composingText = composingText, 1);
    }

    public void commitComposingText(String composingText) {
        getCurrentInputConnection()
            .commitText(composingText, 1);
        this.composingText = "";
    }

    public void deleteSurroundingText(int beforeLength, int afterLength) {
        getCurrentInputConnection()
            .deleteSurroundingText(beforeLength, afterLength);
    }
}