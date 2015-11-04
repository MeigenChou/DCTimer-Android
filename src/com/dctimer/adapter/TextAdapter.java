package com.dctimer.adapter;

import java.util.List;

import com.dctimer.R;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TextAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mListData;
	private String[] mArrayData;
	private LayoutInflater mInflater;
	private int type;
	private int selectedItem;
	
	final class ViewHolder {  
		public TextView textView;
	}
	
	public TextAdapter(Context context, List<String> listData, int selectItem, int type) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mListData = listData;
		this.selectedItem = selectItem;
		this.type = type;
	}
	
	public TextAdapter(Context context, String[] arrayData, int selectItem, int type) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.mArrayData = arrayData;
		this.selectedItem = selectItem;
		this.type = type;
	}
	
	public void setData(List<String> data) {
		this.mListData = data;
	}
	
	public void setData(String[] data) {
		this.mArrayData = data;
	}
	
	public void setSelectItem(int selectItem) {
		this.selectedItem = selectItem;
	}

	@Override
	public int getCount() {
		if(mListData != null) return mListData.size();
		if(mArrayData != null) return mArrayData.length;
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if(mListData != null) return mListData.get(arg0);
		if(mArrayData != null) return mArrayData[arg0];
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			int resId = R.layout.list_item;
			convertView = mInflater.inflate(resId, null);
			holder.textView = (TextView) convertView.findViewById(R.id.text1);
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();
		if(mListData != null) holder.textView.setText(mListData.get(position));
		else holder.textView.setText(mArrayData[position]);
		if(type == 1) {
			if(position == selectedItem)
				holder.textView.setBackgroundColor(Color.WHITE);
			else holder.textView.setBackgroundResource(R.drawable.list_item_bgcolor);
		} else {
			if(position == selectedItem)
				holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.checked_item), null);
			else holder.textView.setCompoundDrawables(null, null, null, null);
		}
		return convertView;
	}
}
