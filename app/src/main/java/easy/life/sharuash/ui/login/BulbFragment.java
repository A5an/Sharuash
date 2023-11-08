package easy.life.sharuash.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import easy.life.sharuash.ChatGPT;
import easy.life.sharuash.DetailBulbActivity;
import easy.life.sharuash.R;
import easy.life.sharuash.UploadActivity;

public class BulbFragment extends Fragment {

     LinearLayout one;
    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.fragment_bulb, container, false);
    }

    public void onStart(){
        super.onStart();

        FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.gptBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatGPT.class);
                startActivity(intent);
            }
        });

        one = (LinearLayout) context.findViewById(R.id.first);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailBulbActivity.class);
                startActivity(intent);
            }
        });
    }
    
}
