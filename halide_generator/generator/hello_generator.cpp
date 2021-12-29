#include "Halide.h"
#include "hdrplus/align.h"
#include "hdrplus/merge.h"


namespace {

    class Hello : public Generator<Hello> {
    public:
        Input <Halide::Buffer<uint16_t>> inputs{"inputs", 3};
        Input<int> minoffset{"minoffset"};
        Input<int> maxoffset{"maxoffset"};
        Input<int> l1mindistance{"minoffset"};
        Input<int> l1maxdistance{"maxoffset"};
        // Merged buffer
        Output <Halide::Buffer<uint16_t>> output{"output", 2};

        void generate() {
            Func alignment = align(inputs,inputs.width(), inputs.height());
            Func merged = merge(inputs, alignment, minoffset, maxoffset, l1mindistance,
                                l1maxdistance);
            output = merged;
        }
    };
}


HALIDE_REGISTER_GENERATOR(Hello, hello)