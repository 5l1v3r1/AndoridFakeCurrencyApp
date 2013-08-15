/*
 *Fake Currency Detector -	Verifies Indian currency serial number starts with fake series
 * 
 *Copyright (C) 2013, DCS.
 *
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.dcs.fakecurrencydetector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dcs.fakecurrencydetector.R;

public class MainActivity extends Activity implements OnClickListener {

	private EditText input;
	private RadioGroup radioGroup;
	private TextView resultView;

	private Map<String, List<String>> fakeJSONData = new HashMap<String, List<String>>();

	private enum Notes {
		ONE(1), FIVE(5), TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100), FIVE_HUNDRED(
				500), THOUSAND(1000);

		Notes(int number) {
			this.number = number;
		};

		private int number;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//
		Button searchButton = (Button) findViewById(R.id.button1);
		searchButton.setOnClickListener(this);
		//
		input = (EditText) findViewById(R.id.editText1);

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		resultView = (TextView) findViewById(R.id.textView1);
		// loadJSONData to map
		loadJSONData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		String serialPrefix = readText(input);
		int id = radioGroup.getCheckedRadioButtonId();
		RadioButton selectedRadio = (RadioButton) radioGroup.findViewById(id);

		String noteValue = selectedRadio.getText().toString();
		boolean found = foundInFakeNotesStore(serialPrefix, noteValue);
		if (found) {
			resultView.setTextColor(Color.RED);
			resultView.setText("Found in fake note series");
		} else {
			resultView.setTextColor(Color.rgb(0x33, 0x66, 0x00));
			resultView.setText("Not found in fake note series");
		}

	}

	private void loadJSONData() {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(getResources()
					.openRawResource(R.raw.data)));
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line).append("\n");
			}

		} catch (FileNotFoundException e) {
			// TODO exception
		} catch (IOException e) {
			// TODO exception
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// should not happen
				}
			}
		}
		try {
			JSONObject jsonData = new JSONObject(buffer.toString());
			for (Notes note : Notes.values()) {
				String noteValue = note.number + "";
				JSONArray jsonArray = jsonData.getJSONArray(noteValue + "");
				for (int i = 0; i < jsonArray.length(); i++) {
					if (!fakeJSONData.containsKey(noteValue)) {
						fakeJSONData.put(noteValue, new ArrayList<String>());
					}
					// adding to list
					fakeJSONData.get(noteValue)
							.add(jsonArray.get(i).toString());
				}
			}

		} catch (JSONException e) {
			// TODO exception
		}
	}

	/**
	 * 
	 * 
	 * @param serialPrefix
	 *            prefix of Indian currency series
	 * @param noteValue
	 *            Currency note value ex: 1000 or 500 or 100
	 * @return true , give note details found in fake data.
	 */
	private boolean foundInFakeNotesStore(String serialPrefix, String noteValue) {

		List<String> fakeData = fakeJSONData.get(noteValue);
		if (fakeData != null) {
			for (String record : fakeData) {
				if (record.equalsIgnoreCase(serialPrefix)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Reads text from EditText
	 * 
	 * @param input
	 * @return
	 */
	private String readText(EditText input) {
		return input.getText().toString();
	}
}
