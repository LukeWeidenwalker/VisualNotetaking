package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;


public class Background extends View {

    private int background;
    private Bitmap backgroundImage;

    Background(Context context, int background) {
        super(context);
        this.background = background;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(background);
    }
}
