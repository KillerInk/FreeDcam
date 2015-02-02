package com.troop.freedcam.sonyapi.sonystuff;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 02.02.2015.
 */
public class DataExtractor
{
    private static int commonHeaderlength = 1 + 1 + 2 + 4;
    private static int payloadHeaderlength = 128;

    private InputStream mInputStream;
    public DataExtractor()
    {}

    public CommonHeader commonHeader;
    public PayLoadHeader payLoadHeader;

    public void ExtractData(InputStream mInputStream) throws IOException {
        commonHeader = new CommonHeader(SimpleLiveviewSlicer.readBytes(mInputStream, commonHeaderlength));
        payLoadHeader = new PayLoadHeader(SimpleLiveviewSlicer.readBytes(mInputStream, payloadHeaderlength));
    }

    public class CommonHeader
    {
        public int PayloadType;
        public int Sequencenumber;
        public CommonHeader(byte[] bytes) throws IOException {
            if (bytes == null || bytes.length != commonHeaderlength) {
                throw new IOException("Cannot read stream for common header.");
            }

            if (bytes[0] != (byte) 0xFF) {
                throw new IOException("Unexpected data format. (Start byte)");
            }
            PayloadType = bytes[1];
            Sequencenumber = SimpleLiveviewSlicer.bytesToInt(bytes,2,2);
        }
    }

    public class PayLoadHeader
    {
        public int jpegSize;
        public int paddingSize;
        public int frameCount;
        public String version;
        public byte[] jpegData;
        public byte[] paddingData;
        public int frameDataSize;
        public int singelFrameDataSize;
        public List<FrameInfo> frameInfoList;
        public PayLoadHeader(byte[] bytes) throws IOException
        {
            if (bytes == null || bytes.length != payloadHeaderlength) {
                throw new IOException("Cannot read stream for payload header.");
            }
            if (bytes[0] != (byte) 0x24 || bytes[1] != (byte) 0x35
                    || bytes[2] != (byte) 0x68
                    || bytes[3] != (byte) 0x79) {
                throw new IOException("Unexpected data format. (Start code)");
            }
            jpegSize = SimpleLiveviewSlicer.bytesToInt(bytes, 4, 3);
            paddingSize = SimpleLiveviewSlicer.bytesToInt(bytes, 7, 1);

            if (commonHeader.PayloadType == 2)
            {
                frameDataSize = SimpleLiveviewSlicer.bytesToInt(bytes, 4, 3);
                version = SimpleLiveviewSlicer.bytesToInt(bytes, 8, 1) + "." + SimpleLiveviewSlicer.bytesToInt(bytes, 9, 1);
                frameCount = SimpleLiveviewSlicer.bytesToInt(bytes, 10, 2);
                if (version.equals("1.0"))
                    singelFrameDataSize = 16;
                else
                    singelFrameDataSize = SimpleLiveviewSlicer.bytesToInt(bytes, 12,2);
            }
            if (commonHeader.PayloadType == 1)
                jpegData = SimpleLiveviewSlicer.readBytes(mInputStream, jpegSize);


            if (commonHeader.PayloadType == 2)
            {
                frameInfoList = new ArrayList<FrameInfo>();
                for (int i = 0; i<frameCount; i++)
                {
                    int read = frameCount * singelFrameDataSize;
                    byte[] framebytes = SimpleLiveviewSlicer.readBytes(mInputStream, read);
                    frameInfoList.add(new FrameInfo(framebytes));
                }
            }
            paddingData = SimpleLiveviewSlicer.readBytes(mInputStream, paddingSize);
        }
    }

    public class FrameInfo
    {
        int TopLeft;
        int BottomRight;
        int Category;
        int Status;
        int AditionalStatus;
        public FrameInfo(byte[] bytes)
        {
            TopLeft = SimpleLiveviewSlicer.bytesToInt(bytes,0,4);
            BottomRight = SimpleLiveviewSlicer.bytesToInt(bytes,4,4);
            Category = SimpleLiveviewSlicer.bytesToInt(bytes, 5,1);
            Status = SimpleLiveviewSlicer.bytesToInt(bytes, 6,1);
            AditionalStatus = SimpleLiveviewSlicer.bytesToInt(bytes, 7,1);
        }
    }
}





