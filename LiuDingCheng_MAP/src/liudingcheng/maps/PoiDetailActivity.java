package liudingcheng.maps;

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
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class PoiDetailActivity extends Activity {
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
		getDetailBundle = getIntent().getExtras();// ��ȡ����
		initView();
		Funcation();
	}

	public void initView() {
		back = (TextView) findViewById(R.id.Detail_back);
		/**
		 * ��ҳ
		 */
		webview = (WebView) findViewById(R.id.Detail_webview);
		/**
		 * ����
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
		 * ������ʾ
		 */
		Name = (TextView) findViewById(R.id.name);
		Address = (TextView) findViewById(R.id.address);
		Phone = (TextView) findViewById(R.id.phone);
		Price = (TextView) findViewById(R.id.price);
		Time = (TextView) findViewById(R.id.yytime);
		Type = (TextView) findViewById(R.id.type);

		// ������ҳ����
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webview.setOverScrollMode(View.OVER_SCROLL_NEVER);
		webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// ��д�˷������������ҳ��������ӻ����ڵ�ǰ��webview����ת��������������Ǳ�
				view.loadUrl(url);
				return true;
			}
		});
		webview.loadUrl(getDetailBundle.getString("getDetailUrl"));
		// ��������
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

	class SingleListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.Detail_back:
				finish();
				break;
			case R.id.phone:
				Dialog CP = new AlertDialog.Builder(ctx, AlertDialog.THEME_TRADITIONAL).setTitle("����绰����")
						.setMessage("��Ҫ����绰��?").setPositiveButton("��", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if (getDetailBundle.getString("getTelephone").isEmpty()) {
									Toast.makeText(ctx, "û�е绰��������������", Toast.LENGTH_SHORT).show();

								} else {
									Intent phonecall = new Intent(Intent.ACTION_DIAL);
									Uri call = Uri.parse("tel:" + Phone.getText().toString().trim());
									phonecall.setData(call);
									startActivity(phonecall);
								}

							}
						}).setNegativeButton("��", null).create();
				CP.show();

				break;

			default:
				break;
			}

		}

	}

}
