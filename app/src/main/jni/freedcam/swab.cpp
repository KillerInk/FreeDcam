#include <stdint.h>
#include <sys/types.h>
#include <endian.h>

void swab(const void *from, void*to, ssize_t n)
{
	ssize_t i;

	if (n < 0)
		return;

	for (i = 0; i< (n/2)*2; i += 2)
		*((uint16_t*)to+i) = __swap16(*((uint16_t*)from+i));
}
