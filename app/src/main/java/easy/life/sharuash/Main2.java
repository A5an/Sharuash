package easy.life.sharuash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import easy.life.sharuash.ui.login.BestMapsFragment;
import easy.life.sharuash.ui.login.BulbFragment;
import easy.life.sharuash.ui.login.Home2;
import easy.life.sharuash.ui.login.Settings;

public class Main2 extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Home2 home2 = new Home2();
    Settings settings = new Settings();
    BestMapsFragment maps = new BestMapsFragment();
    BulbFragment bulb = new BulbFragment();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main32);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bottomNavigationView = findViewById(R.id.bottom_navigation) ;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, home2).commit();
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId())    {

                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home2).commit();
                        return true;
                    case R.id.map:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, maps).commit();
                        return true;
                    case R.id.bulb:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, bulb).commit();
                        return true;
                    case R.id.set:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, settings).commit();
                        return true;

                }

                return false;
            }
        });
    }

}
