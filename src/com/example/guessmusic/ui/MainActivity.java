package com.example.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

import com.example.guessmusic.R;
import com.example.guessmusic.data.Const;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.Song;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.myui.MyGridView;
import com.example.guessmusic.util.Util;

/**
 * ������
 * 
 * @author Administrator
 */
public class MainActivity extends Activity implements IWordButtonClickListener {

	/**
	 * ��״̬ -- ��ȷ
	 */
	private final static int STATUS_ANSWER_RIGHT = 1;

	/**
	 * ��״̬ -- ����
	 */
	private final static int STATUS_ANSWER_WRONG = 2;

	/**
	 * ��״̬ -- ������
	 */
	private final static int STATUS_ANSWER_LACK = 3;

	// ��˸����
	private final static int SPASH_TIMES = 6;

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

	// ���ؽ���
	private View mPassView;

	// ��ǰ�����Ƿ���������
	private boolean mIsRunning = false;

	// �ı�������
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	// ��ѡ�����ֿ��UI����
	private LinearLayout mViewWordsContainer;

	// ��ǰ�ĸ���
	private Song mCurretSong;

	// ��ǰ�ؿ�������
	private int mCurrentStageIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ��ʼ���ؼ�
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);

		mMyGridView = (MyGridView) findViewById(R.id.gridView);
		// ע�����
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

	/**
	 * ��ʼ����
	 */
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

	/**
	 * ���ظ�������ȡ������Ϣ
	 * 
	 * @param stageIndex ��������
	 * @return
	 */
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		return song;
	}

	/**
	 * ��ʼ����Ϸ����
	 */
	private void initCurrentStageData() {
		// ��ȡ��ǰ�ؿ��ĸ�����Ϣ
		mCurretSong = loadStageSongInfo(++mCurrentStageIndex);

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

	/**
	 * ��ʼ����ѡ�����ֿ�
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {

		ArrayList<WordButton> data = new ArrayList<WordButton>();
		// ������еĴ�ѡ����
		String[] words = generateWords();

		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();
			button.setWordString(words[i]);
			data.add(button);
		}
		return data;
	}

	/**
	 * �������еĴ�ѡ����
	 * 
	 * @return
	 */
	private String[] generateWords() {
		Random random = new Random();
		String[] words = new String[MyGridView.COUNT_WORDS];

		// �������
		for (int i = 0; i < mCurretSong.getNameLength(); i++) {
			words[i] = mCurretSong.getNameCharacters()[i] + "";
		}

		// ��ȡ������ֲ���������
		for (int i = mCurretSong.getNameLength(); i < MyGridView.COUNT_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// ��������˳�����ȴ�����Ԫ�������ѡȡһ�����һ��Ԫ�ؽ��н���,
		// Ȼ���ڵڶ���֮��ѡ��һ��Ԫ����ڶ���������ֱ�����һ��Ԫ�ء�
		// �����ܹ�ȷ��ÿ��Ԫ����ÿ��λ�õĸ��ʶ���1/n
		for (int i = MyGridView.COUNT_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}

		return words;
	}

	/**
	 * ��ʼ����ѡ���ı���
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// ��̬�����ѡ�ı���ĳ���
		for (int i = 0; i < mCurretSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);

			final WordButton holder = new WordButton();
			holder.setViewButton((Button) view.findViewById(R.id.item_btn));
			holder.getViewButton().setTextColor(Color.WHITE);
			holder.getViewButton().setText("");
			holder.setIsVisiable(false);
			holder.getViewButton().setBackgroundResource(R.drawable.game_wordblank);

			holder.getViewButton().setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							clearTheAnswer(holder);
						}
					});

			data.add(holder);
		}

		return data;
	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		// Toast.makeText(this, wordButton.getIndex() + "", Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

		// ��ô�״̬
		int checkResult = checkTheAnswer();

		// ����
		if (checkResult == STATUS_ANSWER_RIGHT) {
			// Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
			//���ؽ���
			handlePassEvent();
		} else if (checkResult == STATUS_ANSWER_WRONG) {
			// Toast.makeText(MainActivity.this, "Wrong",  Toast.LENGTH_SHORT).show();
			// ��˸���֣��������û�
			sparkTheWords();
		} else if (checkResult == STATUS_ANSWER_LACK) {
			// Toast.makeText(MainActivity.this, "LACK", Toast.LENGTH_SHORT).show();
			// ����������ɫΪ��ɫ
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).getViewButton()
						.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * ���ô�
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {

			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				// ���ô����ֿ����ݼ��ɼ���
				mBtnSelectWords.get(i).getViewButton()
						.setText(wordButton.getWordString());
				mBtnSelectWords.get(i).setIsVisiable(true);
				mBtnSelectWords.get(i)
						.setWordString(wordButton.getWordString());
				// ��¼����
				mBtnSelectWords.get(i).setIndex(wordButton.getIndex());

				// ���ô�ѡ��Ŀɼ���
				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			}
		}
	}

	/**
	 * �����
	 */
	private void clearTheAnswer(WordButton wordButton) {
		wordButton.getViewButton().setText("");
		wordButton.setWordString("");
		wordButton.setIsVisiable(false);

		// ���ô�ѡ�ı���Ŀɼ���
		setButtonVisiable(mAllWords.get(wordButton.getIndex()), View.VISIBLE);
	}

	/**
	 * ���ô�ѡ�ı����Ƿ�ɼ�
	 */
	private void setButtonVisiable(WordButton wordButton, int visibility) {

		wordButton.getViewButton().setVisibility(visibility);
		wordButton.setIsVisiable((visibility == View.VISIBLE) ? true : false);

	}

	/**
	 * ����������� http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
	 * 
	 * @return
	 */
	private char getRandomChar() {
		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();
		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(80)));

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str.charAt(0);
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// �ȼ�鳤��
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// ����пյģ�˵���𰸻�������
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}

		// �����������������ȷ��
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).getWordString());
		}

		return (sb.toString().equals(mCurretSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
	}

	/**
	 * ������˸
	 */
	private void sparkTheWords() {
		// ��ʱ�����
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpardTimes > SPASH_TIMES) {
							return;
						}

						// ִ����˸�߼���������ʾ��ɫ�Ͱ�ɫ����
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords
									.get(i)
									.getViewButton()
									.setTextColor(
											mChange ? Color.RED : Color.WHITE);
						}

						mChange = !mChange;
					}
				});
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	/**
	 * ������ؽ��漰�¼�
	 */
	private void handlePassEvent() {
		mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
	}

}
