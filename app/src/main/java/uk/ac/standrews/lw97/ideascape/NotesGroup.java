package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NotesGroup extends FrameLayout {

    private String tagGroup;
    long startClickTime;
    long firstClickTime = 0;
    int numberClicks;
    NoteBase noteBase;
    String user;


    NotesGroup(Context context, NoteBase noteBase, String tagGroup, String user) {
        super(context);
        this.tagGroup = tagGroup;
        this.addView(new Background(context));
        this.addViews(noteBase);
        this.noteBase = noteBase;
        this.user = user;
    }

    void addViews(NoteBase noteBase) {
        HashMap<String, ArrayList<Note>> tagDictionary = noteBase.getAllNotes();
        for (Note note : tagDictionary.get(tagGroup)) {
            note.setParentNoteGroup(this);
            this.addView(note);
        }
    }

    public void unselectNotes() {
        int childCount = getChildCount();
        // View 0 is the background
        for (int i = 1; i < childCount; i++) {
            Note note = (Note) getChildAt(i);
            note.selected = false;
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int childCount = getChildCount();
        // Adding each note in the notebase to the layout
        // Remember: View 0 is the background
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
        Log.d("DEBUG", "TouchEvent bubbled up!");

        switch (event.getAction()) {
            // Detecting Doubles taps to create new notes
            case MotionEvent.ACTION_DOWN:
                unselectNotes();
                startClickTime = Calendar.getInstance().getTimeInMillis();
                if (firstClickTime == 0) {
                    firstClickTime = Calendar.getInstance().getTimeInMillis();
                }
                break;

            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                long doubleTapDuration = Calendar.getInstance().getTimeInMillis() - firstClickTime;
                if (clickDuration < Note.MAX_CLICK_DURATION) {
                    numberClicks++;
                }
                else {
                    numberClicks = 0;
                }
                Log.d("DEBUG", "Number of clicks: " + numberClicks);

                if (this.numberClicks >= 1) {
                    if (doubleTapDuration > 2 * Note.MAX_CLICK_DURATION) {
                        firstClickTime = 0;
                        numberClicks = 0;
                    }
                    else {
                        if (this.numberClicks >= 2) {
                            Log.d("DEBUG", "Detected Double-Tap");
                            int[] position = new int[]{(int) event.getX() - Note.standardSideLength / 2, (int) event.getY() - Note.standardSideLength / 2};
                            Note note = new Note(getContext(), null, user, position, "New");
                            this.noteBase.addNote(note);
                            note.setParentNoteGroup(this);
                            this.addView(note);
                            firstClickTime = 0;
                            numberClicks = 0;
                        }
                    }
                }
                break;
        }
        return true;
    }
}
