package com.tysovsky.gmusic.models;

import java.util.UUID;

/**
 * Created by tysovsky on 9/12/17.
 */

public class GMusicSong {

    public UUID     id;
    public String   title,
                    artist,
                    album,
                    albumArtist,
                    composer,
                    genre,
                    kind,
                    storeId,
                    nid,
                    albumId,
                    clientId,
                    comment;
    public int      year,
                    trackNumber,
                    totalTrackCount,
                    discNumber,
                    totalDiscCount,
                    beatsPerMinute,
                    playCount,
                    rating;
    public long     creationTimestamp,
                    recentTimeStamp,
                    lastModifiedTimestamp,
                    durationMillis,
                    estimatedSize;
    public String[] artistIds,
                    artistArtRefs,
                    albumArtRefs;



}
