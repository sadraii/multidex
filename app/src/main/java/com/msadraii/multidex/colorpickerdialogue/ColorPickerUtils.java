/*******************************************************************************
 * Copyright 2013 Gabriele Mariotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.msadraii.multidex.colorpickerdialogue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.msadraii.multidex.R;
import com.msadraii.multidex.Utils;

import java.util.Random;

/**
 * 
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 *
 */
public class ColorPickerUtils {

	
	/**
	 * Utility class for colors
	 * 
	 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
	 *
	 */
	public static class ColorUtils{
		
		/**
		 * Create an array of int with colors
		 * 
		 * @param context
		 * @return
		 */
		public static int[] colorChoices(Context context){
		
			int[] mColorChoices=null;	
			String[] colorValues = context.getResources().getStringArray(R.array.default_color_values);
		
		    if (colorValues !=null && colorValues.length>0) {
		        mColorChoices = new int[colorValues.length];
		        for (int i = 0; i < colorValues.length; i++) {
		            mColorChoices[i] = Color.parseColor(colorValues[i]);
		        }
		    }
		    return mColorChoices;
		}

        public static String colorName(Context context, int color) {
            String colorString = Utils.toHexString(color);
            String[] colorValues = context.getResources().getStringArray(R.array.default_color_values);
            String[] colorNames = context.getResources().getStringArray(R.array.default_color_names);
            for (int i = 0; i < colorValues.length; i++) {
                if (colorValues[i].equals(colorString)) {
                    return colorNames[i];
                }
            }
            return "";
        }

        public static String colorName(Context context, String color) {
            String[] colorValues = context.getResources().getStringArray(R.array.default_color_values);
            String[] colorNames = context.getResources().getStringArray(R.array.default_color_names);
            for (int i = 0; i < colorValues.length; i++) {
                if (colorValues[i].equals(color)) {
                    return colorNames[i];
                }
            }
            return "";
        }

        /**
         * Returns a random color. If there are unused colors, it picks from that list first.
         * @return
         */
        // TODO figure this out
        public static String randomColor(Context context) {
            final String[] defaultValues = context.getResources().getStringArray(R.array.default_color_values);
//            boolean[] usedColorsBool = new boolean[defaultValues.length];
//            final ArrayList<String> colorValues = ColorCodeRepository.getAllColorValues(context);
////            ArrayList<String> usedColors = new ArrayList<String>();
//            for (int i = 0; i < defaultValues.length; i++) {
//                for (int j = 0; j < colorValues.size(); j++) {
//                    if (defaultValues[i].equals(colorValues.get(j))) {
////                        usedColors.add(defaultValues[i]);
//                        usedColorsBool[i] = true;
//                        break;
//                    }
//                }
//            }



            Random rand = new Random();
            return defaultValues[rand.nextInt(defaultValues.length - 1)];



//            return defaultValues[randomInt];
//            ArrayList<String> unusedColors = new ArrayList<String>();
//            for (int i = 0; i < defaultValues.length; i++) {
//                for (int j = 0; j < usedColors.size(); j++) {
//                    if (usedColors.)
//                }
//            }


//            if (usedColors.size() == defaultValues.length) {
//                int randomInt = rand.nextInt(defaultValues.length - 1);
//                return defaultValues[randomInt];
//            } else {
//                ArrayList<String> unusedColors = new ArrayList<String>();
//                for (int i = 0; i < defaultValues.length; i++) {
//                    for (int j = 0; j < usedColors.size(); j++) {
//                        if (usedColors.)
//                    }
//                }
//            }
        }
		
		/**
		 * Parse whiteColor
		 * 
		 * @return
		 */
		public static int parseWhiteColor(){
			return Color.parseColor("#FFFFFF");
		}
		
	}
		
	public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
	
	public static void showAbout(Activity activity) {

        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_about");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new AboutDialog().show(ft, "dialog_about");
    }

    /**
     * About Dialog
     */
    public static class AboutDialog extends DialogFragment {

        private static final String VERSION_UNAVAILABLE = "N/A";

        public AboutDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get app version
            PackageManager pm = getActivity().getPackageManager();
            String packageName = getActivity().getPackageName();
            String versionName;
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                versionName = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = VERSION_UNAVAILABLE;
            }

            // Build the about body view and append the link to see OSS licenses
            SpannableStringBuilder aboutBody = new SpannableStringBuilder();
            aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)));


            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            TextView aboutBodyView = (TextView) layoutInflater.inflate(R.layout.dialog_about, null);
            aboutBodyView.setText(aboutBody);
            aboutBodyView.setMovementMethod(new LinkMovementMethod());

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_about)
                    .setView(aboutBodyView)
                    .setPositiveButton(R.string.about_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }
}
