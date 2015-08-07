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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rokuan.calliopecore.sentence.structure.InterpretationObject;

import java.util.ArrayList;
import java.util.List;

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
    public static final int SPEECH_FRAME = 0;
    public static final int SOUND_FRAME = 1;
    public static final int PARSE_FRAME = 2;
    public static final int TEXT_FRAME = 3;

    public static final int INPUT_TYPE_FRAME = SPEECH_FRAME;

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
    @Bind(R.id.input_command) protected EditText commandText;
    @Bind({ R.id.speech_frame, R.id.sound_frame, R.id.parse_frame, R.id.text_frame }) protected List<View> frames;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(!SpeechRecognizer.isRecognitionAvailable(this.getActivity())){
            // TODO: afficher une dialog qui redirige l'utilisateur vers un STT
        }

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getActivity().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View mainView = inflater.inflate(R.layout.fragment_speech, parent, false);
        ButterKnife.bind(this, mainView);
        //switchToFrame(SPEECH_FRAME);
        //switchToFrame(TEXT_FRAME);
        switchToFrame(INPUT_TYPE_FRAME);
        return mainView;
    }

    @Override
    public void onResume(){
        super.onResume();

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

    @OnClick(R.id.input_submit)
    public void submitText(){
        String command = commandText.getText().toString();

        if(!command.isEmpty()) {
            startProcess(command);
            commandText.getText().clear();
        }
    }

    @OnClick(R.id.speech_button)
    public void startSpeechRecognition(){
        switchToFrame(SOUND_FRAME);
        speech.startListening(recognizerIntent);
    }

    private void switchToFrame(int frameIndex){
        frames.get(frameIndex).setVisibility(View.VISIBLE);

        for(int i=0; i<frames.size(); i++){
            if(i != frameIndex){
                frames.get(i).setVisibility(View.INVISIBLE);
            }
        }
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

        String result = data.get(0);
        startProcess(result);
    }

    private void startProcess(String command){
        try{
            String rightPart = command.length() > 1 ? command.substring(1) : "";

            switchToFrame(PARSE_FRAME);

            long start = System.currentTimeMillis();
            InterpretationObject object = db.parseText(command);
            long end = System.currentTimeMillis();
            Log.i("ParseTime", (end - start) + "ms");

            //switchToFrame(SPEECH_FRAME);
            //switchToFrame(TEXT_FRAME);
            //switchToFrame(INPUT_TYPE_FRAME);

            resultText.setText(Character.toUpperCase(command.charAt(0)) + rightPart);

            if(object != null) {
                long jsonStart =  System.currentTimeMillis();
                String json = InterpretationObject.toJSON(object);
                long jsonEnd = System.currentTimeMillis();
                Log.i("JsonTime", (jsonEnd - jsonStart) + "ms");
                jsonText.setText(json);

                Message jsonMessage = Message.obtain(null, ConnectionService.JSON_MESSAGE, json);
                serviceMessenger.send(jsonMessage);
            } else {
                jsonText.setText("ERROR");
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            //switchToFrame(SPEECH_FRAME);
            //switchToFrame(TEXT_FRAME);
            switchToFrame(INPUT_TYPE_FRAME);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void refresh() {

    }
}
