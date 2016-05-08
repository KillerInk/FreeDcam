//
// Created by GeorgeKiarie on 2/14/2016.
//
#include <inttypes.h>
#include <assert.h>

#ifndef RAWDEFS_H
#define RAWDEFS_H

#define DNG_OPCODEFLAG_OPTIONAL 1
#define DNG_OPCODEFLAG_SKIPPREVIEW 2

#define DNG_OPCODE_GAINMAP_ID 9
#define DNG_OPCODE_GAINMAP_VER 0x01030000

typedef struct __attribute__((packed)) {
    uint32_t count;
} dng_opcodelist_header_t;

typedef struct __attribute__((packed)) {
    uint32_t id;
    uint32_t ver;
    uint32_t flags;
    uint32_t parsize;
} dng_opcode_header_t;

typedef struct __attribute__((packed)) {
    dng_opcode_header_t header;
    uint32_t Top;
    uint32_t Left;
    uint32_t Bottom;
    uint32_t Right;
    uint32_t Plane;
    uint32_t Planes;
    uint32_t RowPitch;
    uint32_t ColPitch;
    uint32_t MapPointsV;
    uint32_t MapPointsH;
    double MapSpacingV;
    double MapSpacingH;
    double MapOriginV;
    double MapOriginH;
    uint32_t MapPlanes;
    float MapGain[0];
} dng_opcode_GainMap_t;
typedef uint8_t uint8_alias_t __attribute__((may_alias));
typedef uint16_t uint16_alias_t __attribute__((may_alias));
typedef uint32_t uint32_alias_t __attribute__((may_alias));
typedef uint64_t uint64_alias_t __attribute__((may_alias));


#define PUT_BIG_16(_dst, _src)				\
do {							\
  assert(sizeof(_dst) == 2);				\
  typeof(_dst) _tmp = (_src);	/* For type checking */	\
  uint16_t _srcu = *(uint16_alias_t *)&_tmp;		\
  uint8_alias_t *_dstp = (uint8_alias_t *)&(_dst);	\
							\
  _dstp[0] = (_srcu&0xff00) >> 8;			\
  _dstp[1] = (_srcu&0x00ff) >> 0;			\
 } while (0)

#define PUT_BIG_32(_dst, _src)				\
do {							\
  assert(sizeof(_dst) == 4);				\
  typeof(_dst) _tmp = (_src);	/* For type checking */	\
  uint32_t _srcu = *(uint32_alias_t *)&_tmp;		\
  uint8_alias_t *_dstp = (uint8_alias_t *)&(_dst);	\
							\
  _dstp[0] = (_srcu&0xff000000) >> 24;			\
  _dstp[1] = (_srcu&0x00ff0000) >> 16;			\
  _dstp[2] = (_srcu&0x0000ff00) >> 8;			\
  _dstp[3] = (_srcu&0x000000ff) >> 0;			\
 } while (0)

#define PUT_BIG_64(_dst, _src)				\
do {							\
  assert(sizeof(_dst) == 8);				\
  typeof(_dst) _tmp = (_src);	/* For type checking */	\
  uint64_t _srcu = *(uint64_alias_t *)&_tmp;		\
  uint8_alias_t *_dstp = (uint8_alias_t *)&(_dst);	\
							\
  _dstp[0] = (_srcu&0xff00000000000000) >> 56;		\
  _dstp[1] = (_srcu&0x00ff000000000000) >> 48;		\
  _dstp[2] = (_srcu&0x0000ff0000000000) >> 40;		\
  _dstp[3] = (_srcu&0x000000ff00000000) >> 32;		\
  _dstp[4] = (_srcu&0x00000000ff000000) >> 24;		\
  _dstp[5] = (_srcu&0x0000000000ff0000) >> 16;		\
  _dstp[6] = (_srcu&0x000000000000ff00) >> 8;		\
  _dstp[7] = (_srcu&0x00000000000000ff) >> 0;		\
 } while (0)

#endif //RAWDEFS_H
