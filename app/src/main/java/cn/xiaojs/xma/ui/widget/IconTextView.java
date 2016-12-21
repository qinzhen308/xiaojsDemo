package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cn.xiaojs.xma.R;

import java.util.ArrayList;
import java.util.List;

/*
 * 
 *
 * 功能描述：文字末尾带有图标的textView，图标会始终在文本的末尾居中显示，多个图标时，需要图标大小一致
 * 作者：zhanghui
 * 创建时间：2016-3-8
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class IconTextView extends View {

	private static final String MORE_STR = "...";
	private static final int GRAVITY_CENTER = 1;

	private Paint paint;
	private CharSequence mText;

	private int mMaxLine = -1;
	private float mTextSize;
	private int mTextColor;
	int line = 1;
	private float lastWidth;
	private boolean mDependIcon;
	private int mMoreHeight;
	private int mBitmapDistance;
	private int mRealTxtHeight;
	private List<Bitmap> mBitmaps;
	private int mGravity;

	private CharSequence mSpecialWords;
	private int mSpecialTextColor;
	private boolean mIsSpecial;
	private boolean mSpecialByPos;
	private int[] mPos;
	public IconTextView(Context context) {
		super(context);
		init(context, null);
	}

	public IconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public IconTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	/**
	 * 设置特定字符显示不同颜色
	 * @param text 指定字符串
	 * @param textColor 指定颜色
	 */
	public void setSpecialDisplay(CharSequence original,CharSequence dest,int textColor){
		if(TextUtils.isEmpty(dest)){
			setText(original);
			return;
		}
		mText = original;
		mSpecialWords = dest;
		mSpecialTextColor = textColor;
		mIsSpecial = true;
		mSpecialByPos = false;
		requestLayout();
	}
	
	/**
	 * 设置特定字符显示不同颜色
	 * @param text 指定字符串
	 * @param textColor 指定颜色
	 */
	public void setSpecialDisplay(CharSequence original,int[] pos,int textColor){
		if(pos == null || pos.length < 1){
			setText(original);
			return;
		}
		mText = original;
		mPos = pos;
		mSpecialTextColor = textColor;
		mIsSpecial = false;
		mSpecialByPos = true;
		requestLayout();
	}
	private void init(Context context, AttributeSet attrs) {
		setWillNotDraw(false);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		if (context != null && attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.IconTextView);
			if (a != null) {
				CharSequence s = a
						.getText(R.styleable.IconTextView_android_text);
				if (!TextUtils.isEmpty(s)) {
					mText = String.valueOf(s);
				}
				mMaxLine = a.getInt(R.styleable.IconTextView_maxLine, -1);
				mBitmapDistance = a.getDimensionPixelSize(
						R.styleable.IconTextView_iconSpacing,
						getContext().getResources().getDimensionPixelSize(R.dimen.px10));
				mMoreHeight = a.getDimensionPixelSize(
						R.styleable.IconTextView_lineSpacing,
						getContext().getResources().getDimensionPixelSize(R.dimen.px15));
				Drawable drawable = a
						.getDrawable(R.styleable.IconTextView_icon);
				mBitmaps = new ArrayList<Bitmap>();
				Bitmap b = getBitmap(drawable);
				if (b != null) {
					mBitmaps.add(b);
				}
				mTextSize = a.getDimension(
						R.styleable.IconTextView_android_textSize,
						getContext().getResources().getDimension(R.dimen.font_28px));
				mTextColor = a.getColor(
						R.styleable.IconTextView_android_textColor,
						getContext().getResources().getColor(R.color.font_black));
				mGravity = a.getInt(R.styleable.IconTextView_gravity, 0);
				paint.setTextSize(mTextSize);
				paint.setColor(mTextColor);
			}
		} else {
			paint.setTextSize(getContext().getResources().getDimension(R.dimen.font_28px));
			paint.setColor(getContext().getResources().getColor(R.color.font_black));
			mBitmapDistance = getContext().getResources().getDimensionPixelSize(R.dimen.px10);
			mMoreHeight = getContext().getResources().getDimensionPixelSize(R.dimen.px15);
		}
	}

	/**
	 * 设置显示的字符串
	 * 
	 * @param text
	 */
	public void setText(CharSequence text) {
		mIsSpecial = false;
		mSpecialByPos = false;
		this.mText = text;
		requestLayout();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(!mIsSpecial && !mSpecialByPos){//按照正常显示形式画
			if (!TextUtils.isEmpty(mText)) {
				int width = getWidth();
				FontMetricsInt fmi = paint.getFontMetricsInt();

				mRealTxtHeight = (int) (Math.ceil((fmi.descent - fmi.ascent)) / 2);
				int txtHeight = getTextHeight(mRealTxtHeight);

				drawOneLineText(canvas, mText.toString(), width, txtHeight);
				if (!isEmpty(mBitmaps)) {// 绘制图标
					for (int i = 0; i < mBitmaps.size(); i++) {
						Bitmap bitmap = mBitmaps.get(i);
						float bitmapWidth = lastWidth + mBitmapDistance;
						if (mDependIcon) {
							canvas.drawBitmap(bitmap, bitmapWidth, (line - 1)
									* txtHeight + mMoreHeight
									- getLastSpare(mRealTxtHeight) / 2, paint);
						} else {
							canvas.drawBitmap(bitmap, bitmapWidth, (line - 1)
									* txtHeight
									+ getDependStringPaddingHeight(mRealTxtHeight),
									paint);
						}
						lastWidth = bitmapWidth + bitmap.getWidth();
					}

				}
				line = 1;
				lastWidth = 0;
			}
		}else if(mIsSpecial && !mSpecialByPos){//需要显示特殊字符串的颜色
			if(hasDest(mText, mSpecialWords)){//特殊字符串完全匹配
				FontMetricsInt fmi = paint.getFontMetricsInt();
				mRealTxtHeight = (int) (Math.ceil((fmi.descent - fmi.ascent)) / 2);
				int txtHeight = getTextHeight(mRealTxtHeight);
				float maxWidth = getWidth();
				CharSequence original = mText;
				CharSequence dest = mSpecialWords;
				drawAll(canvas, original, dest, maxWidth, txtHeight);
				if (!isEmpty(mBitmaps)) {// 绘制图标
					for (int i = 0; i < mBitmaps.size(); i++) {
						Bitmap bitmap = mBitmaps.get(i);
						float bitmapWidth = lastWidth + mBitmapDistance;
						if (mDependIcon) {
							canvas.drawBitmap(bitmap, bitmapWidth, (line - 1)
									* txtHeight + mMoreHeight
									- getLastSpare(mRealTxtHeight) / 2, paint);
						} else {
							canvas.drawBitmap(bitmap, bitmapWidth, (line - 1)
									* txtHeight
									+ getDependStringPaddingHeight(mRealTxtHeight),
									paint);
						}
						lastWidth = bitmapWidth + bitmap.getWidth();
					}

				}
				line = 1;
				lastWidth = 0;
			}else {//特殊字符串未完全匹配，需要拆开字符逐一匹配
				FontMetricsInt fmi = paint.getFontMetricsInt();
				mRealTxtHeight = (int) (Math.ceil((fmi.descent - fmi.ascent)) / 2);
				int txtHeight = getTextHeight(mRealTxtHeight);
				float maxWidth = getWidth();
				float lastLineMaxWidth = getWidth() - paint.measureText(MORE_STR) - getBitmapShowWidth();
				boolean isLastLine = false;
				boolean mNeedMore = false;
				if(!isEmpty(mBitmaps)){
					if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth) || mMaxLine == line){
						isLastLine = true;
						maxWidth = lastLineMaxWidth;
					}
					if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth)){
						mNeedMore = true;
					}else if (getLeftStrWidth() > 0 && mMaxLine == line) {
						mNeedMore = true;
					}
				}
				int i=0;
				for(;i<mText.length();i++){
					char c = mText.charAt(i);
					float nextWidth = getPaintWidth() + paint.measureText(String.valueOf(c));//画之前先计算绘制当前的字符会不会超出最大宽度
					if(nextWidth >= maxWidth){//需要换行处理
						if(isLastLine){//如果文字未显示完需要显示图标，则强制不再显示文字
							break;
						}
						line++;
						paintSb.delete(0, paintSb.length());
						if(!isEmpty(mBitmaps)){
							if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth) || mMaxLine == line){
								isLastLine = true;
								maxWidth = lastLineMaxWidth;
							}
							if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth)){
								mNeedMore = true;
							}else if (getLeftStrWidth() > 0 && mMaxLine == line) {
								mNeedMore = true;
							}
						}
					}
					drawOneChar(canvas, c, getPaintHeight(line, txtHeight));
				}
				if(i == mText.length()){
					mNeedMore = false;
				}
				if(mNeedMore){//是否需要显示...
					paint.setColor(mTextColor);
					canvas.drawText(MORE_STR, getPaintWidth(), getPaintHeight(line, txtHeight), paint);
					paintSb.append(MORE_STR);
				}
				lastWidth = getPaintWidth();
				drawBitmaps(canvas, txtHeight);
				clearBuilder();
				line = 1;
				lastWidth = 0;
			}
		}else if (!mIsSpecial && mSpecialByPos) {//按指定位置标色
			FontMetricsInt fmi = paint.getFontMetricsInt();
			mRealTxtHeight = (int) (Math.ceil((fmi.descent - fmi.ascent)) / 2);
			int txtHeight = getTextHeight(mRealTxtHeight);
			float maxWidth = getWidth();
			float lastLineMaxWidth = getWidth() - paint.measureText(MORE_STR) - getBitmapShowWidth();
			boolean isLastLine = false;
			boolean mNeedMore = false;
			if(!isEmpty(mBitmaps)){
				if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth) || mMaxLine == line){
					isLastLine = true;
					maxWidth = lastLineMaxWidth;
				}
				if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth)){
					mNeedMore = true;
				}else if (getLeftStrWidth() > 0 && mMaxLine == line) {
					mNeedMore = true;
				}
			}
			int i=0;
			for(;i<mText.length();i++){
				char c = mText.charAt(i);
				float nextWidth = getPaintWidth() + paint.measureText(String.valueOf(c));//画之前先计算绘制当前的字符会不会超出最大宽度
				if(nextWidth >= maxWidth){//需要换行处理
					if(isLastLine){//如果文字未显示完需要显示图标，则强制不再显示文字
						break;
					}
					line++;
					paintSb.delete(0, paintSb.length());
					if(!isEmpty(mBitmaps)){
						if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth) || mMaxLine == line){
							isLastLine = true;
							maxWidth = lastLineMaxWidth;
						}
						if(((int)getLeftStrWidth() <= getWidth() && getLeftStrWidth() >= lastLineMaxWidth)){
							mNeedMore = true;
						}else if (getLeftStrWidth() > 0 && mMaxLine == line) {
							mNeedMore = true;
						}
					}
				}
				drawOneChar(canvas, c, getPaintHeight(line, txtHeight),i);
			}
			if(i == mText.length()){
				mNeedMore = false;
			}
			if(mNeedMore){//是否需要显示...
				paint.setColor(mTextColor);
				canvas.drawText(MORE_STR, getPaintWidth(), getPaintHeight(line, txtHeight), paint);
				paintSb.append(MORE_STR);
			}
			lastWidth = getPaintWidth();
			drawBitmaps(canvas, txtHeight);
			clearBuilder();
			line = 1;
			lastWidth = 0;
		}
		
	}
	
	private void drawAll(Canvas canvas,CharSequence original,CharSequence dest,float width,int txtHeight){
		String beforeText = getBeforeSpecialText(original, dest);
		if(TextUtils.isEmpty(beforeText)){
			if(!TextUtils.isEmpty(original)){
				if(original.toString().contains(dest)){//还有关键字,但关键字前没有非关键字，则画关键字
					drawSpecialText(canvas, dest.toString(), width, txtHeight);
					original = original.toString().substring(dest.length());
					drawAll(canvas, original, dest, width, txtHeight);
				}else {//无关键字
					drawNormalText(canvas, original.toString(), width, txtHeight);
				}
			}
		}else {
			drawNormalText(canvas, beforeText, width, txtHeight);
			original = original.toString().substring(beforeText.length());
			drawAll(canvas, original, dest, width, txtHeight);
		}
	}
	
	private void drawNormalText(Canvas canvas,String text,float maxWidth,int txtHeight){
		drawTextByColor(canvas, text, maxWidth, txtHeight, mTextColor);
		
	}
	
	private void drawSpecialText(Canvas canvas,String text,float maxWidth,int txtHeight){
		drawTextByColor(canvas, text, maxWidth, txtHeight, mSpecialTextColor);
	}
	
	private void drawTextByColor(Canvas canvas,String text,float maxWidth,int txtHeight,int color){
		paint.setColor(color);
		float toDrawWidth = 0;
		if(!TextUtils.isEmpty(text)){
			toDrawWidth = paint.measureText(text);
			if(mMaxLine > 0){
				if(mMaxLine >= line){//已到最大行
					if(lastWidth + toDrawWidth > getWidth() - getBitmapShowWidth()){
						int mWidthWithoutBitmap = getWidth() - getBitmapShowWidth() - getStringWidth(MORE_STR);
						String toShow = getLastLineWithoutBitmap(text, mWidthWithoutBitmap, (int)lastWidth);
						canvas.drawText(toShow, lastWidth, getPaintHeight(line, txtHeight), paint);
						lastWidth += getStringWidth(toShow);
						return;
					}
				}
			}
			if(maxWidth - lastWidth >= toDrawWidth){//可以直接画本行
				canvas.drawText(text, lastWidth, getPaintHeight(line, txtHeight), paint);
				lastWidth += toDrawWidth;
			}else {//本行无法画完需要换行
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < text.length(); i++) {
					char c = text.charAt(i);
					sb.append(c);
					if (getStringWidth(sb.toString()) + lastWidth >= maxWidth) {
						sb.deleteCharAt(sb.length() - 1);
						canvas.drawText(sb.toString(), 0,
								getPaintHeight(line, txtHeight), paint);
						String next = text.substring(sb.length());
						line++;
						lastWidth = 0;
						drawTextByColor(canvas, next, maxWidth, txtHeight,color);
						break;
					}
				}
			}
		}
	}
	
	//获取特殊字符串前的字符串
	private String getBeforeSpecialText(CharSequence original,CharSequence dest){
		if(!TextUtils.isEmpty(original) && !TextUtils.isEmpty(dest)){
			if(original.toString().contains(dest)){
				return original.toString().substring(0, original.toString().indexOf(dest.toString()));
			}
		}
		
		return null;
	}
	/**
	 * original是否完全含有dest字符串
	 * @param original
	 * @param dest
	 * @return
	 */
	private boolean hasDest(CharSequence original,CharSequence dest){
		if(!TextUtils.isEmpty(original) && !TextUtils.isEmpty(dest)){
			return original.toString().contains(dest);
		}
		return false;
	}
	
	private void clearBuilder(){
		allSb.delete(0, allSb.length());
		paintSb.delete(0, paintSb.length());
	}

	private void drawOneChar(Canvas canvas,char c,float txtHeight,int pos){
		//画当前字符
		if(inPos(pos)){//需要显示特殊颜色
			drawOneChar(canvas, c, mSpecialTextColor, getPaintWidth(), txtHeight);
		}else {//显示正常颜色
			drawOneChar(canvas, c, mTextColor, getPaintWidth(), txtHeight);
		}
		paintSb.append(c);
		allSb.append(c);
	}
	
	private boolean inPos(int pos){
		if(mPos != null && mPos.length > 0){
			for (int p : mPos) {
				if(p == pos){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 获取剩下的字符串的宽度
	 * @return
	 */
	private float getLeftStrWidth(){
		String left = mText.toString().substring(allSb.length());
		return paint.measureText(left);
	}
	
	private void drawBitmaps(Canvas canvas,int txtHeight){
		if (!isEmpty(mBitmaps)) {// 绘制图标
			for (int i = 0; i < mBitmaps.size(); i++) {
				Bitmap bitmap = mBitmaps.get(i);
				float bitmapWidth = lastWidth + mBitmapDistance;
				if (mDependIcon) {
					canvas.drawBitmap(bitmap, bitmapWidth, (line - 1)
							* txtHeight + mMoreHeight
							- getLastSpare(mRealTxtHeight) / 2, paint);
				} else {
					canvas.drawBitmap(bitmap, bitmapWidth, (line - 1)
							* txtHeight
							+ getDependStringPaddingHeight(mRealTxtHeight),
							paint);
				}
				lastWidth = bitmapWidth + bitmap.getWidth();
			}

		}
	}
	
	private void drawOneChar(Canvas canvas,char c,float txtHeight){
		//画当前字符
		if(charIsInSpecial(c)){//需要显示特殊颜色
			drawOneChar(canvas, c, mSpecialTextColor, getPaintWidth(), txtHeight);
		}else {//显示正常颜色
			drawOneChar(canvas, c, mTextColor, getPaintWidth(), txtHeight);
		}
		paintSb.append(c);
		allSb.append(c);
	}
	//获取已经绘制的字符串的长度，包括左边的字符间距,num从0计数
	private float getPaintWidth(){
		float width = paint.measureText(paintSb.toString());
		return width;
	}
	private StringBuilder paintSb = new StringBuilder();
	private StringBuilder allSb = new StringBuilder();
	
	private void drawOneChar(Canvas canvas,char c,int textColor,float paintWidth,float paintHeight){
		paint.setColor(textColor);
		canvas.drawText(String.valueOf(c), paintWidth, paintHeight, paint);
	}
	
	//特殊字符串中是否包含某一字符
	private boolean charIsInSpecial(char c){
		if(!TextUtils.isEmpty(mSpecialWords)){
			for (int i = 0; i < mSpecialWords.length(); i++) {
				char oneChar = mSpecialWords.charAt(i);
				if( oneChar == c){
					return true;
				}
				if(Character.isUpperCase(oneChar) && Character.isLowerCase(c)){
					if(oneChar + 32 == c){
						return true;
					}
				}
				if(Character.isLowerCase(oneChar) && Character.isUpperCase(c)){
					if(oneChar - 32 == c){
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	private void drawOneLineText(Canvas canvas, String input, float width,
			int txtHeight) {
		StringBuilder sb = new StringBuilder();
		boolean isFull = false;
		if (mMaxLine > 0) {
			if (line >= mMaxLine) {// 已经达到设置的最大行数，直接画最后一行，需要带图标
				// 最后一行文字加图片的宽度大于行宽，省略部分文字保证图片完全显示
				sb = new StringBuilder(input);
				if (getStringWidth(sb.toString()) + getBitmapShowWidth() > getWidth()) {
					int mLastLineWidthWithoutBitmap = getWidth()
							- getBitmapShowWidth() - getStringWidth(MORE_STR);
					String lastLine = getLastLineWithoutBitmap(sb.toString(),
							mLastLineWidthWithoutBitmap);
					canvas.drawText(lastLine, 0,
							getPaintHeight(line, txtHeight), paint);
					lastWidth = getStringWidth(lastLine);
				} else {
					int leftDis = 0;
					if (mGravity == GRAVITY_CENTER) {
						leftDis = (getWidth() - (getStringWidth(sb.toString()) + getBitmapShowWidth())) / 2;
					}
					canvas.drawText(sb.toString(), leftDis,
							getPaintHeight(line, txtHeight), paint);
					lastWidth = getStringWidth(sb.toString()) + leftDis;
				}
				return;
			}
		}

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			sb.append(c);
			if (getStringWidth(sb.toString()) >= width) {
				sb.deleteCharAt(sb.length() - 1);
				canvas.drawText(sb.toString(), 0,
						getPaintHeight(line, txtHeight), paint);
				String next = input.substring(sb.length());
				line++;
				drawOneLineText(canvas, next, width, txtHeight);
				isFull = true;
				break;
			}
		}

		if (!isFull) {
			// 最后一行文字加图片的宽度大于行宽，省略部分文字保证图片完全显示
			if (getStringWidth(sb.toString()) + getBitmapShowWidth() > getWidth()) {
				int mLastLineWidthWithoutBitmap = getWidth()
						- getBitmapShowWidth() - getStringWidth(MORE_STR);
				String lastLine = getLastLineWithoutBitmap(sb.toString(),
						mLastLineWidthWithoutBitmap);
				canvas.drawText(lastLine, 0, getPaintHeight(line, txtHeight),
						paint);
				lastWidth = getStringWidth(lastLine);
			} else {
				int leftDis = 0;
				if (mGravity == GRAVITY_CENTER) {
					leftDis = (getWidth() - (getStringWidth(sb.toString()) + getBitmapShowWidth())) / 2;
				}
				canvas.drawText(sb.toString(), leftDis,
						getPaintHeight(line, txtHeight), paint);
				lastWidth = getStringWidth(sb.toString()) + leftDis;
			}
		}
	}

	private String getLastLineWithoutBitmap(String s, int w) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			sb.append(s.charAt(i));
			if (getStringWidth(sb.toString()) > w) {
				sb.deleteCharAt(sb.length() - 1);
				break;
			}
		}
		sb.append(MORE_STR);
		return sb.toString();
	}

	private String getLastLineWithoutBitmap(String s, int w,int last) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			sb.append(s.charAt(i));
			if (getStringWidth(sb.toString()) + last > w) {
				sb.deleteCharAt(sb.length() - 1);
				break;
			}
		}
		sb.append(MORE_STR);
		return sb.toString();
	}
	
	private int getBitmapShowWidth() {
		if (isEmpty(mBitmaps))
			return 0;
		return (mBitmaps.get(0).getWidth() + mBitmapDistance) * mBitmaps.size();
	}

	private boolean isEmpty(List<Bitmap> maps) {
		if (maps == null)
			return true;
		if (maps.size() < 1)
			return true;
		return false;
	}

	private int getTextHeight(int txtHeight) {

		int th = txtHeight + mMoreHeight;
		if (isEmpty(mBitmaps)) {
			mDependIcon = false;
			if(mMoreHeight < getContext().getResources().getDimensionPixelSize(R.dimen.px10)){
				mMoreHeight = getContext().getResources().getDimensionPixelSize(R.dimen.px10);
			}
			return txtHeight + mMoreHeight;
		}
		if(mBitmaps!= null && mBitmaps.size() > 0){
			int bitHeight = mBitmaps.get(0).getHeight();

			if (bitHeight > txtHeight) {
				th = bitHeight / 2 + txtHeight / 2 + mMoreHeight;
				mDependIcon = true;
			} else {
				mDependIcon = false;
			}
			return th;
		}else{
			mDependIcon = false;
			if(mMoreHeight < getContext().getResources().getDimension(R.dimen.px10));
			return (int) (txtHeight + getContext().getResources().getDimension(R.dimen.px10));
		}

	}

	private int getDependStringPaddingHeight(int txtHeight) {
		int bitHeight = mBitmaps.get(0).getHeight();
		return txtHeight / 2 - bitHeight / 2 + mMoreHeight;
	}

	private int getPaintHeight(int line, int height) {
		return height * line;
	}

	private int getStringWidth(String str) {
		return (int) Math.ceil(paint.measureText(str));
	}

	private int getLastSpare(int realTxtHeight) {
		return (mBitmaps.get(0).getHeight() - realTxtHeight) / 2;
	}

	private Bitmap getBitmap(Drawable drawable) {
		if (drawable != null) {
			if (drawable instanceof BitmapDrawable) {
				return ((BitmapDrawable) drawable).getBitmap();
			}
		}
		return null;
	}

	/**
	 * 设置文字显示的最大行数
	 * <p>
	 * 当文字超过最大行数时，会将文字截断以【...】显示，并在本行末尾添加需要显示的图标
	 * 
	 * @param maxLine
	 */
	public void setMaxLine(int maxLine) {
		this.mMaxLine = maxLine;
		requestLayout();
	}

	/**
	 * 设置文本大小
	 * 
	 * @param textSize
	 */
	public void setTextSize(float textSize) {
		this.mTextSize = textSize;
		paint.setTextSize(textSize);
		requestLayout();
	}

	/**
	 * 设置文本颜色
	 * 
	 * @param color
	 */
	public void setTextColor(int color) {
		this.mTextColor = color;
		paint.setColor(color);
		postInvalidate();
	}

	/**
	 * 设置单个图标
	 * 
	 * @param resId
	 *            参数小于1会不显示图标
	 */
	public void setIcon(int resId) {
		Drawable drawable = null;
		if (resId > 0) {
			drawable = getContext().getResources().getDrawable(resId);
		}
		setIcon(drawable);
	}

	/**
	 * 设置单个图标
	 * 
	 * @param drawable
	 *            参数为null会不显示图标
	 */
	public void setIcon(Drawable drawable) {
		if (drawable == null) {
			mBitmaps = null;
			requestLayout();
			return;
		}
		List<Drawable> d = new ArrayList<Drawable>();
		d.add(drawable);
		addIcons(d);
	}

	/**
	 * 设置多个图标,多个图标需要大小一致
	 * 
	 * @param resIds
	 *            参数为null会不显示图标
	 */
	public void setIcons(int[] resIds) {
		mBitmaps = null;
		if (resIds != null) {
			List<Integer> ids = new ArrayList<Integer>();
			for (int i : resIds) {
				ids.add(i);
			}
			setIcons(ids);
		}
	}

	/**
	 * 设置多个图标,多个图标需要大小一致
	 * 
	 * @param resIds
	 *            参数为null会不显示图标
	 */
	public void setIcons(List<Integer> resIds) {
		mBitmaps = null;
		if (resIds != null) {
			List<Drawable> ds = new ArrayList<Drawable>();
			for (int id : resIds) {
				Drawable d = getResources().getDrawable(id);
				ds.add(d);
			}
			addIcons(ds);
		}
	}

	/**
	 * 设置多个图标,多个图标需要大小一致
	 * 
	 * @param drawables
	 *            参数为null会不显示图标
	 */
	public void addIcons(List<Drawable> drawables) {
		mBitmaps = null;
		if (drawables != null && drawables.size() > 0) {
			mBitmaps = new ArrayList<Bitmap>();
			for (Drawable drawable : drawables) {
				Bitmap b = getBitmap(drawable);
				mBitmaps.add(b);
			}
		}
		requestLayout();
	}

	/**
	 * 设置文字显示方式
	 * 
	 * @param gravity
	 *            参照本类的GRAVITY_LEFT和GRAVITY_CENTER
	 */
	public void setGravity(int gravity) {
		this.mGravity = gravity;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(width);

		setMeasuredDimension(width, height);
	}

	private int measureWidth(int mSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(mSpec);
		int specSize = MeasureSpec.getSize(mSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			int txtLength = 0;
			if (!TextUtils.isEmpty(mText)) {
				txtLength = (int) paint.measureText(mText.toString());
			}
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				if (txtLength > specSize) {
					result = specSize;
				} else {
					if (!isEmpty(mBitmaps)) {
						int lengthWithBitmap = txtLength
								+ mBitmaps.get(0).getWidth() * mBitmaps.size();
						if (specSize < lengthWithBitmap) {
							result = specSize;
						} else {
							result = lengthWithBitmap;
						}
					} else {
						result = txtLength;
					}

				}
			}

		}

		return result;
	}

	private int getLineNum(int width) {
		int txtLength = 0;
		if (!TextUtils.isEmpty(mText)) {
			txtLength = (int) paint.measureText(mText.toString());
		}
		int res = 1;
		if (txtLength >= width) {
			res = txtLength / width + 1;
		}
		if (mMaxLine > 0) {
			res = Math.min(res, mMaxLine);
		}
		return res;

	}

	private int measureHeight(int width) {
		if (width <= 0)
			return 0;
		FontMetricsInt fmi = paint.getFontMetricsInt();

		int realTxtHeight = (int) (Math.ceil((fmi.descent - fmi.ascent)) / 2);
		int txtHeight = getTextHeight(realTxtHeight);
		int res = getLineNum(width) * txtHeight;
		if (mDependIcon) {
			if (!isEmpty(mBitmaps)) {
				res += (mBitmaps.get(0).getHeight() - realTxtHeight) / 2;
			}
		}
		res += getContext().getResources().getDimensionPixelSize(R.dimen.px1) * 10;
		return res;
	}

}
