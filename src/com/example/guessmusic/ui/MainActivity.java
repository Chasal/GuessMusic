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
 * 主界面
 * 
 * @author Administrator
 */
public class MainActivity extends Activity implements IWordButtonClickListener {

	/**
	 * 答案状态 -- 正确
	 */
	private final static int STATUS_ANSWER_RIGHT = 1;

	/**
	 * 答案状态 -- 错误
	 */
	private final static int STATUS_ANSWER_WRONG = 2;

	/**
	 * 答案状态 -- 不完整
	 */
	private final static int STATUS_ANSWER_LACK = 3;

	// 闪烁次数
	private final static int SPASH_TIMES = 6;

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

	// 过关界面
	private View mPassView;

	// 当前动画是否正在运行
	private boolean mIsRunning = false;

	// 文本框容器
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	// 以选择文字框的UI容器
	private LinearLayout mViewWordsContainer;

	// 当前的歌曲
	private Song mCurretSong;

	// 当前关卡的索引
	private int mCurrentStageIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化控件
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);

		mMyGridView = (MyGridView) findViewById(R.id.gridView);
		// 注册监听
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

	/**
	 * 开始动画
	 */
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

	/**
	 * 加载歌曲，获取歌曲信息
	 * 
	 * @param stageIndex 歌曲索引
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
	 * 初始化游戏数据
	 */
	private void initCurrentStageData() {
		// 读取当前关卡的歌曲信息
		mCurretSong = loadStageSongInfo(++mCurrentStageIndex);

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

	/**
	 * 初始化待选择文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {

		ArrayList<WordButton> data = new ArrayList<WordButton>();
		// 获得所有的待选文字
		String[] words = generateWords();

		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			WordButton button = new WordButton();
			button.setWordString(words[i]);
			data.add(button);
		}
		return data;
	}

	/**
	 * 生成所有的待选文字
	 * 
	 * @return
	 */
	private String[] generateWords() {
		Random random = new Random();
		String[] words = new String[MyGridView.COUNT_WORDS];

		// 存入歌名
		for (int i = 0; i < mCurretSong.getNameLength(); i++) {
			words[i] = mCurretSong.getNameCharacters()[i] + "";
		}

		// 获取随机文字并存入数组
		for (int i = mCurretSong.getNameLength(); i < MyGridView.COUNT_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换,
		// 然后在第二个之后选择一个元素与第二个交换，直到最后一个元素。
		// 这样能够确保每个元素在每个位置的概率都是1/n
		for (int i = MyGridView.COUNT_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}

		return words;
	}

	/**
	 * 初始化以选择文本框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// 动态添加已选文本框的长度
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

		// 获得答案状态
		int checkResult = checkTheAnswer();

		// 检查答案
		if (checkResult == STATUS_ANSWER_RIGHT) {
			// Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
			//过关界面
			handlePassEvent();
		} else if (checkResult == STATUS_ANSWER_WRONG) {
			// Toast.makeText(MainActivity.this, "Wrong",  Toast.LENGTH_SHORT).show();
			// 闪烁文字，并提醒用户
			sparkTheWords();
		} else if (checkResult == STATUS_ANSWER_LACK) {
			// Toast.makeText(MainActivity.this, "LACK", Toast.LENGTH_SHORT).show();
			// 设置文字颜色为白色
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).getViewButton()
						.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * 设置答案
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {

			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				// 设置答案文字框内容及可见性
				mBtnSelectWords.get(i).getViewButton()
						.setText(wordButton.getWordString());
				mBtnSelectWords.get(i).setIsVisiable(true);
				mBtnSelectWords.get(i)
						.setWordString(wordButton.getWordString());
				// 记录索引
				mBtnSelectWords.get(i).setIndex(wordButton.getIndex());

				// 设置待选框的可见性
				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			}
		}
	}

	/**
	 * 清除答案
	 */
	private void clearTheAnswer(WordButton wordButton) {
		wordButton.getViewButton().setText("");
		wordButton.setWordString("");
		wordButton.setIsVisiable(false);

		// 设置待选文本框的可见性
		setButtonVisiable(mAllWords.get(wordButton.getIndex()), View.VISIBLE);
	}

	/**
	 * 设置待选文本框是否可见
	 */
	private void setButtonVisiable(WordButton wordButton, int visibility) {

		wordButton.getViewButton().setVisibility(visibility);
		wordButton.setIsVisiable((visibility == View.VISIBLE) ? true : false);

	}

	/**
	 * 生成随机汉字 http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
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
	 * 检查答案
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// 先检查长度
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// 如果有空的，说明答案还不完整
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}

		// 答案完整，继续检查正确性
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).getWordString());
		}

		return (sb.toString().equals(mCurretSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
	}

	/**
	 * 文字闪烁
	 */
	private void sparkTheWords() {
		// 定时器相关
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

						// 执行闪烁逻辑：交替显示红色和白色文字
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
	 * 处理过关界面及事件
	 */
	private void handlePassEvent() {
		mPassView = (LinearLayout)this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);
	}

}
