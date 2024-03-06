package ghost.flasg;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.w3c.dom.Document;
import java.util.concurrent.TimeUnit;

public class Ghost extends Activity {

	private static final int UPDATE_INTERVAL_MS = 1000;
	private static final String FORMAT_TIME = "hh:mm";
	private static final String FORMAT_DATE = "EEEE, dd MMMM yyyy";

	private CameraManager cameraManager;
	private String cameraId;
	private ImageView flashImageView;
	private boolean isFlashOn;

	private RelativeLayout mainLayout;
	private TextView timeTextView, dateTextView;
	private ScheduledExecutorService executorService;
	private Handler handler = new Handler(Looper.getMainLooper());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash);

		flashImageView = findViewById(R.id.flashImageView);
		mainLayout = findViewById(R.id.laymain);
		timeTextView = findViewById(R.id.time_txt);
		dateTextView = findViewById(R.id.other_txt);
		
		configureUI();
		initializeCamera();

		flashImageView.setOnTouchListener((v, event) -> {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				toggleFlash();
				return true;
			}
			return false;
		});

		executorService = Executors.newScheduledThreadPool(5);
		executorService.scheduleAtFixedRate(this::updateTime, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
		//executorService.scheduleAtFixedRate(this::check, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);

		setBa();

	}

	private void configureUI() {
		View decorView = getWindow().getDecorView();
		int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		decorView.setSystemUiVisibility(flags);
		getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
		getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent));
	}

	private void initializeCamera() {
		cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		try {
			if (cameraManager != null) {
				cameraId = cameraManager.getCameraIdList()[0];
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void toggleFlash() {
		try {
			if (isFlashOn)
				turnFlashOff();
			else
				turnFlashOn();
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void turnFlashOn() throws CameraAccessException {
		cameraManager.setTorchMode(cameraId, true);
		isFlashOn = true;
		setFlashImageResource(R.drawable.on);
	}

	private void turnFlashOff() throws CameraAccessException {
		cameraManager.setTorchMode(cameraId, false);
		isFlashOn = false;
		setFlashImageResource(R.drawable.off);
	}

	private void setFlashImageResource(int resourceId) {
		Drawable drawable = getResources().getDrawable(resourceId);
		flashImageView.setImageDrawable(drawable);
	}

	/*
	private void check() {
		boolean isFlashlightOn = Flash_Ghost.isFlashlightOn(getApplicationContext());
		if (isFlashlightOn) {
			isFlashOn = true;
			setFlashImageResource(R.drawable.on);
		} else {
			isFlashOn = false;
			setFlashImageResource(R.drawable.off);
		}
	}
	*/

	private void updateTime() {
		handler.post(() -> {
			timeTextView.setText(new SimpleDateFormat(FORMAT_TIME, Locale.getDefault()).format(new Date()));
			dateTextView.setText(new SimpleDateFormat(FORMAT_DATE, Locale.getDefault()).format(new Date()));
		});
	}

	
	private void setBa() {
		String currentTime = getCurrentTime();
		int backgroundResource = getBackgroundResource(currentTime);
		mainLayout.setBackgroundResource(backgroundResource);
	}

	private String getCurrentTime() {
		return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
	}

	private int getBackgroundResource(String currentTime) {
		try {
			String[][] timeRanges = { { "02:00", "04:00", "bg1" }, { "04:00", "10:00", "bg2" },
					{ "10:00", "16:00", "bg3" }, { "16:00", "18:30", "bg4" }, { "18:30", "24:00", "bg5" } };
			for (String[] range : timeRanges) {
				if (isBetween(currentTime, range[0], range[1])) {
					return getResourceId(range[2]);
				}
			}
		} catch (ParseException e) {
			handleException(e);
		}
		return getResourceId("bg2");
	}

	private boolean isBetween(String currentTime, String startTime, String endTime) throws ParseException {
		int current = compareTime(currentTime, startTime);
		int end = compareTime(currentTime, endTime);

		return current >= 0 && end < 0;
	}

	private int compareTime(String time1, String time2) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
		Date date1 = sdf.parse(time1);
		Date date2 = sdf.parse(time2);

		if (date1 != null && date2 != null)
			return date1.compareTo(date2);
		throw new ParseException("Error parsing date", 0);
	}

	private int getResourceId(String resourceName) {
		return getResources().getIdentifier(resourceName, "drawable", getPackageName());
	}

	private void handleException(ParseException e) {
		e.printStackTrace(); // Handle the exception, e.g., log or return a default value
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
	}

	
}

/*


*/
