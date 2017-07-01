/**
 *
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2014 Christian Skubich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.skubware.opentraining.activity.create_exercise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.skubware.opentraining.R;

/**
 * @class DescriptionFragment
 */
public class DescriptionFragment extends Fragment{
	/** Tag for logging*/
	private final String TAG = "DescriptionFragment";


	private EditText mEditTextExerciseDescription;

	public DescriptionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_create_exercise_description, container, false);

		mEditTextExerciseDescription = (EditText) layout.findViewById(R.id.edittext_description);



		return layout;
	}


	public String getExerciseDescription(){
		return mEditTextExerciseDescription.getText().toString();
	}


}