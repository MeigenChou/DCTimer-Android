package com.dctimer.ui;

import com.dctimer.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
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
		private View contentView; //对话框中间加载的其他布局界面
		
		private DialogInterface.OnClickListener left_btnClickListener;
		private DialogInterface.OnClickListener mid_btnClickListener;
		private DialogInterface.OnClickListener right_btnClickListener;
		private DialogInterface.OnClickListener mOnClickListener;

		public Builder(Context context) {
			this.context = context;
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

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public CharSequence[] getItems() { 
			return mItems; 
		}
		
		public Builder setItems(CharSequence[] mListItem) { 
			this.mItems = mListItem; 
			return this; 
		}
		
		//设置单选List选项及事件，这些属性在之后的create中用到，这里使用Android系统创建dialog的风格 
		public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) { 
			this.mItems = items;
			this.mOnClickListener = listener;
			this.mCheckedItem = checkedItem;
			this.mIsSingleChoice = true;
			return this;
		}

		public Builder setSingleChoiceItems(int itemsId, int checkedItem, final OnClickListener listener) { 
			this.mItems = context.getResources().getTextArray(itemsId);
			this.mOnClickListener = listener;
			this.mCheckedItem = checkedItem;
			this.mIsSingleChoice = true;
			return this;
		}
		
		public Builder setItems(int itemsId, final OnClickListener listener) {
			this.mItems = context.getResources().getTextArray(itemsId);
			this.mOnClickListener = listener;
			return this;
		}
		
		public Builder setItems(CharSequence[] items, final OnClickListener listener) {
            this.mItems = items;
            this.mOnClickListener = listener;
            return this;
        }
				
		public Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
			this.left_btnText = context.getText(textId);
			this.left_btnClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
			this.left_btnText = text;
			this.left_btnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
			this.right_btnText = context.getText(textId);
			this.right_btnClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String text, DialogInterface.OnClickListener listener) {
			this.right_btnText = text;
			this.right_btnClickListener = listener;
			return this;
		}
		
		public Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
			this.mid_btnText = context.getText(textId);
			this.mid_btnClickListener = listener;
			return this;
		}
		
		public Builder setNeutralButton(String text, DialogInterface.OnClickListener listener) {
			this.mid_btnText = text;
			this.mid_btnClickListener = listener;
			return this;
		}

		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context, R.style.mystyle);
			View layout = inflater.inflate(R.layout.customdialog, null);
			
			dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			TextView titleTv = (TextView) layout.findViewById(R.id.title);
			if (title != null) {
				titleTv.setText(title);
				titleTv.getPaint().setFakeBoldText(true);
			} else titleTv.setVisibility(View.GONE);
			
			Button leftBtn = (Button) layout.findViewById(R.id.left_btn);
			Button midBtn = (Button) layout.findViewById(R.id.mid_btn);
			Button rightBtn = (Button) layout.findViewById(R.id.right_btn);
			if (left_btnText != null) {
				leftBtn.setText(left_btnText);
				leftBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (left_btnClickListener != null)
							left_btnClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						dialog.dismiss();
					}
				});
			} else leftBtn.setVisibility(View.GONE);
			if (right_btnText != null) {
				rightBtn.setText(right_btnText);
				rightBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (right_btnClickListener != null)
							right_btnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						dialog.dismiss();
					}
				});
			} else rightBtn.setVisibility(View.GONE);
			if(mid_btnText != null) {
				midBtn.setText(mid_btnText);
				midBtn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						if(mid_btnClickListener != null)
							mid_btnClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
						dialog.dismiss();
					}
				});
			} else midBtn.setVisibility(View.GONE);
			
			if(left_btnText == null && mid_btnText == null && right_btnText == null) {
				layout.findViewById(R.id.mid_line1).setVisibility(View.GONE);
				layout.findViewById(R.id.mid_line2).setVisibility(View.GONE);
				layout.findViewById(R.id.mid_line).setVisibility(View.GONE);
				layout.findViewById(R.id.buttons).setVisibility(View.GONE);
			} else if((left_btnText==null && mid_btnText==null) || (left_btnText==null && right_btnText==null) || (mid_btnText==null && right_btnText==null)) {
				layout.findViewById(R.id.mid_line1).setVisibility(View.GONE);
				layout.findViewById(R.id.mid_line2).setVisibility(View.GONE);
				if(left_btnText != null) leftBtn.setBackgroundResource(R.drawable.single_btn_style);
				if(mid_btnText != null) midBtn.setBackgroundResource(R.drawable.single_btn_style);
				if(right_btnText != null) rightBtn.setBackgroundResource(R.drawable.single_btn_style);
			} else if(left_btnText == null) {
				layout.findViewById(R.id.mid_line1).setVisibility(View.GONE);
				midBtn.setBackgroundResource(R.drawable.left_btn_select);
			} else if(mid_btnText == null) {
				layout.findViewById(R.id.mid_line2).setVisibility(View.GONE);
			} else if(right_btnText == null) {
				layout.findViewById(R.id.mid_line2).setVisibility(View.GONE);
				midBtn.setBackgroundResource(R.drawable.right_btn_select);
			}
			
			TextView messageTv = (TextView) layout.findViewById(R.id.message);
			if (message != null) {
				messageTv.setText(message);
				//messageTv.setAutoLinkMask(Linkify.ALL);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
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
			else {
				messageTv.setVisibility(View.GONE);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}
