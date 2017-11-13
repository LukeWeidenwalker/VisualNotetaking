package uk.ac.standrews.lw97.ideascape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


public class UniverseActivity extends AppCompatActivity {
    private NoteBase notebase;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "Created Universe Activity");
        this.user = getIntent().getStringExtra("user");
        Log.d("DEBUG", "Got intent");

        this.notebase = new NoteBase(this, this.user);
        Log.d("DEBUG", "Created notebase");
        this.notebase.addNote(new Note(this, null, "Luki", new int[] {30, 30}));
        this.notebase.addNote(new Note(this, null, "Luki", new int[] {500, 500}));
        this.notebase.addNote(new Note(this, null, "Luki", new int[] {1000, 1000}));

        FrameLayout fl = new FrameLayout(this);
        fl.addView(new Background(this, Color.BLACK));

        for (String key : this.notebase.getAllNotes().keySet()) {
            for (Note note : this.notebase.getAllNotes().get(key)) {
                Log.d("DEBUG", "Added note to layout");
                fl.addView(note);
            }
        }
        setContentView(fl);
    }

    public boolean onTouchEvent(MotionEvent event) {
        // Detecting touch gestures
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                //touch_start(x, y);
                //invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //touch_move(x, y);
                //invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //touch_up();
                //invalidate();
                break;
        }
        return true;
    }

}
