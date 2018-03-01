//
// Created by troop on 01.03.2018.
//

#ifndef FREEDCAM_GPSINFO_H
#define FREEDCAM_GPSINFO_H


class GpsInfo {
public:
    double Altitude;
    float *Latitude;
    float *Longitude;
    char* Provider;
    float *gpsTime;
    char* gpsDate;

    void clear()
    {
        Longitude = NULL;
        Latitude = NULL;
        Provider = NULL;
        gpsDate = NULL;
        gpsTime = NULL;
    }

    GpsInfo()
    {
        clear();
    }

};


#endif //FREEDCAM_GPSINFO_H
