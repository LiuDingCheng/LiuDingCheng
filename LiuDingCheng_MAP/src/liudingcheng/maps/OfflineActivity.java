package liudingcheng.maps;

import java.util.ArrayList;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import ToolUtil.ToastUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 离线地图
 * 
 * @author LiuDingCheng
 *
 */
@SuppressLint({ "ViewHolder", "DefaultLocale" })
public class OfflineActivity extends Activity implements MKOfflineMapListener {

	private MKOfflineMap mOffline = null;// 离线地图功能
	private ProgressBar statebar;
	private EditText cityNameView;
	private ListView hotCityList, allCityList, localMapListView;
	private LinearLayout hotcity_layout, allcity_layout, localcity_layout;
	private ArrayList<String> hotCities;
	private ArrayList<MKOLSearchRecord> records1;// 获取热闹城市列表
	private ListAdapter hAdapter, allAdapter;
	private ArrayList<String> allCities;// 获取所有支持离线地图的城市列表
	private ArrayList<MKOLSearchRecord> records2;// 获取支持离线地图城市
	protected int city_id;// 城市id
	private Button hotcity_btn, allcity_btn, localmm_btn;
	private TextView downtxt;// 下载进度
	// 后退
	private TextView backtv;
	/**
	 * 已下载的离线地图信息列表
	 */
	private ArrayList<MKOLUpdateElement> localMapList = null;// 下载的本地
	private LMAdapter lmAdapter = null;// 本地适配器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offline_activity);
		initView();// 初始化界面
		initData();// 初始化离线地图数据
		FuncationListener();// 设置监听
		setResult(0x404);

	}

	/**
	 * 初始化布局控件
	 */
	private void initView() {
		city_id = 218;// 初始化cityid 默认武汉
		cityNameView = (EditText) findViewById(R.id.city);// 城市名称
		statebar = (ProgressBar) findViewById(R.id.stateBar);// 下载状态
		//
		hotcity_layout = (LinearLayout) findViewById(R.id.hotcitylist_layout);
		allcity_layout = (LinearLayout) findViewById(R.id.allcitylist_layout);// 全国城市总
		localcity_layout = (LinearLayout) findViewById(R.id.localmap_layout);// 已下载城市布局
		//
		hotCityList = (ListView) findViewById(R.id.hotcitylist);// 热门城市
		allCityList = (ListView) findViewById(R.id.allcitylist);// 全国城市
		localMapListView = (ListView) findViewById(R.id.localmaplist);// 已下载城市
		// 显示布局
		hotcity_btn = (Button) findViewById(R.id.hotButton);
		allcity_btn = (Button) findViewById(R.id.allButton);
		localmm_btn = (Button) findViewById(R.id.localButton);
		// 下载进度
		downtxt = (TextView) findViewById(R.id.downtxt);
		backtv = (TextView) findViewById(R.id.back_offline);
	}

	// 设置监听事件
	public void FuncationListener() {
		hotcity_btn.setOnClickListener(new SingleListener());
		allcity_btn.setOnClickListener(new SingleListener());
		localmm_btn.setOnClickListener(new SingleListener());
		backtv.setOnClickListener(new SingleListener());
	}

	// 内部类实现方法
	class SingleListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.hotButton:
				hotcity_layout.setVisibility(View.VISIBLE);
				allcity_layout.setVisibility(View.GONE);
				localcity_layout.setVisibility(View.GONE);

				break;
			case R.id.allButton:
				hotcity_layout.setVisibility(View.GONE);
				allcity_layout.setVisibility(View.VISIBLE);
				localcity_layout.setVisibility(View.GONE);

				break;
			case R.id.localButton:
				hotcity_layout.setVisibility(View.GONE);
				allcity_layout.setVisibility(View.GONE);
				localcity_layout.setVisibility(View.VISIBLE);

				break;
			case R.id.back_offline:
				finish();
				break;
			default:
				break;
			}

		}

	}

	/**
	 * 初始化离线地图
	 */
	private void initData() {
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		hotCities = new ArrayList<String>();// 要装载热闹城市列表
		// 获取热闹城市
		records1 = mOffline.getHotCityList();
		if (records1 != null) {
			for (MKOLSearchRecord r : records1) {
				hotCities.add("城市:" + r.cityName + " ID:" + r.cityID + " Size:" + this.formatDataSize(r.size));
			}
		}
		hAdapter = (ListAdapter) new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hotCities);
		hotCityList.setAdapter(hAdapter);

		allCities = new ArrayList<String>();// 获取所有支持离线地图的城市
		records2 = mOffline.getOfflineCityList();
		if (records1 != null) {
			for (MKOLSearchRecord r : records2) {
				allCities.add("省会:" + r.cityName + " ID:" + r.cityID + " Size:" + this.formatDataSize(r.size));
			}
		}
		allAdapter = (ListAdapter) new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allCities);
		allCityList.setAdapter(allAdapter);

		allcity_layout.setVisibility(View.VISIBLE);
		localcity_layout.setVisibility(View.GONE);
		hotcity_layout.setVisibility(View.GONE);

		// 获取已下过的离线地图信息
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}

		lmAdapter = new LMAdapter();
		// 显示本地下载地图
		localMapListView.setAdapter(lmAdapter);
	}

	/**
	 * 离线地图所占的容量大小
	 * 
	 * @param size
	 * @return
	 */
	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	/**
	 * 更新状态显示
	 */
	@SuppressLint("DefaultLocale")
	public void updateView() {
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}
		lmAdapter.notifyDataSetChanged();
	}

	/**
	 * 开始下载
	 * 
	 * @param view
	 */
	public void start(View view) {
		// int cityid = Integer.parseInt(cidView.getText().toString());
		mOffline.start(city_id);
		ToastUtil.CreateToastShow(this, null, "温馨提示", "开始下载离线地图. cityid: " + city_id);
		updateView();
	}

//	/**
//	 * 暂停下载
//	 * 
//	 * @param view
//	 */
//	public void stop(View view) {
//		// int cityid = Integer.parseInt(cidView.getText().toString());
//		mOffline.pause(city_id);
//		ToastUtil.CreateToastShow(this, null, "温馨提示", "暂停下载离线地图. cityid: " + city_id);
//		updateView();
//	}

	/**
	 * 搜索离线需市
	 * 
	 * @param view
	 */
	public void search(View view) {

		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView.getText().toString());
		if (records == null || records.size() != 1) {
			ToastUtil.CreateToastShow(this, null, "温馨提示", " 没有 " + cityNameView.getText().toString() + " 的信息");
		} else {
			city_id = records.get(0).cityID;
			ToastUtil.CreateToastShow(this, null, "温馨提示",
					"搜索" + "城市：" + cityNameView.getText().toString() + "\n 编号:" + city_id);
		}

	}

	/**
	 * 从SD卡导入离线地图安装包
	 * 
	 * @param view
	 */
	public void importFromSDCard(View view) {
		@SuppressWarnings("deprecation")
		int num = mOffline.importOfflineData();
		String msg = "";
		if (num == 0) {
			msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";
		} else {
			msg = String.format("成功导入 %d 个离线包，可以在下载管理查看", num);
		}
		ToastUtil.CreateToastShow(this, null, "温馨提示", msg);
		updateView();
	}

	@Override
	protected void onPause() {
		// int cityid = Integer.parseInt(cidView.getText().toString());
		MKOLUpdateElement temp = mOffline.getUpdateInfo(city_id);
		if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
			mOffline.pause(city_id);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * 退出时，销毁离线地图模块
		 */
		mOffline.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				statebar.setProgress(update.ratio);
				downtxt.setText(update.cityName + " : " + update.ratio + "%");
				if (update.ratio == 100) {
					ToastUtil.CreateToastShow(this, null, "温馨提示", update.cityName + "离线地图系在完成!");
					downtxt.setText("没有任务" + " : " + "0" + "%");
					statebar.setProgress(0);

				}
				updateView();
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		}
	}

	public class LMAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localMapList.size();
		}

		@Override
		public Object getItem(int index) {
			return localMapList.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
			view = View.inflate(OfflineActivity.this, R.layout.offline_line, null);
			initViewItem(view, e);
			return view;
		}

		@SuppressLint("ViewHolder")
		void initViewItem(View view, final MKOLUpdateElement e) {
			Button remove = (Button) view.findViewById(R.id.remove);// 删除地图
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);// 地图状态
			TextView ratiotxt = (TextView) view.findViewById(R.id.ratiotxt);// 显示进度
			ratiotxt.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				update.setText("可更新");
			} else {
				update.setText("最新");
			}
			// 删除
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
		}
	}
}
