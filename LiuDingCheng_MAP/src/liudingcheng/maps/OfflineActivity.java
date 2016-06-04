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
 * ���ߵ�ͼ
 * 
 * @author LiuDingCheng
 *
 */
@SuppressLint({ "ViewHolder", "DefaultLocale" })
public class OfflineActivity extends Activity implements MKOfflineMapListener {

	private MKOfflineMap mOffline = null;// ���ߵ�ͼ����
	private ProgressBar statebar;
	private EditText cityNameView;
	private ListView hotCityList, allCityList, localMapListView;
	private LinearLayout hotcity_layout, allcity_layout, localcity_layout;
	private ArrayList<String> hotCities;
	private ArrayList<MKOLSearchRecord> records1;// ��ȡ���ֳ����б�
	private ListAdapter hAdapter, allAdapter;
	private ArrayList<String> allCities;// ��ȡ����֧�����ߵ�ͼ�ĳ����б�
	private ArrayList<MKOLSearchRecord> records2;// ��ȡ֧�����ߵ�ͼ����
	protected int city_id;// ����id
	private Button hotcity_btn, allcity_btn, localmm_btn;
	private TextView downtxt;// ���ؽ���
	// ����
	private TextView backtv;
	/**
	 * �����ص����ߵ�ͼ��Ϣ�б�
	 */
	private ArrayList<MKOLUpdateElement> localMapList = null;// ���صı���
	private LMAdapter lmAdapter = null;// ����������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offline_activity);
		initView();// ��ʼ������
		initData();// ��ʼ�����ߵ�ͼ����
		FuncationListener();// ���ü���
		setResult(0x404);

	}

	/**
	 * ��ʼ�����ֿؼ�
	 */
	private void initView() {
		city_id = 218;// ��ʼ��cityid Ĭ���人
		cityNameView = (EditText) findViewById(R.id.city);// ��������
		statebar = (ProgressBar) findViewById(R.id.stateBar);// ����״̬
		//
		hotcity_layout = (LinearLayout) findViewById(R.id.hotcitylist_layout);
		allcity_layout = (LinearLayout) findViewById(R.id.allcitylist_layout);// ȫ��������
		localcity_layout = (LinearLayout) findViewById(R.id.localmap_layout);// �����س��в���
		//
		hotCityList = (ListView) findViewById(R.id.hotcitylist);// ���ų���
		allCityList = (ListView) findViewById(R.id.allcitylist);// ȫ������
		localMapListView = (ListView) findViewById(R.id.localmaplist);// �����س���
		// ��ʾ����
		hotcity_btn = (Button) findViewById(R.id.hotButton);
		allcity_btn = (Button) findViewById(R.id.allButton);
		localmm_btn = (Button) findViewById(R.id.localButton);
		// ���ؽ���
		downtxt = (TextView) findViewById(R.id.downtxt);
		backtv = (TextView) findViewById(R.id.back_offline);
	}

	// ���ü����¼�
	public void FuncationListener() {
		hotcity_btn.setOnClickListener(new SingleListener());
		allcity_btn.setOnClickListener(new SingleListener());
		localmm_btn.setOnClickListener(new SingleListener());
		backtv.setOnClickListener(new SingleListener());
	}

	// �ڲ���ʵ�ַ���
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
	 * ��ʼ�����ߵ�ͼ
	 */
	private void initData() {
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		hotCities = new ArrayList<String>();// Ҫװ�����ֳ����б�
		// ��ȡ���ֳ���
		records1 = mOffline.getHotCityList();
		if (records1 != null) {
			for (MKOLSearchRecord r : records1) {
				hotCities.add("����:" + r.cityName + " ID:" + r.cityID + " Size:" + this.formatDataSize(r.size));
			}
		}
		hAdapter = (ListAdapter) new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hotCities);
		hotCityList.setAdapter(hAdapter);

		allCities = new ArrayList<String>();// ��ȡ����֧�����ߵ�ͼ�ĳ���
		records2 = mOffline.getOfflineCityList();
		if (records1 != null) {
			for (MKOLSearchRecord r : records2) {
				allCities.add("ʡ��:" + r.cityName + " ID:" + r.cityID + " Size:" + this.formatDataSize(r.size));
			}
		}
		allAdapter = (ListAdapter) new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allCities);
		allCityList.setAdapter(allAdapter);

		allcity_layout.setVisibility(View.VISIBLE);
		localcity_layout.setVisibility(View.GONE);
		hotcity_layout.setVisibility(View.GONE);

		// ��ȡ���¹������ߵ�ͼ��Ϣ
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}

		lmAdapter = new LMAdapter();
		// ��ʾ�������ص�ͼ
		localMapListView.setAdapter(lmAdapter);
	}

	/**
	 * ���ߵ�ͼ��ռ��������С
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
	 * ����״̬��ʾ
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
	 * ��ʼ����
	 * 
	 * @param view
	 */
	public void start(View view) {
		// int cityid = Integer.parseInt(cidView.getText().toString());
		mOffline.start(city_id);
		ToastUtil.CreateToastShow(this, null, "��ܰ��ʾ", "��ʼ�������ߵ�ͼ. cityid: " + city_id);
		updateView();
	}

//	/**
//	 * ��ͣ����
//	 * 
//	 * @param view
//	 */
//	public void stop(View view) {
//		// int cityid = Integer.parseInt(cidView.getText().toString());
//		mOffline.pause(city_id);
//		ToastUtil.CreateToastShow(this, null, "��ܰ��ʾ", "��ͣ�������ߵ�ͼ. cityid: " + city_id);
//		updateView();
//	}

	/**
	 * ������������
	 * 
	 * @param view
	 */
	public void search(View view) {

		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView.getText().toString());
		if (records == null || records.size() != 1) {
			ToastUtil.CreateToastShow(this, null, "��ܰ��ʾ", " û�� " + cityNameView.getText().toString() + " ����Ϣ");
		} else {
			city_id = records.get(0).cityID;
			ToastUtil.CreateToastShow(this, null, "��ܰ��ʾ",
					"����" + "���У�" + cityNameView.getText().toString() + "\n ���:" + city_id);
		}

	}

	/**
	 * ��SD���������ߵ�ͼ��װ��
	 * 
	 * @param view
	 */
	public void importFromSDCard(View view) {
		@SuppressWarnings("deprecation")
		int num = mOffline.importOfflineData();
		String msg = "";
		if (num == 0) {
			msg = "û�е������߰�������������߰�����λ�ò���ȷ�������߰��Ѿ������";
		} else {
			msg = String.format("�ɹ����� %d �����߰������������ع���鿴", num);
		}
		ToastUtil.CreateToastShow(this, null, "��ܰ��ʾ", msg);
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
		 * �˳�ʱ���������ߵ�ͼģ��
		 */
		mOffline.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// �������ؽ��ȸ�����ʾ
			if (update != null) {
				statebar.setProgress(update.ratio);
				downtxt.setText(update.cityName + " : " + update.ratio + "%");
				if (update.ratio == 100) {
					ToastUtil.CreateToastShow(this, null, "��ܰ��ʾ", update.cityName + "���ߵ�ͼϵ�����!");
					downtxt.setText("û������" + " : " + "0" + "%");
					statebar.setProgress(0);

				}
				updateView();
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// �������ߵ�ͼ��װ
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// �汾������ʾ
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
			Button remove = (Button) view.findViewById(R.id.remove);// ɾ����ͼ
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);// ��ͼ״̬
			TextView ratiotxt = (TextView) view.findViewById(R.id.ratiotxt);// ��ʾ����
			ratiotxt.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				update.setText("�ɸ���");
			} else {
				update.setText("����");
			}
			// ɾ��
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
