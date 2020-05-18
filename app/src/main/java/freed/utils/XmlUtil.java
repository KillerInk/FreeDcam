package freed.utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class XmlUtil {
    public static void writeNodeWithName(BufferedWriter writer, String tag, String name) throws IOException {
        writeLine(writer,"<"+tag+ " name = \""+ name +"\">");
    }

    public static void writeTagEnd(BufferedWriter writer,String tag) throws IOException {
        writeLine(writer,"</"+tag+">");
    }

    public static void writeLine(BufferedWriter writer, String s) throws IOException {
        writer.write(s + "\r\n");
    }

    public static String getTagStringWithValue(String tag, String value)
    {
        return  "<" + tag +">" + value + "</" + tag +">" + "\r\n";
    }
}
