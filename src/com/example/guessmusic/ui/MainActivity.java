package com.example.guessmusic.ui;

import java.util.ArrayList;
import com.example.guessmusic.R;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.myui.MyGridView;
import com.example.guessmusic.util.Util;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 主界面
 * 
 * @author Administrator
 */
public class MainActivity extends Activity 
						implements IWordButtonClickListener{

	// 唱片相关动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	// 拨杆相关动画
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	// 唱片控件
	private ImageView mViewPan;
	// 拨杆控件
	private ImageView mViewPanBar;
	// Play按键事件
	private ImageButton mBtnPlayStart;

	// 当前动画是否正在运行
	private boolean mIsRunning = false;

	// 文本框容器
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	// 以选择文字框的UI容器
	private LinearLayout mViewWordsContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化控件
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);

		mMyGridView = (MyGridView) findViewById(R.id.gridView);
		//注册监听
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		// 初始化动画
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 开始拨杆退出动画
				mViewPanBar.startAnimation(mBarOutAnim);
			}
		});

		// 拨杆进入动画
		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 开始唱片动画
				mViewPan.startAnimation(mPanAnim);
			}
		});

		// 拨杆退出动画
		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 整套动画结束
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
				Toast.makeText(MainActivity.this, "End", Toast.LENGTH_SHORT)
						.show();
			}
		});

		mBtnPlayStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT)
						.show();
				// 开始动画播放
				handlePlayButton();
			}
		});

		// 初始化游戏数据
		initCurrentStageData();
	}

	// 开始动画
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!mIsRunning) {
				mIsRunning = true;
				// 开始拨杆进入动画
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);
			}
		}
	}

	// 销毁
	@Override
	protected void onPause() {
		super.onPause();
		mViewPan.clearAnimation();
	}

	// 初始化游戏数据
	private void initCurrentStageData() {
		// 初始化已选择框
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(140, 140);
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).getViewButton(),
					params);
			
		}

		// 获得数据
		mAllWords = initAllWord();
		// 更新数据
		mMyGridView.updateData(mAllWords);
	}

	// 初始化待选择文字框
	private ArrayList<WordButton> initAllWord() {

		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();
			button.setWordString("好");
			data.add(button);
		}
		return data;
	}

	// 初始化以选择文本框
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < 4; i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);
			WordButton holder = new WordButton();
			holder.setViewButton((Button) view.findViewById(R.id.item_btn));
			holder.getViewButton().setTextColor(Color.WHITE);
			holder.getViewButton().setText("");
			holder.setIsVisiable(false);

			holder.getViewButton().setBackgroundResource(
					R.drawable.game_wordblank);
			data.add(holder);
		}

		return data;
	}
	
	
	@Override
	public void onWordButtonClick(WordButton wordButton) {
		Toast.makeText(this, wordButton.getIndex() + "", Toast.LENGTH_SHORT).show();
	}
}
