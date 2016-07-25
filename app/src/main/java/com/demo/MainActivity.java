package com.demo;

import android.app.Activity;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import qfpay.wxshop.data.beans.Tb_contacts;


public class MainActivity extends Activity {
    private static Context mContext;
    EditText seven, count, start;
    Button add, del;

    //    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init data
                String str = seven.getText().toString();
                int countNum = Integer.parseInt(count.getText().toString());
                int startNum = Integer.parseInt(start.getText().toString());
                int end = countNum + startNum;
                int i = 0;
                ArrayList<Tb_contacts> list = new ArrayList<Tb_contacts>();
                for (i = startNum; i < end; i++) {

                    String name = "zhangfei" + i;
                    String number = i+"";
                    int buwei = 4-number.length();
                    for(int b=0;b<buwei;b++){
                        number = "0"+number;
                    }
                    number = str + "" + number;

                    list.add(new Tb_contacts(name, number));

                }

            }
        });

    }


}
