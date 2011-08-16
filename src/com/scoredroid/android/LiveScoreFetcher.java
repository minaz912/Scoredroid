package com.scoredroid.android;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.util.Log;

/**
 * 
 * @author burak
 *
 */
public class LiveScoreFetcher {

	private static HttpClient httpClient;
	private static BasicResponseHandler handler;
	private static HttpGet httpGet;
	private static MediaPlayer player;
	
	public static MatchResult getLiveScore(String home,String away){
		
		String url = "http://178.79.156.64:8080/Livescore/home.html?home="+URLEncoder.encode(home)+"&away="+URLEncoder.encode(away);
		
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		httpGet = new HttpGet(url);
		
		try {
			
			String response = httpClient.execute(httpGet,handler);
			Log.d("SCORODROID", response);
			
			JSONObject musicData = new JSONObject(response);
			String id = musicData.getString("id");
			
			if(id.equals("-1")){
				//no match found
				return null;
			}else{
				
				String homeTeam = musicData.getString("homeTeam");
				String awayTeam = musicData.getString("awayTeam");
				String livescore = musicData.getString("score");
				
				return new MatchResult(Long.parseLong(id), homeTeam, awayTeam, livescore);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
}
