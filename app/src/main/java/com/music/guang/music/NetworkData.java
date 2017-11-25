package com.music.guang.music;

import android.graphics.Bitmap;

/**
 * Created by jsb-1 on 2017/11/24.
 */

public class NetworkData {
    private String filename;
    private String songname;
    private String m4afilesize;
    private String hash320;
    private String mvhash;
    private String ownercount;
    private String sqhash;
    private String filesize320;
    private String duration;
    private String album_id;
    private String hash;
    private String singername;
    private String sqfilesize;
    private String album_name;
    private String album_img;

    public void setAlbum_img(String album_img) {
        this.album_img = album_img;
    }

    public String getAlbum_img() {

        return album_img;
    }

    private String hash128;
    private String filesize128;
    private String fileSize;
    private String req_hash;
    private String imgUrl;
    private String url128;
    private String url320;
    private String urlsq;
    private String urlmv;
    public String getUrlmv() {
        return urlmv;
    }

    public void setUrlmv(String urlmv) {
        this.urlmv = urlmv;
    }

    private String extName;
    private Bitmap pic;
    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public String getExtNamesq() {
        return extNamesq;
    }

    public void setExtNamesq(String extNamesq) {
        this.extNamesq = extNamesq;
    }

    private String extNamesq;
    public String getUrl320() {
        return url320;
    }

    public void setUrl320(String url320) {
        this.url320 = url320;
    }

    public String getUrlsq() {
        return urlsq;
    }

    public void setUrlsq(String urlsq) {
        this.urlsq = urlsq;
    }

    //获取歌词
    private String lycid;
    private String accesskey;
    private String fmt;
    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    //获取格式写真
    private String anthor_xiezhen;
    //构造函数
    public NetworkData() {
        // TODO Auto-generated constructor stub
    }

    public String getFilename() {
        return filename;
    }



    public void setFilename(String filename) {
        this.filename = filename;
    }



    public String getSongname() {
        return songname;
    }



    public void setSongname(String songname) {
        this.songname = songname;
    }



    public String getM4afilesize() {
        return m4afilesize;
    }



    public void setM4afilesize(String m4afilesize) {
        this.m4afilesize = m4afilesize;
    }



    public String getHash320() {
        return hash320;
    }



    public void setHash320(String hash320) {
        this.hash320 = hash320;
    }



    public String getMvhash() {
        return mvhash;
    }



    public void setMvhash(String mvhash) {
        this.mvhash = mvhash;
    }



    public String getOwnercount() {
        return ownercount;
    }



    public void setOwnercount(String ownercount) {
        this.ownercount = ownercount;
    }



    public String getSqhash() {
        return sqhash;
    }



    public void setSqhash(String sqhash) {
        this.sqhash = sqhash;
    }



    public String getFilesize320() {
        return filesize320;
    }



    public void setFilesize320(String filesize320) {
        this.filesize320 = filesize320;
    }



    public String getDuration() {
        return duration;
    }



    public void setDuration(String duration) {
        this.duration = duration;
    }



    public String getAlbum_id() {
        return album_id;
    }



    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }



    public String getHash() {
        return hash;
    }



    public void setHash(String hash) {
        this.hash = hash;
    }



    public String getSingername() {
        return singername;
    }



    public void setSingername(String singername) {
        this.singername = singername;
    }



    public String getSqfilesize() {
        return sqfilesize;
    }



    public void setSqfilesize(String sqfilesize) {
        this.sqfilesize = sqfilesize;
    }



    public String getAlbum_name() {
        return album_name;
    }



    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }



    public String getHash128() {
        return hash128;
    }



    public void setHash128(String hash128) {
        this.hash128 = hash128;
    }



    public String getFilesize128() {
        return filesize128;
    }



    public void setFilesize128(String filesize128) {
        this.filesize128 = filesize128;
    }



    public String getFileSize() {
        return fileSize;
    }



    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }



    public String getReq_hash() {
        return req_hash;
    }



    public void setReq_hash(String req_hash) {
        this.req_hash = req_hash;
    }

    public String getImgUrl() {
        return imgUrl;
    }



    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }



    public String getUrl128() {
        return url128;
    }



    public void setUrl128(String url) {
        this.url128 = url;
    }



    public String getLycid() {
        return lycid;
    }



    public void setLycid(String lycid) {
        this.lycid = lycid;
    }



    public String getAccesskey() {
        return accesskey;
    }



    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }



    public String getAnthor_xiezhen() {
        return anthor_xiezhen;
    }



    public void setAnthor_xiezhen(String anthor_xiezhen) {
        this.anthor_xiezhen = anthor_xiezhen;
    }

}
