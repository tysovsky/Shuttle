package com.tysovsky.gmusic;

import android.util.Base64;

import com.tysovsky.gmusic.models.GMusicSong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by tysovsky on 11/09/17.
 */

public class Utils {
    /**
     * Get signature and salt for a request
     * @param songId UUID of a song to sign
     * @return array of strings where the first string is the signature and the second one is the salt
     */
    public static String[] getSigAndSalt(String songId){

        try {
            byte[] s1 = Base64.decode("VzeC4H4h+T2f0VI180nVX8x+Mb5HiTtGnKgH52Otj8ZCGDz9jRWyHb6QXK0JskSiOgzQfwTY5xgLLSdUSreaLVMsVVWfxfa8Rw==", Base64.DEFAULT);
            byte[] s2 = Base64.decode("ZAPnhUkYwQ6y5DdQxWThbvhJHN8msQ1rqJw0ggKdufQjelrKuiGGJI30aswkgCWTDyHkTGK9ynlqTkJ5L4CiGGUabGeo8M6JTQ==", Base64.DEFAULT);

            byte[] s3 = new byte[s1.length];

            int i = 0;
            for (byte b : s1)
                s3[i] = (byte)(b ^ s2[i++]);

            String key = new String(s3, "ASCII");

            String salt = String.valueOf(System.currentTimeMillis());

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
            javax.crypto.spec.SecretKeySpec secret = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA1");
            mac.init(secret);
            byte[] digest = mac.doFinal((songId+salt).getBytes());

            String signature = Base64.encodeToString(digest, Base64.URL_SAFE);
            signature = signature.replace("\n","");
            signature = signature.replace("=","");

            String[] res = new String[]{signature, salt};


            return res;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String androidIdHexToDecimal(String hexAndroidId){
        String androidIdDecimal= new BigInteger(hexAndroidId, 16).toString();
        androidIdDecimal = androidIdDecimal.substring(0, androidIdDecimal.length() - 3);

        return androidIdDecimal;
    }

    public static ArrayList<GMusicSong> ConvertJsonToGMusicSongList(String jsonResponse) throws JSONException {
        ArrayList<GMusicSong> songs = new ArrayList<>();

        JSONArray jArray = new JSONObject(jsonResponse).getJSONObject("data").getJSONArray("items");

        for (int i = 0; i < jArray.length(); i++){
            JSONObject jSong = jArray.getJSONObject(i);
            GMusicSong song = new GMusicSong();
            try {
                song.id = UUID.fromString(jSong.getString("id"));
            }
            catch (JSONException e){
                song.id = new UUID(0,0);
            }

            try {
                song.artist = jSong.getString("artist");
            }
            catch (JSONException e){
                song.artist = "na";
            }

            try {
                song.album = jSong.getString("album");
            }
            catch (JSONException e){
                song.album = "na";
            }

            try {
                song.title = jSong.getString("title");
            }
            catch (JSONException e){
                song.title = "na";
            }

            try {
                song.comment = jSong.getString("comment");
            }
            catch (JSONException e){
                song.comment = "";
            }

            try {
                song.rating = jSong.getInt("rating");
            }
            catch (JSONException e){
                song.rating = 0;
            }

            try {
                song.composer = jSong.getString("composer");
            }
            catch (JSONException e){
                song.composer = "";
            }

            try {
                song.year = jSong.getInt("year");
            }
            catch (JSONException e){
                song.year = 1970;
            }

            try {
                song.creationTimestamp = jSong.getLong("creationTimestamp");
            }
            catch (JSONException e){
                song.creationTimestamp = 0;
            }

            try {
                song.totalDiscCount = jSong.getInt("totalDiscCount");
            }
            catch (JSONException e){
                song.totalDiscCount = 1;
            }
            try {
                song.recentTimeStamp = jSong.getLong("recentTimestamp");
            }
            catch (JSONException e){
                song.recentTimeStamp = 0;
            }

            try{
                song.albumArtist = jSong.getString("albumArtist");
            }
            catch (JSONException e){
                song.albumArtist = "";
            }

            try{
                song.trackNumber = jSong.getInt("trackNumber");
            }
            catch (JSONException e){
                song.trackNumber = 1;
            }

            try{
                song.discNumber = jSong.getInt("discNumber");
            }
            catch (JSONException e){
                song.discNumber = 1;
            }

            try{
                song.storeId = jSong.getString("storeId");
            }
            catch (JSONException e){
                song.storeId = "na";
            }

            try{
                song.nid = jSong.getString("nid");
            }
            catch (JSONException e){
                song.nid = "na";
            }

            try{
                song.estimatedSize = jSong.getLong("estimatedSize");
            }
            catch (JSONException e){
                song.estimatedSize = 0;
            }

            try{
                song.albumId = jSong.getString("albumId");
            }
            catch (JSONException e){
                song.albumId = "na";
            }

            try{
                song.genre = jSong.getString("genre");
            }
            catch (JSONException e){
                song.genre = "";
            }

            try {
                song.playCount = jSong.getInt("playCount");
            }
            catch (JSONException e){
                song.playCount = 0;
            }

            try {
                song.kind = jSong.getString("kind");
            }
            catch (JSONException e){
                song.kind = "sj#track";
            }

            try{
                song.lastModifiedTimestamp = jSong.getLong("lastModifiedTimestamp");
            }
            catch (JSONException e){
                song.lastModifiedTimestamp = 0;
            }

            try {
                song.clientId = jSong.getString("clientId");
            }
            catch (JSONException e){
                song.clientId = "not_found";
            }

            try {
                song.durationMillis = jSong.getLong("durationMillis");
            }
            catch (JSONException e){
                song.durationMillis = 0;
            }


            try {
                JSONArray albumArtRefArray = jSong.getJSONArray("albumArtRef");
                song.albumArtRefs = new String[albumArtRefArray.length()];
                for (int j = 0; j < albumArtRefArray.length(); j++){
                    song.albumArtRefs[j] = albumArtRefArray.getJSONObject(j).getString("url");
                }
            }
            catch (JSONException e){
                song.albumArtRefs = new String[]{"na"};
            }

            try{
                JSONArray artistIdArray = jSong.getJSONArray("artistId");
                song.artistIds = new String[artistIdArray.length()];
                for (int j = 0; j < artistIdArray.length(); j++){
                    song.artistIds[j] = artistIdArray.getString(j);
                }
            }
            catch (JSONException e){
                song.artistIds = new String[]{"na"};
            }


            try{
                JSONArray artistArtRefArray = jSong.getJSONArray("artistArtRef");
                song.artistArtRefs = new String[artistArtRefArray.length()];
                for (int j = 0; j < artistArtRefArray.length(); j++){
                    song.artistArtRefs[j] = artistArtRefArray.getJSONObject(j).getString("url");
                }
            }
            catch (JSONException e){
                song.artistArtRefs = new String[]{"na"};
            }

            songs.add(song);

        }

        return songs;
    }
}
