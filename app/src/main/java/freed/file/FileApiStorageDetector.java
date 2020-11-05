package freed.file;

import android.os.Build;
import android.text.TextUtils;

import java.io.File;

import freed.utils.Log;
import freed.utils.StringUtils;

public class FileApiStorageDetector
{

    private final String TAG = FileApiStorageDetector.class.getSimpleName();

    private File internalSD;
    private File externalSD;

    public FileApiStorageDetector()
    {
        findStoragePaths();
    }

    public File getInternalSD()
    {
        return internalSD;
    }

    public File getExternalSD()
    {
        return externalSD;
    }

    private void findStoragePaths()
    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
        {
            internalSD = new File(StringUtils.GetInternalSDCARD());
            try {
                externalSD = new File(StringUtils.GetExternalSDCARD());
            }
            catch (Exception ex)
            {
                Log.d(TAG, "No Ext SD!");
            }
        }
        else
        {
            File storagedir = getStorageDirectory();
            if (storagedir != null) {
                File[] files = storagedir.listFiles();
                if(files == null) {
                    Log.e(TAG, "getStorageDirectory().listFiles() is null!");
                    return;
                }
                boolean internalfound = false;
                boolean externalfound = false;
                for (File file : files) {
                    //first lookup emulated path, for backward compatibility they are mounted too
                    //as sdcard1/2 on some devices
                    String filename = file.getName();
                    if (filename.toLowerCase().equals("emulated")) {
                        internalSD = new File(file.getAbsolutePath() + "/0/");
                        if (internalSD.exists())
                            internalfound = true;
                        File extDcim = new File(file.getAbsolutePath() + "/1/");
                        if (extDcim.exists() && !externalfound) {
                            externalfound = true;
                            externalSD = extDcim;
                        }
                    }
                    if (filename.toLowerCase().equals("sdcard0") && !internalfound && file.exists()) {
                        internalSD = new File(file.getAbsolutePath());
                        internalfound = true;
                    }
                    if (filename.toLowerCase().equals("sdcard1") && !externalfound && file.exists()) {
                        File extDcim = new File(file.getAbsolutePath());
                        if (extDcim.exists()) {

                            externalfound = true;
                            externalSD = extDcim;
                        }
                    }
                    //that is the true sdcard finaly /storage/XXX-XXX/
                    if (!filename.toLowerCase().equals("emulated")
                            && !filename.toLowerCase().equals("sdcard0")
                            && !filename.toLowerCase().equals("sdcard1")
                            && !filename.toLowerCase().equals("self")) {
                        if (file.exists()) {
                            externalfound = true;
                            externalSD = file;
                        }
                    }
                }
            }
        }
    }

    private  File getStorageDirectory() {
        String path = System.getenv("ANDROID_STORAGE");
        return (path == null || TextUtils.isEmpty(path)) ? new File("/storage") : new File(path);
    }
}
