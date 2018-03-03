//
// Created by troop on 03.03.2018.
//
#include <jni.h>
#include "JniUtils.h"

#ifndef FREEDCAM_CUSTOMMATRIX_H
#define FREEDCAM_CUSTOMMATRIX_H

class CustomMatrix
{
public:
    float *colorMatrix1;
    float *colorMatrix2;
    float *neutralColorMatrix;
    float *fowardMatrix1;
    float *fowardMatrix2;
    float *reductionMatrix1;
    float *reductionMatrix2;
    double *noiseMatrix;

    CustomMatrix()
    {
        colorMatrix1 = NULL;
        colorMatrix2 = NULL;
        neutralColorMatrix = NULL;
        fowardMatrix1 = NULL;
        fowardMatrix2 = NULL;
        reductionMatrix1 = NULL;
        reductionMatrix2 = NULL;
        noiseMatrix = NULL;
    }

    void clear()
    {
        if(colorMatrix1 != NULL)
        {
            delete[] colorMatrix1;
            colorMatrix1 = NULL;
        }
        if(colorMatrix2 != NULL)
        {
            delete[] colorMatrix2;
            colorMatrix2 = NULL;
        }
        if(neutralColorMatrix != NULL)
        {
            delete[] neutralColorMatrix;
            neutralColorMatrix = NULL;
        }
        if(fowardMatrix1 != NULL)
        {
            delete[] fowardMatrix1;
            fowardMatrix1 = NULL;
        }
        if(fowardMatrix2 != NULL)
        {
            delete[] fowardMatrix2;
            fowardMatrix2 = NULL;
        }
        if(reductionMatrix1 != NULL)
        {
            delete[] reductionMatrix1;
            reductionMatrix1 = NULL;
        }
        if(reductionMatrix2 != NULL)
        {
            delete[] reductionMatrix2;
            reductionMatrix2 = NULL;
        }
        if(reductionMatrix2 != NULL)
        {
            delete[] reductionMatrix2;
            reductionMatrix2 = NULL;
        }
        if(noiseMatrix != NULL)
        {
            delete[] noiseMatrix;
            noiseMatrix = NULL;
        }
    }
};

#endif //FREEDCAM_CUSTOMMATRIX_H
