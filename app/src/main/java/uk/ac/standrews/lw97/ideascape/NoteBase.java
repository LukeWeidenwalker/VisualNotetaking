package uk.ac.standrews.lw97.ideascape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;


public class NoteBase extends Observable {
    // Datastructure to hold all Notes a user has made on a canvas
    private String user;
    private ArrayList<Note> notes;
    // Organise notes into tag groups to allow for more efficient retrieval.
    private HashMap<Integer, ArrayList<Note>> tagDictionary;


    NoteBase() {
        this.notes = new ArrayList<>();
    }


    NoteBase(String user) {
       this.user = user;
       this.notes = new ArrayList();
       loadNotes();
    }

    public void loadNotes() {

    }

    public void saveNotes() {

    }

    public ArrayList<Note> getTagNotes(int tag) {
        return this.tagDictionary.get(tag);
    }
}
