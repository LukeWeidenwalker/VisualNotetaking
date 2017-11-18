package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;



public class NotesGroup extends FrameLayout {

    private String tagGroup;


    NotesGroup(Context context, NoteBase noteBase, String tagGroup) {
        super(context);
        this.tagGroup = tagGroup;
        this.addView(new Background(context));
        this.addViews(noteBase);
    }

    void addViews(NoteBase noteBase) {
        HashMap<String, ArrayList<Note>> tagDictionary = noteBase.getAllNotes();
        for(Note note : tagDictionary.get(tagGroup)) {
            note.setParentNoteGroup(this);
            this.addView(note);
        }
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int childCount = getChildCount();
        // View 0 is the background
        for (int i = 1; i < childCount; i++) {
            Note note = (Note) getChildAt(i);
            if (note.checkPositionOverlap(ev.getX(), ev.getY())) {
                note.dispatchTouchEvent(ev);
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touchevents that are not treated by a note arrive here.
        int childCount = getChildCount();
        // View 0 is the background
        for (int i = 1; i < childCount; i++) {
            Note note = (Note) getChildAt(i);
            note.selected = false;
        }
        Log.d("DEBUG", "TouchEvent bubbled up!");
        return true;
    }
}
