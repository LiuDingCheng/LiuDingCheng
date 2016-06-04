package digitalking_for_ldc.singlemap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/5/22.
 */
public class PoiDetailActivity  extends Activity {
    private TextView back;
    private RatingBar HY, HJ, SS, WS, FW, ZH, KW, JS;
    private TextView Name, Address, Phone, Price, Time, Type;
    private Bundle getDetailBundle = null;
    private WebView webview = null;
    private Context ctx;

    public PoiDetailActivity() {
        ctx = this;

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poidatailactivity);
        getDetailBundle = new Bundle();
        getDetailBundle = getIntent().getExtras();// 获取数据
        initView();
        Funcation();
    }

    public void initView() {
        back = (TextView) findViewById(R.id.Detail_back);
        /**
         * 网页
         */
        webview = (WebView) findViewById(R.id.Detail_webview);
        /**
         * 评分
         */
        HY = (RatingBar) findViewById(R.id.hy);
        FW = (RatingBar) findViewById(R.id.fwrating);
        HJ = (RatingBar) findViewById(R.id.hjrating);
        SS = (RatingBar) findViewById(R.id.ssrating);
        WS = (RatingBar) findViewById(R.id.wsrating);
        ZH = (RatingBar) findViewById(R.id.zhrating);
        KW = (RatingBar) findViewById(R.id.kwrating);
        JS = (RatingBar) findViewById(R.id.jsrating);
        /**
         * 数据显示
         */
        Name = (TextView) findViewById(R.id.name);
        Address = (TextView) findViewById(R.id.address);
        Phone = (TextView) findViewById(R.id.phone);
        Price = (TextView) findViewById(R.id.price);
        Time = (TextView) findViewById(R.id.yytime);
        Type = (TextView) findViewById(R.id.type);

        // 加载网页数据
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl(getDetailBundle.getString("getDetailUrl"));
        // 设置数据
        FW.setRating((float) getDetailBundle.getDouble("getServiceRating"));
        HY.setRating((float) getDetailBundle.getDouble("getFavoriteNum"));
        HJ.setRating((float) getDetailBundle.getDouble("getEnvironmentRating"));
        SS.setRating((float) getDetailBundle.getDouble("getFacilityRating"));
        WS.setRating((float) getDetailBundle.getDouble("getHygieneRating"));
        ZH.setRating((float) getDetailBundle.getDouble("getOverallRating"));
        KW.setRating((float) getDetailBundle.getDouble("getTasteRating"));
        JS.setRating((float) getDetailBundle.getDouble("getTechnologyRating"));

        Name.setText(getDetailBundle.getString("getName"));
        Address.setText(getDetailBundle.getString("getAddress"));
        Phone.setText(getDetailBundle.getString("getTelephone"));
        Price.setText(String.valueOf(getDetailBundle.getDouble("getPrice")));
        Time.setText(getDetailBundle.getString("getShopHours"));
        Type.setText(getDetailBundle.getString("getType"));

    }

    public void Funcation() {
        back.setOnClickListener(new SingleListener());
        Phone.setOnClickListener(new SingleListener());
    }

    class SingleListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.Detail_back:
                    finish();
                    break;
                case R.id.phone:
                    Dialog CP = new AlertDialog.Builder(ctx, AlertDialog.THEME_TRADITIONAL).setTitle("拨打电话请求")
                            .setMessage("你要拨打电话吗?").setPositiveButton("是", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    if (getDetailBundle.getString("getTelephone").isEmpty()) {
                                        Toast.makeText(ctx, "没有电话啊啊啊啊啊啊！", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Intent phonecall = new Intent(Intent.ACTION_DIAL);
                                        Uri call = Uri.parse("tel:" + Phone.getText().toString().trim());
                                        phonecall.setData(call);
                                        startActivity(phonecall);
                                    }

                                }
                            }).setNegativeButton("否", null).create();
                    CP.show();

                    break;

                default:
                    break;
            }

        }

    }

}
