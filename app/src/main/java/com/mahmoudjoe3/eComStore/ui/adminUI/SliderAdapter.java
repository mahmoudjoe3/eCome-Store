package com.mahmoudjoe3.eComStore.ui.adminUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.mahmoudjoe3.eComStore.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    private ArrayList<String> mImageUri;

    public SliderAdapter(ArrayList<String> mImageUri) {
        this.mImageUri = mImageUri;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Picasso.get().load(mImageUri.get(position))
                .centerCrop().fit()
                .into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return this.mImageUri.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder  {
        ImageView imageView;
        public Holder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.Slider_image);
        }
    }
}
