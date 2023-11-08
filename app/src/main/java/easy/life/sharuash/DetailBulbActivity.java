package easy.life.sharuash;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
public class DetailBulbActivity extends AppCompatActivity {

    TextView titlebulb, textbulb;
    ImageView imagebulb;
    Integer num1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bulb);

        toolbar = findViewById(R.id.myToolBar1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        titlebulb = findViewById(R.id.titlebulb);
        textbulb = findViewById(R.id.textbulb);
        imagebulb = findViewById(R.id.imagebulb);

        String title1 = getResources().getString(R.string.titlebulb1);
        String text1 = getResources().getString(R.string.textbulb1);
        Drawable drawable1 = this.getResources().getDrawable(R.drawable.three);
        titlebulb.setText(title1);
        textbulb.setText(text1);
        imagebulb.setImageDrawable(drawable1);
    }
}
