package com.example.fileuploader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MNItemAdapter extends ArrayAdapter<MNItem>{

    Context context; 
    int layoutResourceId;    
    MNItem data[] = null;
    
    public MNItemAdapter(Context context, int layoutResourceId, MNItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ItemHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.firstLine);
            holder.txtCategory = (TextView)row.findViewById(R.id.secondLine);
            holder.txtId = (TextView)row.findViewById(R.id.invisibleLine);    
            holder.icon = (ImageView)row.findViewById(R.id.icon);
            row.setTag(holder);
        }
        else
        {
            holder = (ItemHolder)row.getTag();
        }
        
        MNItem item = data[position];
        holder.txtTitle.setText(item.name);
        holder.txtCategory.setText(item.category);
        holder.txtId.setText(item.id);
        holder.icon.setImageBitmap(item.imageBitmap);
        
        
        return row;
    }
    
    class ItemHolder
    {
        TextView txtTitle;
        TextView txtCategory;
        TextView txtId;
        ImageView icon;
    }
}