#include "Halide.h"
#include "hdrplus/align.h"
#include "hdrplus/merge.h"

using namespace Halide;
namespace {

    class Avarage_generator : public Generator<Avarage_generator> {
    public:
        Input <Halide::Buffer<uint16_t>> inputs{"inputs", 3};
        // Merged buffer
        Output <Halide::Buffer<uint16_t>> output{"output", 2};

        Var x,y,n;

        void generate() {

            output(x,y) = (inputs(x,y,0) + inputs(x,y,1)/2);
        }
    };
}


HALIDE_REGISTER_GENERATOR(Avarage_generator, avarage_generator)