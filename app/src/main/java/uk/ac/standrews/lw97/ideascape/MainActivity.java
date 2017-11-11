package uk.ac.standrews.lw97.ideascape;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Called everytime the activity is called -> setup.
        super.onCreate(savedInstanceState);

        // Sets activity_main.xml as the render instructions
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button enter = (Button) findViewById(R.id.button);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchUniverseActivity();
            }
        });
    }

    public void launchUniverseActivity() {
        Intent intent = new Intent(this, UniverseActivity.class);
        intent.putExtra("user", "Luki");
        startActivity(intent);
    }
}
