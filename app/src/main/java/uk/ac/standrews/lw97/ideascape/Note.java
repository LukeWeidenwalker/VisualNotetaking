package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class Note extends View {
    String title;
    String content;
    int[] position;
    String tag;
    int status;
    String timestamp;
    String user;

    private Rect rectangle;
    private Paint paint;
    int sideLength;



    public Note(Context context, AttributeSet attrs, String user, int[] position) {
        super(context, attrs);
        this.title = "Title";
        this.content = "Content";
        this.position = position;
        this.tag = "None";
        this.status = 1;
        this.timestamp = NoteBase.getTimeStamp();
        this.user = user;
        setupDrawing();
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
        setupDrawing();
    }

    public void setupDrawing() {
        // Setup how it will be drawn
        this.sideLength = 100;

        this.rectangle = new Rect(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);
        this.paint = new Paint();
        this.paint.setColor(Color.GRAY);
    }

    public String getTag() {
        return this.tag;
    }


    public void setContent(String content) {
        this.content = content;
        invalidate();
    }

    public void addContent(String content) {
        this.content += content;
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    public void setPosition(int[] position) {
        this.position = position;
        invalidate();
    }

    public void setTag(String tag) {
        this.tag = tag;
        invalidate();
    }

    public String[] saveNote() {
        // Return a full string array containing all attributes of this note
        return new String[] {this.user, this.timestamp, this.content, this.title, String.valueOf(this.position[0]),
                String.valueOf(this.position[1]), String.valueOf(this.status), this.tag};
    }

    private boolean checkPositionOverlap(float x, float y) {
        return (x > this.position[0] && x < this.position[0] + this.sideLength && y > this.position[1] && y < this.position[1] + this.sideLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(this.rectangle, this.paint);
        //canvas.drawText(this.title, this.position[0], this.position[1], this.paintWhite);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.d("DEBUG", "Registered touch");
        Log.d("DEBUG", String.valueOf(x) + ", " + String.valueOf(y));

        if(checkPositionOverlap(x, y)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("DEBUG", "TOUCHDOWN");

                    this.paint.setColor(Color.GRAY);
                    //touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("DEBUG", "TOUCHMOVE");

                    //touch_move(x, y);
                    //invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("DEBUG", "TOUCHUP");

                    this.paint.setColor(Color.BLUE);

                    //touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
        else {
            return false;
        }


    }
}
