package com.troop.freedcam.gallery.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.troop.freedcam.gallery.R;
import com.troop.freedcam.gallery.adapter.GalleryRecyclerAdapter;
import com.troop.freedcam.gallery.databinding.GalleryFragmentBinding;
import com.troop.freedcam.gallery.helper.BitmapHelper;
import com.troop.freedcam.gallery.viewmodel.GalleryViewModel;

public class GalleryFragment extends Fragment {

    private GalleryViewModel mViewModel;
    private GalleryFragmentBinding galleryFragmentBinding;
    private GalleryRecyclerAdapter adapter;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        galleryFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.gallery_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        mViewModel.create(getActivity().getApplication());
        RecyclerView recyclerView = galleryFragmentBinding.galleryRecylerview;
        new BitmapHelper(getContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size));

        GridLayoutManager gridLayoutManager =new GridLayoutManager(recyclerView.getContext(),4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new GalleryRecyclerAdapter();
        //adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        return galleryFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.loadFreeDcamDcimFiles();
        adapter.setFileHolders(mViewModel.getFiles());
    }

    @Override
    public void onDestroyView() {
        BitmapHelper.GET().clear();
        super.onDestroyView();
    }
}
