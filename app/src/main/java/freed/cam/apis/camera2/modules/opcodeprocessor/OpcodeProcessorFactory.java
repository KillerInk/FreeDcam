package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;

@RequiresApi(api = Build.VERSION_CODES.N)
public class OpcodeProcessorFactory {

    public static BaseOpcodeProcessor getOpCodeProcessor(OpCodes opCodes, CaptureSessionHandler captureSessionHandler)
    {
        switch (opCodes)
        {
            case xiaomi_supereis:
            case xiaomi_supereispro:
                return new XiaomiEisOpcodeProcessor(captureSessionHandler,opCodes);

            case eis_realtime:
            case eis_lookahead:
                return new QcomEisOpcodeProcessor(captureSessionHandler,opCodes);

            case xiaomi_hdr10:
                return new XiaomiHdr10Processor(captureSessionHandler,opCodes);

           /* case lg_hdr10_steady:
                return new LgHdrSteadyOpcodeProcessor(captureSessionHandler,opCodes);*/

            default:
                return new BaseOpcodeProcessor(captureSessionHandler,opCodes) {
                    @Override
                    public void applyOpCodeToSession() {

                    }

                    @Override
                    public void prepareRecording() {

                    }

                    @Override
                    public void startRecording() {

                    }

                    @Override
                    public void stopRecording() {

                    }
                };
        }
    }
}
