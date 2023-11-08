package easy.life.sharuash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import easy.life.sharuash.databinding.ActivityMainBinding;
import easy.life.sharuash.ui.login.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private SessionManager session;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()){
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);

            startActivity(intent);
            finish();
        }
        session = new SessionManager(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        pref = getSharedPreferences("Phone number", MODE_PRIVATE);
        editor = pref.edit();


        binding.generateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.mobileNumber.getText().toString().trim().isEmpty() ) {
                    Toast.makeText(MainActivity.this, "Неподходящий номер телефона", Toast.LENGTH_SHORT).show();
                }
                else if (binding.mobileNumber.getText().toString().trim().length() != 10) {
                    Toast.makeText(MainActivity.this, "Введите реальный номер телефона", Toast.LENGTH_SHORT).show();
                }
                 else if (binding.namephone.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "Введите имя!", Toast.LENGTH_SHORT).show();
                }     else {
                    String pn = "+7"+binding.mobileNumber.getText().toString().trim();
                    String name = binding.namephone.getText().toString().trim();
                    editor.putString("Phone number kz", pn);
                    editor.putString("Name kz", name);
                    editor.commit();
                    otpSend();
                }
            }
        });
    }

    private void otpSend() {
        binding.progressBar.setVisibility(View.VISIBLE);  // Показать полосу прогресса
        binding.generateOtp.setVisibility(View.INVISIBLE);  // Скрыть кнопку "generateOtp"

        // Определение обратных вызовов для проверки номера телефона
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                session.setLogin(true);  // Установка статуса входа пользователя
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                binding.progressBar.setVisibility(View.GONE);  // Скрыть полосу прогресса
                binding.generateOtp.setVisibility(View.VISIBLE);  // Показать кнопку "generateOtp"
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();  // Отобразить сообщение об ошибке
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                binding.progressBar.setVisibility(View.GONE);  // Скрыть полосу прогресса
                binding.generateOtp.setVisibility(View.VISIBLE);  // Показать кнопку "generateOtp"
                Toast.makeText(MainActivity.this, "Код успешно отправлен!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);  // Создание пути до MainActivity2
                intent.putExtra("phone", binding.mobileNumber.getText().toString().trim());  // Передача номера телефона как дополнительной информации
                intent.putExtra("verificationId", verificationId);  // Передача идентификатора проверки как дополнительной информации
                startActivity(intent);  // Запуск MainActivity2
            }
        };

        // Настройка параметров проверки номера телефона
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+7"+binding.mobileNumber.getText().toString().trim())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);  // Инициирование проверки номера телефона
    }
}
