/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.viewer.dngconvert;

import android.R.layout;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.ortiz.touch.TouchImageView;
import com.troop.freedcam.R;
import com.troop.freedcam.R.array;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.string;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.dng.DngProfile;
import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.file.holder.UriHolder;
import freed.jni.ExifInfo;
import freed.jni.RawToDng;
import freed.settings.SettingsManager;
import freed.settings.XmlParserWriter;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 22.12.2015.
 */
public class DngConvertingFragment extends Fragment
{
    final String TAG = DngConvertingFragment.class.getSimpleName();
    private EditText editTextCusotmRowSize;
    private EditText editTextwidth;
    private EditText editTextheight;
    private EditText editTextblacklvl;
    private EditText editTextwhitelvl;
    private Spinner spinnerMatrixProfile;
    private Spinner toneMapProfile;
    private Spinner spinnerColorPattern;
    private Spinner spinnerrawFormat;
    private String[] filesToConvert;
    private DngProfile dngprofile;
    private CheckBox fakeGPS;
    private MatrixChooserParameter matrixChooserParameter;
    private TouchImageView imageView;
    private String tonemaps[];

    private int rawType;
    private String bayerPattern;
    private long filesize;

    private BaseHolder out;

    public static final String EXTRA_FILESTOCONVERT = "extra_files_to_convert";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if (!SettingsManager.getInstance().isInit())
            SettingsManager.getInstance().init();

        return inflater.inflate(R.layout.dngconvertingfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextCusotmRowSize = view.findViewById(id.editText_customrowsize);
        editTextwidth = view.findViewById(id.editText_width);
        editTextheight = view.findViewById(id.editText_height);
        editTextblacklvl = view.findViewById(id.editText_blacklevel);
        editTextwhitelvl = view.findViewById(id.editText_whitelevel);
        spinnerMatrixProfile = view.findViewById(id.spinner_MatrixProfile);
        matrixChooserParameter = new MatrixChooserParameter(SettingsManager.getInstance().getMatrixesMap());
        String[] items = matrixChooserParameter.getStringValues();
        ArrayAdapter<String> matrixadapter = new ArrayAdapter<>(getContext(), layout.simple_spinner_item, items);
        matrixadapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spinnerMatrixProfile.setAdapter(matrixadapter);

        toneMapProfile = view.findViewById(id.spinner_ToneMap);
        tonemaps = new String[SettingsManager.getInstance().getToneMapProfiles().keySet().size()];
        SettingsManager.getInstance().getToneMapProfiles().keySet().toArray(tonemaps);
        ArrayAdapter<String> toneadapter = new ArrayAdapter<>(getContext(), layout.simple_spinner_item, tonemaps);
        toneMapProfile.setAdapter(toneadapter);


        Button buttonconvertToDng = view.findViewById(id.button_convertDng);
        buttonconvertToDng.setOnClickListener(convertToDngClick);

        spinnerColorPattern = view.findViewById(id.spinner_ColorPattern);
        ArrayAdapter<CharSequence> coloradapter = ArrayAdapter.createFromResource(getContext(),
                array.color_pattern, layout.simple_spinner_item);
        coloradapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spinnerColorPattern.setAdapter(coloradapter);

        spinnerrawFormat = view.findViewById(id.spinner_rawFormat);
        ArrayAdapter<CharSequence> rawadapter = ArrayAdapter.createFromResource(getContext(),
                array.raw_format, layout.simple_spinner_item);
        rawadapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
        spinnerrawFormat.setAdapter(rawadapter);
        Button closeButton = view.findViewById(id.button_goback_from_conv);
        closeButton.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            getActivity().setResult(Activity.RESULT_CANCELED, returnIntent);
            getActivity().finish();
        });
        imageView = view.findViewById(id.dngconvert_imageview);
        fakeGPS = view.findViewById(id.checkBox_fakeGPS);

        Button saveDngProfile = view.findViewById(id.button_saveProfile);
        saveDngProfile.setOnClickListener(saveDngProfileClick);

        setDngProfileToUiItems();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setDngProfileToUiItems() {
        filesToConvert = getActivity().getIntent().getStringArrayExtra(EXTRA_FILESTOCONVERT);
        if (filesToConvert != null && filesToConvert.length > 0) {

            getFilesize(filesToConvert[0]);
            if (SettingsManager.getInstance().getDngProfilesMap() == null)
            {
                dngprofile = new DngProfile(0,0,0,0,0,"bggr",0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6),MatrixChooserParameter.NEXUS6);
                Toast.makeText(getContext(), string.unknown_raw_add_manual_stuff, Toast.LENGTH_LONG).show();
            }
            else {
                dngprofile = SettingsManager.getInstance().getDngProfilesMap().get(filesize);
            }
            if (dngprofile == null) {
                dngprofile = new DngProfile(0,0,0,0,0,"bggr",0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6),MatrixChooserParameter.NEXUS6);
                Toast.makeText(getContext(), string.unknown_raw_add_manual_stuff, Toast.LENGTH_LONG).show();
            }
            editTextCusotmRowSize.setText(dngprofile.getRowSize() +"");
            editTextwidth.setText(dngprofile.getWidth() + "");
            editTextheight.setText(dngprofile.getHeight() + "");
            editTextblacklvl.setText(dngprofile.getBlacklvl() + "");
            editTextwhitelvl.setText(dngprofile.getWhitelvl() + "");

            switch (dngprofile.getBayerPatter()) {
                case DngProfile.BGGR:
                    spinnerColorPattern.setSelection(0);
                    break;
                case DngProfile.RGGB:
                    spinnerColorPattern.setSelection(1);
                    break;
                case DngProfile.GRBG:
                    spinnerColorPattern.setSelection(2);
                    break;
                case DngProfile.GBRG:
                    spinnerColorPattern.setSelection(3);
                    break;
                case DngProfile.RGBW:
                    spinnerColorPattern.setSelection(4);
                    break;
            }

            for (int i = 0; i< matrixChooserParameter.getStringValues().length; i++)
                if (matrixChooserParameter.getStringValues()[i].equals(dngprofile.matrixName))
                    spinnerMatrixProfile.setSelection(i);

            String tmp_name = null;
            if (dngprofile.toneMapProfile != null)
                tmp_name = dngprofile.toneMapProfile.getName();
            if (tmp_name == null || TextUtils.isEmpty(tmp_name))
                tmp_name = getString(string.off_);

            for (int i = 0; i< tonemaps.length; i++)
                if (tonemaps[i].equals(tmp_name))
                    toneMapProfile.setSelection(i);

            spinnerrawFormat.setSelection(dngprofile.getRawType());
            if (dngprofile != null){
                spinnerMatrixProfile.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        dngprofile.matrixes = matrixChooserParameter.GetCustomMatrixNotOverWritten(spinnerMatrixProfile.getSelectedItem().toString());
                        dngprofile.matrixName = spinnerMatrixProfile.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinnerColorPattern.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        bayerPattern = spinnerColorPattern.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinnerrawFormat.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        rawType = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                toneMapProfile.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        dngprofile.toneMapProfile = SettingsManager.getInstance().getToneMapProfiles().get(toneMapProfile.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }
        else {
            Toast.makeText(getContext(), string.no_sel_raw, Toast.LENGTH_LONG).show();
        }
    }

    private final OnClickListener convertToDngClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (filesToConvert == null || filesToConvert.length == 0) {
                Toast.makeText(getContext(), string.no_sel_raw, Toast.LENGTH_LONG).show();
            }
            else {
                dngprofile = new DngProfile(Integer.parseInt(editTextblacklvl.getText().toString()),
                        Integer.parseInt(editTextwhitelvl.getText().toString()),
                        Integer.parseInt(editTextwidth.getText().toString()),
                        Integer.parseInt(editTextheight.getText().toString()),
                        rawType,
                        bayerPattern,
                        Integer.parseInt(editTextCusotmRowSize.getText().toString()),
                        matrixChooserParameter.GetCustomMatrixNotOverWritten(spinnerMatrixProfile.getSelectedItem().toString()),
                        spinnerMatrixProfile.getSelectedItem().toString());
                new AsyncConverter().execute(filesToConvert);


            }
        }
    };

    private void getFilesize(String path)
    {
        ActivityInterface activityInterface = (ActivityInterface)getActivity();
        long size = 0;
        if (path.startsWith("uri") ||path.startsWith("content"))
        {
            activityInterface.getFileListController().LoadFreeDcamDCIMDirsFiles();
            List<BaseHolder> files = activityInterface.getFileListController().getFiles();
            for (BaseHolder b : files)
            {
                if (((UriHolder) b).getMediaStoreUri().toString().equals(path)) {
                    try {
                        InputStream stream = b.getInputStream();
                        filesize = stream.available();
                        stream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
            filesize = new File(path).length();
    }

    private final OnClickListener saveDngProfileClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dngprofile = new DngProfile(Integer.parseInt(editTextblacklvl.getText().toString()),
                    Integer.parseInt(editTextwhitelvl.getText().toString()),
                    Integer.parseInt(editTextwidth.getText().toString()),
                    Integer.parseInt(editTextheight.getText().toString()),
                    rawType,
                    bayerPattern,
                    Integer.parseInt(editTextCusotmRowSize.getText().toString()),
                    matrixChooserParameter.GetCustomMatrixNotOverWritten(spinnerMatrixProfile.getSelectedItem().toString()),
                    spinnerMatrixProfile.getSelectedItem().toString());
            SettingsManager.getInstance().getDngProfilesMap().append(filesize,dngprofile);
            new XmlParserWriter().saveDngProfiles(SettingsManager.getInstance().getDngProfilesMap(), SettingsManager.getInstance().getDeviceString(), SettingsManager.getInstance().getAppDataFolder());
            Toast.makeText(getContext(),"Profile Saved", Toast.LENGTH_SHORT).show();
        }
    };

    private class AsyncConverter extends AsyncTask<String[], Integer, Bitmap>
    {
        private ProgressDialog pr;
        public AsyncConverter()
        {

            //pr.setMax(filesToConvert.length);
        }

        @Override
        protected Bitmap doInBackground(String[]... params) {
            String[] files = params[0];
            ActivityInterface activityInterface = (ActivityInterface)getActivity();
            List<BaseHolder> convertFiles = new ArrayList<>();
            if (files[0].startsWith("content") || files[0].startsWith("uri"))
            {
                activityInterface.getFileListController().LoadFreeDcamDCIMDirsFiles();
                List<BaseHolder> baseHolders = activityInterface.getFileListController().getFiles();
                for (String s : files)
                {
                    for (BaseHolder b : baseHolders)
                    {
                        if (((UriHolder) b).getMediaStoreUri().toString().equals(s))
                            convertFiles.add(b);
                    }
                }
            }
            else
            {
                for (String s : files)
                {
                    BaseHolder b = activityInterface.getFileListController().findFile(s);
                    convertFiles.add(b);
                }
            }
            if (convertFiles.size() == 1) {
                return convertRawToDng(convertFiles.get(0));
            }
            else
            {
                int t = 0;
                for (BaseHolder s : convertFiles) {
                    convertRawToDng(s);
                    t++;
                    publishProgress(t);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //pr.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pr = ProgressDialog.show(getContext(), "Converting DNG", "");
        }

        @Override
        protected void onPostExecute(Bitmap map) {
            pr.dismiss();
            pr.cancel();
            pr = null;
            imageView.setImageBitmap(map);
            Log.d(TAG,"Converting Done");
        }
    }

    private Bitmap convertRawToDng(BaseHolder baseHolder)
    {
        byte[] data = null;
        ActivityInterface activityInterface = (ActivityInterface)getActivity();
        try {
            InputStream in = baseHolder.getInputStream();
            data = new byte[in.available()];
            in.read(data);
            in.close();
            //data = RawToDng.readFile(file);
            Log.d("Main", "Filesize: " + data.length + " File:" + baseHolder.getName());

        } catch (IOException ex) {
            Log.WriteEx(ex);
            return null;
        }
        File file;
        if (baseHolder instanceof FileHolder)
            file = new File(((FileHolder) baseHolder).getFile().getAbsolutePath().replace(FileEnding.BAYER, FileEnding.DNG));
        else file = new File(baseHolder.getName().replace(FileEnding.BAYER, FileEnding.DNG));

        RawToDng dng = RawToDng.GetInstance();
        /*dng.setOpcode3(AppSettingsManager.getInstance().getOpcode3());
        dng.setOpcode2(AppSettingsManager.getInstance().getOpcode2());*/
        String intsd = StringUtils.GetInternalSDCARD();
        if (out == null) {
            for (BaseHolder holder: activityInterface.getFileListController().getFiles())
            {
                if (holder.getName().equals(file.getName()))
                    out = holder;
            }
            if(out == null)
                out = activityInterface.getFileListController().getNewImgFileHolder(file);
        }
        dng.setExifData(new ExifInfo(100,0,0,0,0,0,"",""));
        if ((VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP
                || !SettingsManager.getInstance().GetWriteExternal()) && !FileListController.needStorageAccessFrameWork) {

            dng.setBayerData(data, file.getAbsolutePath());
            dng.WriteDngWithProfile(dngprofile);
        }
        else
        {
            ParcelFileDescriptor pfd = null;
            if (((ActivityInterface)getActivity()).getFileListController().getFreeDcamDocumentFolder() != null && SettingsManager.getInstance().GetWriteExternal()) {
                DocumentFile df = ((ActivityInterface) getActivity()).getFileListController().getFreeDcamDocumentFolder();
                DocumentFile wr = df.createFile("image/dng", out.getName());
                try {

                    pfd = getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                } catch (FileNotFoundException | IllegalArgumentException ex) {
                    Log.WriteEx(ex);
                    return null;
                }
            }
            else {
                try {
                    pfd = getContext().getContentResolver().openFileDescriptor(((UriHolder)out).getMediaStoreUri(), "rw");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


            if (pfd != null) {
                dng.SetBayerDataFD(data, pfd, file.getName());
                dng.WriteDngWithProfile(dngprofile);
                try {
                    pfd.close();
                } catch (IOException e) {
                    Log.WriteEx(e);
                    return null;
                }
                pfd = null;
            }
        }


        /*long gpsTime = 1477324747000l;
        String provider = "gps";
        double longitude = 11.65918818;
        double latitude = 48.2503155;
        double altitude = 561.0;
        if (fakeGPS.isChecked())
            dng.SetGpsData(altitude, latitude, longitude, provider, gpsTime);*/

        data = null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getActivity().sendBroadcast(intent);
        if (filesToConvert.length == 1)
        {

            try {
                return out.getBitmapFromDng(getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


}
