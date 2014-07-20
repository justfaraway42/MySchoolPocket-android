package de.justproductions.myschoolpocket.plans;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
 
public class ReloadButton extends ImageButton {
 
    public ReloadButton(Context context) {
        super(context);
    }
 
    public ReloadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public ReloadButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    public void setPressed(boolean pressed) {
        if (pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }
 
}
