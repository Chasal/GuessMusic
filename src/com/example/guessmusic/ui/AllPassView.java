package com.example.guessmusic.ui;

import com.example.guessmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 通关界面
 * @author Administrator
 *
 */

public class AllPassView extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_pass_view);
		
		//隐藏右上角的金币
		FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
	}
}


















