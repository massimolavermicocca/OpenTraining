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


package de.skubware.opentraining.db.parser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.skubware.opentraining.Exceptions.ErrorException;
import de.skubware.opentraining.basic.ActivationLevel;
import de.skubware.opentraining.basic.ExerciseTag;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.basic.License;
import de.skubware.opentraining.basic.License.LicenseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

/**
 * An implementation of a SaxParser for parsing .xml files to a ExerciseType
 * object
 * 
 */
public class ExerciseTypeXMLParser extends DefaultHandler {
	/** Tag for logging */
	private static final String TAG = "ExTypeXMLParser";

	private SAXParser parser = null;
	
	private Context mContext;
	
	private IDataProvider mDataProvider;


	private ExerciseType exType;
	private ExerciseSource mExerciseSource;
	// required argument
	private String name;

	// optional arguments
	private Map<Locale,String> translationMap = new HashMap<Locale, String>(); // optional
	private String description;
	private List<File> imagePaths = new ArrayList<File>();
	private Map<File, License> imageLicenseMap = new HashMap<File, License>();
	private SortedSet<SportsEquipment> requiredEquipment = new TreeSet<SportsEquipment>();
	private SortedSet<Muscle> activatedMuscles = new TreeSet<Muscle>();
	private Map<Muscle, ActivationLevel> activationMap = new HashMap<Muscle, ActivationLevel>();
	private SortedSet<ExerciseTag> exerciseTag = new TreeSet<ExerciseTag>();
	private List<URL> relatedURL = new ArrayList<URL>();
	private List<String> hints = new ArrayList<String>();
	private File iconPath = null;

	public ExerciseTypeXMLParser(Context context, ExerciseSource exerciseSource) {
		mContext = context;
		mExerciseSource = exerciseSource;
		mDataProvider = new DataProvider(mContext);
		
		// create parser
		try {
			SAXParserFactory fac = SAXParserFactory.newInstance();
			parser = fac.newSAXParser();
		} catch (Exception e) {
			Log.v("ExerciseTypeXMLParser", e.getMessage().toString());
		}

	}

	/**
	 * Parses xml file
	 * 
	 * @param f
	 *            Datei
	 */
	public ExerciseType read(File f) {
		try {
			parser.parse(f, this);

			return this.exType;
		} catch (SAXException e) {
			Log.e(TAG, "Error parsing file: " + f.toString() + "\n" + e.getMessage());

		} catch (Exception e) {
			Log.e(TAG, "Error parsing file: " + f.toString() + "\n" + e.getMessage());

		}

		return null;
	}
	
	/**
	 * Parses xml file
	 * 
	 * @param f
	 */
	public ExerciseType read(InputStream f) {
		try {
			parser.parse(f, this);

			return this.exType;
		} catch (SAXException e) {
			Log.e(TAG, "Error parsing file: " + f.toString() + "\n" + e.getMessage(), e);
		} catch (Exception e) {
			Log.e(TAG, "Error parsing file: " + f.toString() + "\n" + e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Called when element begins.
	 * Errors will be logged.
	 * 
	 * @param uri
	 *            name space prefix
	 * @param selected_name
	 *            name of element
	 * @param qname
	 *            full qualified name with uri and name
	 * @param attributes
	 *            The attributes
	 * 
	 * @throws SAXException
	 *             if parsing fails
	 */
	@Override
	public void startElement(String uri, String selected_name, String qname, Attributes attributes) throws SAXException {
		if (qname.equals("ExerciseType")) {
			this.name = attributes.getValue("selected_name");
			String language = attributes.getValue("language");
			checkLanguage(attributes, language);
		}else if(qname.equals("Locale")){
			String language = attributes.getValue("language");
			checkLocaleLanguage(attributes, language);
			String translatedname = attributes.getValue("selected_name");
			checkTranslatedName(attributes, translatedname);
			this.translationMap.put(new Locale(language), translatedname);
		}else if (qname.equals("SportsEquipment")) {
			SportsEquipment eq = mDataProvider.getEquipmentByName(attributes.getValue("selected_name"));
			checkSportsEquipment(attributes, eq);
			this.requiredEquipment.add(eq);
		}else if (qname.equals("Muscle")) {
			setMuscle(attributes);
		}else if (qname.equals("Description")) {
			this.description = attributes.getValue("text");
		}else if (qname.equals("Image")) {
			File im = new File(attributes.getValue("path"));
			this.imagePaths.add(im);

			String author = attributes.getValue("author");
			String licenseTypeShortName = attributes.getValue("license");
			LicenseType licenseType = mDataProvider.getLicenseTypeByName(licenseTypeShortName);
			License license = null;

			license = checkLicense(author, licenseType);

			this.imageLicenseMap.put(im, license);
		}else if (qname.equals("RelatedURL")) {
			addRelatedURL(attributes);
		}else if (qname.equals("Tag")) {
			ExerciseTag tag = mDataProvider.getExerciseTagByName(attributes.getValue("selected_name"));
			checkTag(attributes, tag);
			this.exerciseTag.add(tag);
		}else {
			hintOrIcon(qname, attributes);
		}
	}

	void hintOrIcon(String qname, Attributes attributes) {
		if (qname.equals("Hint")) {
			String hint = attributes.getValue("text");
			this.hints.add(hint);
		}else{
			File iconpath = new File(attributes.getValue("path"));
			this.iconPath = iconpath;
		}
	}

	void setMuscle(Attributes attributes) {
		Muscle muscle = null;
		try{
            muscle = mDataProvider.getMuscleByName(attributes.getValue("selected_name"));
        }catch(IllegalArgumentException illEx){
            Log.e(TAG, "The Muscle: " + attributes.getValue("selected_name") + " couldn't be found. Ex: " + this.name);
        }
		checkMuscle(attributes, muscle);

		this.activatedMuscles.add(muscle);

		int level = ActivationLevel.MEDIUM.getLevel();
		try {
            level = Integer.parseInt(attributes.getValue("level"));
        } catch (Throwable t) {
            Log.e(TAG, "Error parsing ActivationLevel: " + attributes.getValue("level"));
        }
		ActivationLevel actLevel = ActivationLevel.getByLevel(level);
		this.activationMap.put(muscle, actLevel);
	}

	void addRelatedURL(Attributes attributes) {
		try {
            this.relatedURL.add(new URL(attributes.getValue("url")));
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error, URL: " + attributes.getValue("url") + " is not valid/is malformed \n" + e.getMessage());
        }
	}

	void checkMuscle(Attributes attributes, Muscle muscle) {
		if (muscle == null) {
            Log.e(TAG, "The Muscle: " + attributes.getValue("selected_name") + " couldn't be found. Ex: " + this.name);
        }
	}

	void checkSportsEquipment(Attributes attributes, SportsEquipment eq) {
		if (eq == null) {
            Log.e(TAG, "The SportsEquipment: " + attributes.getValue("selected_name") + " couldn't be found.");
        }
	}

	private void checkTag(Attributes attributes, ExerciseTag tag) {
		if (tag == null) {
            Log.e(TAG, "The Tag: " + attributes.getValue("selected_name") + " couldn't be found.");
        }
	}

	@NonNull
	private License checkLicense(String author, LicenseType licenseType) {
		License license;
		if(author != null){ // licenseType cannot be null
            license = new License(licenseType, author);
        }else{
            license = new License(); // has LicenseType Unknown
        }
		return license;
	}

	private void checkTranslatedName(Attributes attributes, String translatedname) {
		if (translatedname == null) {
            Log.e(TAG, "Locale without translatedname" + attributes.getValue("selected_name"));
        }
	}

	private void checkLocaleLanguage(Attributes attributes, String language) {
		if (language == null) {
            Log.e(TAG, "Locale without language" + attributes.getValue("selected_name"));
        }
	}

	private void checkLanguage(Attributes attributes, String language) {
		if (language == null) {
            Log.i(TAG, "Default name without language " + attributes.getValue("selected_name"));
        }else{
            this.translationMap.put(new Locale(language), this.name);
        }
	}


	/**
	 * When {@code </ExerciseType>} is reached, the parsing is finished.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("ExerciseType")) {

			// let the builder do its job :)
			try {
				this.exType = new ExerciseType.Builder(this.name, this.mExerciseSource).translationMap(this.translationMap).activatedMuscles(this.activatedMuscles).activationMap(this.activationMap).description(this.description)
                        .exerciseTags(this.exerciseTag).imagePath(this.imagePaths).neededTools(this.requiredEquipment).relatedURL(this.relatedURL)
                        .imageLicenseMap(this.imageLicenseMap).hints(hints).iconPath(iconPath).build();
			} catch (ErrorException e) {
				Log.v("ExerciseTypeXMLParser", e.getMessage().toString());
			}

			this.name = null;
			
			this.translationMap = new HashMap<Locale, String>();
			this.description = null;
			this.imagePaths = new ArrayList<File>();
			this.imageLicenseMap = null;
			this.requiredEquipment = new TreeSet<SportsEquipment>();
			this.activatedMuscles = new TreeSet<Muscle>();
			this.activationMap = new HashMap<Muscle, ActivationLevel>();
			this.exerciseTag = new TreeSet<ExerciseTag>();
			this.relatedURL = new ArrayList<URL>();
			this.hints = new ArrayList<String>();
			this.iconPath = null;

		}

	}

}