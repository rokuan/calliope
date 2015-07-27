package apps.rokuan.com.calliope_helper.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

import java.util.ArrayList;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper.service.ConnectionService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 17/07/15.
 */
public class SpeechFragment extends PlaceholderFragment implements RecognitionListener {
    private SpeechRecognizer speech;
    private Intent recognizerIntent;
    private CalliopeSQLiteOpenHelper db;

    private boolean bound = false;
    private Messenger serviceMessenger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            bound = false;
        }
    };

    @Bind(R.id.recognized_text) protected TextView resultText;
    @Bind(R.id.object_json) protected TextView jsonText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_home, parent, false);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getActivity().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        ButterKnife.bind(this, mainView);

        return mainView;
    }

    @Override
    public void onResume(){
        super.onResume();

        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        speech = SpeechRecognizer.createSpeechRecognizer(this.getActivity());
        speech.setRecognitionListener(this);

        db = new CalliopeSQLiteOpenHelper(this.getActivity());

        Intent serviceIntent = new Intent(this.getActivity().getApplicationContext(), ConnectionService.class);
        this.getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause(){
        super.onPause();

        this.getActivity().unbindService(serviceConnection);

        if(speech != null){
            speech.destroy();
            speech = null;
        }

        if(db != null){
            db.close();
            db = null;
        }
    }

    @OnClick(R.id.speech_button)
    public void startSpeechRecognition(){
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        Log.i("HomeFragment", "SpeechResults");

        try{
            String result = data.get(0);
            String rightPart = result.length() > 1 ? result.substring(1) : "";
            InterpretationObject object = db.parseText(result);

            resultText.setText(Character.toUpperCase(result.charAt(0)) + rightPart);

            if(object != null) {
                String json = InterpretationObject.toJSON(object);
                jsonText.setText(json);

                Message jsonMessage = Message.obtain(null, ConnectionService.JSON_MESSAGE, json);
                serviceMessenger.send(jsonMessage);
            } else {
                jsonText.setText("ERROR");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
