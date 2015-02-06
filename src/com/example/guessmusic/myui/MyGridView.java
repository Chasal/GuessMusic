package com.example.guessmusic.myui;

import java.util.ArrayList;
import com.example.guessmusic.R;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.util.Util;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

/**
 * 创建网格布局，用于显示待选汉字
 * @author Administrator
 *
 */
public class MyGridView extends GridView {
	//显示汉字的个数
	public static final int COUNT_WORDS = 24;
	//存放Button
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	//上下文
	private Context mContext;
	
	//适配器
	private MyGridAdapter mAdapter;
	
	private IWordButtonClickListener mWordClickListener;
	
	private Animation mScaleAnimation;
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}
	
	public void updateData(ArrayList<WordButton> list){
		mArrayList = list;
		
		//重新设置数据源
		setAdapter(mAdapter);
	}
	
	class MyGridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int pos, View v, ViewGroup p) {
			final WordButton holder;
			
			if(v == null){
				v = Util.getView(mContext,R.layout.self_ui_gridview_item);
				holder = mArrayList.get(pos);
				
				//加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				//设置动画的延迟时间
				mScaleAnimation.setStartOffset(pos * 10);
				
				holder.setIndex(pos);
				
				if(holder.getViewButton() == null){
					holder.setViewButton((Button)v.findViewById(R.id.item_btn));
					
					holder.getViewButton().setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mWordClickListener.onWordButtonClick(holder);
						}
					});
				}
				
				v.setTag(holder);
			}else {
				holder = (WordButton) v.getTag();
			}
			
			holder.getViewButton().setText(holder.getWordString());
			
			//播放动画
			v.startAnimation(mScaleAnimation);
			return v;
		}
		
	}
	
	/**
	 * 注册监听接口
	 * @param clickListener
	 */
	public void registOnWordButtonClick(IWordButtonClickListener clickListener){
		mWordClickListener = clickListener;
	}

}

