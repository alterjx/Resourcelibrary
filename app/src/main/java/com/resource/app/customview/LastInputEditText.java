package com.resource.app.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * TODO: description
 *
 */

public class LastInputEditText extends EditText {

    public LastInputEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LastInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LastInputEditText(Context context) {
        super(context);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
    }
}
