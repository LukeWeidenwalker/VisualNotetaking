package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NotesGroup extends FrameLayout {

    public String tagGroup;
    long startClickTime;
    long firstClickTime = 0;
    int numberClicks;
    NoteBase noteBase;
    String user;
    SparseArray<PointF> mActivePointers;
    int zoomCounter;
    String displayingActivity;


    NotesGroup(Context context, NoteBase noteBase, String user, String displayingActivity) {
        super(context);
        this.addView(new Background(context));
        setDisplayingActivity(displayingActivity);
        this.noteBase = noteBase;
        this.user = user;
        if(noteBase != null) {
            this.addViews(noteBase);
        }

        mActivePointers = new SparseArray<>();
    }


    NotesGroup(Context context, ArrayList<String[]> notesArray, String user, String displayingActivity) {
        super(context);
        this.addView(new Background(context));
        setDisplayingActivity(displayingActivity);
        this.user = user;
        if (!(notesArray == null)) {
            this.addViews(notesArray);
        }
        mActivePointers = new SparseArray<>();
    }


    void addViews(NoteBase noteBase) {
        HashMap<String, ArrayList<Note>> tagDictionary = noteBase.getAllNotes();
        for (String tagGroup : tagDictionary.keySet()) {
            for (Note note : tagDictionary.get(tagGroup)) {
                Log.d("DEBUG", "!!! Adding views from notebase");

                note.setParentNoteGroup(this);
                note.setDisplayingActivity(this.displayingActivity);
                this.addView(note);
            }
        }
    }


    void addViews(ArrayList<String[]> noteArray) {
        // Add views that are passed over as an intent
        for (String[] note : noteArray) {
            Note newNote = new Note(getContext(), null, note, this.displayingActivity);

            newNote.setParentNoteGroup(this);
            newNote.setDisplayingActivity(this.displayingActivity);
            this.addView(newNote);
        }
    }


    public ArrayList<String[]> toArraylist() {
        ArrayList<String[]> asArrayList = new ArrayList<>();
        int childCount = getChildCount();

        for(int i = 1; i < childCount; i++) {
            Note newNote = (Note) getChildAt(i);
            asArrayList.add(newNote.saveNote());
        }
        return asArrayList;
    }



    public void unselectNotes() {
        int childCount = getChildCount();
        // View 0 is the background
        for (int i = 1; i < childCount; i++) {
            Note note = (Note) getChildAt(i);
            note.selected = false;
        }
    }


    public void singleTouch(MotionEvent event) {
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
                            // TODO: Fix non-reset after one click
                            Log.d("DEBUG", "Detected Double-Tap");
                            if (this.displayingActivity.equals("constellation")) {
                                int[] position = new int[]{(int) event.getX() - Note.standardSideLength / 2, (int) event.getY() - Note.standardSideLength / 2};
                                Note note = new Note(getContext(), null, user, position, "New", "constellation");
                                note.setParentNoteGroup(this);
                                this.addView(note);
                            }
                            firstClickTime = 0;
                            numberClicks = 0;
                        }
                    }
                }
                break;
        }
    }


    public void multiTouch(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.d("DEBUG", "Multiple Pointers: DOWN");

                PointF f = new PointF();
                f.x = event.getX(pointerIndex);
                f.y = event.getY(pointerIndex);
                mActivePointers.put(pointerId, f);
                break;
            }

            // A pointer was moved.
            case MotionEvent.ACTION_MOVE: {
                double[][] distances = new double[2][2];
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        distances[i][0] = (event.getX(i) - point.x);
                        distances[i][1] = (event.getY(i) - point.y);

                        // Set saved previous point to current point.
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }

                try {
                    // Check if the two movements offset each other
                    if (Math.abs((distances[0][0] + distances[1][0])) < 2.6 && Math.abs(distances[0][1] + distances[1][1]) < 2.6) {
                        // Not signal on minor movement
                        if (Math.abs(distances[0][0]) + Math.abs(distances[1][0]) + Math.abs(distances[0][1]) + Math.abs(distances[1][1]) > 0.5) {
                            zoomCounter++;
                            Log.d("DEBUG", "Zoom in counter: " + zoomCounter);
                        }
                    }
                }
                catch (ArrayIndexOutOfBoundsException a) {
                    Log.d("DEBUG", "Array Out of Bounds");
                }

                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (zoomCounter >= 2) {
                    Log.d("DEBUG", "ZoomIn detected");
                    if (this.displayingActivity.equals("universe")) {
                        goToConstellationActivity();
                    }
                    if (this.displayingActivity.equals("constellation")) {
                        backToMainActivity();
                    }

                    zoomCounter = 0;
                }

                Log.d("DEBUG", "Multiple Pointers: UP");
                mActivePointers.remove(pointerId);
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touchevents that are not treated by a note arrive here.
        //Log.d("DEBUG", "TouchEvent bubbled up!");
        //Log.d("DEBUG", "Pointer Count: " + event.getPointerCount());

        if(event.getPointerCount() == 2) {
            multiTouch(event);
        }

        else if (event.getPointerCount() == 1) {
            singleTouch(event);
        }

        return true;
    }


    public void backToMainActivity() {
        Log.d("DEBUG", "Going back to main");
        ((UniverseActivity) getContext()).launchMainActivity();

    }


    public void goToConstellationActivity() {
        ((MainActivity) getContext()).launchUniverseActivity(this.tagGroup);
    }


    public void setDisplayingActivity(String displayingActivity) {
        this.displayingActivity = displayingActivity;
    }
}
