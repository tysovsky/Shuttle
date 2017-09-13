package com.tysovsky.gmusic.core;

import com.tysovsky.gmusic.models.GMusicSong;
import com.tysovsky.gmusic.Utils;

import java.util.UUID;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by tysovsky on 11/09/17.
 */

public class RequestManager {

    public static String authToken, androidId;

    private static final String jsUrl = "https://mclients.googleapis.com/sj/v2.5/",
            jsStreamUrl = "https://mclients.googleapis.com/music/";


    public static Request getAllSongsRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(jsUrl+"trackfeed").newBuilder()
                .addQueryParameter("alt", "json")
                .addQueryParameter("dv", "0")
                .addQueryParameter("hl", "en_US")
                .addQueryParameter("include-tracks", "true")
                .addQueryParameter("tier", "aa")
                .addQueryParameter("updated-min", "-1");


        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{max-results:20000}");


        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "GoogleLogin auth="+authToken)
                .post(body)
                .build();

        return request;
    }

    public static Request streamUrlRequest(UUID id){
        String[] sigAndSalt = Utils.getSigAndSalt(id.toString());
        HttpUrl.Builder urlBuilder = HttpUrl.parse(jsStreamUrl+"mplay").newBuilder()
                .addQueryParameter("net", "mob")
                .addQueryParameter("dv", "0")
                .addQueryParameter("hl", "en_US")
                .addQueryParameter("opt", "hi")
                .addQueryParameter("tier", "aa")
                .addQueryParameter("sig", sigAndSalt[0])
                .addQueryParameter("slt", sigAndSalt[1])
                .addQueryParameter("songid", id.toString())
                .addQueryParameter("pt", "e");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("X-Device-ID", Utils.androidIdHexToDecimal(androidId))
                .addHeader("Authorization", "GoogleLogin auth="+authToken)
                .get()
                .build();

        return request;
    }
}
