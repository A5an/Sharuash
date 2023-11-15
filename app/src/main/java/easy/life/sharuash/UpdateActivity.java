package easy.life.sharuash;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class UpdateActivity extends AppCompatActivity {
    String title, desc, lang;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    CheckBox collar;
    ImageView Image;
    Button Button;
    EditText Desc, Title, Lang;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Boolean isChecked;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        toolbar = findViewById(R.id.myToolBar1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String noimage = getString(R.string.noimage);
        Button = findViewById(R.id.updateButton);
        Desc = findViewById(R.id.updateDesc);
        Image = findViewById(R.id.updateImage);
        Lang = findViewById(R.id.updateLang);
        Title = findViewById(R.id.updateTitle);
        collar = findViewById(R.id.updatecollar);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            Image.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, noimage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );



        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(Image);
            Title.setText(bundle.getString("Title"));
            Desc.setText(bundle.getString("Description"));
            Lang.setText(bundle.getString("Language"));
            isChecked = bundle.getBoolean("Collar");
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }
        SharedPreferences pref = getSharedPreferences("Phone number", MODE_PRIVATE);
        String phoneNumber = pref.getString("Phone number kz", "");
        databaseReference = FirebaseDatabase.getInstance().getReference(phoneNumber).child(key);
        collar.setChecked(isChecked);
        collar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the variable when the checkbox state changes
            this.isChecked = isChecked;
        });

        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(UpdateActivity.this, Main2.class);
                startActivity(intent);
            }
        });
    }
    public void saveData(){


        String net2 = getString(R.string.net2);
        String net = getString(R.string.net);
        if(isNetworkConnected()) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();


            if (uri != null) {
                storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlImage = uriTask.getResult();
                        imageUrl = urlImage.toString();
                        updateData();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(UpdateActivity.this, net2 + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                imageUrl = oldImageURL;
                updateData();
                dialog.dismiss();
            }
        }
        else {
            Toast.makeText(UpdateActivity.this, net, Toast.LENGTH_SHORT).show();
        }
    }
    public void updateData() {
        String net2 = getString(R.string.net2);
        String net = getString(R.string.net);
        title = Title.getText().toString().trim();
        desc = Desc.getText().toString().trim();
        lang = Lang.getText().toString();
        DataClass dataClass = new DataClass(title, desc, lang, imageUrl, isChecked);
        if (isNetworkConnected()) {
            databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateActivity.this, "Обновлено!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateActivity.this, net2 + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(UpdateActivity.this, net, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo Network = cm.getActiveNetworkInfo();
        return Network != null && Network.isConnected();
    }
}