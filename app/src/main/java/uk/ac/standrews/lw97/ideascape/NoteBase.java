package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;


public class NoteBase extends Observable {
    // Datastructure to hold all Notes a user has made on a canvas
    private String user;
    private ArrayList<Note> notes;
    // Organise notes into tag groups to allow for more efficient retrieval.
    private HashMap<String, ArrayList<Note>> tagDictionary;
    private Context context;
    private AttributeSet attrs;


    NoteBase(Context context) {
        this.notes = new ArrayList<>();
    }


    NoteBase(Context context, String user) {
        this.context = context;
        this.user = user;
        this.notes = new ArrayList<>();
        this.tagDictionary = new HashMap<>();
        loadNotes();
    }


    public void loadNotes() {
        // TODO: Improve this so that some characters are escaped: https://stackoverflow.com/questions/769621/dealing-with-commas-in-a-csv-file
        BufferedReader reader = null;
        try {
            File file = new File(this.context.getFilesDir(), this.user + "-notes.csv");
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] valuesLine = line.split(";");

                    if (!(this.tagDictionary.keySet().contains(valuesLine[valuesLine.length-1]))) {
                        this.tagDictionary.put(valuesLine[valuesLine.length-1], new ArrayList<Note>());
                        this.tagDictionary.get(valuesLine[valuesLine.length-1]).add(new Note(context, attrs, valuesLine));
                    }


                }
            }

        }
        catch (IOException ioe) {
            Log.e(getTimeStamp(), ioe.getMessage());
        }

    }


    public void saveNotes() {
        // Format (8 entries): USER;TIMESTAMP;CONTENT;TITLE;POSITION_X;POSITION_Y;STATUS;TAG
    }

    public void saveNotes(String user) {

    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public ArrayList<Note> getTagNotes(int tag) {
        return this.tagDictionary.get(tag);
    }
}
