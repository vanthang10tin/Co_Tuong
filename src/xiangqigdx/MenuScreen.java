package se.xiangqigdx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuScreen extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClickvsAI(View view) {
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("gameMode", "AI");
        startActivity(intent);
    }

    public void OnClickvsPlayer(View view) {
        Intent intent = new Intent(this, GameScreen.class); 
        intent.putExtra("gameMode", "Player");
        startActivity(intent);
    }

    public void OnClickHistory(View view) {
        // Handle history button click
    }

    public void OnClickAbout(View view) {
        // Handle about button click
    }
}