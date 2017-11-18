package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;


public class NoteBase{
    // Datastructure to hold all Notes a user has made on a canvas
    private String user;
    // Organise notes into tag groups to allow for more efficient retrieval.
    private HashMap<String, ArrayList<Note>> tagDictionary;
    private Context context;
    private AttributeSet attrs;


    // Constructor

    public NoteBase(Context context, String user) {
        this.context = context;
        this.user = user;
        this.tagDictionary = new HashMap<>();
        loadNotes();
    }


    public void addNote(Note note) {
        if(this.tagDictionary.keySet().contains(note.getTag())) {
            this.tagDictionary.get(note.getTag()).add(note);
        }
        else {
            this.tagDictionary.put(note.getTag(), new ArrayList<Note>());
            this.tagDictionary.get(note.getTag()).add(note);
        }
    }


    public void loadNotes() {
        // TODO: Improve this so that some characters are escaped: https://stackoverflow.com/questions/769621/dealing-with-commas-in-a-csv-file
        BufferedReader reader;
        try {
            Log.d("DEBUG", "Trying to access files");
            Log.d("DEBUG", this.context.getFilesDir().getAbsolutePath());
            String path = this.context.getFilesDir().getAbsolutePath();
            Log.d("Files", "Path: " + path);
            File directory = new File(path);
            File[] files = directory.listFiles();
            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                Log.d("Files", "FileName:" + files[i].getName());
            }
            File file = new File(this.context.getFilesDir(), this.user + "-notes.csv");
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] valuesLine = line.split(";");

                    if (!(this.tagDictionary.keySet().contains(valuesLine[valuesLine.length-1]))) {
                        this.tagDictionary.put(valuesLine[7], new ArrayList<Note>());
                    }

                    this.tagDictionary.get(valuesLine[valuesLine.length-1]).add(new Note(context, attrs, valuesLine));
                }
            }
        }
        catch (IOException ioe) {
            Log.e(getTimeStamp(), ioe.getMessage());
        }
    }


    public void saveNotes() {
        // Format (8 entries): USER;TIMESTAMP;CONTENT;TITLE;POSITION_X;POSITION_Y;STATUS;TAG
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(this.context.getFilesDir().getAbsolutePath() + this.user + "-notes.csv");
            if (!(file.exists())) {
                boolean fileCreated = file.createNewFile();
                if (fileCreated) {
                    Log.d("File Created", this.user + "-notes.csv");
                }
            }

            for (String key : tagDictionary.keySet()) {
                for (Note note : tagDictionary.get(key)) {
                    for (String s : note.saveNote()) {
                        sb.append(s);
                        sb.append(";");
                    }
                    sb.append("\r\n");
                }
            }

            FileWriter fw = new FileWriter(this.context.getFilesDir() + this.user + "-notes.csv", false);
            // Write content to file.
            fw.write(sb.toString());
            fw.flush();
            fw.close();
        }
        catch (IOException ioe) {
            Log.e(getTimeStamp(), ioe.getMessage());
        }
    }


    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public ArrayList<Note> getTagNotes(String tag) {
        return this.tagDictionary.get(tag);
    }

    public HashMap<String, ArrayList<Note>> getAllNotes() {
        return this.tagDictionary;
    }

}
