package com.example.functions;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;

public class Functions {

	JSONParser json = new JSONParser();

	//public static String url = "http://phphosting.osvin.net/waveaxis/Web_API/";
	public static String url = "http://pal.waveaxis.com/Web_API/";
	

	/**
	 * API to addDevice to backend
	 * @param localArrayList
	 * @return
	 */

	public HashMap addDevice(ArrayList localArrayList) {
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		@SuppressWarnings("rawtypes")
		HashMap localHashMap = new HashMap();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "addDevice.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("Response");

			if (resopnse.equalsIgnoreCase("true")) {
				JSONObject getDATA = localJSONObject.getJSONObject("GetData");
				localHashMap.put("Response", "true");
				localHashMap.put("MessageWhatHappen",
						localJSONObject.getString("MessageWhatHappen"));
				localHashMap.put("machineid", getDATA.getString("machineid"));
				localHashMap.put("deviceid", getDATA.getString("deviceid"));

			} else {
				localHashMap.put("Response", "false");
				localHashMap.put("MessageWhatHappen",
						localJSONObject.getString("MessageWhatHappen"));
			}
			return localHashMap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localHashMap;

		}

	}
	
	/**
	 * to get machine details and issue list
	 * @param localArrayList
	 * @return
	 */

	public ArrayList<HashMap<String, String>> MachineModule(
			ArrayList localArrayList) {

		@SuppressWarnings("rawtypes")
		ArrayList<HashMap<String, String>> locallist = new ArrayList<HashMap<String, String>>();
		try {
			String cpkValue;
			String oeeValue;
			
			
			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "getMachinenModule.php?",
							"POST", localArrayList)).toString());

			String resopnse = localJSONObject.getString("Response");
			
		
			JSONArray getData = localJSONObject.getJSONArray("GetData");
			if (resopnse.equalsIgnoreCase("true")) {
				for (int i = 0; i < getData.length(); i++) {
					HashMap localHashMap = new HashMap();
					localHashMap.put("machine_id", getData.getJSONObject(i)
							.get("machine_id"));

					localHashMap.put("machine_name", getData.getJSONObject(i)
							.get("machine_name"));
					localHashMap.put("module_id",
							getData.getJSONObject(i).get("module_id"));

					localHashMap.put("module_name", getData.getJSONObject(i)
							.get("module_name"));
					localHashMap.put("timer",
							getData.getJSONObject(i).get("timer"));

					locallist.add(localHashMap);
				}
				
			}

			return locallist;

		} catch (Exception ae) {
			ae.printStackTrace();
			return locallist;

		}

	}
	
	/**
	 * to add issue on backend side
	 * @param localArrayList
	 * @return
	 */

	public HashMap addIssue(ArrayList localArrayList) {

		@SuppressWarnings("rawtypes")
		HashMap localhashmap = new HashMap();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "addIssue.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("Response");
			if(resopnse.equalsIgnoreCase("true")){
				localhashmap.put("response", "true");
				localhashmap.put("issue_id",localJSONObject.getString("GetData"));
			} else if(resopnse.equalsIgnoreCase("false")){
				localhashmap.put("response", "false");
			}
			
			return localhashmap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localhashmap;

		}

	}
	
	/**
	 *  to updateIssue details
	 * @param localArrayList
	 * @return
	 */
	
	public HashMap updateIssue(ArrayList localArrayList) {

		@SuppressWarnings("rawtypes")
		HashMap localhashmap = new HashMap();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "update_issueTime.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("ResponseCode");
			if(resopnse.equalsIgnoreCase("true")){
				localhashmap.put("response", "true");
				localhashmap.put("MessageWhatHappen",localJSONObject.getString("MessageWhatHappen"));
			} else if(resopnse.equalsIgnoreCase("false")){
				localhashmap.put("response", "false");
				localhashmap.put("MessageWhatHappen",localJSONObject.getString("MessageWhatHappen"));
			}
			
			return localhashmap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localhashmap;

		}

	}
	
	/**
	 * to update pending issuee detail
	 * @param localArrayList
	 * @return
	 */
	
	public HashMap pendingIssue(ArrayList localArrayList) {

		@SuppressWarnings("rawtypes")
		HashMap localhashmap = new HashMap();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "still_pendingIssue.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("ResponseCode");
			if(resopnse.equalsIgnoreCase("true")){
				localhashmap.put("response", "true");
				
			} else if(resopnse.equalsIgnoreCase("false")){
				localhashmap.put("response", "false");
			}
			
			return localhashmap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localhashmap;

		}

	}
	
	public HashMap addModuleValue(ArrayList localArrayList) {

		@SuppressWarnings("rawtypes")
		HashMap localhashmap = new HashMap();
		try {

			JSONObject localJSONObject = new JSONObject(Html.fromHtml(
					this.json.makeHttpRequest(url + "fixedmodule.php?", "POST",
							localArrayList)).toString());

			String resopnse = localJSONObject.getString("Response");
			if(resopnse.equalsIgnoreCase("true")){
				localhashmap.put("response", "true");
				
			} else if(resopnse.equalsIgnoreCase("false")){
				localhashmap.put("response", "false");
			}
			
			return localhashmap;

		} catch (Exception ae) {
			ae.printStackTrace();
			return localhashmap;

		}

	}

}
