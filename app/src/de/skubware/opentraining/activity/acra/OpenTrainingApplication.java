package de.skubware.opentraining.activity.acra;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import de.skubware.opentraining.BuildConfig;
import de.skubware.opentraining.R;

@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.

)

/**
 * @class OpenTrainingApplication
 * @extends Application
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
