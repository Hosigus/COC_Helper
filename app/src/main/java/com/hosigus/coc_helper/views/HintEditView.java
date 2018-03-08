package com.hosigus.coc_helper.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hosigus.coc_helper.R;

/**
 * Created by 某只机智 on 2018/2/14.
 * 用于展示和编辑人物卡的，左侧有提示文本的EditText
 * 如： (hint)  (ET)
 *      姓名：  XXX
 */

public class HintEditView extends LinearLayout{

    private TextView hintView;
    private EditText editView;

    public HintEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    /**
     * 初始化数据
     */
    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.item_hint_edit,this,true);
        hintView = findViewById(R.id.item_hint_edit_hint);
        editView = findViewById(R.id.item_hint_edit_edit);

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.HintEditView);
        hintView.setText(typedArray.getString(R.styleable.HintEditView_hintText));
        editView.setInputType(typedArray.getInteger(R.styleable.HintEditView_textInputType, InputType.TYPE_CLASS_TEXT));
        boolean editAble = typedArray.getBoolean(R.styleable.HintEditView_editAble,true);
        if (!editAble){
            editView.setEnabled(false);
            editView.setTextColor(Color.BLACK);
        }
        typedArray.recycle();
    }
    public void setHintText(String text) {
        hintView.setText(text);
    }
    public void setEditText(String text) {
        editView.setText(text);
    }
    public void setOnChangeListener(TextView.OnEditorActionListener listener){
        editView.setOnEditorActionListener(listener);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        editView.setOnFocusChangeListener(onFocusChangeListener);
    }

    public String getEditText() {
        if (editView.getText()==null)
            return "";
        return editView.getText().toString();
    }
    public void setEditAble(boolean able){
        editView.setEnabled(able);
    }
}
