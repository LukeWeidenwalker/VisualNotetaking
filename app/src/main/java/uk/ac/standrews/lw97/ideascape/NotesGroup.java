package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


public class NotesGroup extends FrameLayout {

    NotesGroup(Context context) {
        super(context);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int childcount = getChildCount();
        // View 0 is the background
        for(int i = 1; i < childcount; i++) {
            Note note = (Note) getChildAt(i);
            if(note.checkPositionOverlap(ev.getX(), ev.getY())) {
                note.dispatchTouchEvent(ev);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
