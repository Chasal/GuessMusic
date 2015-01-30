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
 * ������
 * 
 * @author Administrator
 */
public class MainActivity extends Activity 
						implements IWordButtonClickListener{

	// ��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	// ������ض���
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	// ��Ƭ�ؼ�
	private ImageView mViewPan;
	// ���˿ؼ�
	private ImageView mViewPanBar;
	// Play�����¼�
	private ImageButton mBtnPlayStart;

	// ��ǰ�����Ƿ���������
	private boolean mIsRunning = false;

	// �ı�������
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	// ��ѡ�����ֿ��UI����
	private LinearLayout mViewWordsContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ��ʼ���ؼ�
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);

		mMyGridView = (MyGridView) findViewById(R.id.gridView);
		//ע�����
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		// ��ʼ������
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
				// ��ʼ�����˳�����
				mViewPanBar.startAnimation(mBarOutAnim);
			}
		});

		// ���˽��붯��
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
				// ��ʼ��Ƭ����
				mViewPan.startAnimation(mPanAnim);
			}
		});

		// �����˳�����
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
				// ���׶�������
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
				// ��ʼ��������
				handlePlayButton();
			}
		});

		// ��ʼ����Ϸ����
		initCurrentStageData();
	}

	// ��ʼ����
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!mIsRunning) {
				mIsRunning = true;
				// ��ʼ���˽��붯��
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);
			}
		}
	}

	// ����
	@Override
	protected void onPause() {
		super.onPause();
		mViewPan.clearAnimation();
	}

	// ��ʼ����Ϸ����
	private void initCurrentStageData() {
		// ��ʼ����ѡ���
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(140, 140);
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).getViewButton(),
					params);
			
		}

		// �������
		mAllWords = initAllWord();
		// ��������
		mMyGridView.updateData(mAllWords);
	}

	// ��ʼ����ѡ�����ֿ�
	private ArrayList<WordButton> initAllWord() {

		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();
			button.setWordString("��");
			data.add(button);
		}
		return data;
	}

	// ��ʼ����ѡ���ı���
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
