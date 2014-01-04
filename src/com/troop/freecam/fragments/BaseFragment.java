package com.troop.freecam.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.MainActivity;

/**
 * Created by troop on 02.01.14.
 */
public class BaseFragment extends Fragment
{
    protected View view;
    protected CameraManager camMan;
    protected MainActivity activity;

    public BaseFragment(CameraManager camMan, MainActivity activity) {
        this.camMan = camMan;
        this.activity = activity;
    }
}
