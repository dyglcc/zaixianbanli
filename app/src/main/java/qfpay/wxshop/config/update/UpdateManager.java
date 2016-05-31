package qfpay.wxshop.config.update;

import qfpay.wxshop.utils.ConstValue;
import qfpay.wxshop.utils.MobAgentTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import qfpay.wxshop.R;
import qfpay.wxshop.utils.Toaster;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.umeng.analytics.MobclickAgent;

public class UpdateManager {

	private Context mContext;

	// 提示语
	private String updateComment;

	// 返回的安装包url
	// private String apkUrl =
	// "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";
	private String apkUrl;

	private Dialog noticeDialog;

	private Dialog downloadDialog;
	/* 下载包安装路径 */
	private String savePath;

	private String fileName = "Wxshop.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;

	private static final int DOWN_UPDATE = 1;

	private static final int DOWN_OVER = 2;

	private int progress;

	private Thread downLoadThread;

	private boolean interceptFlag = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				showInstallDialog();
				installApk();
				break;
			default:
				break;
			}
		};
	};
	private String[] conments;

	public UpdateManager(Context context, String url, String comment) {
		this.mContext = context;
		this.apkUrl = url;
		conments = comment.split("&&");
		updateComment = "";
		getComments();
		savePath = ConstValue.getDownLoadDir();
	}

	private void showInstallDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(mContext.getResources().getString(
				R.string.download_done));
		builder.setMessage(mContext.getString(R.string.start_setup));
		builder.setNegativeButton(mContext.getString(R.string.setup_now),
				new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				installApk();
			}
		});

		builder.setCancelable(false);
	    AlertDialog create = builder.create();
		if (mContext != null) {
			if (!((Activity) mContext).isFinishing()) {
				create.show();
			}
		}

	}

	private void getComments() {
		for (int i = 0; i < conments.length; i++) {
			if (i == conments.length - 1) {
				updateComment = updateComment + conments[i];
			} else {
				updateComment = updateComment + conments[i] + "\n";
			}
		}
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				if (apkUrl == null || apkUrl.equals("")) {
					Toaster.l(
							mContext,
							mContext.getResources().getString(
									R.string.download_url_empty));
					return;
				}
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String apkFile = savePath + fileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param imgUrl
	 */

	private void downloadApk() {
		MobAgentTools.OnEventMobOnDiffUser(mContext, "start_update");
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param imgUrl
	 */
	private void installApk() {
		File apkfile = new File(savePath + fileName);
		if (!apkfile.exists()) {
			return;
		}
		MobAgentTools.OnEventMobOnDiffUser(mContext, "start_update_install");
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);

	}
}
