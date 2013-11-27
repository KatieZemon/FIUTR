package com.example.fiutr;

import java.util.ArrayList;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import android.app.Activity;

public class WifiAdapterItem extends ArrayAdapter<LocationNetwork> implements CompoundButton.OnCheckedChangeListener
{
	Context context;
	int resource;
	ArrayList<LocationNetwork> stuff;
	SparseBooleanArray checked;
	
	public WifiAdapterItem(Context context, int textViewResourceId, ArrayList<LocationNetwork> objects)
	{
		super(context, textViewResourceId, objects);
		this.context = context;
		this.resource = textViewResourceId;
		this.stuff = objects;
		checked = new SparseBooleanArray(stuff.size());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		genericWifiHolder holder = null;
		
		if(row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);
			holder = new genericWifiHolder();
			holder.title = (TextView) row.findViewById(R.id.title);
			holder.chk = (CheckBox) row.findViewById(R.id.checkbox);
			row.setTag(holder);
		}
		else
		{
			holder = (genericWifiHolder) row.getTag();
		}
		
		LocationNetwork currentItem = stuff.get(position);
		holder.title.setText(currentItem.returnName());
		holder.chk.setTag(position);
		holder.chk.setChecked(checked.get(position,false));
		holder.chk.setOnCheckedChangeListener(this);
		return row;
	}
	
	public boolean isChecked(int position)
	{
		return checked.get(position,false);
	}
	
	public void setChecked(int position, boolean isChecked)
	{
		checked.put(position, isChecked);
	}
	
	public void toggle(int position)
	{
		setChecked(position,!isChecked(position));
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		checked.put((Integer) buttonView.getTag(), isChecked);
	}
	
	static class genericWifiHolder
	{
		TextView title;
		CheckBox chk;
	}
}