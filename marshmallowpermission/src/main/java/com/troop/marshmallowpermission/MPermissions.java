package com.troop.marshmallowpermission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by Ingo on 13.12.2015.
 */
public class MPermissions
{

    public static void requestCameraPermission(Fragment fragment) {
        if (fragment.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
        {
            new ConfirmationDialog().show(fragment.getChildFragmentManager(), "dialog");
        } else {
            fragment.getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    public static void requestSDPermission(Fragment fragment) {
        if (fragment.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            new ConfirmationDialog().show(fragment.getChildFragmentManager(), "dialog");
        } else {
            fragment.getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }
    public static void requestAudioSDPermission(Fragment fragment) {
        if (fragment.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.CAPTURE_AUDIO_OUTPUT))
        {
            new ConfirmationDialog().show(fragment.getChildFragmentManager(), "dialog");
        } else {
            fragment.getActivity().requestPermissions(new String[]{Manifest.permission.CAPTURE_AUDIO_OUTPUT},
                    1);
        }
    }
    public static void requestAudioVideoPermission(Fragment fragment) {
        if (fragment.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.CAPTURE_VIDEO_OUTPUT))
        {
            new ConfirmationDialog().show(fragment.getChildFragmentManager(), "dialog");
        } else {
            fragment.getActivity().requestPermissions(new String[]{Manifest.permission.CAPTURE_VIDEO_OUTPUT},
                    1);
        }
    }

    public static void requestFineLocationPermission(Fragment fragment) {
        if (fragment.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            new ConfirmationDialog().show(fragment.getChildFragmentManager(), "dialog");
        } else {
            fragment.getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
    public static void requestCoarsePermission(Fragment fragment) {
        if (fragment.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            new ConfirmationDialog().show(fragment.getChildFragmentManager(), "dialog");
        } else {
            fragment.getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    public static void requestMicPermission(Fragment fragment) {
        if (fragment.getActivity().shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO))
        {
            new ConfirmationDialog().show(fragment.getChildFragmentManager(), "dialog");
        } else {
            fragment.getActivity().requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
    }
    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage("dialog")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().requestPermissions(
                                    new String[]{Manifest.permission.CAMERA},
                                    1);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }
}
