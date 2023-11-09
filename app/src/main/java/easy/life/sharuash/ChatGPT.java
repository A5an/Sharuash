package easy.life.sharuash;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class ChatGPT extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    private Toolbar toolbar;
    private Handler typingHandler = new Handler();
    private int typingPosition = 0;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build();
    private List<String> conversationHistory;
    private Handler responseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatgpt);


        responseHandler = new Handler(Looper.getMainLooper());

        toolbar = findViewById(R.id.myToolBar1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        messageList = new ArrayList<>();
        conversationHistory = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");
            conversationHistory.add(question);
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (typingHandler.hasCallbacks(null)) {
                typingHandler.removeCallbacksAndMessages(null);
            }
        }
        messageList.remove(messageList.size()-1);
        conversationHistory.add(response);
        addToChat(response,Message.SENT_BY_BOT);
        messageEditText.setEnabled(true);
        sendButton.setEnabled(true);
    }

    void callAPI(String question){
        messageEditText.setEnabled(false);
        sendButton.setEnabled(false);
        String text1 = getString(R.string.typing);
        addToChat(text1, Message.SENT_BY_BOT);
        Animation();

        String apikey = BuildConfig.AI_API;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray messageArr = new JSONArray();

            JSONObject sobj = new JSONObject();
            sobj.put("role", "system");
            sobj.put("content", "You are a helpful assistant for home animal holders and animal's theme overall.");
            messageArr.put(sobj);

            for (String message : conversationHistory) {
                JSONObject obj = new JSONObject();
                obj.put("role", "user");
                obj.put("content", "Use no more than 300 tokens for your response, but if the response needs more tokens, just compress the response no matter what : " + message);
                messageArr.put(obj);
            }
            jsonBody.put("messages", messageArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer "+apikey)
                .post(body)
                .build();

        String text = getString(R.string.resend);
        String text2 = getString(R.string.resend2);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse(text2 + " " + e.getMessage()+". "+text);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        responseHandler.post(() -> addResponse(result.trim()));
//                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
//                    addResponse("Failed to load response due to "+response.body().string());
                    responseHandler.post(() -> {
                        try {
                            addResponse(text2 + " "+ response.body().string() + ". "+text);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });





    }

    private void Animation() {
        typingPosition = 0;
        runAnimation();
    }

    private void runAnimation() {
        String typingMessage = getString(R.string.typing);
        if (typingPosition <= typingMessage.length()) {
            String typingText = typingMessage.substring(0, typingPosition);
            messageList.set(messageList.size() - 1, new Message(typingText, Message.SENT_BY_BOT));
            messageAdapter.notifyItemChanged(messageList.size() - 1);
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            typingPosition++;
            if (typingPosition <= typingMessage.length()) {
                typingHandler.postDelayed(this::runAnimation, 100);
            }
        }
    }

}

