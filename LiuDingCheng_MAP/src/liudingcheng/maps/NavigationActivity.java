package liudingcheng.maps;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine.WalkingStep;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import ToolUtil.DrivingOverlayUtil;
import ToolUtil.ToastUtil;
import ToolUtil.TransitOverlayUtil;
import ToolUtil.WalkingOverlayUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����
 * 
 * @author LiuDingCheng
 *
 */
public class NavigationActivity extends Activity {
	private LocationClient N_locationclient;// ��λ�ͻ���
	public N_bdlocationlistener N_bdlocationlistener;// ��λ������
	private MyOrientationListener myOrientationListener;// ���򴫸����ļ�����
	private double N_lat;// ��ǰ�ľ���
	private double N_lng;// ��ǰά��
	private volatile boolean Nisfristlocation = true;// ��һ�ζ�λ
	private int Nxdirection;// ���򴫸���X�����ֵ
	private float Ncurrentaccracy;// ��ǰ�ľ���
	private LocationMode Ncurrentmode;// ��λģʽ
	private static Context navigation;
	private TextView searchTv, showresultnumber;
	private EditText searchWord, searchcity;
	private Bundle mbundle;
	private Vibrator vibrator = null;// �ֻ���
	private DialogRecognitionListener DRL = null;// ���������¼�
	private BaiduASRDigitalDialog BAD = null;// �����Ի���
	// ����İٶ�����key
	private final String API_KEY = "uXv45Q3RyP5q348CD9U6sg0Xcg6PAz13";// ��ͼ/����ʶ��
	private final String SECRET_KEY = "bYFsCCjXaluiE1v0MpiKSDC7jrHzdPCv";// ����ʶ��
	private PoiSearch poisearch = null;// ʲô�ٶ�poi����
	private MapView nearby_mapview;// ��ͼ
	private BaiduMap nearby_baidumap;// �ٶ�ͼ��
	// ��λ
	private LinearLayout N_loc_btn;// ��λ
	private LinearLayout N_getaddr_btn;// ��ȡλ��
	// ��Ӹ�����
	private MarkerOptions N_markeroptions;
	private Marker N_marker = null;
	private TextView walktv, bustv, cartv;
	private View N_view;// ��ͼ
	private PopupWindow N_popupwindow = null;// ��������
	/**
	 * ·��
	 */
	private RoutePlanSearch N_routeplansearch = null;// ��·�滮poi
	private PlanNode N_SN, N_EN;// ����յ�
	private TransitRoutePlanOption N_transitrouteplanoption;// ����
	private WalkingRoutePlanOption N_walkingrouteplanoption;// ����
	private DrivingRoutePlanOption N_drivingrouteplanoption;// �ݳ�
	private RouteLine<?> N_routeline = null;// ·����Ϣ
	// ����
	private TextView backtv;
	/**
	 * ��������
	 */
	private LinearLayout N_NNaviline;// �򿪵���
	private NaviParaOption N_naviparaoption = null;
	private boolean isNavi = true;// �Ƿ�����������
	private TextView N_changeNavi;// ��ʾ��������
	private TextView N_nearby_changelook;// �鿴����
	/**
	 * ��ȡ��ַ
	 */
	private GeoCoder N_geocoder;// �������
	private ReverseGeoCodeOption N_reversegeocodeoption = null;// ������
	private boolean isMyaddress = true;// �ҵĵ�ַ�ж�
	private LinearLayout N_showinfo_line;// ��ʾ����
	private TextView N_showdatainfotv;// ��ʾ����
	private LinearLayout N_showdatail_line;// ��ʾ����
	private boolean isShowDatail = true;

	/**
	 * ���������Ϣ
	 */
	private String N_currentaddress = null;// ��ǰ�ĵ���λ��
	private String N_targetaddress = null;// Ŀ�����λ��
	private LatLng N_currentlatlng = null;// ��ǰ�ĵ���γ��
	private LatLng N_targetlatlng = null;// Ŀ�����γ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.nearby_activity);
		initView();
		funcationListener();

	}

	public NavigationActivity() {
		navigation = NavigationActivity.this;
	}

	// ��ʼ������
	public void initView() {
		/**
		 * ·�߹滮
		 */
		N_routeplansearch = RoutePlanSearch.newInstance();
		N_walkingrouteplanoption = new WalkingRoutePlanOption();
		N_transitrouteplanoption = new TransitRoutePlanOption();
		N_drivingrouteplanoption = new DrivingRoutePlanOption();
		// ��ʼ������ʵ��
		N_geocoder = GeoCoder.newInstance();
		// ������ ��ʼ��ʵ��
		N_reversegeocodeoption = new ReverseGeoCodeOption();
		/**
		 * ����ѡ���ʼ��
		 */
		N_naviparaoption = new NaviParaOption();
		N_NNaviline = (LinearLayout) findViewById(R.id.N_Naviline);
		/**
		 * ��ʼ��������
		 */
		N_markeroptions = new MarkerOptions();

		// ��ȡ��ͼ
		nearby_mapview = (MapView) findViewById(R.id.nearby_mapView);
		// ��ȡͼ��
		nearby_baidumap = nearby_mapview.getMap();
		nearby_baidumap.setTrafficEnabled(true);// ������ͨ��ͼ
		nearby_baidumap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));// ����mapview������
		// nearby_baidumap.setMyLocationEnabled(true);// ������λͼ��
		// ��ȡϵͳ����
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);// ��
		// ��ȡ��Դ
		searchTv = (TextView) findViewById(R.id.nearby_search_tv);// ��ѯ
		searchWord = (EditText) findViewById(R.id.nearby_searchword_et);// �ؼ���
		searchcity = (EditText) findViewById(R.id.nearby_searccity_et);// ����
		showresultnumber = (TextView) findViewById(R.id.resultnumber_);// ������
		// ��ʾ�б�
		backtv = (TextView) findViewById(R.id.back_nearby);

		// ��ʼ�� PoiSearch ����
		poisearch = PoiSearch.newInstance();// ֮�����ü���
		// ��ȡ��λ����
		N_loc_btn = (LinearLayout) findViewById(R.id.N_locline);
		// ��ȡ��ַ
		N_getaddr_btn = (LinearLayout) findViewById(R.id.N_getaddr);
		/**
		 * ��ȡ����
		 */
		N_view = NavigationActivity.this.getLayoutInflater().inflate(R.layout.nearby_routeview, null);
		N_popupwindow = new PopupWindow(N_view, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		walktv = (TextView) N_view.findViewById(R.id.nearby_walk);// ����
		cartv = (TextView) N_view.findViewById(R.id.nearby_car);// ����
		bustv = (TextView) N_view.findViewById(R.id.nearby_bus);// ����
		/**
		 * ��ʾ����
		 */
		N_showinfo_line = (LinearLayout) findViewById(R.id.nearby_showinfo_line);
		N_showdatainfotv = (TextView) findViewById(R.id.nearby_datainfo_tv);// ��ʾ����
		N_showdatail_line = (LinearLayout) findViewById(R.id.N_showdatail_line);
		N_changeNavi = (TextView) findViewById(R.id.nearby_changeNavi);// ��ʾ��������
		N_nearby_changelook = (TextView) findViewById(R.id.nearby_changelook);// ���鿪��

		// Ĭ�ϲ�����
		isNavi = true;
		/**
		 * ��ʾ�ж���
		 */
		isShowDatail = true;
		// ��λģʽ
		Ncurrentmode = LocationMode.NORMAL;// Ĭ����ͨģʽ
		// ��һ�ζ�λ
		Nisfristlocation = true;
		// ��ʼ����λ
		N_initLocation();
		// ��ʼ�����򴫸���
		N_initOritation();

	}

	/**
	 * ���ü����¼�
	 */
	public void funcationListener() {
		/**
		 * ·�߹滮
		 */
		// ����·�߹滮����
		N_routeplansearch.setOnGetRoutePlanResultListener(N_planresultlistener);
		// ���ü���
		walktv.setOnClickListener(new SingleListener());
		cartv.setOnClickListener(new SingleListener());
		bustv.setOnClickListener(new SingleListener());
		nearby_baidumap.setOnMarkerClickListener(new NMarkerListener());// ���������¼�
		// �������
		N_geocoder.setOnGetGeoCodeResultListener(N_GeoCodeResultListener);
		poisearch.setOnGetPoiSearchResultListener(poiListener);// ���ü�������
		backtv.setOnClickListener(new SingleListener());// ����
		N_loc_btn.setOnClickListener(new SingleListener());// ���ö�λ�����¼�
		N_getaddr_btn.setOnClickListener(new SingleListener());
		/**
		 * ��ʾ����
		 */
		N_showdatail_line.setOnClickListener(new SingleListener());
		/**
		 * ����������������
		 */
		N_NNaviline.setOnClickListener(new SingleListener());// ��������
		searchTv.setOnClickListener(new SingleListener());// ����
		// �����¼�����
		searchTv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				vibrator.vibrate(new long[] { 50, 100 }, -1);
				YYDialog();
				return false;
			}
		});

	}

	// �ڲ�������¼�
	class SingleListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// ���
			N_SN = PlanNode.withCityNameAndPlaceName(searchcity.getText().toString().trim(), N_currentaddress);
			// �յ�
			N_EN = PlanNode.withCityNameAndPlaceName(searchcity.getText().toString().trim(), N_targetaddress);
			switch (v.getId()) {
			case R.id.nearby_search_tv:
				nearby_baidumap.clear();
				showresultnumber.setVisibility(View.VISIBLE);
				poisearch.searchInCity(
						new PoiCitySearchOption().pageCapacity(50).city(searchcity.getText().toString().trim())
								.keyword(searchWord.getText().toString().trim()).pageNum(1));
				break;
			case R.id.N_locline:
				N_Loc();// ��λ�¼�
				break;
			case R.id.N_getaddr:
				// ��ǰ����λ��
				isMyaddress = true;
				LatLng pLL = new LatLng(N_lat, N_lng);
				N_reversegeocodeoption.location(pLL);

				N_geocoder.reverseGeoCode(N_reversegeocodeoption);
				break;
			case R.id.nearby_walk:
				/**
				 * ����
				 */
				N_NNaviline.setVisibility(View.VISIBLE);
				N_walkingrouteplanoption.from(N_SN);// ���
				N_walkingrouteplanoption.to(N_EN);// �յ�
				N_routeplansearch.walkingSearch(N_walkingrouteplanoption);
				N_popupwindow.dismiss();
				break;
			case R.id.nearby_car:
				/**
				 * ����
				 */
				N_NNaviline.setVisibility(View.VISIBLE);
				N_drivingrouteplanoption.from(N_SN);// ���
				N_drivingrouteplanoption.to(N_EN);// �յ�
				N_routeplansearch.drivingSearch(N_drivingrouteplanoption);
				N_popupwindow.dismiss();
				break;
			case R.id.nearby_bus:
				/**
				 * ����
				 */
				N_NNaviline.setVisibility(View.VISIBLE);
				N_transitrouteplanoption.city(searchcity.getText().toString().trim());// ����
				N_transitrouteplanoption.from(N_SN);// ���
				N_transitrouteplanoption.to(N_EN);// �յ�
				N_routeplansearch.transitSearch(N_transitrouteplanoption);
				N_popupwindow.dismiss();
				break;
			case R.id.N_Naviline:
				if (isNavi) {
					TTSRoute(isNavi);
					isNavi = false;
				} else if (!isNavi) {
					TTSRoute(isNavi);
					isNavi = true;
				}
				break;
			case R.id.N_showdatail_line:
				Log.e("L", "isShowDatail ��ʾ----" + isShowDatail);
				if (isShowDatail == true) {
					N_showinfo_line.setVisibility(View.VISIBLE);
					N_nearby_changelook.setText("��������");
					isShowDatail = false;
				} else if (isShowDatail == false) {
					Log.e("L", "isShowDatail ����ʾ----" + isShowDatail);
					N_showinfo_line.setVisibility(View.GONE);
					N_nearby_changelook.setText("�鿴����");
					isShowDatail = true;
				}
				break;
			case R.id.back_nearby:
				Ncurrentmode = LocationMode.NORMAL;// ����Ϊ��ͨģʽ
				finish();
				break;

			default:

				break;
			}

		}

	}

	/**
	 * ���������¼�
	 */
	class NMarkerListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			// ��ȡ����
			N_popupwindow.showAtLocation(findViewById(R.id.nearby_mapView),
					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, ((int) nearby_mapview.getX()),
					((int) nearby_mapview.getY()));

			isMyaddress = false;
			N_reversegeocodeoption.location(arg0.getPosition());
			N_geocoder.reverseGeoCode(N_reversegeocodeoption);
			return false;
		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		N_popupwindow.dismiss();
		N_NNaviline.setVisibility(View.GONE);
		BaiduMapNavigation.finish(navigation);
		return false;

	};

	/**
	 * �������� ���ðٶ�app Ϊʲôû�п������أ��ٶȲ�����
	 */
	public void TTSRoute(boolean en) {
		Log.e("en", "----------------------------------------" + en);
		if (en) {
			Ncurrentmode = LocationMode.COMPASS;

			try {

				N_naviparaoption.startName(N_currentaddress).startPoint(N_currentlatlng).endPoint(N_targetlatlng)
						.endName(N_targetaddress);
				BaiduMapNavigation.setSupportWebNavi(false);
				// �ݳ�
				BaiduMapNavigation.openBaiduMapNavi(N_naviparaoption, navigation);// ����
			} catch (BaiduMapAppNotSupportNaviException e) {
				ToastUtil.CreateToastShow(NavigationActivity.this, null, "��ܰ��ʾ", "���ðٶȵ�ͼAPI�ӿ�ʧ��\n�밲װ�ٶȵ�ͼAPP���߰ٶȵ�ͼ�汾����");

			}
			N_changeNavi.setText("�رյ���");

		} else if (!en) {
			Ncurrentmode = LocationMode.NORMAL;
			BaiduMapNavigation.finish(navigation);// ��������
			N_changeNavi.setText("��������");
		}

	}

	/**
	 * ·�߹滮
	 */
	OnGetRoutePlanResultListener N_planresultlistener = new OnGetRoutePlanResultListener() {
		private StringBuffer SB = new StringBuffer();

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult drivingrouteresult) {
			// TODO Auto-generated method stub
			nearby_baidumap.clear();// ���ͼ�㸲����
			N_showdatainfotv.setText("");
			SB.delete(0, SB.length());
			if (drivingrouteresult == null || drivingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(navigation, null, "��ܰ��ʾ", "û���ҵ����ʵ�·��");
			}
			if (drivingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (drivingrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
				N_routeline = drivingrouteresult.getRouteLines().get(0);// ��һ����
				SB.append("���\t\t" + N_currentaddress).append("\t\t\t----\t\t\t").append("�յ�\t\t" + N_targetaddress)
						.append("\n\n");
				for (int i = 0; i < N_routeline.getAllStep().size(); i++) {

					DrivingRouteLine.DrivingStep step = (DrivingStep) N_routeline.getAllStep().get(i);

					SB.append(i + "\t��\t:" + step.getEntranceInstructions() + "\t\t\t")
							.append(i + "\t��\t:" + step.getExitInstructions() + "\t\t\t");
				}
				N_showdatainfotv.setText(SB);
				DrivingOverlayUtil drivingoverlayutil = new DrivingOverlayUtil(nearby_baidumap, navigation);
				drivingoverlayutil.setData(drivingrouteresult.getRouteLines().get(0));
				drivingoverlayutil.addToMap();
				drivingoverlayutil.zoomToSpan();

			}

		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult transitrouteresult) {
			// TODO Auto-generated method stub
			nearby_baidumap.clear();// ���ͼ�㸲����
			N_showdatainfotv.setText("");
			SB.delete(0, SB.length());
			if (transitrouteresult == null || transitrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(navigation, null, "��ܰ��ʾ", "û���ҵ����ʵ�·��");
			}
			if (transitrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (transitrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {

				N_routeline = transitrouteresult.getRouteLines().get(0);// ��һ����
				SB.append("���\t\t" + N_currentaddress).append("\t\t\t----\t\t\t").append("�յ�\t\t" + N_targetaddress)
						.append("\n\n");
				for (int i = 0; i < N_routeline.getAllStep().size(); i++) {
					TransitRouteLine.TransitStep step = (TransitStep) N_routeline.getAllStep().get(i);
					SB.append(i + "\t\t:" + step.getInstructions() + "\t\t\t");
				}

				N_showdatainfotv.setText(SB);
				TransitOverlayUtil transitoverlayutil = new TransitOverlayUtil(nearby_baidumap, navigation);
				transitoverlayutil.setData(transitrouteresult.getRouteLines().get(0));
				transitoverlayutil.addToMap();
				transitoverlayutil.zoomToSpan();
			}

		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult walkingrouteresult) {
			// TODO Auto-generated method stub
			nearby_baidumap.clear();// ���ͼ�㸲����
			N_showdatainfotv.setText("");
			SB.delete(0, SB.length());
			if (walkingrouteresult == null || walkingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(navigation, null, "��ܰ��ʾ", "û���ҵ����ʵ�·��");
			}
			if (walkingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (walkingrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
				N_routeline = walkingrouteresult.getRouteLines().get(0);// ��һ����
				Log.e("L", "��ʾ����");

				SB.append("���\t\t" + N_currentaddress).append("\t\t\t----\t\t\t").append("�յ�\t\t" + N_targetaddress)
						.append("\n\n");
				for (int i = 0; i < N_routeline.getAllStep().size(); i++) {
					WalkingRouteLine.WalkingStep step = (WalkingStep) N_routeline.getAllStep().get(i);
					SB.append(i + "\t���\t:" + step.getEntranceInstructions()).append("\t\t\t")
							.append(i + "\t����\t:" + step.getExitInstructions()).append("\t\t\t");
				}

				N_showdatainfotv.setText(SB);
				WalkingOverlayUtil walkingoverlayutil = new WalkingOverlayUtil(nearby_baidumap, navigation);
				walkingoverlayutil.setData(walkingrouteresult.getRouteLines().get(0));
				walkingoverlayutil.addToMap();
				walkingoverlayutil.zoomToSpan();
			}

		}

	};

	/**
	 * �������
	 */
	OnGetGeoCoderResultListener N_GeoCodeResultListener = new OnGetGeoCoderResultListener() {

		// �ŵ��������
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reversegeocoderesult) {
			// TODO Auto-generated method stub
			if (reversegeocoderesult == null || reversegeocoderesult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(navigation, null, "������ʾ��", "�޷���ȡ��ϸ��ַ��Ϣ��");
				return;

			}

			if (Nisfristlocation) {

			}
			if (isMyaddress == true) {
				N_currentaddress = reversegeocoderesult.getAddress();
				N_currentlatlng = reversegeocoderesult.getLocation();
				ToastUtil.CreateToastShow(navigation, null, "�ҵ�λ��",
						N_currentaddress + "\n����:" + N_currentlatlng.latitude + "\nά��:" + N_currentlatlng.longitude);
			} else if (isMyaddress == false) {
				N_targetaddress = reversegeocoderesult.getAddress();
				N_targetlatlng = reversegeocoderesult.getLocation();
				ToastUtil.CreateToastShow(navigation, null, "Ŀ�ĵ�λ��",
						N_targetaddress + "\n����:" + N_targetlatlng.latitude + "\nά��:" + N_targetlatlng.longitude);
			}

		}

		// ���������
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geocoderesult) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * ��λ��
	 */
	public void N_Loc() {
		LatLng NLL = new LatLng(N_lat, N_lng);// ��ȡ��γ��
		MapStatusUpdate nll = MapStatusUpdateFactory.newLatLng(NLL);
		nearby_baidumap.animateMapStatus(nll);

	}

	/**
	 * ���ðٶ�poi���������¼�
	 */
	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

		@SuppressWarnings("null")
		@SuppressLint("InflateParams")
		@Override
		public void onGetPoiResult(PoiResult poiresult) {
			// TODO Auto-generated method stub
			List<PoiInfo> infos = new ArrayList<PoiInfo>();
			infos = poiresult.getAllPoi();// ��ȡpoi��Ϣ

			if (poiresult.error == ERRORNO.RESULT_NOT_FOUND) {
				showresultnumber.setText("���ҵ� 0 ����Ϣ");

				ToastUtil.CreateToastShow(navigation, null, "��ܰ��ʾ", "δ�ҵ�����Ҫ�Ľ����");
			} else {
				BitmapDescriptor Nicon = BitmapDescriptorFactory.fromResource(R.drawable.nmarker);
				showresultnumber.setText("���ҵ�" + infos.size() + "����Ϣ");
				for (int i = 0; i < infos.size(); i++) {
					MapStatusUpdate nearby_ll = MapStatusUpdateFactory.newLatLng(infos.get(0).location);
					nearby_baidumap.animateMapStatus(nearby_ll);
					N_markeroptions.position(infos.get(i).location).zIndex(10).icon(Nicon);
					N_marker = (Marker) nearby_baidumap.addOverlay(N_markeroptions);

				}
			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poidetailresult) {
			// TODO Auto-generated method stub
			if (poidetailresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(navigation, null, "������ʾ", "û���ҵ���Ϣ��Ϣ");
			} else {
				// �����ɹ�

			}

		}
	};

	/**
	 * ��ʼ�����򴫸���
	 */
	private void N_initOritation() {
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new liudingcheng.maps.MyOrientationListener.OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						Nxdirection = (int) x;

						// ���춨λ����
						MyLocationData locData = new MyLocationData.Builder().accuracy(Ncurrentaccracy)
								// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
								.direction(Nxdirection).latitude(N_lat).longitude(N_lng).build();
						// ���ö�λ����
						nearby_baidumap.setMyLocationData(locData);
						// �����Զ���ͼ��
						MyLocationConfiguration config = new MyLocationConfiguration(Ncurrentmode, true, null);
						nearby_baidumap.setMyLocationConfigeration(config);

					}
				});
	}

	/**
	 * �Զ��嶨λ��ʵ�ְٶȶ�λ�����¼�
	 */
	public class N_bdlocationlistener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || nearby_mapview == null)
				return;
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(Nxdirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			Ncurrentaccracy = location.getRadius();// �뾶
			// ���ö�λ���� ��γ��
			nearby_baidumap.setMyLocationData(locData);
			N_lat = location.getLatitude();// ��ȡ����
			N_lng = location.getLongitude();// ��ȡγ��
			MyLocationConfiguration config = new MyLocationConfiguration(Ncurrentmode, true, null);
			nearby_baidumap.setMyLocationConfigeration(config);
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
			if (Nisfristlocation) {// ���Ϊ��
				Nisfristlocation = true;
				// ��λ
				LatLng Nll = new LatLng(location.getLatitude(), location.getLongitude());//
				Log.e("L", "����   ---��γ��" + Nll);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(Nll);
				nearby_baidumap.animateMapStatus(u);
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * ��ʼ����λ��ش���
	 */
	private void N_initLocation() {
		// ��λ��ʼ�� nearby_locationListener
		N_locationclient = new LocationClient(navigation);// �½�һ����λ�ͻ���
		N_bdlocationlistener = new N_bdlocationlistener();// ��λ������
		N_locationclient.registerLocationListener(N_bdlocationlistener);// ���ö�λ���������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ���ö�λ����
		option.setAddrType("all");
		N_locationclient.setLocOption(option);
	}

	// ����ʶ��Ի���
	public void YYDialog() {
		// ��������
		mbundle = new Bundle();
		mbundle.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);// ����Key
		mbundle.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);// ����Key
		mbundle.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);// ��������ʶ��Ի���Ϊ��ɫ��������
		BAD = new BaiduASRDigitalDialog(navigation, mbundle);
		DRL = new DialogRecognitionListener() {
			@Override
			public void onResults(Bundle mResults) {
				// ��ȡ�Ǳ��ַ�ת
				ArrayList<String> rs = mResults != null ? mResults.getStringArrayList(RESULTS_RECOGNITION) : null;
				if (rs != null && rs.size() > 0) {
					searchWord.setText(rs.get(0));
				}

			}

		};
		BAD.setDialogRecognitionListener(DRL);
		// ��������ʶ��ģʽΪ����ģʽ
		BAD.setSpeechMode(BaiduASRDigitalDialog.SPEECH_MODE_INPUT);
		// ��������ʶ��
		BAD.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE, true);
		BAD.show();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		nearby_baidumap.setMyLocationEnabled(true); // ����ͼ�㶨λ
		if (!N_locationclient.isStarted()) {
			N_locationclient.start();// ��ʼ��λ
		}
		// �������򴫸���
		myOrientationListener.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		nearby_mapview.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		nearby_mapview.onResume();
	}

	private void onstop() {
		// TODO Auto-generated method stub
		nearby_baidumap.setMyLocationEnabled(false);// �رն�λ
		N_locationclient.stop();// ������λ����
		myOrientationListener.stop();
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// �ͷ�ʵ��
		N_routeplansearch.destroy();
		nearby_mapview.onDestroy();// ����ʵ��
		poisearch.destroy();// ����ʵ��
		super.onDestroy();
	}

}
