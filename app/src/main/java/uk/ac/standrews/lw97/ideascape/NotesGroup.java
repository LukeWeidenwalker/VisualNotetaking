package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class NotesGroup extends FrameLayout {
    NotesGroup(Context context) {
        super(context);
        this.addView(new Background(context));
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
        Log.d("DEBUG", "TouchEvent bubbled up!");
        return true;
    }
}
