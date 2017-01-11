package cn.xiaojs.xma.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/1/11.
 */

public class RecipientEditText extends EditText {

    public RecipientEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addRecipient(String recipientId,String recipientName) {

        RecipientSpan span = createSpan(recipientId,recipientName);

        Editable editable = getText();
        int start = editable.length();
        int end = start + recipientName.length();

        editable.append(recipientName);

        editable.setSpan(span,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public List<String> getRecipientIds() {

        List<String> ids = null;
        Editable editable = getText();
        RecipientSpan[] spans = editable.getSpans(0,editable.length(),RecipientSpan.class);
        if (spans != null && spans.length > 0) {

            ids = new ArrayList<>(spans.length);
            for (RecipientSpan span : spans) {
                ids.add(span.recipientId);
            }
        }

        return ids;
    }

    private RecipientSpan createSpan(String recipientId,String recipientName) {
        Bitmap bitmap = createBitmap(recipientName,getPaint());
        Drawable result = new BitmapDrawable(getResources(), bitmap);
        result.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        return new RecipientSpan(result,recipientId,recipientName);
    }

    private Bitmap createBitmap(String text,TextPaint paint) {

        int width = (int) paint.measureText(text);
        int height = getLineHeight(); //(int) getResources().getDimension(R.dimen.chip_height);
        int originColor = paint.getColor();

        paint.setColor(getResources().getColor(R.color.input_at));

        Bitmap tmpBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tmpBitmap);
        canvas.drawText(text,0,text.length(),0,getTextYOffset(text,getPaint(),height),getPaint());
        paint.setColor(originColor);

        return tmpBitmap;
    }

    private float getTextYOffset(String text, TextPaint paint, int height) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textHeight = bounds.bottom - bounds.top ;
        return height - ((height - textHeight) / 2) - (int)paint.descent();
    }

//    private class RecipientTextWatcher implements TextWatcher {
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            Log.d("===NO","start="+start+", before="+before+", count="+count +", selectStart="+getSelectionStart());
//
//            if (before > count) {
//               //del key
//            }
//
//            MyImageSpan[] span = getText().getSpans(0,getSelectionStart(),MyImageSpan.class);
//            if (span != null)
//                Log.d("***len=","="+span.length);
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//
//        }
//    }


    private static class RecipientSpan extends ImageSpan{
        protected String recipientId;
        protected String recipientName;

        public RecipientSpan(Drawable d,String recipientId,String recipientName) {
            super(d, DynamicDrawableSpan.ALIGN_BOTTOM);
            this.recipientId = recipientId;
            this.recipientName = recipientName;
        }
    }


}
