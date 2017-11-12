package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class Note extends View {
    String title;
    String content;
    int[] position;
    String tag;
    int status;
    String timestamp;
    String user;


    public Note(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public Note(Context context, AttributeSet attrs, String[] valuesLine) {
        super(context, attrs);
        this.position = new int[2];
        this.user = valuesLine[0];
        this.timestamp = valuesLine[1];
        this.content = valuesLine[2];
        this.title = valuesLine[3];
        this.position[0] = Integer.parseInt(valuesLine[4]);
        this.position[1] = Integer.parseInt(valuesLine[5]);
        this.status = Integer.parseInt(valuesLine[6]);
        this.tag = valuesLine[7];
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

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String[] saveNote() {
        // Return a full string array containing all attributes of this note
        return new String[] {this.user, this.timestamp, this.content, this.title, String.valueOf(this.position[0]),
                String.valueOf(this.position[1]), String.valueOf(this.status), this.tag};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
