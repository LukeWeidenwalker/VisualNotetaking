package uk.ac.standrews.lw97.ideascape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;


public class Background extends AppCompatImageView {

    private Bitmap backgroundImage;

    Background(Context context, int backgroundColor) {
        super(context);
        backgroundImage = Bitmap.createBitmap(MainActivity.getScreenWidth(), MainActivity.getScreenHeight(), Bitmap.Config.ARGB_8888);
        backgroundImage.eraseColor(backgroundColor);
        this.setImageBitmap(this.backgroundImage);
    }


    Background(Context context) {
        super(context);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.galaxy_bg_1);
        this.backgroundImage = Bitmap.createScaledBitmap(b, MainActivity.getScreenWidth() + 128, MainActivity.getScreenHeight(), false);
        this.setImageBitmap(this.backgroundImage);
    }
}
