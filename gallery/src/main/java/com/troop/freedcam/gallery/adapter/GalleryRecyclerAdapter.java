package com.troop.freedcam.gallery.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.gallery.R;
import com.troop.freedcam.gallery.databinding.GalleryItemBinding;
import com.troop.freedcam.gallery.model.GalleryItemModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.MyViewHolder> {

    private final String TAG = GalleryRecyclerAdapter.class.getSimpleName();
    private List<GalleryItemModel> galleryItemModelList;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GalleryItemBinding galleryListItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.gallery_item, parent, false);
        return new MyViewHolder(galleryListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.setIsRecyclable(false);
        if (position < galleryItemModelList.size())
            holder.bind(galleryItemModelList.get(position));
        Log.d(TAG, "update pos: "+ position + " adapterpos:" + holder.getAdapterPosition());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (galleryItemModelList != null)
            return galleryItemModelList.size();
        return 0;
    }

    public void setFileHolders(List<BaseHolder> fileHolders)
    {
        Log.d(TAG, "setFilesHolders Size:" +fileHolders.size());
        galleryItemModelList = new ArrayList<>();
        int i = 0;
        for (BaseHolder b : fileHolders) {
            GalleryItemModel itemModel = new GalleryItemModel();
            itemModel.setBaseHolder(b);
            galleryItemModelList.add(itemModel);
            i++;
        }
        notifyItemInserted(i);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        GalleryItemBinding binding;
        public MyViewHolder(GalleryItemBinding v) {
            super(v.getRoot());
            binding = v;
        }


        public void bind(GalleryItemModel model)
        {
            binding.setGalleryItemModel(model);
            //binding.executePendingBindings();
            /*if (binding.getGalleryItemModel() == null || !model.getBaseHolder().getName().equals(binding.getGalleryItemModel().getBaseHolder().getName())) {
                binding.setVariable(BR.galleryItemModel,model);
                //binding.executePendingBindings();
            }*/
        }

    }

}
