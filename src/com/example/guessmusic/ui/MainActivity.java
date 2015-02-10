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
import android.widget.TextView;
import android.widget.Toast;

import com.example.guessmusic.R;
import com.example.guessmusic.data.Const;
import com.example.guessmusic.model.IAlertDialogButtonListener;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.Song;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.myui.MyGridView;
import com.example.guessmusic.util.MyPlayer;
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

	private static final int ID_DIALOG_DELETE_WORD = 1;

	private static final int ID_DIALOG_TIP_ANSWER = 2;

	private static final int ID_DIALOG_LACK_COINS = 3;

	/**
	 * 闪烁次数
	 */
	private final static int SPASH_TIMES = 6;

	/**
	 * 当前金币数量
	 */
	private int mCurrentCoins = Const.TOTAL_COINS;
	
	/**
	 * 过关界面的总金币
	 */
	private TextView mViewTxtMainCoins;
	
	/**
	 * 金币View
	 */
	private TextView mViewCurrentCoins;

	/**
	 * 唱片相关动画
	 */
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	/**
	 * 拨杆相关动画
	 */
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	/**
	 * 唱片控件
	 */
	private ImageView mViewPan;

	/**
	 * 拨杆控件
	 */
	private ImageView mViewPanBar;

	/**
	 * 当前关的索引
	 */
	private TextView mCurrentStagePassView;

	private TextView mCurrentStageView;
	
	/**
	 * 当前的歌曲名称
	 */
	private TextView mCurrentSongNamePassView;

	/**
	 * Play按键事件
	 */
	private ImageButton mBtnPlayStart;

	/**
	 * 过关界面
	 */
	private View mPassView;

	/**
	 * 当前动画是否正在运行
	 */
	private boolean mIsRunning = false;

	/**
	 * 文本框容器
	 */
	private ArrayList<WordButton> mAllWords;
	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	/**
	 * 以选择文字框的UI容器
	 */
	private LinearLayout mViewWordsContainer;

	/**
	 * 当前的歌曲
	 */
	private Song mCurretSong;

	/**
	 * 当前关卡的索引
	 */
	private int mCurrentStageIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//读取数据
		int[] datas = Util.loadData(this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
		
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

		// 处理删除按键事件
		handleDeleteWord();

		// 处理提示按键事件
		handleTipWord();
		
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
				
				//播放音乐
				MyPlayer.playSong(MainActivity.this, mCurretSong.getSongFileName());
			}
		}
	}

	// 销毁
	@Override
	protected void onPause() {
		super.onPause();
		
		//保存游戏数据
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		
		mViewPan.clearAnimation();
		
		//停止音乐
		MyPlayer.stopTheSong(MainActivity.this);
		
		
	}

	/**
	 * 加载歌曲，获取歌曲信息
	 * 
	 * @param stageIndex
	 *            歌曲索引
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

		// 清空原来的答案
		mViewWordsContainer.removeAllViews();

		LayoutParams params = new LayoutParams(140, 140);
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).getViewButton(),
					params);

		}

		// 显示当前关的索引
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		
		// 显示当前关的金币
		mViewCurrentCoins = (TextView) findViewById(R.id.text_bar_coins);
		if(mViewCurrentCoins != null){
			mViewCurrentCoins.setText((mCurrentCoins + 3) + "");
		}
		
		// 获得数据
		mAllWords = initAllWord();
		// 更新数据
		mMyGridView.updateData(mAllWords);
		
		//一开始播放音乐
		handlePlayButton();
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
			holder.getViewButton().setBackgroundResource(
					R.drawable.game_wordblank);

			holder.getViewButton().setOnClickListener(
					new View.OnClickListener() {

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
		// Toast.makeText(this, wordButton.getIndex() + "",
		// Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

		// 获得答案状态
		int checkResult = checkTheAnswer();

		// 检查答案
		if (checkResult == STATUS_ANSWER_RIGHT) {
			// Toast.makeText(MainActivity.this, "Right",
			// Toast.LENGTH_SHORT).show();
			// 过关界面
			handlePassEvent();
		} else if (checkResult == STATUS_ANSWER_WRONG) {
			// Toast.makeText(MainActivity.this, "Wrong",
			// Toast.LENGTH_SHORT).show();
			// 闪烁文字，并提醒用户
			sparkTheWords();
		} else if (checkResult == STATUS_ANSWER_LACK) {
			// Toast.makeText(MainActivity.this, "LACK",
			// Toast.LENGTH_SHORT).show();
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
		//显示过关界面
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);

		// 停止未完成的动画
		mViewPan.clearAnimation();
		
		//停止音乐
		MyPlayer.stopTheSong(MainActivity.this);
		
		//播放音效
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);
		
		// 显示当前关的索引
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}

		// 显示当前关的索引
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurretSong.getSongName());
		}
		
		
		//显示总金币
		mViewTxtMainCoins = (TextView) findViewById(R.id.txt_main_coins);
		if(mViewTxtMainCoins != null){
			mViewTxtMainCoins.setText((mCurrentCoins + 3) + "");
		}
		
		// 下一关按键处理
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			
				// 判断是否通关
				if (judeAppPassed()) {
					// 进入通关界面
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// 开始新一关
					mPassView.setVisibility(View.GONE);
					// 加载关卡数据
					initCurrentStageData();
				}
			}
		});
	}

	/**
	 * 判断是否通关
	 */
	private boolean judeAppPassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
	}

	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
			}
		});
	}

	/**
	 * 处理提示按键事件
	 */
	private void handleTipWord() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
			}
		});
	}

	/**
	 * 自动选择一个答案
	 */
	private void tipAnswer() {
		boolean tipWord = false;

		// 减少金币的数量
		if (!handleCoins(-getTipWordCoins())) {
			// 金币数量不够，显示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}

		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				// 根据当前的答案框条件选择对应的文字并填入
				onWordButtonClick(findIsAnswerWord(i));

				tipWord = true;

				break;
			}
		}

		// 没有找到可以填充的答案
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkTheWords();
		}
	}

	/**
	 * 找到一个答案文字
	 * 
	 * @param index
	 *            当前需要填入答案框的索引
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;

		for (int i = 0; i < MyGridView.COUNT_WORDS; i++) {
			buf = mAllWords.get(i);

			if (buf.getWordString().equals(
					mCurretSong.getNameCharacters()[index] + "")) {
				return buf;
			}
		}

		return null;

	}

	/**
	 * 删除文字
	 */
	private void deleteOneWord() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// 金币不够，显示提示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}

		// 将这个索引对应的WordButton设置为不可见
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * 增加或减少指定数量的金币
	 * 
	 * @param data
	 * @return true 增加/减少成功，false 失败
	 */
	private boolean handleCoins(int data) {

		// 判断当前总的金币数量是否可被减少
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;

			mViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		} else {
			// 金币不够
			return false;
		}
	}

	/**
	 * 从配置文件里读取删除操作所需要用的金币
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * 从配置文件里读取提示操作所需要用的金币
	 * 
	 * @return
	 */
	private int getTipWordCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * 找到一个不是答案的汉字，并且当前是可见的
	 * 
	 * @return
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;

		while (true) {
			int index = random.nextInt(MyGridView.COUNT_WORDS);
			buf = mAllWords.get(index);

			if (buf.isIsVisiable() && !isTheAnswerWord(buf)) {
				return buf;
			}
		}
	}

	/**
	 * 判断某个文字是否为答案
	 * 
	 * @return
	 */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;

		for (int i = 0; i < mCurretSong.getNameLength(); i++) {
			if (word.getWordString().equals(
					mCurretSong.getNameCharacters()[i] + "")) {
				result = true;
				break;
			}
		}
		return result;
	}

	// 删除错误的答案
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}
	};

	// 答案提示
	private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			tipAnswer();
		}
	};

	// 金币不足
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			Toast.makeText(MainActivity.this, "金币不足", Toast.LENGTH_SHORT)
					.show();
		}
	};

	/**
	 * 弹出对话框的类型
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins()
					+ "个金币去掉一个错误文字", mBtnOkDeleteWordListener);
			break;

		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "确认花掉" + getTipWordCoins()
					+ "个金币获得一个文字提示", mBtnOkTipAnswerListener);
			break;

		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "金币不足，请去商店补充",
					mBtnOkLackCoinsListener);
			break;
		}
	}
}
