package freed.file;

import androidx.documentfile.provider.DocumentFile;

import java.util.List;

import freed.file.holder.BaseHolder;
import freed.file.holder.DocumentHolder;
import freed.utils.StringUtils;

public class DocumentFileController
{
    public void readFilesFromFolder(DocumentHolder folder, List<BaseHolder> list, FileListController.FormatTypes formatsToShow, boolean external)
    {
        DocumentFile[] files = folder.getDocumentFile().listFiles();
        if (files == null)
            return;
        for (DocumentFile f : files)
        {
            if (formatsToShow == FileListController.FormatTypes.all && (
                    f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG)
                            || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS)
                            || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW)
                            || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER)
                            || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG)
                            || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4)
            ))
                list.add(new DocumentHolder(f,external));
            else if (formatsToShow == FileListController.FormatTypes.dng && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG))
                list.add(new DocumentHolder(f,external));
            else if (formatsToShow == FileListController.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW))
                list.add(new DocumentHolder(f,external));
            else if (formatsToShow == FileListController.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER))
                list.add(new DocumentHolder(f,external));
            else if (formatsToShow == FileListController.FormatTypes.jps && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS))
                list.add(new DocumentHolder(f,external));
            else if (formatsToShow == FileListController.FormatTypes.jpg && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG))
                list.add(new DocumentHolder(f,external));
            else if (formatsToShow == FileListController.FormatTypes.mp4 && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4))
                list.add(new DocumentHolder(f,external));
        }
    }
}
