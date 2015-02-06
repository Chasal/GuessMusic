package com.example.guessmusic.util;

import com.example.guessmusic.R;
import com.example.guessmusic.model.IAlertDialogButtonListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Util {
	
	private static AlertDialog mAlertDialog;
	
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(layoutId, null);

		return layout;
	}

	/**
	 * ������ת
	 * 
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class desti) {
		Intent intent = new Intent(context, desti);
		context.startActivity(intent);

		// �رյ�ǰ��Activity
		((Activity) context).finish();

	}

	/**
	 * ��ʾ�Զ���Ի���
	 * 
	 * @param context
	 * @param message
	 * @param listaner
	 */
	public static void showDialog(Context context, String message,
			final IAlertDialogButtonListener listaner) {

		View dialogView = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view);

		ImageButton btnOkView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCalcelView = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_cancel);
		TextView txtMessageView = (TextView) dialogView
				.findViewById(R.id.text_dialog_message);

		txtMessageView.setText(message);

		btnOkView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//�رնԻ���
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}
				
				//�¼��ص�
				if(listaner != null){
					listaner.onClick();
				}
			}
		});

		btnCalcelView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//�رնԻ���
				if(mAlertDialog != null){
					mAlertDialog.cancel();
				}
			}
		});
		
		//Ϊdialog����view
		builder.setView(dialogView);
		mAlertDialog = builder.create();
		
		//��ʾ�Ի���
		mAlertDialog.show();
		
	}

}















