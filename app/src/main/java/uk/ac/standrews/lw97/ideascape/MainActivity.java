package uk.ac.standrews.lw97.ideascape;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public NoteBase notebase;
    private String user;
    NotesGroup notesGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "Created Universe Activity");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if(getIntent() != null) {
            this.user = getIntent().getStringExtra("user");
            if (getIntent().getSerializableExtra("overallNotebase") != null) {
                Log.d("DEBUG", "Received Hashmap intent");
                Log.d("DEBUG", "Created notebase");
                decodeUpstreamIntent((HashMap<String, ArrayList<String[]>>) getIntent().getSerializableExtra("overallNotebase"), getIntent().getStringExtra("user"));
            }
            else {
                this.notebase = new NoteBase(this, null, this.user);
            }
        }
        else {
            this.user = "Luki";
            this.notebase = new NoteBase(this, null, this.user);
        }

        Log.d("DEBUG", "Loading Notes group from Notebase");
        Log.d("DEBUG", "Size notebase " + notebase.tagDictionary.size());

        notesGroup = new NotesGroup(this, this.notebase, user, "universe");
        notesGroup.tagGroup = "None";
        Log.d("DEBUG", "Childcount: "  + notesGroup.getChildCount());

        setContentView(notesGroup);
    }


    @Override
    protected void onDestroy() {
        Log.d("DEBUG", "Destroyed Main Activity");
        super.onDestroy();
    }

    public static int getScreenWidth() {
        Log.d("DEBUG", String.valueOf(Resources.getSystem().getDisplayMetrics().widthPixels));
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        Log.d("DEBUG", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels));
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public HashMap<String, ArrayList<String[]>> encodeDownstreamIntent() {
        HashMap<String, ArrayList<String[]>> intentNotes = new HashMap<>();
        for (String tag : this.notebase.tagDictionary.keySet()) {
            intentNotes.put(tag, new ArrayList<String[]>());
            for (Note note : this.notebase.tagDictionary.get(tag)) {
                intentNotes.get(tag).add(note.saveNote());
            }
        }

        return intentNotes;
    }


    public void decodeUpstreamIntent(HashMap<String, ArrayList<String[]>> updatedNoteBase, String user) {
        this.notebase = new NoteBase(this, null, user);
        for (String tag : updatedNoteBase.keySet()) {
            this.notebase.tagDictionary.put(tag, new ArrayList<Note>());
            for (String[] noteString : updatedNoteBase.get(tag)) {
                Note note = new Note(this, null, noteString, "universe");
                note.setDisplayingActivity("universe");
                this.notebase.tagDictionary.get(tag).add(note);
            }
        }
        Log.d("DEBUG", "Main Actvity - Notebase Size: " + this.notebase.tagDictionary.size());

    }


    public void launchUniverseActivity(String tag) {
        //Intent intent = new Intent(this, UniverseActivity.class);
        Intent intent = new Intent(this, UniverseActivity.class);

        // Pass current notebase
        intent.putExtra("user", "Luki");
        intent.putExtra("tag", tag);
        intent.putExtra("overallNotebase", encodeDownstreamIntent());
        startActivity(intent);
    }
}

