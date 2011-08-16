package com.scoredroid.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SpeechInputActivity extends Activity implements android.view.View.OnClickListener,OnItemClickListener,OnInitListener{
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private ListView mList;
    private Button speakButton;
    private TextView homeText;
    private TextView awayText;
    private Button searchButton;
    private boolean changer = true;
    private ProgressDialog loadingDialog;
    private MatchResult resultMatch;
    private TextToSpeech ttsEng;
    
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//title bar must be set before setting content layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        super.onCreate(savedInstanceState);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.voice_recognition);

        // Get display items for later interaction
        speakButton = (Button) findViewById(R.id.btn_speak);
        homeText = (TextView) findViewById(R.id.homeText);
        awayText = (TextView) findViewById(R.id.awayText);
        searchButton = (Button) findViewById(R.id.btn_search);
        searchButton.setOnClickListener(this);
        mList = (ListView) findViewById(R.id.list);
        
        ttsEng = new TextToSpeech(this, this);
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak in English for team name");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    matches));
            mList.setOnItemClickListener(this);
           
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
	    return true;
    }

    /*
	 * if menu item selected do sth !!
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    
		
		// Handle item selection
	    switch (item.getItemId()) {
	    case R.id.clear: //this is refresh menu button
	    	
	    	homeText.setText("Home");	    	
	    	awayText.setText("Away");
	    	changer = true;
	        return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    
	    
	}
    
	@Override
	public void onClick(View v) {
		// TOspeakButton.setText("Press and say away team name");DO Auto-generated method stub
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_speak) {
			
            startVoiceRecognitionActivity();
        }else if(v.getId() == R.id.btn_search){
        	
        	//now go to server and fetch match scores
        	new GetMetadata().execute(null);
        }
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub

		loadingDialog = ProgressDialog.show(SpeechInputActivity.this, "", "Loading.Please wait...",true);
    	loadingDialog.setCancelable(true);
    	
		return loadingDialog;
    }
	
    class GetMetadata extends AsyncTask<ArrayList<String>, Void, String>{

    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		showDialog(1);
    	}
    	
		@Override
		protected String doInBackground(ArrayList<String>... params) {
				
			resultMatch = LiveScoreFetcher.getLiveScore(homeText.getText().toString(), awayText.getText().toString());
			return null;
		}
    	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			
			super.onPostExecute(result);
			loadingDialog.dismiss();
			sayResult(resultMatch);
		}
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String selection = (String) mList.getAdapter().getItem((int)arg3);

		//switch between textviews to set home and away teams
		if(changer){
			Log.d("SCORODROID",	 "Home team is : " + selection);
			homeText.setText(selection.toUpperCase());
			changer = false;
		}else{
			Log.d("SCORODROID",	 "Home team is : " + selection);
			awayText.setText(selection.toUpperCase());
			changer = true;
		}
	}

	public void sayResult(MatchResult result){
	
		if(result == null){
			//crap no match found with give team names
			ttsEng.speak("Match Not Found", TextToSpeech.QUEUE_FLUSH, null);
			
		}else{
			//match found now use tts engine to say the live score
			String [] scores = result.getScore().split("-");
			ttsEng.speak(result.getHomeTeam()+"  "+ scores[0] +"  "+
					result.getAwayTeam() +"  "+ scores[1], TextToSpeech.QUEUE_FLUSH, null);
			
		}
	}

	
	@Override
	public void onInit(int status) {
		
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = ttsEng.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               // Lanuage data is missing or the language is not supported.
                Log.e("SCORODROID", "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.

                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                // Greet the user.
            	Log.e("SCORODROID", "Language is available.");
            }
        } else {
            // Initialization failed.
            Log.e("SCORODROID", "Could not initialize TextToSpeech.");
        }
	}

}