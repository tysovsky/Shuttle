package com.simplecity.amp_library.services;

import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.simplecity.amp_library.utils.LogHelper;
import com.simplecity.amp_library.utils.MediaIDHelper;

import java.util.ArrayList;
import java.util.List;

public class MusicProvider {

    private static final String TAG = "MusicProvider";

    @interface State {
        int NON_INITIALIZED = 0;
        int INITIALIZING = 1;
        int INITIALIZED = 2;
    }

    @State
    private volatile int mCurrentState = State.NON_INITIALIZED;

    public boolean isInitialized() {
        if (true) return true;
        return mCurrentState == State.INITIALIZED;
    }

    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId, Resources resources) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        String[] genres = new String[]{"One", "Two", "Three"};

        MediaMetadataCompat[] metadataCompats = new MediaMetadataCompat[]{
                new MediaMetadataCompat.Builder()
                        .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, "Tool")
                        .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, "Lateralus")
                        .putText(MediaMetadataCompat.METADATA_KEY_TITLE, "Disposition")
                        .build()
        };

        if (!MediaIDHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        if (MediaIDHelper.MEDIA_ID_ROOT.equals(mediaId)) {
            mediaItems.add(createBrowsableMediaItemForRoot(resources));

        } else if (MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE.equals(mediaId)) {
            for (String genre : genres) {
                mediaItems.add(createBrowsableMediaItemForGenre(genre, resources));
            }

        } else if (mediaId.startsWith(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE)) {
            String genre = MediaIDHelper.getHierarchy(mediaId)[1];
            for (MediaMetadataCompat metadata : metadataCompats) {
                mediaItems.add(createMediaItem(metadata));
            }

        } else {
            LogHelper.w(TAG, "Skipping unmatched mediaId: ", mediaId);
        }
        return mediaItems;
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForRoot(Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE)
                .setTitle("Browse genres")
                .setSubtitle("Browse subtitle")
                .setIconUri(Uri.parse("android.resource://" +
                        "com.simplecity.amp_library/drawable/ic_play_white"))
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForGenre(String genre, Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(MediaIDHelper.createMediaID(null, MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE, genre))
                .setTitle(genre)
                .setSubtitle(genre + " subtitle")
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)
        String genre = metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(metadata.getDescription().getMediaId(), MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE, genre);
        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .build();
        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }

}
