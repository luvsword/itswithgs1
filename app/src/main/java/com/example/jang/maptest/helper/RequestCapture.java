/* Copyright 2016 Square, Inc.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */
package com.example.jang.maptest.helper;

/**
 * Created by luvsword on 2016-11-13.
 */

import android.os.AsyncTask;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import com.google.gson.Gson;
import android.util.Log;

import java.io.IOException;
import com.example.jang.maptest.App.AppConfig;

public class RequestCapture extends AsyncTask<String, Void, String>{

    static OkHttpClient client = new OkHttpClient();
    static String url = AppConfig.EPCIS_SERVER_CAPTURE;
    public static final MediaType mediaType = MediaType.parse("application/xml; charset=utf-8");
    private static final String TAG = "RequestCapture";
    @Override
    protected String doInBackground(String... params) {

        try {
            String xml = params[0];
            RequestBody body = RequestBody.create(mediaType, xml);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "doInBackground end");
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "result = " + result);
        return ;
    }
}
