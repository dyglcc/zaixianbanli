package qfpay.wxshop.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import banli.jinniu.com.R;

public class Toaster {

	public static void l(Context context, String string) {

		View toastRoot = ((Activity) context).getLayoutInflater().inflate(
				R.layout.toast, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(string);

		Toast t = new Toast(context);
		t.setGravity(Gravity.CENTER, 0, 10);
		t.setDuration(Toast.LENGTH_LONG);
		t.setView(toastRoot);
		t.show();

	}

	public static void s(Context context, String string) {

		View toastRoot = ((Activity) context).getLayoutInflater().inflate(
				R.layout.toast, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(string);

		Toast t = new Toast(context);
		t.setGravity(Gravity.CENTER, 0, 10);
		t.setDuration(Toast.LENGTH_SHORT);
		t.setView(toastRoot);
		t.show();
	}

	public static void imageToast(Context context, String string) {

		View toastRoot = ((Activity) context).getLayoutInflater().inflate(
				R.layout.toast_image, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(string);

		Toast t = new Toast(context);
		t.setGravity(Gravity.CENTER, 0, 10);
		t.setDuration(Toast.LENGTH_LONG);
		t.setView(toastRoot);
		t.show();		
		

//		Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER, 0, 0);
//		LinearLayout toastView = (LinearLayout) toast.getView();
//
//		LinearLayout.LayoutParams paraLayout = new LinearLayout.LayoutParams(200,200);
//		toastView.setLayoutParams(paraLayout);
//
//		ImageView imageCodeProject = new ImageView(context);
//		imageCodeProject.setImageResource(R.drawable.red_cat);
//
//		toastView.addView(imageCodeProject, 0);
//		toast.show();

	}

}
