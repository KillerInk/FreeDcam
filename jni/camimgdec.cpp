#include "camimgdec.h"
//#include <cstddef>
//#include <cstdint>
#include <stdint.h>
#include <stddef.h>
#include <android/log.h>

#define LOG_TAG "libcamimgdec"

#define LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##__VA_ARGS__)

static inline int32_t clamp(int32_t const &value, int32_t const &min, int32_t const &max);
static void throw_NullPointerException(JNIEnv *env, char const * const message);
static void throw_IndexOutOfBoundsException(JNIEnv *env, char const * const message);

template <typename T> class safe_array {
private:
	JNIEnv *env_;
	jarray const &array_;
	T* ptr_;
	size_t size_;
	bool is_copy_;
	mutable bool is_aborted_;
	T dummy_;
public:
	safe_array(JNIEnv *env, jarray const &array)
		: env_(env), array_(array), ptr_(NULL), size_(0), is_copy_(false), is_aborted_(false), dummy_(0) {
		jboolean is_copy = JNI_FALSE;
		ptr_  = static_cast<T*>(env->GetPrimitiveArrayCritical(array, &is_copy));
		size_ = env->GetArrayLength(array);
		is_copy_ = (JNI_FALSE == is_copy) ? false : true;
	}
	~safe_array() {
		if (is_aborted_) {
			env_->ReleasePrimitiveArrayCritical(array_, ptr_, JNI_ABORT);
		} else {
			env_->ReleasePrimitiveArrayCritical(array_, ptr_, is_copy_ ? JNI_COMMIT : 0);
		}
	}
	T& operator[] (int const &index) {
		if ((index < 0) || ((size_t)index >= size_)) {
			throw_IndexOutOfBoundsException(env_, "Index out of bounds");
			return dummy_;
		}
		return ptr_[index];
	}
	T const& operator[] (int const &index) const {
		if ((index < 0) || ((size_t)index >= size_)) {
			throw_IndexOutOfBoundsException(env_, "Index out of bounds");
			return dummy_;
		}
		return ptr_[index];
	}
	T* get() {
		return ptr_;
	}
	T const *get() const {
		return ptr_;
	}
	size_t size() const {
		return size_;
	}
	void abort() const {
		is_aborted_ = true;
	}
	bool is_copy() const {
		return is_copy_;
	}
	bool is_aborted() const {
		return is_aborted_;
	}
};

template <typename T> class safe_local_ref {
private:
	JNIEnv *env_;
	T ref_;
public:
	safe_local_ref(JNIEnv *env, T obj)
		: env_(env), ref_(obj) {
	}
	~safe_local_ref() {
		env_->DeleteLocalRef(ref_);
	}
	T get() const {
		return ref_;
	}
	bool operator == (T &opr) const {
		return ref_ == opr;
	}
	bool operator != (T &opr) const {
		return ref_ != opr;
	}
	bool operator ! () const {
		return !ref_;
	}
	friend bool operator == (T left, safe_local_ref<T> const &right) {
		return left == right.ref_;
	}
	friend bool operator != (T left, safe_local_ref<T> const &right) {
		return left != right.ref_;
	}
};

/*
 * Class:     crimsonwoods_android_libs_cameraimagedecoder_CameraImageDecoder
 * Method:    decodeNV21
 * Signature: ([I[BII)V
 */
JNIEXPORT void JNICALL Java_crimsonwoods_android_libs_cameraimagedecoder_CameraImageDecoder_decodeNV21
  (JNIEnv *env, jclass thiz, jintArray rgba, jbyteArray yu12, jint width, jint height)
{
	if (NULL == rgba) {
		throw_NullPointerException(env, "'rgba' have to be set not null.");
		return;
	}

	if (NULL == yu12) {
		throw_NullPointerException(env, "'yu12' have to be set not null.");
		return;
	}

	uint32_t const frame_size = width * height;

	safe_array<uint32_t> rgba_array(env, rgba);
	safe_array<uint8_t>  yu12_array(env, yu12);

	uint32_t y_ofst = 0;

	for (int32_t y = 0; y < height; ++y) {
		uint32_t uv_ofst = frame_size + (y >> 1) * width;
		int32_t u = 0, v = 0;
		for (int32_t x = 0; x < width; ++x) {
			int32_t y = clamp(yu12_array[y_ofst] - 16, 0, 255);
			if (0 == (x & 1)) {
				v = yu12_array[uv_ofst++] - 128;
				u = yu12_array[uv_ofst++] - 128;
			}
			if (JNI_FALSE != env->ExceptionCheck()) {
				rgba_array.abort();
				return;
			}
			int32_t const iy = 1192 * y;
			int32_t const r = clamp(iy + 1634 * v,            0, 262143);
			int32_t const g = clamp(iy -  833 * v -  400 * u, 0, 262143);
			int32_t const b = clamp(iy +    0 * v + 2066 * u, 0, 262143);
			rgba_array[y_ofst] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			++y_ofst;
		}
	}
}

/*
 * Class:     crimsonwoods_android_libs_cameraimagedecoder_CameraImageDecoder
 * Method:    decodeYUY2
 * Signature: ([I[BII)V
 */
JNIEXPORT void JNICALL Java_crimsonwoods_android_libs_cameraimagedecoder_CameraImageDecoder_decodeYUY2
  (JNIEnv *env, jclass thiz, jintArray rgba, jbyteArray yuy2, jint width, jint height)
{
	if (NULL == rgba) {
		throw_NullPointerException(env, "'rgba' have to be set not null.");
		return;
	}

	if (NULL == yuy2) {
		throw_NullPointerException(env, "'yuy2' have to be set not null.");
		return;
	}

	safe_array<uint32_t> rgba_array(env, rgba);
	safe_array<uint8_t>  yuy2_array(env, yuy2);

	uint32_t ofst = 0;

	for (int32_t y = 0; y < height; ++y) {
		int32_t u = 0, v = 0;
		for (int32_t x = 0; x < width; ++x) {
			uint32_t y_ofst = ofst << 1;
			int32_t y = clamp(yuy2_array[y_ofst] - 16, 0, 255);
			if (0 == (x & 1)) {
				u = yuy2_array[y_ofst + 1] - 128;
				v = yuy2_array[y_ofst + 3] - 128;
			}
			if (JNI_FALSE != env->ExceptionCheck()) {
				rgba_array.abort();
				return;
			}
			int32_t const iy = 1192 * y;
			int32_t const r = clamp(iy + 1634 * v,            0, 262143);
			int32_t const g = clamp(iy -  833 * v -  400 * u, 0, 262143);
			int32_t const b = clamp(iy +    0 * v + 2066 * u, 0, 262143);
			rgba_array[ofst] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			++ofst;
		}
	}
}

static inline int32_t clamp(int32_t const &value, int32_t const &min, int32_t const &max)
{
	return value < min ? min : value > max ? max : value;
}

static void throw_exception(JNIEnv *env, char const * const cls, char const * const message)
{
	safe_local_ref<jclass> ex_cls(env, env->FindClass(cls));
	if (!ex_cls) {
		return;
	}
	env->ThrowNew(ex_cls.get(), message);
}

static void throw_NullPointerException(JNIEnv *env, char const * const message)
{
	throw_exception(env, "java/lang/NullPointerException", message);
}

static void throw_IndexOutOfBoundsException(JNIEnv *env, char const * const message)
{
	throw_exception(env, "java/lang/IndexOutOfBoundsException", message);
}

