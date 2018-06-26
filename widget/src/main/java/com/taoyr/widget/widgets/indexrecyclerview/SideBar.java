package com.taoyr.widget.widgets.indexrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.taoyr.widget.R;

public class SideBar extends View {

    //private static final int TEXT_DIALOG_OFFSET = PictureUtils.dip2px(20);
    private int mTextDialogOffset = dip2px(getContext(), 20);
    public static final String[] DEFAULT_INDEX_TABLE = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private int mChoosing = -1;// 选中
    private Paint mPainter = new Paint();
    private TextView mTextDialog;
    private String[] mIndexTable;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    private String[] getIndexTable() {
        if (mIndexTable == null || mIndexTable.length < 1) {
            return DEFAULT_INDEX_TABLE;
        }
        return mIndexTable;
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        int singleHeight = height / getIndexTable().length;// 获取每一个字母的高度

        for (int i = 0; i < getIndexTable().length; i++) {
            mPainter.setColor(getResources().getColor(R.color.colorPrimary));
            mPainter.setTypeface(Typeface.DEFAULT_BOLD);
            mPainter.setAntiAlias(true);
            mPainter.setTextSize(sp2px(getContext(), 12));
            // 选中的状态
            if (i == mChoosing) {
                // mPainter.setColor(Color.parseColor("#3399ff"));
                mPainter.setColor(getResources().getColor(R.color.colorAccent));
                mPainter.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - mPainter.measureText(getIndexTable()[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(getIndexTable()[i], xPos, yPos, mPainter);
            mPainter.reset();// 重置画笔
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = mChoosing;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * getIndexTable().length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                mChoosing = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                int textHeight = mTextDialog.getHeight();
                int textWidth = mTextDialog.getWidth();
                // float yPox = event.getRawY() - textWidth / 2;
                // getRawY得到的是相对于Global屏幕的绝对位置，getY为父容器中的相对位置
                // setY是设定在父容器中的相对位置，因此应该取getY
                float yPox = event.getY() - textHeight / 2; // 居中显示
                float xPos = getX() - textWidth - mTextDialogOffset;
                setBackgroundResource(R.drawable.shape_sidebar_background);
                if (c >= 0 && c < getIndexTable().length) {
                    if (mTextDialog != null) {
                        mTextDialog.setText(getIndexTable()[c]);
                        mTextDialog.setTextSize(30);
                        mTextDialog.setVisibility(View.VISIBLE);
                        // 跟随手指移动
                        mTextDialog.setX(xPos);
                        mTextDialog.setY(yPox);
                    }

                    if (oldChoose != c) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(getIndexTable()[c]);
                        }
                        mChoosing = c;
                    }

                    // 更新画面，触发onDraw（可能不会立即调用）
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public void setIndxTable(String[] table) {
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}