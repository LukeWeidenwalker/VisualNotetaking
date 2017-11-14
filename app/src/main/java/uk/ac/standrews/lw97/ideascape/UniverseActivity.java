package uk.ac.standrews.lw97.ideascape;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


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
        this.notebase.addNote(new Note(this, null, "Luki", new int[] {30, 30}, "One"));
        this.notebase.addNote(new Note(this, null, "Luki", new int[] {500, 500}, "Two"));
        this.notebase.addNote(new Note(this, null, "Luki", new int[] {1000, 1000}, "Three"));

        NotesGroup notesGroup = new NotesGroup(this);

        for (String key : this.notebase.getAllNotes().keySet()) {
            for (Note note : this.notebase.getAllNotes().get(key)) {
                Log.d("DEBUG", "Added note to layout");
                notesGroup.addView(note);
            }
        }



        setContentView(notesGroup);
    }
}
