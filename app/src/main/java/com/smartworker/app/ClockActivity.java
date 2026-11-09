package com.smartworker.app;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class ClockActivity extends AppCompatActivity {
    private LinearLayout llAlarmsContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        TextView tvDateClock = findViewById(R.id.tvDateClock);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE، dd MMMM yyyy", new Locale("ar"));
        tvDateClock.setText(sdf.format(new Date()));
        llAlarmsContainer = findViewById(R.id.llAlarmsContainer);
        updateAlarmsList();
        Button btnAddAlarm = findViewById(R.id.btnAddAlarm);
        btnAddAlarm.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(ClockActivity.this,
                    (view, hourOfDay, minute) -> {
                        scheduleAlarm(hourOfDay, minute);
                        saveAlarm(hourOfDay, minute);
                        updateAlarmsList();
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });
    }
    private void scheduleAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
    private void saveAlarm(int hour, int minute) {
        SharedPreferences prefs = getSharedPreferences("Alarms", MODE_PRIVATE);
        String existing = prefs.getString("alarm_list", "");
        String newAlarm = hour + ":" + minute + ",";
        String searchKey = hour + ":" + minute + ",";
        if (existing.indexOf(searchKey) == -1) {
            prefs.edit().putString("alarm_list", existing + newAlarm).apply();
        }
    }
    private void updateAlarmsList() {
        llAlarmsContainer.removeAllViews();
        SharedPreferences prefs = getSharedPreferences("Alarms", MODE_PRIVATE);
        String saved = prefs.getString("alarm_list", "");
        if (saved.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("لا توجد منبهات مضافة.");
            empty.setTextColor(getResources().getColor(android.R.color.darker_gray));
            empty.setGravity(android.view.Gravity.CENTER);
            empty.setPadding(0, 20, 0, 20);
            llAlarmsContainer.addView(empty);
        } else {
            String[] alarms = saved.split(",");
            for (String alarm : alarms) {
                if (alarm.length() > 0) {
                    String[] parts = alarm.split(":");
                    if (parts.length == 2) {
                        TextView tv = new TextView(this);
                        tv.setText("منبه عند: " + String.format(Locale.getDefault(), "%02d:%02d", Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
                        tv.setTextSize(18f);
                        tv.setTextColor(getResources().getColor(android.R.color.white));
                        tv.setPadding(20, 20, 20, 20);
                        llAlarmsContainer.addView(tv);
                    }
                }
            }
        }
    }
}
