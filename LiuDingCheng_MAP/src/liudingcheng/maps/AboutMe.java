package liudingcheng.maps;

import ToolUtil.ToastUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author LiuDingCheng
 *
 */
public class AboutMe extends Activity {
	private ImageView mImageView;
	private ListView mlistview;
	private Context aboutme;

	private TelephonyManager phoneMgr;
	String[] Dev = { "APP", "开发者", "型号", "OS", "SDK", "版本", "UI", "硬件制造商" };// 名称
	String[] Infos = { "LiuDingChengMaps", "刘定成", Build.MODEL.toString(),
			"Android "+Build.VERSION.RELEASE, Build.VERSION.SDK.toString(), "V2.1.0",  Build.BRAND, Build.MANUFACTURER };// 信息

	public AboutMe() {
		aboutme = this;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutme_activity);
		initView();// 界面 数据初始化
		Funcation();// 设置参数

		mlistview.setAdapter(new infoBase());

	}

	/**
	 * 初始化界面
	 */
	public void initView() {
		mImageView = (ImageView) findViewById(R.id.about_me);
		mlistview = (ListView) findViewById(R.id.about_listview);

	}

	/**
	 * 设置监听事件
	 */
	public void Funcation() {
		mImageView.setOnClickListener(new SingleListener());
	}

	class SingleListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.about_me:
				ToastUtil.CreateToastShow(aboutme, null, "联系开发者", "@DigitalKing\n@数字帝国\n微博:@小小古怪");

				break;

			default:
				break;
			}

		}

	}

	class infoBase extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Dev.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(aboutme, R.layout.about_line, null);
				holder = new ViewHolder();
				holder.onestr = (TextView) convertView.findViewById(R.id.txt_one);
				holder.toewstr = (TextView) convertView.findViewById(R.id.txt_tow);

				// 绑定ViewHolder对象
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
			}

			holder.onestr.setText(Dev[position]);
			holder.toewstr.setText(Infos[position]);

			return convertView;
		}

		/**
		 * 存放空间
		 * 
		 * @author LiuDingCheng
		 *
		 */
		class ViewHolder {
			public TextView onestr = null;
			public TextView toewstr = null;

		}
	}

}
