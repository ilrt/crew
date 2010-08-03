#include "net_crew_0005fvre_codec_ffmpeg_FFMPEGCodec.h"
extern "C" {
#include <avcodec.h>
#include <avutil.h>
#include <swscale.h>
}
#include <map>

class FFMpegJ {
    private:
        static bool av_codec_initialized;

        std::map<int, CodecID> codecMap;
        int BUFFER_PROCESSED_OK;
        int BUFFER_PROCESSED_FAILED;
        int INPUT_BUFFER_NOT_CONSUMED;
        int OUTPUT_BUFFER_NOT_FILLED;

        jmethodID getDataMethod;
        jmethodID getOffsetMethod;
        jmethodID getLengthMethod;
        jmethodID setOffsetMethod;
        jmethodID setLengthMethod;
        jmethodID setTimestampMethod;
        jmethodID setSequenceNumberMethod;

        AVCodecContext *codecContext;
        AVCodec *codec;
        bool isEncoding;
        int pictureSize;
        AVFrame *frame;
        AVFrame *intermediateFrame;
        int frameFinished;
        int bytesProcessed;
        PixelFormat pixFmt;
        PixelFormat intermediatePixFmt;
        int width;
        int height;
        bool flipped;
        int frameCount;
        SwsContext *swScaleContext;
        bool swinit;
        uint8_t *buffer;

    public:
        FFMpegJ(JNIEnv *env, jobject peer);
        ~FFMpegJ();
        long openCodec(bool isEncoding, int codecId);
        bool init(int pixFmt, int width, int height, int intermediatePixFmt,
            int intermediateWidth, int intermediateHeight, bool flipped);
        int decode(JNIEnv *env, jobject input, jobject output);
        int encode(JNIEnv *env, jobject input, jobject output);
        bool closeCodec();
        int getOutputSize();
};
