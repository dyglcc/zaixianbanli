package qfpay.wxshop.activity;

import java.util.List;

import banli.jinniu.com.R;
import qfpay.wxshop.image.jinniu.ProgressUploadFile;
import qfpay.wxshop.takepicUtils.PictureBean;
import qfpay.wxshop.takepicUtils.TakePicUtils;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.BitmapUtil;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;




/**
 * 完善店铺信息界面
 */
public class InputShopNameActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_reg_improvement);

        initViews();


    }

    private View layoutTakePic;
    private EditText etWexinHao;
    private EditText etShopName;
    // private Button btnBack;
    private Button btnComplete;
    private ImageView ivPhoto;

    private void initViews() {

        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        layoutTakePic = findViewById(R.id.layout_takepic);
        etWexinHao = (EditText) findViewById(R.id.et_weixinhao);
        etShopName = (EditText) findViewById(R.id.et_shopname);
        btnComplete = (Button) findViewById(R.id.btn_complete);

        btnComplete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String shopName = etShopName.getText().toString();
                if (shopName.equals("")) {
                    Utils.showDialog(InputShopNameActivity.this, null,
                            getString(R.string.mm_hint),
                            getString(R.string.input_store_name),
                            getString(R.string.know), "", false, true);
                    etShopName.requestFocus();
                    return;

                }

                // if (selectedPicDIR == null || selectedPicDIR.equals("")) {
                // Utils.showDialog(InputShopNameActivity.this, null, "喵喵提示",
                // "给自己的小店拍一张图片吧", "知道了", "", false, true);
                // return;
                // }

                // save2Server();
                uploadFile2Server();

            }
        });

        layoutTakePic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                TakePicUtils.getInstance().init(InputShopNameActivity.this);
                TakePicUtils.getInstance().takePic(
                        TakePicUtils.TAKE_PIC_MODE_CROP);
            }
        });

    }

    private void uploadFile2Server() {
        // Utils.showProgressDialog(PublishActivity.this, "上传图片...");
        if (selectedPicDIR == null || selectedPicDIR.equals("")) {
            // Toaster.l(InputShopNameActivity.this,
            // getString(R.string.upload_pic));
            // 如果没有头像的话，就用默认的头像地址,上传到服务器。
//            save2Server(WDConfig.DEFAULT_AVATOR);
            return;
        }
        String tokenString = Utils.getQiniuToken();

        ProgressUploadFile uploadFile = new ProgressUploadFile();

        uploadFile.run(selectedPicDIR,tokenString,com.qiniu.upload.tool.Config.UP_HOST);
    }


    private String url_finish_pic;

//    protected void save2Server(String avatorUrl) {
//
//        String name = etShopName.getText().toString().trim();
//        String weixin = etWexinHao.getText().toString().trim();
//
//        AbstractNet net = new ShopCreateImpl(InputShopNameActivity.this);
//        Bundle bun = new Bundle();
//        bun.putString("shop_name", name);
//        // if (url_finish_pic != null && !url_finish_pic.equals("")) {
//        bun.putString("shop_avatar", avatorUrl);
//        // }
//        if (!weixin.equals("")) {
//
//            MobAgentTools.OnEventMobOnDiffUser(InputShopNameActivity.this,
//                    "wechat_number");
//        }
//        bun.putString("shop_weixin", weixin);
//        net.request(bun, new MainHandler(InputShopNameActivity.this) {
//
//            @Override
//            public void onSuccess(Bundle bundle) {
//                finish();
//                MainActivity_.intent(InputShopNameActivity.this).start();
//            }
//
//            @Override
//            public void onFailed(Bundle bundle) {
//            }
//        });
//
//    }

    private String selectedPicDIR;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == TakePicUtils.TAKE_PIC_REQUEST_CODE) {
                TakePicUtils.getInstance().receivePics(requestCode, resultCode,
                        data);
            } else if (requestCode == TakePicUtils.CROP_PICTURE_DONE) {
                selectedPicDIR = "";
                PictureBean pb = TakePicUtils.getInstance().receivePics(
                        requestCode, resultCode, data);
                if (pb != null) {
                    selectedPicDIR = pb.getFileStr();
                    if (!selectedPicDIR.equals("")) {
                        Bitmap map = Utils.toOvalBitmap(BitmapUtil
                                .GetBitmap(selectedPicDIR));
                        ivPhoto.setImageBitmap(map);
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 是否有GPS信息
     *
     * @param context
     * @return
     */
    private static boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null) {
            return false;
        }
        final List<String> providers = mgr.getAllProviders();
        if (providers == null) {
            return false;
        }
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

//	/**
//	 * 获取地理位置
//	 */
//	public  void getLocation(Context context) {
//
//		try {
//			// 设备有GPS模块就启动定位功能
//			if (hasGPSDevice(context)) {
//				// 有网才开始定位
//				if (Utils.isCanConnectionNetWork(context)) {
//					try {
//
//						WxShopApplication.locationManager = LocationManagerProxy
//								.getInstance(context);
//						Criteria cri = new Criteria();
//						cri.setAccuracy(Criteria.ACCURACY_COARSE);
//						cri.setAltitudeRequired(false);
//						cri.setBearingRequired(false);
//						cri.setCostAllowed(false);
//						String bestProvider = WxShopApplication.locationManager
//								.getBestProvider(cri, true);
//						WxShopApplication.locationManager
//								.requestLocationUpdates(bestProvider, 2000, 10,
//										listener);
//
//					} catch (Exception e) {
//					}
//				}
//			}
//		} catch (Exception e) {
//		}
//
//	}

//	private LocationListener listener = new LocationListener() {
//
//		@Override
//		public void onLocationChanged(Location location) {
//			// TODO Auto-generated method stub
//			if (location != null) {
//				Double geoLat = location.getLatitude();
//				Double geoLng = location.getLongitude();
//				String locationString = (Double.toString(geoLat) + "," + Double
//						.toString(geoLng));
//				T.w("Get Location:" + locationString);
//				T.d(Integer.toString(locationString.length()));
//
//				if (locationString != null) {
//					WxShopApplication.locationString = locationString;
//					// 定位成功时间
//					WxShopApplication.dataEngine
//							.setLocationString(locationString);
//					WxShopApplication.locationManager.removeUpdates(this);
//					WxShopApplication.locationManager.destory();
//				}
//			}
//		}

//		@Override
//		public void onProviderDisabled(String provider) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onProviderEnabled(String provider) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//			// TODO Auto-generated method stub
//
//		}
//	};
}

