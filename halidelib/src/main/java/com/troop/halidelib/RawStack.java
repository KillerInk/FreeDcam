package com.troop.halidelib;

import java.nio.ByteBuffer;

public class RawStack {

    private final String TAG = RawStack.class.getSimpleName();
    static
    {
        System.loadLibrary("halide");
    }

    private ByteBuffer byteBuffer;

    private native ByteBuffer init();
    private native void setBaseFrame(ByteBuffer buffer, byte[] fileBytes,int width, int heigt);
    private native void setBaseFrameBuffer(ByteBuffer buffer, ByteBuffer fileBytes,int width, int heigt);
    private native void stackFrame(ByteBuffer buffer, byte[] nextframe);
    private native void stackFrameAvarage(ByteBuffer buffer, byte[] nextframe);
    private native void stackByteBufferAvarage(ByteBuffer buffer, ByteBuffer nextframe);
    private native void stackFrameBuffer(ByteBuffer buffer, ByteBuffer nextframe);
    private native void getOutput(ByteBuffer byteBuffer,byte[] output);
    private native void setUpShift(ByteBuffer byteBuffer, int upshift);
    private native void clear(ByteBuffer byteBuffer);
    private native void stackImages(ByteBuffer imagebuffers, byte[] output);
    private native void stackTo14(ByteBuffer imagebuffers, byte[] output);
    private native void setFirstFrame(ByteBuffer buffer, ByteBuffer img, int width, int height, int imagecount);
    private native void setNextFrame(ByteBuffer buffer, ByteBuffer img);

    public RawStack()
    {
        byteBuffer = init();
    }


    public void setFirstFrame(ByteBuffer img, int width, int height, int imagecount)
    {
        if (byteBuffer != null)
            setFirstFrame(byteBuffer,img,width,height,imagecount);
    }

    public void setNextFrame(ByteBuffer img)
    {
        if (byteBuffer != null)
            setNextFrame(byteBuffer,img);
    }

    public void stackAll(byte[] output)
    {
        if (byteBuffer != null)
            stackImages(byteBuffer,output);
    }

    public void mergeTo14bit(byte[] out)
    {
        if (byteBuffer != null)
            stackTo14(byteBuffer,out);
    }

    public synchronized void setShift(int shift)
    {
        if (byteBuffer != null)
            setUpShift(byteBuffer,shift);
    }

    public synchronized void setFirstFrame(byte[] bytes, int width, int height)
    {
        setBaseFrame(byteBuffer,bytes,width,height);
    }

    public synchronized void setFirstFrame(ByteBuffer bytes, int width, int height)
    {
        setBaseFrameBuffer(byteBuffer,bytes,width,height);
    }

    public synchronized void stackNextFrame(byte[] bytes)
    {
        stackFrame(byteBuffer,bytes);
    }

    public synchronized void stackNextFrame(ByteBuffer bytes)
    {
        stackFrameBuffer(byteBuffer,bytes);
    }

    public synchronized void stackNextFrameAvarage(byte[] bytes)
    {
        stackFrameAvarage(byteBuffer,bytes);
    }

    public synchronized void stackNextFrameAvarage(ByteBuffer bytes)
    {
        stackByteBufferAvarage(byteBuffer,bytes);
    }


    public synchronized void getOutputBuffer(byte[] output)
    {
        if (byteBuffer != null)
            getOutput(byteBuffer,output);
    }

    public void clear()
    {
        if (byteBuffer != null)
            clear(byteBuffer);
    }
}
