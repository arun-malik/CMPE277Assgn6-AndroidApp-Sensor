package cmpe277.assgn6.sensor;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cmpe277.assgn6.utility.Constants;

public class Track extends Activity {

	private SensorManager sensorManager;
	private String ACTIVITY_NAME = "Track";
	private Sensor sensorStepDetector;
	private SensorStepDetectorListner ssdObj;
	private ToggleButton tglTorch;
	private TextView txtSteps;
	private Camera cameraHandler;
	private Parameters camParameters;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track);

		Log.i(ACTIVITY_NAME,Constants.ON_CREATE);

		tglTorch = (ToggleButton) findViewById(R.id.tglTorch);
		txtSteps = (TextView) findViewById(R.id.txtSteps);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

		if(null == sensorStepDetector){
			Toast.makeText(Track.this, "Step Detector Sensor is not available on this Phone", Toast.LENGTH_LONG).show();
		}

		tglTorch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@SuppressLint("NewApi")
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				boolean isTorchAvailable= getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
				boolean isFlashAvailable= getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
				
				if(isTorchAvailable && isFlashAvailable) {
					if(tglTorch.isChecked()){
						
						try {
							cameraHandler = Camera.open();
							camParameters = cameraHandler.getParameters();
							cameraHandler.setPreviewTexture(new SurfaceTexture(0));
							cameraHandler.startPreview();
							camParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
							cameraHandler.setParameters(camParameters);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					else
					{
						camParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
						cameraHandler.setParameters(camParameters);
						cameraHandler.stopPreview();
						cameraHandler.release();
					}
				} else {
					Toast.makeText(Track.this, "Camera / Flash is not available in this Phone", Toast.LENGTH_LONG).show();;
					return;
				}
			}
		});



	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(ACTIVITY_NAME,Constants.ON_START);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(ACTIVITY_NAME,Constants.ON_RESTART);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(ACTIVITY_NAME,Constants.ON_RESUME);
		ssdObj = new SensorStepDetectorListner();

		sensorManager.registerListener(ssdObj, sensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(ACTIVITY_NAME,Constants.ON_PAUSE);

		sensorManager.unregisterListener(ssdObj);
		
		if (cameraHandler != null) {
			cameraHandler.release();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(ACTIVITY_NAME,Constants.ON_STOP);
		
		if (cameraHandler != null) {
			cameraHandler.release();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(ACTIVITY_NAME,Constants.ON_DESTROY);
	}

	public class SensorStepDetectorListner implements SensorEventListener
	{
		private  int stepsCount =0 ;

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.values[0] == 1.0f) {
				stepsCount++;
				txtSteps.setText(Integer.toString(stepsCount));
				if(stepsCount == 20) { 
					tglTorch.setChecked(true);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

	}

}
