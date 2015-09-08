package jp.co.ifl;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.content.Context;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;

public class TestCameraView extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback{
    private SurfaceHolder mHolder; //ホルダー
    private Camera mCamera;
    private int width;
    private int height;
    private static final int PORT=4680;
    private static final String IP_ADDR ="192.168.0.9";
    private byte[] mFrameBuffer;
    private Context con;
    public TestCameraView(Context context,Camera camera){
       super(context);
       con=context;
       mCamera=camera;
       mHolder=getHolder();  
       mHolder.addCallback(this); 
       mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public void surfaceCreated(SurfaceHolder holder) {
       try {
             mCamera.setPreviewDisplay(holder);
       } catch (Exception e) {
       }
    }
/*
 *surfaceDestroy
 *destruct Camera obejct.
 */
    public void surfaceDestroyed(SurfaceHolder holder) {
              mCamera.setPreviewCallback(null);
              mCamera.release();
              mCamera=null;
    }

/*
 * surfaceChange 
 * stream start point 
 * At first,CameraPreview is Stop,then Camera parameter sets
 * Next,settle preview callback to invoke onPreviewFrame method
 * and Starting preview on surface. 
 */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
       try{
             mCamera.stopPreview();
       } catch (Exception e){
       }
       try{
       //Configration Camera Parameter(full-size)
       Camera.Parameters parameters = mCamera.getParameters();
       this.width=parameters.getPreviewSize().width;
       this.height=parameters.getPreviewSize().height;
       parameters.setPreviewFormat(ImageFormat.NV21);
       mCamera.setParameters(parameters);
       mCamera.setPreviewCallback(this);
       mCamera.startPreview();
      }catch(Exception e){
      }
   }


public void onPreviewFrame(byte[] data,Camera camera){
      try{
        //convert YuvImage(NV21) to JPEG Image data
         YuvImage yuvimage=new YuvImage(data,ImageFormat.NV21,this.width,this.height,null);
         ByteArrayOutputStream baos=new ByteArrayOutputStream();
         yuvimage.compressToJpeg(new Rect(0,0,this.width,this.height),100,baos);
         byte[] jdata =baos.toByteArray();
         //send JPEG Image data(frame) to PC(Image player)
         sendData(jdata);
      }catch(Exception e){
      }
}

/*
 * sendData 
 * establish communication endpoint.
 * and send JPEG-frame
 */


private void sendData(byte[] data){
        Socket socket;
        BufferedOutputStream out;
        try{
          socket = new Socket(IP_ADDR,PORT);
          out=new BufferedOutputStream(socket.getOutputStream());
          out.write(data);
          if(out!=null) out.close();
          if(socket!=null) socket.close();
        }catch(Exception e){
        }
   }
}

