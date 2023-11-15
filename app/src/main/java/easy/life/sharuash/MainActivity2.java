package easy.life.sharuash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import easy.life.sharuash.databinding.ActivityMain2Binding;
import easy.life.sharuash.ui.login.SessionManager;


public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    private   String verificationId;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        editTextInput();
         binding.otp.setText(String.format(
                 "+7-%s", getIntent().getStringExtra("phone")
         ));
        verificationId =  getIntent().getStringExtra("verificationId");

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()){
            Intent intent = new Intent(MainActivity2.this, Main2.class);

            startActivity(intent);
            finish();
        }
        session = new SessionManager(getApplicationContext());



        if (session.isLoggedIn()) {
            Intent intent = new Intent(MainActivity2.this, Main2.class);
            startActivity(intent);
            finish();
        }
        session = new SessionManager(getApplicationContext());


        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity2.this, "Код успешно отправлен", Toast.LENGTH_SHORT).show();
            }
        });


        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar2.setVisibility(View.VISIBLE);
                binding.verify.setVisibility(View.INVISIBLE);


                if (binding.otpBox1.getText().toString().trim().isEmpty() ||
                        binding.otpBox2.getText().toString().trim().isEmpty() ||
                        binding.otpBox3.getText().toString().trim().isEmpty() ||
                        binding.otpBox4.getText().toString().trim().isEmpty() ||
                        binding.otpBox5.getText().toString().trim().isEmpty() ||
                        binding.otpBox6.getText().toString().trim().isEmpty()
                ) {
                    Toast.makeText(MainActivity2.this, "Код не правильный!", Toast.LENGTH_SHORT).show();
                } else {
                    if (verificationId != null) {
                        String code = binding.otpBox1.getText().toString().trim() +
                                binding.otpBox2.getText().toString().trim() +
                                binding.otpBox3.getText().toString().trim() +
                                binding.otpBox4.getText().toString().trim() +
                                binding.otpBox5.getText().toString().trim() +
                                binding.otpBox6.getText().toString().trim();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth.getInstance()
                                .signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            binding.progressBar2.setVisibility(View.VISIBLE);
                                            binding.verify.setVisibility(View.INVISIBLE);
                                            Toast.makeText(MainActivity2.this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity2.this, Main2.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            session.setLogin(true);
                                        } else {
                                            binding.progressBar2.setVisibility(View.GONE);
                                            binding.verify.setVisibility(View.VISIBLE);
                                            Toast.makeText(MainActivity2.this, "Код не правильный!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void editTextInput() {
         binding.otpBox1.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 
             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                    binding.otpBox2.requestFocus();
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });
        binding.otpBox2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.otpBox3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.otpBox3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.otpBox4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.otpBox4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.otpBox5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.otpBox5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.otpBox6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
