package troop.com.imageviewer;

import java.io.File;
import java.util.List;

/**
 * Created by Ingo on 13.12.2015.
 */
public class FileUtils
{
    public static void readFilesFromFolder(File folder, List<File> fileList)
    {
        File[] folderfiles = folder.listFiles();
        for (File f : folderfiles)
        {
            if (!f.isDirectory() &&
                    (f.getAbsolutePath().endsWith(".jpg") ||
                            f.getAbsolutePath().endsWith(".mp4")||
                            f.getAbsolutePath().endsWith(".dng")||
                            f.getAbsolutePath().endsWith(".raw")))
                fileList.add(f);
        }
    }

    public static void readSubFolderFromFolder(File folder, List<File> folderList)
    {
        File[] folderfiles = folder.listFiles();
        for (File f : folderfiles)
        {
            if (f.isDirectory() && !f.isHidden())
                folderList.add(f);
        }
    }
}
