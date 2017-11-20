package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.util.Calendar;


public class Note extends View {
    String displayingActivity;
    String title;
    String content;
    String tag;
    int status;
    String timestamp;
    String user;
    NotesGroup parentNoteGroup;
    static String newline = System.getProperty("line.separator");

    int[] position;
    int midX;
    int midY;

    private RectF hitbox;
    private RectF hitboxStroke;
    private RectF star;


    private Paint paintRect;
    private Paint paintStroke;
    private Paint paintText;

    static int standardSideLength = 300;
    int sideLength;
    int starRadius;
    int strokeWidth;
    Context context;

    // Keyboard
    String mText;
    String inputDecider = "";
    OnKeyListener keyListener;
    InputMethodManager imm;

    int primaryColor = getResources().getColor(R.color.colorPrimary, null);
    int darkPrimaryColor = getResources().getColor(R.color.colorPrimaryDark, null);
    int accentColor = getResources().getColor(R.color.colorAccent, null);
    int invisibleColor = getResources().getColor(R.color.invisible, null);

    // Motion variables
    int lastDragX = 0;
    int lastDragY = 0;
    boolean selected;
    boolean keyboardExpanded = false;
    long startClickTime;
    static final int MAX_CLICK_DURATION = 200;

    // -------------------
    // Constructors
    // -------------------
    public Note(Context context, AttributeSet attrs, String user, int[] position, String title, String displayingActivity) {
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
        this.displayingActivity = displayingActivity;
        setupDrawing();
        setupKeyboard();

    }

    public Note(Context context, AttributeSet attrs, String[] valuesLine, String displayingActivity) {
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
        this.displayingActivity = displayingActivity;

        setupDrawing();
        setupKeyboard();
    }


    // -------------------
    // Keyboard
    // -------------------
    public boolean inputDeciderSetter(String mText) {
        if(inputDecider.equals("title")) {
            setTitle(mText);
        }
        else if (inputDecider.equals("content")) {
            setContent(mText);
        }
        return true;
    }

    public boolean initMText() {
        if(inputDecider.equals("title")) {
            mText = title;
        }
        else if (inputDecider.equals("content")){
            mText = content;
        }
        return true;
    }

    public void interpretKeycode(int keyCode, KeyEvent event) {
        // Normal Alphabet + chars
        if (((keyCode >= KeyEvent.KEYCODE_A) && (keyCode <= KeyEvent.KEYCODE_PERIOD)) || (keyCode == KeyEvent.KEYCODE_SPACE)
                || ((keyCode >= KeyEvent.KEYCODE_MINUS) && (keyCode <= KeyEvent.KEYCODE_AT))) {
            mText = mText + (char) event.getUnicodeChar();
        }

        // Delete Button
        else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if(mText.length() > 0) {
                mText = mText.substring(0, mText.length() - 1);
            }
        }

        // Enter Button
        else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            mText = mText + newline;
        }
    }

    public void setupKeyboard() {
        // As in https://stackoverflow.com/questions/27717531/get-input-text-with-customview-without-edittext-android
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        initMText();

        keyListener = new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    interpretKeycode(keyCode, event);
                    return inputDeciderSetter(mText);
                }
                return false;
            }
        };
        setOnKeyListener(keyListener);
    }


    // -------------------
    // Drawing
    // -------------------
    public void setupDrawing() {
        // Setup how it will be drawn


        this.sideLength = 300;
        this.starRadius = 20;
        this.strokeWidth = 2;
        this.hitbox = new RectF(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);
        this.hitboxStroke = new RectF(this.position[0] - this.strokeWidth, this.position[1] - this.strokeWidth,
                this.position[0] + sideLength + this.strokeWidth, this.position[1] + sideLength + this.strokeWidth);
        this.star = new RectF(this.position[0], this.position[1], this.position[0] + this.starRadius, this.position[1] + this.starRadius);

        this.midX = this.position[0] + (this.sideLength / 2);
        this.midY = this.position[1] + (this.sideLength / 2);
        this.paintRect = new Paint();
        this.paintRect.setColor(this.darkPrimaryColor);
        this.paintStroke = new Paint();
        this.paintStroke.setColor(this.darkPrimaryColor);
        //this.paintStroke.setShader(new LinearGradient(0, 0, 0, getHeight(), this.darkPrimaryColor, this.accentColor, Shader.TileMode.MIRROR));
        this.paintText = new Paint();
        this.paintText.setColor(this.primaryColor);
        this.paintText.setTextAlign(Paint.Align.CENTER);
        this.paintText.setTextSize(72);
        if (this.displayingActivity != null) {
            if (this.displayingActivity.equals("constellation")) {
                Typeface bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/AppleSDGothicNeo.ttc");
                this.paintText.setTypeface(bold);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.displayingActivity != null) {
            if (this.displayingActivity.equals("constellation")) {
                onDrawConstellation(canvas);
            }
            else {
                onDrawUniverse(canvas);
            }
        }
        else {
            Log.d("DEBUG", "Mistake here");

        }
    }

    public void onDrawConstellation(Canvas canvas) {
        if(selected) {
            this.paintStroke.setColor(this.accentColor);
        }
        else {
            this.paintStroke.setColor(this.invisibleColor);
        }
        this.hitbox = new RectF(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);

        canvas.drawRoundRect(this.hitboxStroke, this.sideLength / 6, this.sideLength / 6, this.paintStroke);
        canvas.drawRoundRect(this.hitbox, this.sideLength / 6, this.sideLength / 6, this.paintRect);
        canvas.drawText(this.title, this.hitbox.centerX(), this.hitbox.top + (this.sideLength / 3), this.paintText);
        canvas.drawText(this.content, this.hitbox.centerX(), this.hitbox.top + (5 * this.sideLength / 6), this.paintText);
        canvas.drawText("Constellation: " + tag, 600, 65, paintText);

    }

    public void onDrawUniverse(Canvas canvas) {
        this.hitbox = this.star;
        canvas.drawRoundRect(this.star, 5, 5, this.paintText);
        this.paintText.setTextSize(24);
        canvas.drawText(this.tag, this.star.centerX() + 30, this.star.centerY() - 30, this.paintText);
        this.paintText.setTextSize(78);
        canvas.drawText("Universe", 600, 120, paintText);


    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.hitboxStroke = new RectF(this.position[0] - this.strokeWidth, this.position[1] - this.strokeWidth,
                this.position[0] + sideLength + this.strokeWidth, this.position[1] + sideLength + this.strokeWidth);
        this.hitbox = new RectF(this.position[0], this.position[1], this.position[0] + sideLength, this.position[1] + sideLength);
        this.star = new RectF(this.position[0], this.position[1], this.position[0] + this.starRadius, this.position[1] + this.starRadius);
    }


    // -------------------
    // Handling Touch Events
    // -------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("DEBUG", "Registered touch on: " + this.title);
        Log.d("DEBUG", String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()));

        int xEvent = (int) event.getX();
        int yEvent = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Collapse Keyboard if necessary.
                if(keyboardExpanded) {
                    collapseKeyboard();
                }

                // Setting motion variables
                if (checkPositionOverlap(xEvent, yEvent)) {
                    startClickTime = Calendar.getInstance().getTimeInMillis();

                    this.selected = true;
                    this.lastDragX = (int) event.getX();
                    this.lastDragY = (int) event.getY();

                    // Add a glowing outline to the note when selected
                    return true;
                }
                //else {
                    //this.selected = false;
                //}
                break;


            case MotionEvent.ACTION_MOVE:
                if (this.selected) {
                    //Log.d("DEBUG", "Moving Note!");
                    int xdelta = (int) event.getX();
                    int ydelta = (int) event.getY();
                    this.changeX(xdelta - this.lastDragX);
                    this.changeY(ydelta - this.lastDragY);
                    this.lastDragX = xdelta;
                    this.lastDragY = ydelta;
                    invalidate();
                    return true;
                }
                break;


            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - this.startClickTime;
                if (clickDuration < MAX_CLICK_DURATION) {
                    Log.d("DEBUG", "Y value: " + event.getY());
                    if(event.getY() < (hitbox.top + (this.sideLength/3))){
                        Log.d("DEBUG", "Editing title.");
                        this.inputDecider = "title";
                    }
                    else {
                        Log.d("DEBUG", "Editing content.");

                        this.inputDecider = "content";
                    }

                    Log.d("DEBUG", "Trying to open keyboard on: " + this.title);

                    initMText();
                    expandKeyboard();
                    invalidate();
                    return true;
                }

                if (this.selected) {
                    this.paintStroke.setColor(this.darkPrimaryColor);
                    this.selected = false;
                    invalidate();
                    return true;
                }

                break;
        }
        return false;
    }


    // -------------------
    // Misc
    // -------------------

    boolean checkPositionOverlap(float x, float y) {
        return (x > this.position[0] && x < this.position[0] + this.sideLength && y > this.position[1] && y < this.position[1] + this.sideLength);
    }

    public void expandKeyboard() {
        this.requestFocus();
        this.requestFocusFromTouch();
        this.imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        this.keyboardExpanded = true;
    }

    public void collapseKeyboard() {
        this.requestFocus();
        this.requestFocusFromTouch();
        this.imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
        this.keyboardExpanded = false;
    }


    // -------------------
    // Setters and getters
    // -------------------

    public String getTag() {
        return this.tag;
    }

    public void setContent(String content) {
        this.content = content;
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

    public void setParentNoteGroup(NotesGroup parentNoteGroup) {
        this.parentNoteGroup = parentNoteGroup;
    }

    public void setDisplayingActivity(String act) {
        this.displayingActivity = act;
    }

    public String[] saveNote() {
        // Return a full string array containing all attributes of this note
        return new String[]{this.user, this.timestamp, this.content, this.title, String.valueOf(this.position[0]),
                String.valueOf(this.position[1]), String.valueOf(this.status), this.tag};
    }
}
