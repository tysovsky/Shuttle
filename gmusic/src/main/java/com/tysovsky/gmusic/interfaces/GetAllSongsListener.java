package com.tysovsky.gmusic.interfaces;

import com.tysovsky.gmusic.models.GMusicSong;

import java.util.List;

/**
 * Created by tysovsky on 9/10/17.
 */

public interface GetAllSongsListener {
    void OnCompleted(int status, List<GMusicSong> songs);
}
