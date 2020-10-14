package com.troop.freedcam.gallery.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.troop.freedcam.gallery.R;
import com.troop.freedcam.gallery.util.PermissionManager;



public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);
        PermissionManager permissionManager = new PermissionManager(this);
        if(!permissionManager.isPermissionGranted(PermissionManager.Permissions.SdCard))
            permissionManager.requestPermission(PermissionManager.Permissions.SdCard);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, GalleryFragment.newInstance())
                    .commitNow();
        }
    }
}
