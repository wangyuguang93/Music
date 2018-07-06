package com.music.guang.musicG;

import android.graphics.Bitmap;

/**
 * Created by guang on 2017/8/19.
 */

public class MusicData {
        private int id;
        private int trackId;
        private String title;
        private String album;
        private String artist;
        private String url;
        private int duration;
        private long albumId;
        private long size;
        private Bitmap bnendi_pic;
        private boolean isPlayging;


    public boolean getisPlayging() {
        return isPlayging;
    }

    public void setPlayging(boolean isplayging) {
        this.isPlayging = isplayging;
    }

    public Bitmap getBnendi_pic() {
            return bnendi_pic;
        }
        public void setBnendi_pic(Bitmap bnendi_pic) {
            this.bnendi_pic = bnendi_pic;
        }
        private String disName;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getAlbum() {
            return album;
        }
        public void setAlbum(String album) {
            this.album = album;
        }
        public String getArtist() {
            return artist;
        }
        public void setArtist(String artist) {
            this.artist = artist;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getTrackId() {
            return trackId;
        }
        public void setTrackId(int trackId) {
            this.trackId = trackId;
        }
        public int getDuration() {
            return duration;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }
        public long getAlbumId() {
            return albumId;
        }
        public void setAlbumId(long albumId) {
            this.albumId = albumId;
        }
        public long getSize() {
            return size;
        }
        public void setSize(long size) {
            this.size = size;
        }
        public String getDisName() {
            return disName;
        }
        public void setDisName(String disName) {
            this.disName = disName;
        }
    }