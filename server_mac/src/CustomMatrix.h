//
// Created by troop on 03.03.2018.
//

#ifndef FREEDCAM_CUSTOMMATRIX_H
#define FREEDCAM_CUSTOMMATRIX_H

#include <iostream>

class CustomMatrix
{
public:
    float * colorMatrix1 = NULL;
    float * colorMatrix2 = NULL;
    float * neutralColorMatrix = NULL;
    float * fowardMatrix1 = NULL;
    float * fowardMatrix2 = NULL;
    float * reductionMatrix1 = NULL;
    float * reductionMatrix2 = NULL;
    double * noiseMatrix = NULL;

    CustomMatrix()
    {
      
    }

    void clear()
    {
        if(colorMatrix1 != NULL)
        {
            delete[] colorMatrix1;
            
        }
        if(colorMatrix2 != NULL)
        {
            delete[] colorMatrix2;
           
        }
        if(neutralColorMatrix != NULL)
        {
            delete[] neutralColorMatrix;
           
        }
        if(fowardMatrix1 != NULL)
        {
            delete[] fowardMatrix1;
        }
        if(fowardMatrix2 != NULL)
        {
            delete[] fowardMatrix2;
        }
        if(reductionMatrix1 != NULL)
        {
            delete[] reductionMatrix1;
        }
		if (reductionMatrix2 != NULL)
		{
			delete[] reductionMatrix2;
		}
        if(reductionMatrix2 != NULL)
        {
            delete[] reductionMatrix2;
        }
        if(noiseMatrix != NULL)
        {
            delete[] noiseMatrix;
        }
    }
};

#endif //FREEDCAM_CUSTOMMATRIX_H
