package easy.life.sharuash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;


public class DetailActivity extends AppCompatActivity {
    String key = "";
    String imageUrl = "";
    Boolean isChecked;
    TextView Desc, Title, Lang, colar;
    ImageView Image;
    FloatingActionButton delete, edit;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        toolbar = findViewById(R.id.myToolBar1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        colar = findViewById(R.id.havecollar);
        Desc = findViewById(R.id.detailDesc);
        Image = findViewById(R.id.detailImage);
        Title = findViewById(R.id.detailTitle);
        delete = findViewById(R.id.deleteButton);
        edit = findViewById(R.id.editButton);
        Lang = findViewById(R.id.detailLang);
        String havecol = getString(R.string.havecollar);
        String nocol = getString(R.string.nocollar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Desc.setText(bundle.getString("Description"));
            Title.setText(bundle.getString("Title"));
            Lang.setText(bundle.getString("Language"));
            isChecked = bundle.getBoolean("Collar");
            if (isChecked) {
                colar.setText(havecol);
            } else {
                colar.setText(nocol);
            }
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(Image);
        }

        String deleted = getString(R.string.delete);
        SharedPreferences pref = getSharedPreferences("Phone number", MODE_PRIVATE);
        String phoneNumber = pref.getString("Phone number kz", "");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(phoneNumber);
                reference.child(key).removeValue();
                Toast.makeText(DetailActivity.this, deleted, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Main2.class));
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", Title.getText().toString())
                        .putExtra("Description", Desc.getText().toString())
                        .putExtra("Language", Lang.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key)
                        .putExtra("Collar", isChecked);
                startActivity(intent);
            }
        });
    }
}