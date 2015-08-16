/*
 * Copyright 2015 Ashish Ranjan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashish.msngr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ShareExternalServer appUtil;
	String regId;
	String userId;
	AsyncTask<Void, Void, String> shareRegidTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appUtil = new ShareExternalServer();

		regId = getIntent().getStringExtra("regId");
		userId = getIntent().getStringExtra("userId");
		Log.d("MainActivity", "regId: " + regId);

		final Context context = this;
		shareRegidTask = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String result = appUtil.shareRegIdWithAppServer(context, regId,userId);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				shareRegidTask = null;
				((TextView)(findViewById(R.id.lblMessage))).setText("Done");
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();
				try{if(result.contains(":")){
					Intent i = new Intent(context,ChatActivity.class);
					i.putExtra("userId",result.substring(result.indexOf(":")+2));
					startActivity(i);
				}}catch(Exception e){Toast.makeText(context,e+" "+e.getMessage(),Toast.LENGTH_LONG).show();}
			}

		};
		shareRegidTask.execute(null, null, null);
	}

}
