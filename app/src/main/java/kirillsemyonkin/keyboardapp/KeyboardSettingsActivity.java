package kirillsemyonkin.keyboardapp;

import static org.xmlpull.v1.XmlPullParser.START_TAG;

import static kirillsemyonkin.keyboardapp.KeyboardAppService.parseLocaleList;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class KeyboardSettingsActivity
    extends AppCompatActivity
    implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final String TITLE_TAG = "settingsActivityTitle";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.keyboard_settings_view);
        if (savedInstanceState == null)
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new MainFragment())
                .commit();
        else
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));

        getSupportFragmentManager()
            .addOnBackStackChangedListener(() -> {
                if (getSupportFragmentManager()
                    .getBackStackEntryCount() == 0)
                    setTitle(R.string.settings_title);
            });

        var actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager()
            .popBackStackImmediate())
            return true;
        finish();
        return true;
    }

    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller,
                                             Preference pref) {
        // Instantiate the new Fragment
        var args = pref.getExtras();
        var fragment
            = getSupportFragmentManager()
            .getFragmentFactory()
            .instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class MainFragment extends PreferenceFragmentCompat {
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.main_settings, rootKey);
        }
    }

    public static class LanguagesFragment extends PreferenceFragmentCompat {
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            try (var parser = getResources().getXml(R.xml.locales)) {
                while (parser.getEventType() != START_TAG
                    || !parser.getName().equals("locales"))
                    parser.next();
                var locales = parseLocaleList(getResources(), parser);

                var preferenceManager = getPreferenceManager();
                var ctx = preferenceManager.getContext();
                var screen = preferenceManager
                    .createPreferenceScreen(ctx);
                for (var locale : locales.keySet()) {
                    var pref = new SwitchPreferenceCompat(ctx);
                    pref.setKey(locale);

                    var title = getResources()
                        .getIdentifier(locale, "string", "kirillsemyonkin.keyboardapp");
                    pref.setTitle(title);

                    screen.addPreference(pref);
                }

                setPreferenceScreen(screen);
            } catch (XmlPullParserException | IOException | Resources.NotFoundException e) { // FIXME temp
                e.printStackTrace();
            }
        }
    }
}