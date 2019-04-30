package br.com.lucasfrancisco.modulopatrimonio.applications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID_01 = "channel01";
    public static final String CHANNEL_ID_02 = "channel02";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel01 = new NotificationChannel(CHANNEL_ID_01, "Channel 01", NotificationManager.IMPORTANCE_HIGH);
            channel01.setDescription("Este é o canal 01!");

            NotificationChannel channel02 = new NotificationChannel(CHANNEL_ID_02, "Channel 02", NotificationManager.IMPORTANCE_LOW);
            channel01.setDescription("Este é o canal 02!");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel01);
            manager.createNotificationChannel(channel02);
        }
    }
}
