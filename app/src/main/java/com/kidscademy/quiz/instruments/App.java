package com.kidscademy.quiz.instruments;

import com.kidscademy.app.AppBase;
import com.kidscademy.quiz.instruments.model.GameEngine;
import com.kidscademy.quiz.instruments.model.GameEngineImpl;
import com.kidscademy.quiz.instruments.model.KeyboardControl;
import com.kidscademy.quiz.instruments.model.QuizEngine;
import com.kidscademy.quiz.instruments.model.QuizEngineImpl;
import com.kidscademy.quiz.instruments.util.Audit;
import com.kidscademy.quiz.instruments.util.Preferences;
import com.kidscademy.quiz.instruments.util.Repository;
import com.kidscademy.quiz.instruments.util.Storage;
import com.kidscademy.quiz.instruments.view.AnswerView;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Application singleton holds global states, generates crash report and implements application active detection logic.
 * <p>
 * <h5>Android Process Creation</h5>
 * <p>
 * An Android process is created when an activity should be activated; a process is just a run-time container for an
 * activity instance. The ultimate goal is to start an activity but before that Android creates application singleton
 * and invoke {@link #onCreate()}. After on init callback returns Android continue with activity creation. This is
 * true even if callback starts another activity; Android will still init activity requested by platform then will
 * init that requested by callback.
 * <p>
 * Now, which activity is created when application starts depends on Android platform and external applications. For
 * example, if application is started from home launcher main activity will be created; if application is recreated from
 * recent applications, platform will restore last active activity. Also an activity could be explicitly requested by a
 * third party application. This behavior can be adjusted by <code>launchMode</code> attribute from manifest or intent
 * flags.
 * <p>
 * To sum-up, there is no way to route application start-up to different activity. Platform chosen activity will be
 * created anyway, even if on init callback requests another activity start.
 *
 * @author Iulian Rotaru
 */
public class App extends AppBase {
    public static final String PROJECT_NAME = "quiz.instruments";

    /**
     * Application instance creation. Application is guaranteed by Android platform to be created in a single instance.
     * <p>
     * Initialization occurs in two steps: first is this callback invoked by Android. The second is
     * {@link #onPostCreate()}; if storage is loaded post-init is invoked immediately by this method. If storage is not
     * loaded, {@link MainActivity} will route application start-up logic to storage loading asynchronous task that, when
     * done, will invoke post-init. This way post-init is guaranteed to be called when storage is loaded.
     */
    @Override
    public void onAppCreate() {
        preferences = new Preferences();
        repository = new Repository();
        storage = new Storage(getApplicationContext());
        audit = new Audit();

        if (storage().isValid() && storage().isLoaded()) {
            onPostCreate();
        }
    }

    public static App instance() {
        return (App) instance;
    }

    /**
     * Application running code encoded as follow: 0 - flag not initialized, 1 - running Espresso UI tests, 2 - running production code.
     */
    private static AtomicInteger runningMode = new AtomicInteger();

    public static boolean isTest() {
        if (runningMode.get() == 0) {
            try {
                Class.forName("android.support.test.espresso.Espresso");
                runningMode.set(1);
            } catch (ClassNotFoundException e) {
                runningMode.set(2);
            }
        }
        return runningMode.get() == 1;
    }

    public static Preferences prefs() {
        return (Preferences) AppBase.prefs();
    }

    /**
     * Get application storage singleton instance.
     *
     * @return application storage.
     * @see #storage
     */
    public static Storage storage() {
        return (Storage) AppBase.storage();
    }

    /**
     * Get application storage singleton instance.
     *
     * @return application storage.
     * @see #storage
     */
    public static Repository repository() {
        return (Repository) AppBase.repository();
    }

    public static GameEngine getGameEngine(AnswerView answerView, KeyboardControl keyboardView) {
        return new GameEngineImpl(App.storage(), App.audit(), answerView, keyboardView);
    }

    /**
     * Get audit singleton instance.
     *
     * @return audit instance.
     * @see #audit
     */
    public static Audit audit() {
        return (Audit) AppBase.audit();
    }

    // TODO: remove after moving to Assets utility class

    private final static int[] backgroundResIds = new int[]
            {
                    R.drawable.page_bg1, R.drawable.page_bg2, R.drawable.page_bg3, R.drawable.page_bg4, R.drawable.page_bg5, R.drawable.page_bg6, R.drawable.page_bg7,
                    R.drawable.page_bg8, R.drawable.page_bg9, R.drawable.page_bg10
            };

    private static final Random random = new Random();

    public static int getBackgroundResId() {
        return backgroundResIds[random.nextInt(backgroundResIds.length)];
    }
}
