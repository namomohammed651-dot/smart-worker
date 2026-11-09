package com.smartworker.app;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvDate = findViewById(R.id.tvCurrentDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE، dd MMMM yyyy", new Locale("ar"));
        tvDate.setText(sdf.format(new Date()));
        Button btnGoToClock = findViewById(R.id.btnGoToClock);
        btnGoToClock.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ClockActivity.class));
        });
    }
}
