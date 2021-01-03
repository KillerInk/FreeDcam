//
// Created by troop on 21.06.2018.
//


#include "DngProfile.h"
#include "CustomMatrix.h"
#include "DngWriter.h"
#include "OpCode.h"
#include "../include/HalideBuffer.h"
#include "../include/stage1_alignmerge.h"
#include "../include/stage1_align_merge.h"
#include "../include/avarage_generator.h"
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>

#ifndef FREEDCAM_RAWSTACKPIPENATIVE_H
#define FREEDCAM_RAWSTACKPIPENATIVE_H

#define  LOG_TAG    "freedcam.RawStackPipeNative"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)



class RawStackPipeNative
{
public:
    RawStackPipeNative()
    {}
    Halide::Runtime::Buffer<uint16_t> input;
    Halide::Runtime::Buffer<uint16_t> input_to_merge;
    Halide::Runtime::Buffer<uint16_t> output;
    uint16_t * inputdata, *mergedata, *outdata;
    int width;
    int height;
    int offset;
    OpCode * opCode =NULL;
    int upshift = 0,minoffset = -128, maxoffset = 128,l1mindistance=4,l1maxdistance = 128, imagecount = 0;

    int bl = 0;



    void setBaseFrame(int width, int height, uint16_t * images, int imagecount)
    {
        this->width = width;
        this->height = height;
        Halide::Runtime::Buffer<uint16_t> tmp(width, height, imagecount);
        input = tmp;
        inputdata = input.data();
        offset = width*height;
        LOGD("setBaseFrame %i", imagecount);
        for (int i = 0; i < offset; ++i) {
            if(upshift > 0) {
                inputdata[i] = ((images[i]) << upshift) + bl;
            } else
            {
                inputdata[i] = ((images[i]) << upshift);
            }
        }
    }

    void setNextFrame(uint16_t * image)
    {
        imagecount++;
        LOGD("setNextFrame %i", imagecount);
        long tmpoffset = offset*imagecount;
        for (int i = 0; i < offset; ++i) {
            if(upshift > 0) {
                inputdata[i+tmpoffset] = ((image[i]) << upshift) + bl;
            } else
            {
                inputdata[i+tmpoffset] = ((image[i]) << upshift);
            }
        }

    }

    uint16_t * merge_align()
    {
        LOGD("merge align");
        Halide::Runtime::Buffer<uint16_t> out(width, height);
        stage1_align_merge(input,minoffset, maxoffset,l1mindistance,l1maxdistance,out);
        imagecount = 0;
        out.copy_to_host();
        outdata = out.data();
        return (uint16_t*) outdata;
    }

    void init(int width, int height, uint16_t * firstdata)
    {
        LOGD("init upshift %i", upshift);
        this->width = width;
        this->height = height;
        offset = width*height;
        LOGD("init input");
        Halide::Runtime::Buffer<uint16_t> tmp(width, height, 2);
        input = tmp;
        LOGD("init input_to_merge");
        Halide::Runtime::Buffer<uint16_t> tmp2(width, height, 2);
        input_to_merge = tmp2;
        LOGD("init output");
        Halide::Runtime::Buffer<uint16_t> tmp3(width, height);
        output = tmp3;
        inputdata = input.data();
        mergedata = input_to_merge.data();
        outdata = output.data();
        LOGD("copy data");
        for (int i = 0; i < offset; ++i) {
            if(upshift > 0) {

                inputdata[i] = ((firstdata[i]) << upshift) + bl;
                mergedata[i] = ((firstdata[i]) << upshift) + bl;
            } else
            {
                inputdata[i] = ((firstdata[i]) << upshift);
                mergedata[i] = ((firstdata[i]) << upshift);
            }
        }
        LOGD("init done");
        //delete[] firstdata;
    }

    void stackFrame(uint16_t * nextdata)
    {
        LOGD("stackframe");
        for (int i = 0; i < offset; ++i) {
            if(upshift > 0) {
                inputdata[i + offset] = ((nextdata[i]) << upshift) + bl;
                mergedata[i + offset] = ((nextdata[i]) << upshift) + bl;
            } else{
                inputdata[i + offset] = ((nextdata[i]) << upshift);
                mergedata[i + offset] = ((nextdata[i]) << upshift);
            }
        }

        stage1_alignmerge(input,input_to_merge,minoffset, maxoffset,l1mindistance,l1maxdistance,output);
        output.copy_to_host();
        input_to_merge.copy_to_host();
        for (int i = 0; i < offset; ++i) {
            mergedata[i] = outdata[i];
        }
        LOGD("stackframedone");
    }


    void stackFrameAvarage(uint16_t * nextdata)
    {
        LOGD("stackframe");
        for (int i = 0; i < offset; ++i) {
            if(upshift > 0) {
                inputdata[i + offset] = ((nextdata[i]) << upshift) + bl;
                mergedata[i + offset] = ((nextdata[i]) << upshift) + bl;
            } else{
                inputdata[i + offset] = ((nextdata[i]) << upshift);
                mergedata[i + offset] = ((nextdata[i]) << upshift);
            }
        }
        input.set_host_dirty(true);
        avarage_generator(input,output);
        output.copy_to_host();
        for (int i = 0; i < offset; ++i) {
            inputdata[i] = outdata[i];
        }
        LOGD("stackframedone");
    }

    void clear()
    {
        input_to_merge.deallocate();
        input.deallocate();
        output.deallocate();
    }

   /* void writeJpeg(DngProfile * profile, CustomMatrix * customMatrix, char* outfile, ExifInfo * exifInfo)
    {
        Halide::Runtime::Buffer<uint8_t> jpeg_output(width, height, 4);
        stage2_RawToRgb(output,
                        profile->blacklevel[0],
                        profile->whitelevel,
                        customMatrix->neutralColorMatrix[0],
                        customMatrix->neutralColorMatrix[1],
                        customMatrix->neutralColorMatrix[1],
                        customMatrix->neutralColorMatrix[2],
                        1.1,
                        1.8,
                        jpeg_output);

        FILE *fp = fopen(outfile, "wb");
        *//* write header to the file *//*
        *//* write image data bytes to the file *//*
        unsigned char * tmp = jpeg_output.data();
        LOGD("write tmp %i", sizeof(tmp));
        fwrite(tmp, sizeof(tmp), 1, fp);
        fclose(fp);
    }*/

    void writeDng(DngProfile * profile, CustomMatrix * customMatrix, char* outfile, ExifInfo * exifInfo)
    {
        LOGD("write dng");
        DngWriter * writer = new DngWriter();
        writer->customMatrix = customMatrix;
        writer->exifInfo = exifInfo;
        profile->rawType = 6;
        writer->dngProfile = profile;
        if(upshift > 0)
        {
            writer->dngProfile->blacklevel[0] += bl;
            writer->dngProfile->blacklevel[1] += bl;
            writer->dngProfile->blacklevel[2] += bl;
            writer->dngProfile->blacklevel[3] += bl;
        }
        writer->bayerBytes = (unsigned char*) output.data();
        writer->rawSize = width*height*2;
        writer->_make = "hdr+";
        writer->_model = "model";
        writer->fileSavePath = (char*)outfile;
        if(opCode != NULL)
            writer->opCode = opCode;
        writer->WriteDNG();

        delete writer;
        input_to_merge.deallocate();
        input.deallocate();
        output.deallocate();
        /*
        input_to_merge.deallocate();
        delete[] input_to_merge.data();
        delete input_to_merge;
        input.deallocate();
        delete[] input.data();
        delete input;
        output.deallocate();
        delete[] output.data();
        delete output;
        delete outfile;
        delete customMatrix;
        delete profile;*/
        LOGD("write dng done");
    }
};
#endif //FREEDCAM_RAWSTACKPIPENATIVE_H
