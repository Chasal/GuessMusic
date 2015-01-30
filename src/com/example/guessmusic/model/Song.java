package com.example.guessmusic.model;

public class Song {
	
	//歌曲名称
	private String mSongName;
	//歌曲的文件名
	private String mSongFileName;
	//歌曲的名字长度
	private int mNameLength;
	
	//将歌曲名字转为数组
	public char[] getNameCharacters(){
		return mSongName.toCharArray();
	}
	
	public String getSongName() {
		return mSongName;
	}
	public void setSongName(String songName) {
		this.mSongName = songName;
		
		this.mNameLength = songName.length();
	}
	public String getSongFileName() {
		return mSongFileName;
	}
	public void setSongFileName(String songFileName) {
		this.mSongFileName = songFileName;
	}
	public int getNameLength() {
		return mNameLength;
	}
	
	
	
}
