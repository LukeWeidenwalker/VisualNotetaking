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

    private Rect hitbox;
    private Paint paint;
    int sideLength;

    int startDragX = 0;
    int startDragY = 0;
    int lastDragX = 0;
    int lastDragY = 0;

    int endDragX;
    int endDragY;
    boolean selected;

    public Note(Context context, AttributeSet attrs, String user, int[] position) {
        super(context, attrs);
        this.title = "Title";
        this.content = "Content";
        this.position = position;
        this.tag = "None";
        this.status = 1;
        this.timestamp = NoteBase.getTimeStamp();
        this.user = user;
        this.selected = false;
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
        this.selected = false;
        setupDrawing();
    }

    public void setupDrawing() {
        // Setup how it will be drawn
        this.sideLength = 300;
        this.hitbox = new Rect(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);
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

    public void changeX(int deltaX) {
        this.position[0] += deltaX;
    }

    public void changeY(int deltaY) {
        this.position[1] += deltaY;
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
        canvas.drawRect(this.hitbox, this.paint);
        //canvas.drawText(this.title, this.position[0], this.position[1], this.paintWhite);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.hitbox = new Rect(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("DEBUG", "Registered touch");
        Log.d("DEBUG", String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(checkPositionOverlap((int)event.getX(), (int)event.getY())) {
                        Log.d("DEBUG", "Note selected!");
                        this.selected = true;
                        this.lastDragX = (int) event.getX();
                        this.lastDragY = (int) event.getY();

                        this.paint.setColor(Color.BLUE);
                        invalidate();
                }
                break;

                case MotionEvent.ACTION_MOVE:
                        if(this.selected) {
                            Log.d("DEBUG", "Moving Note!");
                            int xdelta = (int) event.getX();
                            int ydelta = (int) event.getY();
                            if(Math.abs(xdelta - this.lastDragX) / 3 > 1) {
                                this.changeX(xdelta - this.lastDragX);
                            }
                            if(Math.abs(ydelta - this.lastDragY) / 3 > 1) {
                                this.changeY(ydelta - this.lastDragY);
                            }

                            this.lastDragX = xdelta;
                            this.lastDragY = ydelta;

                            Log.d("DEBUG", "New position: " + (int) event.getX() + ", " + (int) event.getY());
                            invalidate();
                        }
                        break;

                case MotionEvent.ACTION_UP:
                    if(this.selected) {
                        Log.d("DEBUG", "TOUCHUP");

                        this.endDragX = (int)event.getX();
                        this.endDragY = (int)event.getY();
                        this.setPosition(new int[] {endDragX, endDragY});
                        this.paint.setColor(Color.GRAY);
                        this.selected = false;
                        invalidate();
                    }
                    break;
            }
        return true;
    }
}
