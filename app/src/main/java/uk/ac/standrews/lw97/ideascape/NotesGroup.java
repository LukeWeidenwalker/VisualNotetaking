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

    private String tagGroup;
    long startClickTime;
    long firstClickTime = 0;
    int numberClicks;
    NoteBase noteBase;
    String user;
    SparseArray<PointF> mActivePointers;
    double differencePoints;
    int pinchCounter;



    NotesGroup(Context context, NoteBase noteBase, String tagGroup, String user) {
        super(context);
        this.tagGroup = tagGroup;
        this.addView(new Background(context));
        this.addViews(noteBase);
        this.noteBase = noteBase;
        this.user = user;
        mActivePointers = new SparseArray<>();
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
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Detect pinch gestures
        // As in http://www.vogella.com/tutorials/AndroidTouch/article.html#exercise-multitouch.




        // Proceed normally if only one pointer is detected.

//        int childCount = getChildCount();
//        // Dispatching touch events to any child views that are touched
//        // Remember: View 0 is the background
//        for (int i = 1; i < childCount; i++) {
//            Note note = (Note) getChildAt(i);
//            if (note.checkPositionOverlap(event.getX(), event.getY())) {
//                note.dispatchTouchEvent(event);
//                break;
//            }
//        }
        return false;
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
                            int[] position = new int[]{(int) event.getX() - Note.standardSideLength / 2, (int) event.getY() - Note.standardSideLength / 2};
                            Note note = new Note(getContext(), null, user, position, "New");
                            note.setParentNoteGroup(this);
                            this.addView(note);
                            this.noteBase.addNote(note);
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

            // a pointer was moved
            case MotionEvent.ACTION_MOVE: {
                //Log.d("DEBUG", "Multiple Pointers: MOVED");
                double[][] distances = new double[2][2];
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.get(event.getPointerId(i));
                    if (point != null) {
                        distances[i][0] = (event.getX(i) - point.x);
                        distances[i][1] = (event.getY(i) - point.y);
                        point.x = event.getX(i);
                        point.y = event.getY(i);
                    }
                }

                try {
                    Log.d("DEBUG", "Total distance: " + (Math.abs(distances[0][0]) + Math.abs(distances[1][0]) + Math.abs(distances[0][1]) + Math.abs(distances[1][1])));
                    Log.d("DEBUG", "XOffset: " + (Math.abs(distances[0][0] + distances[1][0])));
                    Log.d("DEBUG", "YOffset: " + (Math.abs(distances[0][1] + distances[1][1])));


                    if (Math.abs((distances[0][0] + distances[1][0])) < 2.6 && Math.abs(distances[0][1] + distances[1][1]) < 3) {
                        if (Math.abs(distances[0][0]) + Math.abs(distances[1][0]) + Math.abs(distances[0][1]) + Math.abs(distances[1][1]) > 0.5) {
                            pinchCounter++;
                            Log.d("DEBUG", "Pinchcounter: " + pinchCounter);
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
                if (pinchCounter >= 4) {
                    Log.d("DEBUG", "Pinch detected");
                    pinchCounter = 0;
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
}
