package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class Note extends View {
    String title;
    String content;
    int[] position;
    int tag;
    int status;


    public Note(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public Note(Context context, AttributeSet attrs, int[] position) {
        super(context, attrs);
        this.position = position;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void addContent(String content) {
        this.content += content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
