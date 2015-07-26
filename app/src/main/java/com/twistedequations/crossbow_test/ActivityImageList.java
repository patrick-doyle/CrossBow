package com.twistedequations.crossbow_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;


import com.crossbow.volley.CrossbowImage;
import com.crossbow.volley.toolbox.Crossbow;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Patrick on 22/06/2015.
 */
public class ActivityImageList extends AppCompatActivity {

    @Bind(R.id.list_view)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        ButterKnife.bind(this);

        listView.setAdapter(new ImageAdapter());

    }

    private class ImageAdapter extends BaseAdapter {

        String[] imageUrls = getResources().getStringArray(R.array.image_list);

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public String getItem(int position) {
            return imageUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return imageUrls[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if(convertView == null) {
                imageView = (ImageView) getLayoutInflater().inflate(R.layout.list_item_image, parent, false);
            }
            else {
                imageView = ImageView.class.cast(convertView);
            }

            Crossbow.get(getApplicationContext())
                    .loadImage()
                    .fade(200)
                    .source(imageUrls[position])
                    .centerCrop()
                    .debug()
                    .into(imageView);

            return imageView;
        }
    }
}
