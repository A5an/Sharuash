package easy.life.sharuash.ui.login;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import easy.life.sharuash.LanguageManager;
import easy.life.sharuash.MainActivity;
import easy.life.sharuash.R;



public class Settings extends Fragment {
    private static final int SELECT_PHOTO = 100;
    Activity context;
     ImageView profile;
    private SessionManager session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    public void onStart(){
        super.onStart();

       Button quit = (Button) context.findViewById(R.id.quit);
       Button chat = (Button) context.findViewById(R.id.chat);
       Button change = (Button) context.findViewById(R.id.image_change);
       Button lang1 = (Button) context.findViewById(R.id.language);
       Button lang2 = (Button) context.findViewById(R.id.language2);
        LanguageManager lang = new LanguageManager(context);
        TextView textView = (TextView) context.findViewById(R.id.textViewset);
        TextView textView2 = (TextView) context.findViewById(R.id.textViewname);

        session = new SessionManager(context.getApplicationContext());

        SharedPreferences pref = context.getSharedPreferences("Phone number", MODE_PRIVATE);
        String phoneNumber = pref.getString("Phone number kz", "");
        String name = pref.getString("Name kz", "");
        textView.setText(phoneNumber);
        textView2.setText(name);

       lang1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                   lang.updateResource("kk");
                   getActivity().recreate();

           }
       });
       lang2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                   lang.updateResource("ru");
                   getActivity().recreate();
           }
       });
       profile = (ImageView) context.findViewById(R.id.image_profile);
       change.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_PICK);
               intent.setType("image/*")    ;
               startActivityForResult(intent, SELECT_PHOTO);
           }
       });
       quit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               session.setLogin(false);
               Intent intent = new Intent(context, MainActivity.class);
               startActivity(intent);
           }
       });
       chat.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                  gotourl("https://t.me/sharuash");
                             }
                         });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {


                Uri select = data.getData();
                InputStream inputStream = null;

                try {
                    assert select != null;
                    inputStream = context.getContentResolver().openInputStream(select);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                BitmapFactory.decodeStream(inputStream);
                profile.setImageURI(select);
            }
        }
    }
    private void gotourl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}
