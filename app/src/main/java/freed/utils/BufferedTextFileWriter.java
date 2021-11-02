package freed.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import freed.FreedApplication;

public class BufferedTextFileWriter {

    BufferedWriter outwriter;
    FileWriter fileWriter;
    private String filename;

    public BufferedTextFileWriter(String filename)
    {
        this.filename = filename;
        File outfile = new File(FreedApplication.getContext().getExternalFilesDir(null)+ "/"+filename+".txt");
        try {
            outfile.createNewFile();
            if (!outfile.getParentFile().exists()) {
                outfile.getParentFile().mkdirs();
            }
            if (!outfile.exists())
                outfile.createNewFile();

            fileWriter = new FileWriter(outfile,true);
            outwriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String s)
    {

        try {
            outwriter.write(s);
            outwriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close()
    {
        try {
            outwriter.flush();
            outwriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getFilename() {
        return filename;
    }
}
