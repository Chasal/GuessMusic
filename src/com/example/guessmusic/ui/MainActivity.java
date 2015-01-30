package com.example.guessmusic.ui;

import com.example.guessmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * ������
 * @author Administrator
 */
public class MainActivity extends Activity {
	
	//��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	//������ض���
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	//��Ƭ�ؼ�
	private ImageView mViewPan;
	//���˿ؼ�
	private ImageView mViewPanBar;
	//Play�����¼�
	private ImageButton mBtnPlayStart;
	
	//��ǰ�����Ƿ���������
	private boolean mIsRunning = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//��ʼ���ؼ�
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		
		//��ʼ������
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
				//��ʼ�����˳�����
				mViewPanBar.startAnimation(mBarOutAnim);
			}
		});
		
		//���˽��붯��
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
				//��ʼ��Ƭ����
				mViewPan.startAnimation(mPanAnim);
			}
		});
		
		//�����˳�����
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
				//���׶�������
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
				Toast.makeText(MainActivity.this, "End", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		mBtnPlayStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
				//��ʼ��������
				handlePlayButton();
			}
		});
		
	}
	
	private void handlePlayButton(){
		if(mViewPanBar != null){
			if(!mIsRunning){
				mIsRunning = true;
				//��ʼ���˽��붯��
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);
			}
		}
		
		
	}


}











