package troop.com.imageviewer.holder;

import java.io.File;

/**
 * Created by troop on 12.12.2015.
 *
 * This class represent the State of GridImageview when its added or not to Gridview and updates the
 * GridviewItem when its visibile/invisible on screen
 */
public class FileHolder extends BaseHolder
{
    private File file;

    private boolean isFolder = false;

    public FileHolder(File file)
    {
        this.file = file;
        if (file.isDirectory())
            isFolder=true;
    }

    public File getFile()
    {
        return file;
    }

    public boolean IsFolder()
    {
        return isFolder;
    }
}
