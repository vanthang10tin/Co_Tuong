package se.xiangqigdx.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import se.xiangqigdx.GameScreen;
import se.xiangqigdx.Main;
import se.xiangqigdx.R;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
//        setContentView(R.layout.activity_main);
        configuration.useImmersiveMode = false; // Recommended, but not required.
        initialize(new Main(), configuration);
    }

}
