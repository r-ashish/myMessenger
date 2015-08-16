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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ChatActivity extends Activity {
    String senderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        senderId = getIntent().getStringExtra("userId");
        setTitle(senderId);
        setContentView(R.layout.activity_chat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    AsyncTask<Void, Void, String> sendMessageTask;
    public  void sendMessage(View view){
        final EditText idBox = ((EditText)findViewById(R.id.userId));
        final EditText msgBox = ((EditText)findViewById(R.id.msg));
        final String userId = idBox.getText().toString();
        final String msg = msgBox.getText().toString();
        try{
        sendMessageTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("message", msg);
                paramsMap.put("userid",userId);
                paramsMap.put("fromid",senderId);
                try {
                    URL serverUrl = null;
                    try {
                        serverUrl = new URL(Config.APP_SERVER_URL_MSG);
                    } catch (MalformedURLException e) {
                        Log.e("AppUtil", "URL Connection Error: "
                                + Config.APP_SERVER_URL_MSG, e);
                        result = "Invalid URL: " + Config.APP_SERVER_URL_MSG;
                    }

                    StringBuilder postBody = new StringBuilder();
                    Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet()
                            .iterator();

                    while (iterator.hasNext()) {
                        Map.Entry<String, String> param = iterator.next();
                        postBody.append(param.getKey()).append('=')
                                .append(param.getValue());
                        if (iterator.hasNext()) {
                            postBody.append('&');
                        }
                    }
                    String body = postBody.toString();
                    byte[] bytes = body.getBytes();
                    HttpURLConnection httpCon = null;
                    try {
                        httpCon = (HttpURLConnection) serverUrl.openConnection();
                        httpCon.setDoOutput(true);
                        httpCon.setUseCaches(false);
                        httpCon.setFixedLengthStreamingMode(bytes.length);
                        httpCon.setRequestMethod("POST");
                        httpCon.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded;charset=UTF-8");
                        OutputStream out = httpCon.getOutputStream();
                        out.write(bytes);
                        out.close();

                        InputStream is = new BufferedInputStream(httpCon.getInputStream());
                        String status = ShareExternalServer.readStream(is);
                        result =  "Message sent successfully!";

                    } finally {
                        if (httpCon != null) {
                            httpCon.disconnect();
                        }
                    }

                } catch (IOException e) {
                    result = "Look's like there's a problem with your Internet Connection.\nCheck your connection and try again!";
                    Log.e("AppUtil", "Error in sharing with App Server: " + e);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                sendMessageTask = null;
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
                idBox.setText("");
                msgBox.setText("");
            }

        };
        if(userId.equals("")) Toast.makeText(this,"Please enter UserId!",Toast.LENGTH_LONG).show();
        else if(msg.equals(""))Toast.makeText(this,"Message is empty!",Toast.LENGTH_LONG).show();
        else sendMessageTask.execute(null,null,null);}catch(Exception e){Toast.makeText(this,e+" "+e.getMessage(),Toast.LENGTH_LONG).show();}
    }
}
