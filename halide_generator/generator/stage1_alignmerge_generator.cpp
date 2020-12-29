#include "Halide.h"
#include "hdrplus/align.h"
#include "hdrplus/merge.h"

using namespace Halide;
namespace {

    class Stage1_alignmerge : public Generator<Stage1_alignmerge> {
    public:
        Input <Halide::Buffer<uint16_t>> inputs{"inputs", 3};
        Input <Halide::Buffer<uint16_t>> inputsaligned{"inputsaligned", 3};
        Input<int> minoffset{"minoffset"};
        Input<int> maxoffset{"maxoffset"};
        Input<int> l1mindistance{"l1mindistance"};
        Input<int> l1maxdistance{"l1maxdistance"};
        // Merged buffer
        Output <Halide::Buffer<uint16_t>> output{"output", 2};

        void generate() {
            Func alignment = align(inputs,inputs.width(), inputs.height());
            Func merged = merge(inputsaligned, alignment, minoffset, maxoffset,l1mindistance,l1maxdistance,inputs.width(), inputs.height(),inputs.dim(2).extent());
            output = merged;
        }
    };
}


HALIDE_REGISTER_GENERATOR(Stage1_alignmerge, stage1_alignmerge)