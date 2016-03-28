package troop.com.imageviewer;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.ortiz.touch.TouchImageView;
import com.troop.androiddng.RawToDng;
import com.troop.filelogger.Logger;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import troop.com.views.MyHistogram;

/**
 * Created by troop on 21.08.2015.
 */
public class ImageFragment extends Fragment implements I_Activity.I_OnActivityResultCallback
{
    final String TAG = ImageFragment.class.getSimpleName();
    private TouchImageView imageView;
    private File file;
    private int mImageThumbSize = 0;
    private View.OnClickListener onClickListener;
    private int tag;

    private LinearLayout ll;

    private TextView iso;
    private TextView shutter;
    private TextView focal;
    private TextView fnumber;
    private TextView filename;
    private LinearLayout exifinfo;
    private MyHistogram myHistogram;
    private Button deleteButton;
    private Button play;
    private LinearLayout bottombar;

    public void SetFilePath(File filepath)
    {
        this.file = filepath;
    }

    public File GetFilePath()
    {
        return file;
    }

    public void SetOnclickLisnter(View.OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        return inflater.inflate(R.layout.imageframent, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.imageView = (TouchImageView)view.findViewById(R.id.imageView_PicView);

        if(savedInstanceState != null && file == null)
        {
            file = new File((String) savedInstanceState.get(ScreenSlideFragment.SAVESTATE_FILEPATH));
        }

        myHistogram = new MyHistogram(view.getContext());
        ll = (LinearLayout)view.findViewById(R.id.histoView);
        ll.addView(myHistogram);
        bottombar =(LinearLayout)view.findViewById(R.id.bottom_bar);

        exifinfo = (LinearLayout)view.findViewById(R.id.exif_info);
        exifinfo.setVisibility(View.GONE);
        iso = (TextView)view.findViewById(R.id.textView_iso);
        iso.setText("");
        shutter = (TextView)view.findViewById(R.id.textView_shutter);
        shutter.setText("");
        focal = (TextView)view.findViewById(R.id.textView_focal);
        focal.setText("");
        fnumber = (TextView)view.findViewById(R.id.textView_fnumber);
        fnumber.setText("");
        filename = (TextView)view.findViewById(R.id.textView_filename);

        this.play = (Button)view.findViewById(R.id.button_play);
        play.setVisibility(View.GONE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file == null)
                    return;
                if (!file.getAbsolutePath().endsWith(StringUtils.FileEnding.RAW) || !file.getAbsolutePath().endsWith(StringUtils.FileEnding.BAYER)) {
                    Uri uri = Uri.fromFile(file);

                    Intent i = new Intent(Intent.ACTION_EDIT);
                    if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.MP4))
                        i.setDataAndType(uri, "video/*");
                    else
                        i.setDataAndType(uri, "image/*");
                    Intent chooser = Intent.createChooser(i, "Choose App");
                    //startActivity(i);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(chooser);
                    }

                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final File tmp = file;
                            convertRawToDng(tmp);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScreenSlideFragment) getParentFragment()).addFile(tmp);
                                }
                            });
                        }
                    }).start();

                }
            }
        });
        this.deleteButton = (Button)view.findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.GONE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.IS_L_OR_BIG() || StringUtils.WRITE_NOT_EX_AND_L_ORBigger()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    DocumentFile sdDir = FileUtils.getExternalSdDocumentFile();
                    if (sdDir == null) {
                        I_Activity i_activity = (I_Activity) getActivity();
                        i_activity.ChooseSDCard(ImageFragment.this);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Delete File?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }


            }
        });
        Logger.d(TAG,"onViewCreated");
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    if (!StringUtils.IS_L_OR_BIG() || file.canWrite()) {
                        boolean d = file.delete();
                        MediaScannerManager.ScanMedia(getContext(), file);
                    }
                    else
                    {
                        boolean d= FileUtils.delteDocumentFile(file);
                    }
                    ((ScreenSlideFragment)getParentFragment()).reloadFilesAndSetLastPos();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (file != null && file.getAbsolutePath() != null)
            outState.putString(ScreenSlideFragment.SAVESTATE_FILEPATH, file.getAbsolutePath());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG,"omResume");
        imageView.setOnClickListener(onImageClick);
        if (file != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadImage();
                }
            }).start();
            updateUi(file);
        }

    }

    private View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (ImageFragment.this.onClickListener != null)
                ImageFragment.this.onClickListener.onClick(v);
        }
    };

    private void loadImage()
    {
        final Bitmap response = getBitmap();

        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(response);
                myHistogram.setBitmap(response,false);
            }
        });
    }

    private Bitmap getBitmap()
    {
        Bitmap response =null;
        try {
            response = BitmapHelper.getBitmap(file,false,mImageThumbSize,mImageThumbSize);
        }
        catch (IllegalArgumentException ex)
        {

        }
        return response;
    }

    private void updateUi(File file)
    {
        if (file != null)
        {
            filename.setText(file.getName());
            deleteButton.setVisibility(View.VISIBLE);
            if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.JPG) || file.getAbsolutePath().endsWith(StringUtils.FileEnding.JPS)) {
                processJpeg(file);
                exifinfo.setVisibility(View.VISIBLE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.MP4)) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.DNG)) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
            }
            if (file.getAbsolutePath().endsWith(StringUtils.FileEnding.RAW) || file.getAbsolutePath().endsWith(StringUtils.FileEnding.BAYER)) {
                exifinfo.setVisibility(View.GONE);
                //myHistogram.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }

        }
        else
        {
            filename.setText("No Files");
            myHistogram.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
        }
    }

    private void processJpeg(final File file)
    {
        try {
            final Metadata metadata = JpegMetadataReader.readMetadata(file);
            final Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);
            iso.setText("ISO: " +exifsub.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
            shutter.setText("Exposure Time: " +exifsub.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
            fnumber.setText("Aperture:" +exifsub.getString(ExifSubIFDDirectory.TAG_FNUMBER));
            focal.setText("Focal Length:" +exifsub.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
        } catch (EOFException ex)
        {
            Logger.d(TAG, "Failed to read Exif");
        } catch (IOException e) {
            Logger.d(TAG, "Failed to read Exif");
        } catch (JpegProcessingException e) {
            Logger.d(TAG, "Failed to read Exif");
        }
        catch (NullPointerException ex)
        {
            Logger.d(TAG, "Failed to read Exif");
        }
    }

    private void convertRawToDng(File file)
    {
        byte[] data = null;
        try {
            data = RawToDng.readFile(file);
            Logger.d("Main", "Filesize: " + data.length + " File:" + file.getAbsolutePath());

        } catch (FileNotFoundException e) {
            Logger.exception(e);
        } catch (IOException e) {
            Logger.exception(e);
        }

        String out =null;
        if (file.getName().endsWith(StringUtils.FileEnding.RAW))
           out = file.getAbsolutePath().replace(StringUtils.FileEnding.RAW, StringUtils.FileEnding.DNG);
        if (file.getName().endsWith(StringUtils.FileEnding.BAYER))
            out = file.getAbsolutePath().replace(StringUtils.FileEnding.BAYER, StringUtils.FileEnding.DNG);
        RawToDng dng = RawToDng.GetInstance();
        if (!StringUtils.IS_L_OR_BIG()
                || file.canWrite())
            dng.SetBayerData(data, out);
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(true);
            DocumentFile wr = df.createFile("image/dng", file.getName().replace(StringUtils.FileEnding.JPG, StringUtils.FileEnding.DNG));
            ParcelFileDescriptor pfd = null;
            try {

                pfd = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException e) {
                Logger.exception(e);
            }
            catch (IllegalArgumentException e)
            {
                Logger.exception(e);
            }
            if (pfd != null) {
                dng.SetBayerDataFD(data, pfd, file.getName());
                try {
                    pfd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pfd = null;
            }
        }
        dng.setExifData(100, 0, 0, 0, 0, "", "0", 0);
        dng.WriteDNG(null);
        data = null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getActivity().sendBroadcast(intent);
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void SetVisibility(boolean Visible)
    {
        if (!Visible)
        {
            deleteButton.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            myHistogram.setVisibility(View.GONE);
            bottombar.setVisibility(View.GONE);
        }
        else
        {
            deleteButton.setVisibility(View.VISIBLE);
            play.setVisibility(View.VISIBLE);
            myHistogram.setVisibility(View.VISIBLE);
            bottombar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResultCallback(Uri uri)
    {

    }
}
