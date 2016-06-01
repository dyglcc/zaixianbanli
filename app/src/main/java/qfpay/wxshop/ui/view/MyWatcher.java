package qfpay.wxshop.ui.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class MyWatcher implements TextWatcher {
	public MyWatcher(EditText edittext){
		this.focuseEditTextView = edittext;
	}
	private EditText focuseEditTextView;
	private int len;
	private int beforeLen;
	private int afterLen;
	private int selectionPositon;

	private boolean endIsFengefu;
	private boolean change = true;
	public abstract void nextDo(Object...obj);
	public abstract String tiaoShuju(String str,String fengefu);

	public void onTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		beforeLen = arg0.length();
		selectionPositon = focuseEditTextView.getSelectionStart();
	}


	
	public void afterTextChanged(Editable afterStr) {

		if (change) {
			change = false;
			return;
		}
		if (afterStr == null) {
			return;
		}
		if (afterStr.equals("")) {
			return;
		}
		afterLen = afterStr.length();
		len = afterLen - beforeLen;
		change = true;
		String srcString = afterStr.toString();
		focuseEditTextView.setText("");
		if (len >= 0) {
		} else {
			int selection = selectionPositon;
			if (endIsFengefu) {
			} else {
				int selection2 = selectionPositon;
				if (selection2 <= 0) {
					selection2 = 0;
				} else if (selection2 > srcString.length()) {
					selection2 = srcString.length();
				}
				focuseEditTextView.setSelection(selection2);
			}
		}
	}
}
