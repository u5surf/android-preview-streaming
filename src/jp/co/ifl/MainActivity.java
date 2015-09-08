package jp.co.ifl;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;  
import android.view.WindowManager;
import android.hardware.Camera;
import android.widget.FrameLayout;

public class MainActivity extends Activity{
    private Camera mCamera;
    private TestCameraView mPreview;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCamera = getCameraInstance();
        mPreview = new TestCameraView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
     }

     public static Camera getCameraInstance()
     {
        Camera c=null;
        try{
          c=Camera.open();
        }catch(Exception e){
        }
        return c;
     }
}

