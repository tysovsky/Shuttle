package com.tysovsky.gmusic.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.Log;

import com.tysovsky.gmusic.Constants;
import com.tysovsky.gmusic.models.GMusicSong;
import com.tysovsky.gmusic.interfaces.GetAllSongsListener;
import com.tysovsky.gmusic.interfaces.GetStreamUrlListener;
import com.tysovsky.gmusic.interfaces.LoginListener;
import com.tysovsky.gmusic.Status;
import com.tysovsky.gmusic.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.*;
import okhttp3.Call;
import svarzee.gps.gpsoauth.Gpsoauth;

/**
 * Created by tysovsky on 9/9/17.
 */

public class GMusicClient {

    public static final String TAG = "GMusicClient";


    private static Context context;

    private OkHttpClient httpClient;
    private String masterToken;
    private String authToken;

    private String androidId;

    private boolean isSubscribed;



    public GMusicClient(Context context){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.context = context;
        httpClient = new OkHttpClient();
        //Check if already logged in
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        masterToken = prefs.getString(Constants.SP_MASTER_TOKEN, null);
        authToken = prefs.getString(Constants.SP_AUTH_TOKEN, null);
        androidId = prefs.getString(Constants.SP_ANDROID_ID, null);


        RequestManager.authToken = authToken;
        RequestManager.androidId = androidId;


    }

    public static GMusicClient getInstance(){
        return new GMusicClient(context);
    }

    public boolean login(String username, String password, String androidId){
        this.androidId = androidId;
        RequestManager.androidId = androidId;

        try {

            Gpsoauth gpsoauth = new Gpsoauth(httpClient);

            masterToken = gpsoauth.performMasterLoginForToken(username, password, androidId);

            Response oauthRes = gpsoauth.performOAuth(username, masterToken, androidId,
                    "sj", "com.google.android.music",
                    "38918a453d07199354f8b19af05ec6562ced5788");

            String[] responses = oauthRes.body().string().split("\n");
            for (int i = 0; i < responses.length; i++){
                if (responses[i].contains("Auth=")){
                    authToken=responses[i].substring(5);
                }
            }

            RequestManager.authToken = authToken;

            //Save the tokens to SharedPreferences
            SharedPreferences.Editor prefsBuilder = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
            prefsBuilder.putString(Constants.SP_MASTER_TOKEN, masterToken);
            prefsBuilder.putString(Constants.SP_AUTH_TOKEN, authToken);
            prefsBuilder.putString(Constants.SP_ANDROID_ID, androidId);
            prefsBuilder.commit();

            return true;

        }
        catch (Exception e){
            Log.d(TAG, "Exception logging in: " + e.getMessage());
        }
        return false;
    }
    public void loginAsync(final String username, final String password, final String androidId, final LoginListener listener){
        final HandlerThread loginThread = new HandlerThread("login_thread");
        loginThread.start();
        Handler loginHandler = new Handler(loginThread.getLooper());
        loginHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.OnComplete(login(username, password, androidId)? Status.SUCCESS:Status.FAILURE);
                loginThread.quit();
            }
        });

    }


    public ArrayList<GMusicSong> getAllSongs(){

        try (Response response = httpClient.newCall(RequestManager.getAllSongsRequest()).execute()) {
            if (!response.isSuccessful()){
                return null;
            }

            return Utils.ConvertJsonToGMusicSongList(response.body().string());

        }

        catch (Exception e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }
    public void getAllSongsAsync(final GetAllSongsListener listener){
        httpClient.newCall(RequestManager.getAllSongsRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.OnCompleted(Status.FAILURE, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    listener.OnCompleted(Status.SUCCESS, Utils.ConvertJsonToGMusicSongList(response.body().string()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public String getStreamingUrl(UUID id){
        String streamUrl = null;
        try (Response response = httpClient.newCall(RequestManager.streamUrlRequest(id)).execute()) {

            streamUrl =  response.networkResponse().toString();
            streamUrl = streamUrl.substring(streamUrl.indexOf("url=")+4, streamUrl.length()-1);

        }

        catch (Exception e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }

        return streamUrl;
    }
    public void getStreamingUrlAsync(final UUID id, final GetStreamUrlListener listener){
        httpClient.newCall(RequestManager.streamUrlRequest(id)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.OnCompleted(Status.FAILURE, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String streamUrl =  response.networkResponse().toString();
                streamUrl = streamUrl.substring(streamUrl.indexOf("url=")+4, streamUrl.length()-1);
                listener.OnCompleted(Status.SUCCESS, streamUrl);
            }
        });
    }


    public void logout(){
        SharedPreferences.Editor prefsBuilder = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        prefsBuilder.remove(Constants.SP_MASTER_TOKEN);
        prefsBuilder.remove(Constants.SP_AUTH_TOKEN);
        prefsBuilder.commit();

    }

    public boolean isAuthenticated(){
        if (masterToken != null && authToken != null){
            return true;
        }
        else{
            return false;
        }
    }



}
