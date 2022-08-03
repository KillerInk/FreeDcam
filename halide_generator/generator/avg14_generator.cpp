#include "include/Halide.h"

using namespace Halide;
using namespace Halide::ConciseCasts;

namespace {

    class Avg14_generator : public Generator<Avg14_generator> {
    public:
        Input <Halide::Buffer<uint16_t>> inputs{"inputs", 3};
        // Merged buffer
        Output <Halide::Buffer<uint16_t>> output{"output", 2};
        Var x{"x"},y{"y"},n("n");
        void generate() {
            RDom r1(1, inputs.dim(2).max());
            output(x,y) = u16(sum(inputs(x,y,r1)));

            output.compute_root().parallel(x).vectorize(y, 32);
        }
    };
}

HALIDE_REGISTER_GENERATOR(Avg14_generator, avg14_generator)