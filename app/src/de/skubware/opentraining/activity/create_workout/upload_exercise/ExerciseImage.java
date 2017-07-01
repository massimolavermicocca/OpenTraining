package de.skubware.opentraining.activity.create_workout.upload_exercise;

import android.content.Context;

import java.io.File;

import de.skubware.opentraining.basic.License;
import de.skubware.opentraining.db.IDataProvider;

/**
 * @class ExerciseImage
 */
public class ExerciseImage {
	File mImagePath;
	String mRealImagePath;

	License mLicense;

	public ExerciseImage(File image, License license, Context context){
		mLicense = license;
		mImagePath = image;
		mRealImagePath = context.getFilesDir().toString() + "/" + IDataProvider.CUSTOM_IMAGES_FOLDER + "/" + mImagePath;
	}

	public String getRealImagePath(){
		return mRealImagePath;
	}

}
