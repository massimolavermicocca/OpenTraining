package de.skubware.opentraining.activity.acra;

import android.app.Application;

import org.acra.ACRA;
import de.skubware.opentraining.BuildConfig;

/**
 * @class OpenTrainingApplication
 */
public class OpenTrainingApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        if (!BuildConfig.DEBUG){
            ACRA.init(this);
            ACRA.getErrorReporter().setReportSender(new ACRACrashReportMailer()); // default crash report sender
        }
    }


}
