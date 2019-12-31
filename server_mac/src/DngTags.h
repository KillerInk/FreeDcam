//
// Created by troop on 17.03.2018.
//

#ifndef FREEDCAM_DNGTAGS_H
#define FREEDCAM_DNGTAGS_H

#include "tiff/libtiff/tiffiop.h"
#include <stdlib.h>

#define N(a) (sizeof(a) / sizeof (a[0]))

#define TIFFTAG_FOWARDMATRIX1		50964
#define TIFFTAG_FOWARDMATRIX2		50965
#define TIFFTAG_NOISEPROFILE		51041
#define TIFFTAG_PROFILETONECURVE	50940
#define TIFFTAG_PROFILEHUESATMAPDIMS 50937
#define TIFFTAG_PROFILEHUESATMAPDATA1 50938
#define TIFFTAG_PROFILEHUESATMAPDATA2 50939
#define TIFFTAG_PROFILENAME 50936
#define TIFFTAG_OPC2 51009 /* OpCode 2 lens shit */
#define TIFFTAG_OPC3 51022 /* OpCode 3 lens shit */

#define TIFFTAG_BASELINEEXPOSUREOFFSET	51109
#define     TIFFTAG_EP_STANDARD_ID         37398

#define GPSTAG_GPSVersionID		0
#define GPSTAG_GPSLatitudeRef		1
#define GPSTAG_GPSLatitude		2
#define GPSTAG_GPSLongitudeRef		3
#define GPSTAG_GPSLongitude		4
#define GPSTAG_GPSAltitudeRef		5
#define GPSTAG_GPSAltitude		6
#define GPSTAG_GPSTimeStamp		7
#define GPSTAG_GPSSatellites		8
#define GPSTAG_GPSStatus		9
#define GPSTAG_GPSMeasureMode		10
#define GPSTAG_GPSDOP		11
#define GPSTAG_GPSSpeedRef		12
#define GPSTAG_GPSSpeed		13
#define GPSTAG_GPSTrackRef		14
#define GPSTAG_GPSTrack		15
#define GPSTAG_GPSImgDirectionRef		16
#define GPSTAG_GPSImgDirection		17
#define GPSTAG_GPSMapDatum		18
#define GPSTAG_GPSDestLatitudeRef		19
#define GPSTAG_GPSDestLatitude		20
#define GPSTAG_GPSDestLongitudeRef		21
#define GPSTAG_GPSDestLongitude		22
#define GPSTAG_GPSDestBearingRef		23
#define GPSTAG_GPSDestBearing		24
#define GPSTAG_GPSDestDistanceRef		25
#define GPSTAG_GPSDestDistance		26
#define GPSTAG_GPSProccesingMethod		27
#define GPSTAG_GPSAreaInformation		28
#define GPSTAG_GPSDateStamp		29
#define GPSTAG_GPSDifferential		30


static const TIFFFieldInfo  dngFields[] = {
        { TIFFTAG_OPC2, -3, -3, TIFF_BYTE, FIELD_CUSTOM, 0, 1, "OpcodeList2" },
        { TIFFTAG_OPC3, -3, -3, TIFF_BYTE,FIELD_CUSTOM, 0, 1, "OpcodeList3" },
        { TIFFTAG_PROFILETONECURVE, -1, -1, TIFF_FLOAT, FIELD_CUSTOM, 0, 1, "ProfileToneCurve" },
        { TIFFTAG_PROFILEHUESATMAPDATA1, -1, -1, TIFF_FLOAT, FIELD_CUSTOM, 0, 1, "ProfileHueSatMapData1" },
        { TIFFTAG_PROFILEHUESATMAPDATA2, -1, -1, TIFF_FLOAT, FIELD_CUSTOM, 0, 1, "ProfileHueSatMapData2" },
        { TIFFTAG_PROFILEHUESATMAPDIMS, -1, -1, TIFF_LONG,  FIELD_CUSTOM, 0, 1, "ProfileHueSatMapDims" },
        { TIFFTAG_FOWARDMATRIX1, -1, -1, TIFF_SRATIONAL, FIELD_CUSTOM, 0, 1, "ForwardMatrix1" },
        { TIFFTAG_FOWARDMATRIX2, -1, -1, TIFF_SRATIONAL, FIELD_CUSTOM, 0, 1, "ForwardMatrix2" },
        { TIFFTAG_NOISEPROFILE, -1, -1, TIFF_DOUBLE, FIELD_CUSTOM, 0, 1, "NoiseProfile" },
        { TIFFTAG_BASELINEEXPOSUREOFFSET, 1, 1, TIFF_SRATIONAL, FIELD_CUSTOM, 0, 0, "BaselineExposureOffset" },
        { EXIFTAG_EXPOSURETIME, 1, 1, TIFF_RATIONAL,  FIELD_CUSTOM, 1, 0, "ExposureTime" },
        { EXIFTAG_FNUMBER, 1, 1, TIFF_RATIONAL,  FIELD_CUSTOM, 1, 0, "FNumber" },
        { EXIFTAG_APERTUREVALUE, 1, 1, TIFF_RATIONAL, FIELD_CUSTOM, 1, 0, "ApertureValue" },
        { EXIFTAG_ISOSPEEDRATINGS, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "ISOSpeedRatings" },
        { EXIFTAG_FLASH, 1, 1, TIFF_SHORT,  FIELD_CUSTOM, 1, 0, "Flash" },
        { EXIFTAG_FOCALLENGTH, 1, 1, TIFF_RATIONAL, FIELD_CUSTOM, 1, 0, "FocalLength" },
        { EXIFTAG_EXPOSUREINDEX, 1, 1, TIFF_RATIONAL, FIELD_CUSTOM, 1, 0, "ExposureIndex" },
        { TIFFTAG_EP_STANDARD_ID, 4, 4, TIFF_BYTE,  FIELD_CUSTOM, 0, 0, "TIFFEPStandardID" },


};

static const TIFFFieldInfo
        gpsFields[] = {
        { GPSTAG_GPSVersionID, 4, 4, TIFF_BYTE, FIELD_CUSTOM, 0, 0, "GPSVersionID" },
        { GPSTAG_GPSLatitudeRef, 2, 2, TIFF_ASCII, FIELD_CUSTOM, 1, 0, "GPSLatitudeRef" },
        { GPSTAG_GPSLatitude, 3, 3, TIFF_RATIONAL,  FIELD_CUSTOM, 1, 0, "GPSLatitude" },
        { GPSTAG_GPSLongitudeRef, 2, 2, TIFF_ASCII, FIELD_CUSTOM, 1, 0, "GPSLongitudeRef" },
        { GPSTAG_GPSLongitude, 3, 3, TIFF_RATIONAL, FIELD_CUSTOM, 1, 0, "GPSLongitude" },
        { GPSTAG_GPSAltitudeRef, -1, -1, TIFF_BYTE,  FIELD_CUSTOM, 1, 1, "GPSAltitudeRef" },
        { GPSTAG_GPSAltitude, 1, 1, TIFF_RATIONAL,  FIELD_CUSTOM, 1, 0, "GPSAltitude" },
        { GPSTAG_GPSTimeStamp, 3, 3, TIFF_RATIONAL,  FIELD_CUSTOM, 1, 0, "GPSTimeStamp" },
        { GPSTAG_GPSSatellites, -1, -1, TIFF_SHORT,  FIELD_CUSTOM, 1, 1, "GPSSatellites" },
        { GPSTAG_GPSStatus, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSStatus" },
        { GPSTAG_GPSMeasureMode, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSMeasureMode" },
        { GPSTAG_GPSDOP, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSDOP" },
        { GPSTAG_GPSSpeedRef, -1, -1, TIFF_SHORT,  FIELD_CUSTOM, 1, 1, "GPSSpeedRef" },
        { GPSTAG_GPSSpeed, -1, -1, TIFF_SHORT,  FIELD_CUSTOM, 1, 1, "GPSSpeed" },
        { GPSTAG_GPSTrackRef, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSTrackRef" },
        { GPSTAG_GPSTrack, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSTrack" },
        { GPSTAG_GPSImgDirectionRef, 20, 20, TIFF_ASCII,  FIELD_CUSTOM, 1, 0, "GPSImgDirectionRef" },
        { GPSTAG_GPSImgDirection, 1, 1, TIFF_RATIONAL,  FIELD_CUSTOM, 1, 0, "GPSImgDirection" },
        { GPSTAG_GPSMapDatum, -1, -1, TIFF_SHORT,  FIELD_CUSTOM, 1, 1, "GPSMapDatum" },
        { GPSTAG_GPSDestLatitudeRef, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSDestLatitudeRef" },
        { GPSTAG_GPSDestLatitude, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSDestLatitude" },
        { GPSTAG_GPSDestLongitudeRef, -1, -1, TIFF_SHORT,  FIELD_CUSTOM, 1, 1, "GPSDestLongitudeRef" },
        { GPSTAG_GPSDestLongitude, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSDestLongitude" },
        { GPSTAG_GPSDestBearingRef, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSDestBearingRef" },
        { GPSTAG_GPSDestBearing, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSDestBearing" },
        { GPSTAG_GPSDestDistanceRef, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSDestDistanceRef" },
        { GPSTAG_GPSDestDistance, -1, -1, TIFF_SHORT,  FIELD_CUSTOM, 1, 1, "GPSDestDistance" },
        { GPSTAG_GPSProccesingMethod, 14, 14, TIFF_ASCII, FIELD_CUSTOM, 1, 0, "GPSProccesingMethod" },
        { GPSTAG_GPSAreaInformation, -1, -1, TIFF_SHORT, FIELD_CUSTOM, 1, 1, "GPSAreaInformation" },
        { GPSTAG_GPSDateStamp, 11, 11, TIFF_ASCII,  FIELD_CUSTOM, 1, 0, "GPSDateStamp" },
        { GPSTAG_GPSDifferential, -1, -1, TIFF_SHORT,  FIELD_CUSTOM, 1, 1, "GPSDifferential" }
};

//////////////////////////////////////////////////////////////////////////GPS ///////////////////

static const TIFFFieldArray gpsFieldArray = { tfiatOther, 0, TIFFArrayCount(gpsFields), (TIFFField*) gpsFields };
static const TIFFFieldArray dngTAGS = { tfiatOther, 0, TIFFArrayCount(dngFields), (TIFFField*) dngFields };

const static TIFFFieldArray* _TIFFmGetGPSFields(void)
{
    return(&gpsFieldArray);
}

const static TIFFFieldArray* _TIFFmGetDNGFields(void)
{
    return(&dngTAGS);
}
/*
int static TIFFCreateGPSDirectory(TIFF* tif)
{
    const TIFFFieldArray* gpsFieldArray;
    gpsFieldArray = _TIFFmGetGPSFields();
    return TIFFCreateCustomDirectory(tif, gpsFieldArray);
}*/
/////////////////////////////////////////////


static TIFFExtendProc _ParentExtender = NULL;

static void
_XTIFFDefaultDirectory(TIFF *tif)
{
    /* Install the extended Tag field info */
	TIFFMergeFieldInfo(tif, dngFields, N(dngFields));
	TIFFMergeFieldInfo(tif, gpsFields, N(gpsFields));

    /* Since an XTIFF client module may have overridden
     * the default directory method, we call it now to
     * allow it to set up the rest of its own methods.
     */

    if (_ParentExtender)
        (*_ParentExtender)(tif);
}

static
void _XTIFFInitialize(void)
{
    static int first_time=1;

    if (! first_time) return; /* Been there. Done that. */
    first_time = 0;

    /* Grab the inherited method and install */
    _ParentExtender = TIFFSetTagExtender(_XTIFFDefaultDirectory);
}

#endif //FREEDCAM_DNGTAGS_H
