//
// Created by troop on 01.03.2018.
//

#ifndef FREEDCAM_DNGPROFILE_H
#define FREEDCAM_DNGPROFILE_H

class DngProfile
{
public:
    long whitelevel;
    float *blacklevel;
    char* bayerformat;
    int rawType;
    int rawwidht, rawheight, rowSize;

    void clear()
    {
        bayerformat;
        blacklevel;
    }
};

#endif //FREEDCAM_DNGPROFILE_H
