package com.example.waveaxis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

public class Home extends Activity {

	TextView timer;
	TransparentProgressDialog db;
	TextView problem_name;
	Spinner problem_spinner;

	Boolean istimerStart = false;

	ArrayAdapter<String> dataAdapter1;
	ArrayAdapter<String> dataAdapter;
	Boolean isConnected;
	TextView machine_id;
	Button fixed;
	int timerCOUNT = 1;
	Boolean isProblemSelected = false;
	LinearLayout linearlayout1, linearlayout2, linearlayout3;
	LinearLayout layout_orientation;
	LinearLayout update_layout;
	LinearLayout scrollview_weight;
	RelativeLayout timer_weight;
	Boolean isDialogAlreadyShown = false;
	Boolean isTablet = false;
	int orientation = 1;
	long timeInMillisUnit;
	String problemNAME = "";

	EditText update_value;
	Button update_button;
	Boolean isMachineModuleFirstTime = false;

	TextView icpk_value, oee_value;

	// *****************STart time *****************//
	private Handler customHandler = new Handler();
	private long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;

	protected void showDialog(String msg) {
		final Dialog dialog;
		dialog = new Dialog(Home.this);
		dialog.setCancelable(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(0);
		dialog.getWindow().setBackgroundDrawable(d);

		Button ok;
		TextView message;

		dialog.setContentView(R.layout.dialog);
		ok = (Button) dialog.findViewById(R.id.ok);
		message = (TextView) dialog.findViewById(R.id.message);

		message.setText(msg);

		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		dialog.show();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.home);
		timer = (TextView) findViewById(R.id.timer);

		problem_spinner = (Spinner) findViewById(R.id.problem_spinner);
		fixed = (Button) findViewById(R.id.fixed);
		machine_id = (TextView) findViewById(R.id.machine_id);
		linearlayout1 = (LinearLayout) findViewById(R.id.linearlayout1);
		linearlayout2 = (LinearLayout) findViewById(R.id.linearlayout2);
		linearlayout3 = (LinearLayout) findViewById(R.id.linearlayout3);
		scrollview_weight = (LinearLayout) findViewById(R.id.scrollview_weight);
		update_value = (EditText) findViewById(R.id.update_value);
		update_button = (Button) findViewById(R.id.update_button);
		timer_weight = (RelativeLayout) findViewById(R.id.timer_weight);
		layout_orientation = (LinearLayout) findViewById(R.id.layout_orientation);
		problem_name = (TextView) findViewById(R.id.problem_name);
		icpk_value = (TextView) findViewById(R.id.icpk_value);
		oee_value = (TextView) findViewById(R.id.oee_value);
		update_layout = (LinearLayout) findViewById(R.id.update_layout);
		isConnected = NetConnection.checkInternetConnectionn(Home.this);

		isTablet = isTablet(getApplicationContext());

		orientation = getResources().getConfiguration().orientation;
		Log.e("orientaiton ==>>", "*******" + orientation + "**************");

		if (orientation == 1) {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		} else if (orientation == 2) {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		// int secs = (int) (12442100 / 1000);
		// int mins = secs / 60;
		//
		// Log.e("updatedTime===",""+updatedTime);
		// Log.e("mins===",""+secs / 60);
		// Log.e("hr===",""+mins / 60);
		//
		// secs = secs % 60;
		//
		// Log.e("mins===",""+secs / 60);
		// Log.e("secs====",""+secs);

		if (isConnected) {
			String m_androidId = Secure.getString(getContentResolver(),
					Secure.ANDROID_ID);
			String model = android.os.Build.MODEL;
			Log.i("m_androidId===", "" + m_androidId + " model==" + model);

			new addDevice(m_androidId, model).execute(new Void[0]);
		} else {
			showDialog("No internet connection.");
		}

		/**
		 * check if time value gets equal to timer specified from backend.
		 */
		final Handler localHandler = new Handler();
		localHandler.postDelayed(new Runnable() {
			public void run() {

				if (Constants.TIMER != null) {
//					if(!timer.getText().toString().equals("00:00:00")){
					if (timer.getText().toString().equals(Constants.TIMER)) {
						new pendingIssue(Constants.ISSUE_ID)
								.execute(new Void[0]);
					}
//					}
				}
				localHandler.postDelayed(this, 1000L);
			}
		}, 1000L);

		/*******
		 * to automatically update Problem spinner
		 */
		final Handler localHandler1 = new Handler();
		localHandler1.postDelayed(new Runnable() {
			public void run() {

				isMachineModuleFirstTime = true;
				new getMachineModule(Constants.DEVICE_ID).execute(new Void[0]);

				localHandler1.postDelayed(this, 20000L);
			}
		}, 20000L);

		problem_spinner.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (istimerStart) {
					Toast.makeText(getApplicationContext(),
							"Please wait until time gets stopped.",
							Toast.LENGTH_SHORT).show();
					return true;
				} else {
					return false;
				}
			}
		});

		update_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String moduleValue = update_value.getText().toString();
				String value = moduleValue;
				try {
					int valueNum = Integer.parseInt(value);

					if (problemNAME.contains("OEE")) {
						Constants.OEE_VALUE = value;
					} else if (problemNAME.contains("CPK")) {
						Constants.ICPK_VALUE = value;
					}

					if (valueNum > 0) {
						new addModuleValue(Constants.MACHINE_ID,
								Constants.MODULE_ID, moduleValue, problemNAME)
								.execute(new Void[0]);
					} else if (valueNum == 0) {
						showDialog("Please enter valid value.");
					}
				} catch (Exception e) {
					showDialog("Please enter CPK/OEE value.");
				}
			}
		});

		problem_spinner.post(new Runnable() {
			public void run() {
				problem_spinner
						.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								Log.i("problem spinner run",
										"problme spinner run");
								Log.i("position==", "" + position);
								if (position > 0) {
									if (!istimerStart) {
										isProblemSelected = true;
										problemNAME = Constants.issue_list.get(
												position - 1)
												.get("module_name");
										Constants.TIMER = Constants.issue_list
												.get(position - 1).get("timer");
										Constants.TIMERINMILLIS = ConvertToMillis(Constants.TIMER);
										timeInMillisUnit = ConvertToMillis(Constants.TIMER);
										Constants.MODULE_ID = Constants.issue_list
												.get(position - 1).get(
														"module_id");
										Constants.MODULE_NAME = Constants.issue_list
												.get(position - 1).get(
														"module_name");
										Constants.MACHINE_ID = Constants.issue_list
												.get(position - 1).get(
														"machine_id");
										Constants.MACHINE_NAME = Constants.issue_list
												.get(position - 1).get(
														"machine_name");
										Constants.PROBLEM_SPINNER_POSITION = position;

										problem_name
												.setText(Constants.issue_list
														.get(position - 1).get(
																"module_name"));

										new addIssue(Constants.MACHINE_ID,
												Constants.MODULE_ID,
												Constants.MODULE_NAME,
												Constants.TIMER)
												.execute(new Void[0]);

									} else {

										showDialog("Timer is already started.Please wait until timer gets stopped.");
									}
								} else {

								}

							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {

							}
						});
			}
		});

		fixed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {

					customHandler.removeCallbacks(updateTimerThread);
					String getTIME = timer.getText().toString();
					long getTimeInMillis = ConvertToMillis(getTIME);

					// if (getTIME.equalsIgnoreCase("00:00:00")) {
					if (isProblemSelected) {

						new updateIssue(Constants.ISSUE_ID, getTIME)
								.execute(new Void[0]);
					} else {
						showDialog("You cannot submit the issue without selecting the problem.");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * convert to millis
	 */
	public long ConvertToMillis(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		String inputString = time;
		long timeinMilis = 0;

		Date date;
		try {
			date = sdf.parse(inputString);
			System.out.println("in milliseconds: " + date.getTime());
			timeinMilis = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeinMilis;

	}

	/**
	 * convert to 00:00:00 format
	 */

	public String ConvertToTimeFormat(long time) {
		long millis = time;
		String hms = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(millis)));
		System.out.println(hms);
		return hms;
	}

	/**
	 * add Device to the database to get Machine ID.
	 * 
	 * @param devicetoken
	 *            token or unique id of the device
	 * @param devicename
	 *            device name
	 * 
	 */

	public class addDevice extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();
		String deviceID, model_name;
		ArrayList localArrayList = new ArrayList();

		public addDevice(String m_androidId, String model) {
			deviceID = m_androidId;
			model_name = model;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("devicetoken",
						deviceID));
				localArrayList.add(new BasicNameValuePair("devicename",
						model_name));
				result = function.addDevice(localArrayList);

				Log.e("result item lit==", "" + result);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();
			try {
				if (result.get("Response").equals("true")) {
					Toast.makeText(getApplicationContext(),
							(String) result.get("MessageWhatHappen"),
							Toast.LENGTH_SHORT).show();
					Constants.DEVICE_ID = (String) result.get("deviceid");
					if (isConnected) {

						new getMachineModule(Constants.DEVICE_ID)
								.execute(new Void[0]);
					} else {
						showDialog("No internet connection.");
					}
				} else {
					Toast.makeText(getApplicationContext(),
							(String) result.get("MessageWhatHappen"),
							Toast.LENGTH_SHORT).show();

				}
			} catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
				ae.printStackTrace();
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Home.this,
					R.drawable.loading_waveaxis);
			db.show();
		}

	}

	/**
	 * to get machine module
	 */
	public class getMachineModule extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		String deviceID;
		ArrayList localArrayList = new ArrayList();

		public getMachineModule(String device_id) {
			deviceID = device_id;

		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList
						.add(new BasicNameValuePair("deviceid", deviceID));

				result = function.MachineModule(localArrayList);

				Log.e("result ==", "" + result);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			if (!isMachineModuleFirstTime) {
				db.dismiss();
			}
			try {

				if (result.size() > 0) {

					Constants.issue_list.clear();
					Constants.issue_list.addAll(result);
					String machine_id_ofDevice = Constants.issue_list.get(0)
							.get("machine_id");
					String machine_name = Constants.issue_list.get(0).get(
							"machine_name");
					// machine_id.setText(machine_name + " (ID : "
					// + machine_id_ofDevice + ")");

					machine_id.setText(machine_name);

					String[] list = new String[Constants.issue_list.size() + 1];

					list[0] = "Select a problem";

					for (int i = 0; i < Constants.issue_list.size(); i++) {
						list[i + 1] = Constants.issue_list.get(i).get(
								"module_name");

						if (Constants.issue_list.get(i).get("module_name")
								.equals("OEE Shift")) {
							Constants.OEE_VALUE = Constants.issue_list.get(i)
									.get("timer");
						} else if (Constants.issue_list.get(i)
								.get("module_name").equals("CPK")) {
							Constants.ICPK_VALUE = Constants.issue_list.get(i)
									.get("timer");
						}
					}

					icpk_value.setText("CPK : " + Constants.ICPK_VALUE);
					oee_value.setText("OEE : " + Constants.OEE_VALUE);

					dataAdapter = new ArrayAdapter<String>(Home.this,
							R.layout.problem_spinner_item, R.id.text, list);

					problem_spinner.setAdapter(dataAdapter);

				} else {
					String[] list = new String[] { "Select a problem" };
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
							Home.this, R.layout.problem_spinner_item,
							R.id.text, list);

					problem_spinner.setAdapter(dataAdapter);

					Toast.makeText(getApplicationContext(),
							"no issue list found", Toast.LENGTH_SHORT).show();
					if (!isMachineModuleFirstTime) {
						showDialog("No machine or module is assigned to this device from the backend.Please contact Admin and launch the app again.");
					}
				}
			} catch (Exception ae) {
				if (!isMachineModuleFirstTime) {
					// showDialog(Constants.ERROR_MSG);
					showDialog("No machine or module is assigned to this device from the backend.Please contact Admin and launch the app again.");
				}
				ae.printStackTrace();
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			if (!isMachineModuleFirstTime) {
				db = new TransparentProgressDialog(Home.this,
						R.drawable.loading_waveaxis);
				db.show();
			}
		}

	}

	/**
	 * API to addIssue
	 * 
	 * 
	 */

	public class addIssue extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();
		ArrayList localArrayList = new ArrayList();
		String machineID, moduleID, moduleNAME;
		String timerVAL;

		public addIssue(String mACHINE_ID, String mODULE_ID,
				String mODULE_NAME, String timer) {
			machineID = mACHINE_ID;
			moduleID = mODULE_ID;
			moduleNAME = mODULE_NAME;
			timerVAL = timer;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("machineid",
						machineID));
				localArrayList
						.add(new BasicNameValuePair("moduleid", moduleID));
				localArrayList.add(new BasicNameValuePair("issuedesc", ""));

				result = function.addIssue(localArrayList);

				Log.e("result ==", "" + result);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();
			try {
				if (result.get("response").equals("true")) {
					Constants.ISSUE_ID = (String) result.get("issue_id");

					// startTimer(Constants.TIMERINMILLIS);
					istimerStart = true;
					Log.e("Problme NAME ======", "" + problemNAME);
					if (problemNAME.contains("CPK")) {
						update_layout.setVisibility(View.VISIBLE);
					} else if (problemNAME.contains("OEE Shift")) {
						update_layout.setVisibility(View.VISIBLE);
					} else {
						if (timerVAL.equals("0") || timerVAL.equals("00:00:00")) {
							showDialog("The Timer value has not been set for this problem, please ask Admin to set it up.");
						} else {
							startTime = SystemClock.uptimeMillis();
							customHandler.postDelayed(updateTimerThread, 0);
						}
					}

					linearlayout1.setVisibility(View.GONE);
					linearlayout2.setVisibility(View.GONE);
					linearlayout3.setVisibility(View.VISIBLE);

					// if (isTablet) {
					// timer_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 3f));
					// scrollview_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 4f));
					// timer.setTextSize(100);
					// layout_orientation
					// .setOrientation(LinearLayout.VERTICAL);
					// // set margins
					// LinearLayout.LayoutParams params = new
					// LinearLayout.LayoutParams(
					// LayoutParams.WRAP_CONTENT,
					// LayoutParams.WRAP_CONTENT);
					// params.setMargins(0, 10, 0, 0);
					// timer.setLayoutParams(params);
					// } else {
					// timer_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 2.6f));
					// scrollview_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 4.5f));
					// timer.setTextSize(65);
					// layout_orientation
					// .setOrientation(LinearLayout.VERTICAL);
					// // set margins
					// LinearLayout.LayoutParams params = new
					// LinearLayout.LayoutParams(
					// LayoutParams.WRAP_CONTENT,
					// LayoutParams.WRAP_CONTENT);
					// params.setMargins(0, 20, 0, 0);
					// timer.setLayoutParams(params);
					// }

				}
			} catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
				Toast.makeText(getApplicationContext(), "issue not added",
						Toast.LENGTH_SHORT).show();
				ae.printStackTrace();
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();

			db = new TransparentProgressDialog(Home.this,
					R.drawable.loading_waveaxis);
			db.show();
		}

	}

	/**
	 * to updateIssue
	 * 
	 * 
	 */

	public class updateIssue extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();
		ArrayList localArrayList = new ArrayList();
		String issueID, timeToSolve;

		public updateIssue(String iSSUE_ID, String tIMETOSOLVEStringFormat) {
			issueID = iSSUE_ID;
			timeToSolve = tIMETOSOLVEStringFormat;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("issueid", issueID));
				localArrayList.add(new BasicNameValuePair("timetosolve",
						timeToSolve));

				result = function.updateIssue(localArrayList);

				Log.e("result ==", "" + result);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("response").equals("true")) {

					problem_spinner.setSelection(0, false);
					isProblemSelected = false;
					istimerStart = false;

					showDialog((String) result.get("MessageWhatHappen"));
					timer.setText("00:00:00");
					linearlayout1.setVisibility(View.VISIBLE);
					linearlayout2.setVisibility(View.VISIBLE);
					linearlayout3.setVisibility(View.INVISIBLE);
					try {
						update_layout.setVisibility(View.INVISIBLE);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// if (isTablet(getApplicationContext())) {
					// timer_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 2f));
					// scrollview_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 5.4f));
					// timer.setTextSize(90);
					//
					// layout_orientation
					// .setOrientation(LinearLayout.HORIZONTAL);
					// // set margins
					// LinearLayout.LayoutParams params = new
					// LinearLayout.LayoutParams(
					// LayoutParams.WRAP_CONTENT,
					// LayoutParams.WRAP_CONTENT);
					// params.setMargins(20, 0, 0, 0);
					// timer.setLayoutParams(params);
					// } else {
					// timer_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 1.6f));
					// scrollview_weight
					// .setLayoutParams(new LinearLayout.LayoutParams(
					// LayoutParams.FILL_PARENT, 0, 5.9f));
					// timer.setTextSize(45);
					// layout_orientation
					// .setOrientation(LinearLayout.HORIZONTAL);
					//
					// // set margins
					// LinearLayout.LayoutParams params = new
					// LinearLayout.LayoutParams(
					// LayoutParams.WRAP_CONTENT,
					// LayoutParams.WRAP_CONTENT);
					// params.setMargins(20, 0, 0, 0);
					// timer.setLayoutParams(params);
					// }
				} else {
					Toast.makeText(getApplicationContext(),
							"Issue not updated.", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
				Toast.makeText(getApplicationContext(), "issue not updated",
						Toast.LENGTH_SHORT).show();
				ae.printStackTrace();
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Home.this,
					R.drawable.loading_waveaxis);
			db.show();
		}

	}

	public class pendingIssue extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();
		ArrayList localArrayList = new ArrayList();
		String issueID, timeToSolve;

		public pendingIssue(String iSSUE_ID) {
			issueID = iSSUE_ID;

		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("issueid", issueID));

				result = function.pendingIssue(localArrayList);

				Log.e("result ==", "" + result);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("response").equals("true")) {
					// timerCOUNT++;

					Constants.TIMERINMILLIS = timeInMillisUnit * timerCOUNT;
					Constants.TIMER = ConvertToTimeFormat(Constants.TIMERINMILLIS);

					Log.i("timerCount==", "" + timerCOUNT);
					Log.i("Constants.TIMERINMILLIS==", ""
							+ Constants.TIMERINMILLIS);
					Log.i("Constants.TIMER==", "" + Constants.TIMER);
				}
			} catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
				Toast.makeText(getApplicationContext(), "issue not updated",
						Toast.LENGTH_SHORT).show();
				ae.printStackTrace();
				timerCOUNT++;

				Constants.TIMERINMILLIS = timeInMillisUnit * timerCOUNT;
				Constants.TIMER = ConvertToTimeFormat(Constants.TIMERINMILLIS);

				Log.i("timerCount==", "" + timerCOUNT);
				Log.i("Constants.TIMERINMILLIS==", "" + Constants.TIMERINMILLIS);
				Log.i("Constants.TIMER==", "" + Constants.TIMER);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Home.this,
					R.drawable.loading_waveaxis);
			db.show();
		}

	}

	public class addModuleValue extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();
		ArrayList localArrayList = new ArrayList();

		String macineID, moduleID, moduleVALUE, problem_name;

		public addModuleValue(String mACHINE_ID, String mODULE_ID,
				String moduleValue, String problemNAME) {
			this.macineID = mACHINE_ID;
			this.moduleID = mODULE_ID;
			this.moduleVALUE = moduleValue;
			this.problem_name = problemNAME;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("machine_id",
						this.macineID));
				localArrayList.add(new BasicNameValuePair("module_id",
						this.moduleID));
				localArrayList.add(new BasicNameValuePair("module_value",
						this.moduleVALUE));

				result = function.addModuleValue(localArrayList);

				Log.e("result ==", "" + result);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("response").equals("true")) {

					istimerStart = false;
					isProblemSelected = false;

					if (problem_name.contains("OEE")) {
						Constants.OEE_VALUE = moduleVALUE;
					} else if (problem_name.contains("CPK")) {
						Constants.ICPK_VALUE = moduleVALUE;
					}

					icpk_value.setText("CPK : " + Constants.ICPK_VALUE);
					oee_value.setText("OEE : " + Constants.OEE_VALUE);

					// update_layout.setVisibility(View.INVISIBLE);
					update_value.setText("");
					linearlayout1.setVisibility(View.VISIBLE);
					linearlayout2.setVisibility(View.VISIBLE);
					linearlayout3.setVisibility(View.INVISIBLE);
					try {
						update_layout.setVisibility(View.INVISIBLE);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
				Toast.makeText(getApplicationContext(), "value not updated",
						Toast.LENGTH_SHORT).show();
				ae.printStackTrace();
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Home.this,
					R.drawable.loading_waveaxis);
			db.show();
		}

	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	@Override
	public void onBackPressed() {

		showLogoutDialog("Are you sure you want to exit from the app.");
	}

	private void showLogoutDialog(String msg) {
		final Dialog dialog;
		dialog = new Dialog(Home.this);
		dialog.setCancelable(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(0);
		dialog.getWindow().setBackgroundDrawable(d);

		Button yes, no;
		TextView message;

		dialog.setContentView(R.layout.exitdialog);
		yes = (Button) dialog.findViewById(R.id.yes);
		no = (Button) dialog.findViewById(R.id.no);
		message = (TextView) dialog.findViewById(R.id.message);

		message.setText(msg);

		no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.show();
	}

	Runnable updateTimerThread = new Runnable() {

		public void run() {
			//
			// timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			// updatedTime = timeSwapBuff + timeInMilliseconds;

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeSwapBuff + timeInMilliseconds;

			int mins = (int) ((updatedTime / (1000 * 60)) % 60);
			int hr = (int) ((updatedTime / (1000 * 60 * 60)) % 24);
			int secs = (int) (updatedTime / 1000);
			// int mins = secs / 60;
			// int hr = mins / 60;

			int milliseconds = (int) (updatedTime % 1000);

			secs = secs % 60;

			timer.setText("" + String.format("%02d", hr) + ":"
					+ String.format("%02d", mins) + ":"
					+ String.format("%02d", secs));
			customHandler.postDelayed(this, 0);
		}

	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			orientation = 2;
			// Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			orientation = 1;
			// Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}
}