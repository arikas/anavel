package com.anasion.anavel.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Vostro on 10/08/2016.
 */
public class GridviewAdapter extends BaseAdapter
{
    Context context;
    String classContext;
    ArrayList<Beanclass> beans;

    public GridviewAdapter(Context context, ArrayList<Beanclass> beans, String classContext) {
        this.beans = beans;
        this.context = context;
        this.classContext = classContext;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            viewHolder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.gridview, null);

            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(viewHolder);


        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        final com.anasion.anavel.myapplication.Beanclass beans = (com.anasion.anavel.myapplication.Beanclass) getItem(position);

        viewHolder.image.setImageBitmap(beans.getImage());

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager.getInstance(context).saveClickImage(beans.getImage());
                SessionManager.getInstance(context).saveClickUrl(beans.getUrl());

                if(classContext=="dashboard") {
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    context.startActivity(intent);
                }
                else if(classContext=="searchDashboard") {

                }
            }
        });

        return convertView;
    }

    public class ViewHolder {
        ImageView image;
    }


}
