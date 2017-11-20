package uk.ac.standrews.lw97.ideascape;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


public class UniverseActivity extends AppCompatActivity {
    private String user;
    HashMap<String, ArrayList<String[]>> intentNotes;
    String tag = "None";
    NotesGroup notesGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        this.intentNotes = new HashMap<>();

        Log.d("DEBUG", "Created Constellation Activity");
        if (getIntent() != null) {
            this.user = getIntent().getStringExtra("user");
            this.tag = getIntent().getStringExtra("tag");
            if (getIntent().getSerializableExtra("overallNotebase") != null) {
                decodeDownstreamIntent((HashMap<String, ArrayList<String[]>>) getIntent().getSerializableExtra("overallNotebase"));
            }
        }


        Log.d("DEBUG", "Got intent");
        notesGroup = new NotesGroup(this, this.intentNotes.get(this.tag), user, "constellation");
//        try {
//            notesGroup = new NotesGroup(this, this.intentNotes.get(this.tag), user, "constellation");
//        }
//        catch (Exception e) {
//            notesGroup = new NotesGroup(this, new ArrayList<String[]>(), user, "constellation");
//        }
        notesGroup.tagGroup = this.tag;

        setContentView(notesGroup);
    }

    public void launchMainActivity() {
        //Intent intent = new Intent(this, UniverseActivity.class);
        Intent intent = new Intent(this, MainActivity.class);

        // Pass current notebase
        intent.putExtra("user", "Luki");
        intent.putExtra("overallNotebase", encodeUpstreamIntent());
        startActivity(intent);
    }


    public HashMap<String, ArrayList<String[]>> encodeUpstreamIntent() {
        if (this.intentNotes.get(tag) != null) {
            this.intentNotes.remove(tag);
        }

        this.intentNotes.put(tag, new ArrayList<String[]>());

        if(!(notesGroup.toArraylist().isEmpty())) {
            for (String[] note : notesGroup.toArraylist()) {
                this.intentNotes.get(tag).add(note);
            }
        }
        Log.d("DEBUG", "notesGroup size: " + notesGroup.toArraylist().size());

        Log.d("DEBUG", "Const to Uni size: " + intentNotes.size());

        // Let members of this tag group be adjusted.
        return this.intentNotes;
    }


    public void decodeDownstreamIntent(HashMap<String, ArrayList<String[]>> updatedNoteBase) {
        this.intentNotes = updatedNoteBase;
    }

    public static int getScreenWidth() {
        Log.d("DEBUG", String.valueOf(Resources.getSystem().getDisplayMetrics().widthPixels));
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        Log.d("DEBUG", String.valueOf(Resources.getSystem().getDisplayMetrics().heightPixels));
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
