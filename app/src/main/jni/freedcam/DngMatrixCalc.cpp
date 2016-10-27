//
// Created by GeorgeKiarie on 10/27/2016.
//


#include <jni.h>
#include <stdlib.h>
#include <android/log.h>
#define  LOG_TAG    "freedcam.DngMatrix"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

typedef struct {
    int32_t numerator;
    int32_t denominator;
} cam_rational_type_t;

#define FLOAT_TO_Q(exp, f) \
    ((int32_t)((f*(1<<(exp))) + ((f<0) ? -0.5 : 0.5)))

#define FLOAT_TO_RATIONAL(In,Out, M, N) ({ \
    int i, j; \
    for (i=0; i<M ; i++) \
    for (j=0; j<N; j++){ \
      Out[i][j].numerator = FLOAT_TO_Q(7, In[i][j]); \
      Out[i][j].denominator = FLOAT_TO_Q(7, 1); \
    } \
})

#define MATRIX_MULT(IN1, IN2, OUT, M, N, L) ({ \
    int i, j, k; \
    for (i=0; i<M; i++) \
    for (j=0; j<L; j++) { \
      OUT[i][j] = 0; \
      for (k=0; k<N; k++) \
      OUT[i][j] += (IN1[i][k] * IN2[k][j]); \
    } })

#define MATRIX_INVERSE_3x3(MatIn, MatOut) ({\
    typeof (MatOut[0]) __det; \
    __det = MatIn[0]*(MatIn[4]*MatIn[8]-MatIn[5]*MatIn[7]) + \
    MatIn[1]*(MatIn[5]*MatIn[6]-MatIn[3]*MatIn[8]) + \
    MatIn[2]*(MatIn[3]*MatIn[7]-MatIn[4]*MatIn[6]); \
    if (__det == 0) \
    return; \
    MatOut[0] = (MatIn[4]*MatIn[8] - MatIn[5]*MatIn[7]) / __det; \
    MatOut[1] = (MatIn[2]*MatIn[7] - MatIn[1]*MatIn[8]) / __det; \
    MatOut[2] = (MatIn[1]*MatIn[5] - MatIn[2]*MatIn[4]) / __det; \
    MatOut[3] = (MatIn[5]*MatIn[6] - MatIn[3]*MatIn[8]) / __det; \
    MatOut[4] = (MatIn[0]*MatIn[8] - MatIn[2]*MatIn[6]) / __det; \
    MatOut[5] = (MatIn[2]*MatIn[3] - MatIn[0]*MatIn[5]) / __det; \
    MatOut[6] = (MatIn[3]*MatIn[7] - MatIn[4]*MatIn[6]) / __det; \
    MatOut[7] = (MatIn[1]*MatIn[6] - MatIn[0]*MatIn[7]) / __det; \
    MatOut[8] = (MatIn[0]*MatIn[4] - MatIn[1]*MatIn[3]) / __det; \
})

extern "C"
{
    JNIEXPORT void JNICALL Java_freed_jni_DngMatrixCalc_calc(JNIEnv *env, jobject thiz);
}

static float sRGB2XYZ [3][3] = {
  {0.4360747, 0.3850649, 0.1430804},
  {0.2225045, 0.7168786, 0.0606169},
  {0.0139322, 0.0971045, 0.7141733}
};
static float XYZ2RGB[3][3] = {
  {0.4124564,  0.3575761,  0.1804375},
  {0.2126729,  0.7151522,  0.0721750},
  {0.0193339,  0.1191920,  0.9503041}
};

static float D65_to_ref_A[3][3] = {
  {1.2164557, 0.1109905,  -0.1549325},
  {0.1533326,  0.9152313,  -0.0559953},
  {-0.0239469, 0.0358984,  0.3147529}
};

void sensor_generate_A_matrix()
{
  float cc_mat[3][3], wb_mat[3][3], out_mat[3][3];

 cam_rational_type_t forward_mat[3][3],color_mat[3][3];

  float *ptr1, *ptr2;
  float tmp[3][3], tmp1[3][3], tmp2[3][3];

  cc_mat[0][0] = 2.30214; cc_mat[0][1] = -1.33554; cc_mat[0][2] = 0.0334;
  cc_mat[1][0] = -0.1544; cc_mat[1][1] = 1.6976; cc_mat[1][2] = -0.5432;
  cc_mat[2][0] = -0.0438; cc_mat[2][1] = -0.6834; cc_mat[2][2] = 1.7272;

  wb_mat[0][0] = 1.182000;
  wb_mat[0][1] = 0;
  wb_mat[0][2] = 0;
  wb_mat[1][0] = 0;
  wb_mat[1][1] = 1.000000;
  wb_mat[1][2] = 0;
  wb_mat[2][0] = 0;
  wb_mat[2][1] = 0;
  wb_mat[2][2] = 2.661000;

  /* Forward Transform: sRGB2XYZ * CC */
  MATRIX_MULT(sRGB2XYZ, cc_mat, out_mat, 3, 3, 3);
  FLOAT_TO_RATIONAL(out_mat, forward_mat, 3, 3);

  //CM = Invert (H * W * CC' * G) 1510

    MATRIX_MULT(D65_to_ref_A, XYZ2RGB, tmp, 3, 3, 3);
    MATRIX_MULT(tmp, cc_mat, tmp1, 3, 3, 3);

  MATRIX_MULT(tmp1, wb_mat, tmp2, 3, 3, 3);

  ptr1 = (float*) tmp2;
  ptr2 = (float*) out_mat;
  MATRIX_INVERSE_3x3(ptr1, ptr2);

  FLOAT_TO_RATIONAL(out_mat, color_mat, 3, 3);
}

void sensor_generate_D65_matrix()
{
  float cc_mat[3][3], wb_mat[3][3], out_mat[3][3];

   cam_rational_type_t forward_mat[3][3],color_mat[3][3];

  float *ptr1, *ptr2;
  float tmp[3][3], tmp1[3][3], tmp2[3][3];

  cc_mat[0][0] = 1.8543; cc_mat[0][1] = -0.8709; cc_mat[0][2] = 0.0167;
  cc_mat[1][0] = -0.0772; cc_mat[1][1] = 1.3487; cc_mat[1][2] = -0.2716;
  cc_mat[2][0] = 0.1526; cc_mat[2][1] = -0.8302; cc_mat[2][2] = 1.6776;

  wb_mat[0][0] = 2.109000;
  wb_mat[0][1] = 0;
  wb_mat[0][2] = 0;
  wb_mat[1][0] = 0;
  wb_mat[1][1] = 1.000000;
  wb_mat[1][2] = 0;
  wb_mat[2][0] = 0;
  wb_mat[2][1] = 0;
  wb_mat[2][2] = 1.390000;

  /* Forward Transform: sRGB2XYZ * CC */
  MATRIX_MULT(sRGB2XYZ, cc_mat, out_mat, 3, 3, 3);
  FLOAT_TO_RATIONAL(out_mat, forward_mat, 3, 3);

  for(int i = 0; i<2;i++)
  {
  LOGD("FM:%f\n",forward_mat[0][i]);

  }
  for(int i = 0; i<2;i++)
    {
    LOGD("FM:%f\n",forward_mat[1][i]);

    }
    for(int i = 0; i<2;i++)
      {
      LOGD("FM:%f\n",forward_mat[2][i]);

      }

  //CM = Invert (H * W * CC' * G) 1510

    MATRIX_MULT(XYZ2RGB, cc_mat, tmp1, 3, 3, 3);

  MATRIX_MULT(tmp1, wb_mat, tmp2, 3, 3, 3);

  ptr1 = (float*) tmp2;
  ptr2 = (float*) out_mat;
  MATRIX_INVERSE_3x3(ptr1, ptr2);

  FLOAT_TO_RATIONAL(out_mat, color_mat, 3, 3);
   for(int i = 0; i<2;i++)
    {
    LOGD("FM:%f\n",color_mat[0][i]);

    }
    for(int i = 0; i<2;i++)
      {
      LOGD("FM:%f\n",color_mat[1][i]);

      }
      for(int i = 0; i<2;i++)
        {
        LOGD("FM:%f\n",color_mat[2][i]);

        }
}

void sensor_generate_calib_trans(cam_rational_type_t matrix[3][3],
  float r_gain, float b_gain)
{
  float wb_cal[3][3] = {{0,0,0},
    {0,0,0},
    {0,0,0}};

  wb_cal[0][0] = r_gain;
  wb_cal[1][1] = 1;
  wb_cal[2][2] = b_gain;

  FLOAT_TO_RATIONAL(wb_cal, matrix, 3, 3);
}

void sensor_generate_unit_matrix(cam_rational_type_t matrix[3][3])
{
  float wb_cal[3][3] = {{1,0,0},
    {0,1,0},
    {0,0,1}};

  FLOAT_TO_RATIONAL(wb_cal, matrix, 3, 3);
}

JNIEXPORT void JNICALL Java_freed_jni_DngMatrixCalc_calc(JNIEnv *env, jobject thiz)
{
    sensor_generate_D65_matrix();
}


