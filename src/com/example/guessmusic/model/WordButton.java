package com.example.guessmusic.model;

import android.widget.Button;

/**
 * 实体类，创建每个Button的Word
 * @author Administrator
 *
 */
public class WordButton {
	//第几个Word
	private int mIndex;
	//是否可见
	private boolean mIsVisiable;
	//要显示的字
	private String mWordString;
	private Button mViewButton;
	
	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		this.mIndex = index;
	}

	public boolean isIsVisiable() {
		return mIsVisiable;
	}

	public void setIsVisiable(boolean isVisiable) {
		this.mIsVisiable = isVisiable;
	}

	public String getWordString() {
		return mWordString;
	}

	public void setWordString(String wordString) {
		this.mWordString = wordString;
	}

	public Button getViewButton() {
		return mViewButton;
	}

	public void setViewButton(Button viewButton) {
		this.mViewButton = viewButton;
	}
}
