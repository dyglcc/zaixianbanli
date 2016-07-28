package qfpay.wxshop.tab;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jiafen.jinniu.com.R;


public class MineSegment extends Fragment{

	Button btn_jihuo,btn_copy;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_5, null);
		btn_jihuo = (Button) view.findViewById(R.id.btn_jihuo);
		btn_jihuo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		btn_copy = (Button) view.findViewById(R.id.btn_copy);
		btn_copy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				}
		});
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActivity().getActionBar();
		super.onCreate(savedInstanceState);
	}
}