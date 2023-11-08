package easy.life.sharuash;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class UploadActivity extends AppCompatActivity {
    String imageURL;
    Uri uri;
    CheckBox collar;
    ImageView Image;
    Button save;
    EditText Topic, Desc, Lang;
    Boolean isChecked = false;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        toolbar = findViewById(R.id.myToolBar1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String noimage = getString(R.string.noimage);
        collar = findViewById(R.id.collar);
        Image = findViewById(R.id.uploadImage);
        Desc = findViewById(R.id.uploadDesc);
        Topic = findViewById(R.id.uploadTopic);
        Lang = findViewById(R.id.uploadLang);
        save = findViewById(R.id.saveButton);

        collar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.isChecked = isChecked;
        });


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
                            Toast.makeText(UploadActivity.this, noimage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    public void saveData(){

        String net2 = getString(R.string.net2);
        String net = getString(R.string.net);

        if(isNetworkConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            if (uri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                        .child(uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri urlImage = uriTask.getResult();
                        imageURL = urlImage.toString();
                        uploadData();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(UploadActivity.this, net2 + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                imageURL = "https://i.ibb.co.com/hfdBtyJ/photo-2023-04-23-22-13-04.jpg";
                uploadData();
                dialog.dismiss();
            }
        }
        else {
            Toast.makeText(UploadActivity.this, net, Toast.LENGTH_SHORT).show();
        }
    }


    // Метод для загрузки данных о животном
    public void uploadData() {
        // Получение строковых ресурсов для сообщений о сети
        String net2 = getString(R.string.net2);
        String net = getString(R.string.net);

        // Извлечение данных из пользовательского ввода
        String title = Topic.getText().toString();
        String desc = Desc.getText().toString();
        String lang = Lang.getText().toString();

        // Создание объекта DataClass с полученными данными
        DataClass dataClass = new DataClass(title, desc, lang, imageURL, isChecked);

        // Получение текущей даты и времени
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        // Получение номера телефона из SharedPreferences
        SharedPreferences pref = getSharedPreferences("Phone number", MODE_PRIVATE);
        String phoneNumber = pref.getString("Phone number kz", "");

        // Проверка доступности сети
        if (isNetworkConnected()) {
            // Отправка данных в Firebase Realtime Database и добавление обработчика успешного выполнения
            FirebaseDatabase.getInstance().getReference(phoneNumber).child(currentDate)
                    .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Если операция завершена успешно, отобразить сообщение "Сохранено!"
                                Toast.makeText(UploadActivity.this, "Сохранено!", Toast.LENGTH_SHORT).show();
                                finish(); // Закрыть текущую активность
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Если операция завершится неудачно, отобразить сообщение об ошибке сети
                            Toast.makeText(UploadActivity.this, net2 + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Если сеть недоступна, отобразить сообщение о недоступности сети
            Toast.makeText(UploadActivity.this, net, Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для проверки подключения к сети
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo Network = cm.getActiveNetworkInfo();
        return Network != null && Network.isConnected();
    }


}
