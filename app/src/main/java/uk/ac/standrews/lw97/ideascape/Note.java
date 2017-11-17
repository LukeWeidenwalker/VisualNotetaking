package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class Note extends View {
    String title;
    String content;
    String tag;
    int status;
    String timestamp;
    String user;

    int[] position;
    int midX;
    int midY;

    private RectF hitbox;
    private Paint paintRect;
    private Paint paintText;
    int sideLength;
    Context context;

    int primaryColor = getResources().getColor(R.color.colorPrimary, null);
    int darkPrimaryColor = getResources().getColor(R.color.colorPrimaryDark, null);
    int accentColor = getResources().getColor(R.color.colorAccent, null);


    int lastDragX = 0;
    int lastDragY = 0;
    boolean selected;



    public Note(Context context, AttributeSet attrs, String user, int[] position, String title) {
        super(context, attrs);
        this.context = context;
        this.title = title;
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
        Typeface bold = Typeface.createFromAsset(this.context.getAssets(), "fonts/AppleSDGothicNeo.ttc");

        this.sideLength = 300;
        this.hitbox = new RectF(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);
        this.midX = this.position[0] + (this.sideLength / 2);
        this.midY = this.position[1] + (this.sideLength / 2);
        this.paintRect = new Paint();
        this.paintRect.setColor(this.darkPrimaryColor);
        this.paintText = new Paint();
        this.paintText.setColor(this.primaryColor);
        this.paintText.setTypeface(bold);
        this.paintText.setTextSize(72);
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
        this.midX += deltaX;
    }

    public void changeY(int deltaY) {
        this.position[1] += deltaY;
        this.midY += deltaY;
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

    boolean checkPositionOverlap(float x, float y) {
        return (x > this.position[0] && x < this.position[0] + this.sideLength && y > this.position[1] && y < this.position[1] + this.sideLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(this.hitbox, this.sideLength/6, this.sideLength/6, this.paintRect);
        canvas.drawText(this.title, this.hitbox.centerX(), this.hitbox.centerY(), this.paintText);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.hitbox = new RectF(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);
    }

    @Override
    public boolean callOnClick() {
        Log.d("DEBUG", "Registered click on: " + this.title);
        return super.callOnClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("DEBUG", "Registered touch on: " + this.title);
        Log.d("DEBUG", String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()));

        int xEvent = (int)event.getX();
        int yEvent = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(checkPositionOverlap(xEvent, yEvent)) {
                    //Log.d("DEBUG", "Note selected!");
                    this.selected = true;
                    this.lastDragX = (int) event.getX();
                    this.lastDragY = (int) event.getY();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(this.selected) {
                    //Log.d("DEBUG", "Moving Note!");
                    int xdelta = (int) event.getX();
                    int ydelta = (int) event.getY();
                    this.changeX(xdelta - this.lastDragX);
                    this.changeY(ydelta - this.lastDragY);
                    this.lastDragX = xdelta;
                    this.lastDragY = ydelta;

                    //Log.d("DEBUG", "New position: " + (int) event.getX() + ", " + (int) event.getY());
                }
                break;

            case MotionEvent.ACTION_UP:
                if(this.selected) {
                    //Log.d("DEBUG", "TOUCHUP");
                    this.paintRect.setColor(this.darkPrimaryColor);
                    this.selected = false;
                }

                break;
        }
        invalidate();
        return true;
    }
}
