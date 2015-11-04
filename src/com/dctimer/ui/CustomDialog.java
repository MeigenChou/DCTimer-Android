package com.dctimer.ui;

import com.dctimer.Configs;
import com.dctimer.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class CustomDialog extends Dialog {
	public CustomDialog(Context context) {
		super(context);
	}
	
	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public void show() {
		Window dw = getWindow();
		WindowManager.LayoutParams p = dw.getAttributes();
		p.width = Configs.dip300;
		dw.setAttributes(p);
		setCanceledOnTouchOutside(false);
		super.show();
	}
	
	public static class Builder {
		private Context context;
		private CharSequence title;
		private CharSequence message;
		private CharSequence left_btnText;
		private CharSequence mid_btnText;
		private CharSequence right_btnText;
		private CharSequence[] mItems;
		private int mCheckedItem;
		private boolean mIsSingleChoice;
		private View contentView;
		private ProgressBar mProgress;
		private boolean showProgress;
		private TextView tvMsg;
		
		private OnClickListener left_btnClickListener;
		private OnClickListener mid_btnClickListener;
		private OnClickListener right_btnClickListener;
		private OnClickListener mOnClickListener;
		
		public Builder(Context context) {
			this.context = context;
			this.showProgress = false;
		}
		
		public Builder(Context context, boolean showProgress) {
			this.context = context;
			this.showProgress = showProgress;
		}
		
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}
		
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}
		
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}
		
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setView(View v) {
			this.contentView = v;
			return this;
		}
		
		public Builder setIcon(int icon) {	//TODO
			return this;
		}

		public CharSequence[] getItems() { 
			return mItems; 
		}
		
		public Builder setItems(CharSequence[] mListItem) { 
			this.mItems = mListItem; 
			return this; 
		}
		
		public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, OnClickListener listener) { 
			this.mItems = items;
			this.mOnClickListener = listener;
			this.mCheckedItem = checkedItem;
			this.mIsSingleChoice = true;
			return this;
		}
		
		public Builder setSingleChoiceItems(int itemsId, int checkedItem, OnClickListener listener) { 
			this.mItems = context.getResources().getTextArray(itemsId);
			this.mOnClickListener = listener;
			this.mCheckedItem = checkedItem;
			this.mIsSingleChoice = true;
			return this;
		}
		
		public Builder setItems(int itemsId, OnClickListener listener) {
			this.mItems = context.getResources().getTextArray(itemsId);
			this.mOnClickListener = listener;
			return this;
		}
		
		public Builder setItems(CharSequence[] items, OnClickListener listener) {
            this.mItems = items;
            this.mOnClickListener = listener;
            return this;
        }
		
		public Builder setPositiveButton(int textId, OnClickListener listener) {
			this.left_btnText = context.getText(textId);
			this.left_btnClickListener = listener;
			return this;
		}
		
		public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
			this.left_btnText = text;
			this.left_btnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int textId, OnClickListener listener) {
			this.right_btnText = context.getText(textId);
			this.right_btnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String text, OnClickListener listener) {
			this.right_btnText = text;
			this.right_btnClickListener = listener;
			return this;
		}
		
		public Builder setNeutralButton(int textId, OnClickListener listener) {
			this.mid_btnText = context.getText(textId);
			this.mid_btnClickListener = listener;
			return this;
		}
		
		public Builder setNeutralButton(String text, OnClickListener listener) {
			this.mid_btnText = text;
			this.mid_btnClickListener = listener;
			return this;
		}
		
		public void setProgress(int p, int s, boolean file) {
			int per = p * 100 / s;
			mProgress.setProgress(per);
			if(file)
				tvMsg.setText(String.format("%.2f K / %.2f K (%d %%)", p/1024.0, s/1024., per));
			else tvMsg.setText(p+" / "+s+" ("+per+" %)");
		}
		
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialog);
			int resId = R.layout.custom_dialog;
			View layout = inflater.inflate(resId, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			TextView titleTv = (TextView) layout.findViewById(R.id.title);
			if (title != null) {
				titleTv.setText(title);
				titleTv.getPaint().setFakeBoldText(true);
			} else titleTv.setVisibility(View.GONE);
			
			tvMsg = (TextView) layout.findViewById(R.id.message);
			if (message != null) {
				tvMsg.setText(message);
			} else if (contentView != null) {
				((FrameLayout) layout.findViewById(R.id.content)).addView(contentView);
			} else if (mItems != null) {
				ListView lvListItem = (ListView) layout.findViewById(R.id.lvListItem);
				lvListItem.setVisibility(View.VISIBLE);
				if(mIsSingleChoice)
					lvListItem.setAdapter(new ArrayAdapter<Object>(context, R.layout.spinner_dropdown_item, mItems));
				else lvListItem.setAdapter(new ArrayAdapter<Object>(context, R.layout.list_item, mItems));
				lvListItem.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						mOnClickListener.onClick(dialog, position);
						dialog.dismiss();
					}
				});
				lvListItem.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				lvListItem.setItemChecked(mCheckedItem, true);
				lvListItem.setSelection(mCheckedItem);
			}
			else if (showProgress) {
				tvMsg.setText("");
			} else {
				tvMsg.setVisibility(View.GONE);
			}
			
			//LinearLayout buttonLayout = (LinearLayout) layout.findViewById(R.id.buttonLayout);
			if (left_btnText != null) {
				Button leftBtn = (Button) layout.findViewById(R.id.left_button);
				leftBtn.setText(left_btnText);
				leftBtn.setVisibility(View.VISIBLE);
				leftBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (left_btnClickListener != null)
							left_btnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						dialog.dismiss();
					}
				});
			}
			if(mid_btnText != null) {
				Button midBtn = (Button) layout.findViewById(R.id.mid_button);
				midBtn.setText(mid_btnText);
				midBtn.setVisibility(View.VISIBLE);
				midBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (mid_btnClickListener != null)
							mid_btnClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
						dialog.dismiss();
					}
				});
			}
			if (right_btnText != null) {
				Button rightBtn = (Button) layout.findViewById(R.id.right_button);
				rightBtn.setText(right_btnText);
				rightBtn.setVisibility(View.VISIBLE);
				rightBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (right_btnClickListener != null)
							right_btnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						dialog.dismiss();
					}
				});
			}
			
			mProgress = (ProgressBar) layout.findViewById(R.id.progBar);
			if(showProgress) {
				mProgress.setVisibility(View.VISIBLE);
				mProgress.setProgress(0);
			}
			
			return dialog;
		}
		
		public void show() {
			create().show();
		}
	}
}
