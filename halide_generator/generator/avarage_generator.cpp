#include "Halide.h"

using namespace Halide;
namespace {

    class Avarage_generator : public Generator<Avarage_generator> {

    public:
        Input <Halide::Buffer<uint16_t>> inputs{"inputs", 3};
        // Merged buffer
        Output <Halide::Buffer<uint16_t>> output{"output", 2};

        Var x{"x"},y{"y"};
        Var block2{"block2"}, thread2{"thread2"},xo{"xo"}, yo{"yo"}, xi{"xi"}, yi{"yi"};

        void generate() {

            /*Expr merged{"merged"};
            merged =  (inputs(x,y,0) + inputs(x,y,1))/2;*/
            output(x,y) = (inputs(x,y,0) + inputs(x,y,1))/2;;



            /*output.split(x,block2, thread2 ,16);
            output.gpu_blocks(block2).gpu_threads(thread2);
            merged.compute_root();*/
            /**
             * produce merged:
                  for y:
                    for x:
                      merged(...) = ...
                consume merged:
                  produce output:
                    for y:
                      gpu_block x.block2<Default_GPU>:
                        gpu_thread x.thread2 in [0, 15]<Default_GPU>:
                          output(...) = ...
             */

            /*output.split(x,block2, thread2 ,16);
            output.gpu_blocks(block2).gpu_threads(thread2);*/
            /**
             * produce output:
                  for y:
                    gpu_block x.block2<Default_GPU>:
                      gpu_thread x.thread2 in [0, 15]<Default_GPU>:
                        output(...) = ...
             */

            /*
            output.split(x,block2, thread2 ,16);
            output.parallel(y).gpu_blocks(block2).gpu_threads(thread2);*/
            /**
             * produce output:
                  parallel y:
                    gpu_block x.block2<Default_GPU>:
                      gpu_thread x.thread2 in [0, 15]<Default_GPU>:
                        output(...) = ...
             */


            /*output.gpu_tile(x, y, xo, yo, xi, yi, 16, 16);
            merged.compute_at(output, xo);
            merged.gpu_threads(x, y);*/
            /**
             * produce output:
              gpu_block y.yo<Default_GPU>:
                gpu_block x.xo<Default_GPU>:
                  produce merged:
                    gpu_thread y<Default_GPU>:
                      gpu_thread x<Default_GPU>:
                        merged(...) = ...
                  consume merged:
                    gpu_thread y.yi in [0, 15]<Default_GPU>:
                      gpu_thread x.xi in [0, 15]<Default_GPU>:
                        output(...) = ...
             */

           /* output.gpu_tile(x, y, xo, yo, xi, yi, 16, 16);
            output.compute_root();*/
            /**
             * produce output:
                  gpu_block y.yo<Default_GPU>:
                    gpu_block x.xo<Default_GPU>:
                      gpu_thread y.yi in [0, 15]<Default_GPU>:
                        gpu_thread x.xi in [0, 15]<Default_GPU>:
                          output(...) = ...
             */


            /*merged.gpu_tile(x, y, xo, yo, xi, yi, 16, 16);
            merged.compute_root();*/
            /**
             * produce merged:
                  gpu_block y.yo<Default_GPU>:
                    gpu_block x.xo<Default_GPU>:
                      gpu_thread y.yi in [0, 15]<Default_GPU>:
                        gpu_thread x.xi in [0, 15]<Default_GPU>:
                          merged(...) = ...
                consume merged:
                  produce output:
                    for y:
                      for x:
                        output(...) = ...
             */


            /*merged.gpu_tile(x, y, xo, yo, xi, yi, 16, 16);
            output.gpu_tile(x, y, xo, yo, xi, yi, 16, 16);
            merged.compute_root();*/
            /**
             * produce merged:
                  gpu_block y.yo<Default_GPU>:
                    gpu_block x.xo<Default_GPU>:
                      gpu_thread y.yi in [0, 15]<Default_GPU>:
                        gpu_thread x.xi in [0, 15]<Default_GPU>:
                          merged(...) = ...
                consume merged:
                  produce output:
                    gpu_block y.yo<Default_GPU>:
                      gpu_block x.xo<Default_GPU>:
                        gpu_thread y.yi in [0, 15]<Default_GPU>:
                          gpu_thread x.xi in [0, 15]<Default_GPU>:
                            output(...) = ...
             */



           /* merged.gpu_tile(x, y, xo, yo, xi, yi, 16, 16);
            merged.compute_root();
            output.split(x,block2, thread2 ,16);
            output.parallel(y).gpu_blocks(block2).gpu_threads(thread2);
*/
            /**
             * produce merged:
                  gpu_block y.yo<Default_GPU>:
                    gpu_block x.xo<Default_GPU>:
                      gpu_thread y.yi in [0, 15]<Default_GPU>:
                        gpu_thread x.xi in [0, 15]<Default_GPU>:
                          merged(...) = ...
                consume merged:
                  produce output:
                    parallel y:
                      gpu_block x.block2<Default_GPU>:
                        gpu_thread x.thread2 in [0, 15]<Default_GPU>:
                          output(...) = ...
             */

            //output.gpu_tile(x,xi,16);
            /**
            produce output:
              gpu_block y.y<Default_GPU>:
                gpu_block x.x<Default_GPU>:
                  gpu_thread y.xo in [0, 15]<Default_GPU>:
                    gpu_thread x.xi in [0, 15]<Default_GPU>:
                      output(...) = ...
             */

            //output.tile(x,y,xi,xo,16,16);
            output.gpu_tile(x, y, xi, yi, 32, 32);
            //output.compute_root();

            /**
             produce output:
              for y.y:
                for x.x:
                  for y.xo in [0, 15]:
                    for x.xi in [0, 15]:
                      output(...) = ...
             */

            //the only working method that create not a black output
            //output.parallel(y).vectorize(x,128);
            /**
             * produce output:
                  parallel y:
                    for x.x:
                      vectorized x.v11 in [0, 15]:
                        output(...) = ...
             */

            output.print_loop_nest();

        }
    };
}


HALIDE_REGISTER_GENERATOR(Avarage_generator, avarage_generator)