package com.SigmaDating.apk.utilities;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class PhoneTextWatcher implements TextWatcher {
    EditText Edt;
    int keyDel = 0;

    public PhoneTextWatcher(EditText edt) {
        Edt = edt;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Edt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL)
                    keyDel = 1;
                return false;
            }
        });

        if (keyDel == 0) {
            int len = Edt.getText().length();
            String input = Edt.getText().toString();
            if (len == 4) {
                Edt.setText(input.substring(0, 3) + "-" + input.substring(3));
                Edt.setSelection(Edt.getText().length());
            }
            if (len == 8) {
                Edt.setText(input.substring(0, 7) + "-" + input.substring(7));
                Edt.setSelection(Edt.getText().length());
            }
        } else if (keyDel == 1) {
            int len = Edt.getText().length();
            if (len == 4 || len == 8) {
                Edt.setText(Edt.getText().delete(Edt.getText().length() - 1, Edt.getText().length()));
                Edt.setSelection(Edt.getText().length());
            }
            keyDel = 0;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}