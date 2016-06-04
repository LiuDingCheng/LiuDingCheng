package liudingcheng.maps;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import ToolUtil.DrivingOverlayUtil;
import ToolUtil.ToastUtil;
import ToolUtil.TransitOverlayUtil;
import ToolUtil.WalkingOverlayUtil;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����ͼ
 * 
 * @author LiuDingCheng
 *
 */
public class MainActivity extends Activity {

	private MapView mMapView;// �ٶȵ�ͼ�ؼ�
	private BaiduMap mBaiduMap;// ��ͼʵ��
	private LocationClient locationClient;// ��λ�ͻ���
	public LDCLocationListener locationListener;// ��λ������
	private MyOrientationListener myOrientationListener;// ���򴫸����ļ�����
	private LocationMode mCurrentMode;// ��λģʽ
	private volatile boolean isFristLocation = true;// �Ƿ��ǵ�һ�ζ�λ
	// ����һ�εľ�γ�� ��γ��
	private double mCurrentLantitude;// ��ǰλ�õľ���
	private double mCurrentLongitude;// ��ǰλ�õ�ά��
	private float mCurrentAccracy;// ��ǰ�ľ���
	private int mXDirection;// ���򴫸���X�����ֵ
	private Button btn1, btn2, btn3;// ������·�ߣ�����
	private LinearLayout localicon_line;// ��λ
	private LinearLayout layericon_line;// ��ά
	private LinearLayout nearbyicon_line;// �����������ѯ��ʾ
	private LinearLayout mainSearch_line;// ��ѯ��ͼ
	private EditText mainkey;// �ؼ���
	private TextView mainsearch;// ��ѯ����
	// �������ּ���
	private View popview;// ��������
	PopupWindow popupWindow = null;// ������ͼ
	private Context main;
	private static boolean isExit = false;
	private boolean isShowSearchView = true;// ��ʾ��ѯ��ͼ��
	private boolean isShowRoute = true;// ��ʾ·�߹滮
	private boolean is3D = true;// �ж��Ƿ�Ϊ3D
	// poi��ѯ
	private PoiSearch poisearch = null;// ʲô�ٶ�poi����
	private PoiNearbySearchOption nearbySearchOption = null;// poi������������
	private OverlayOptions overlayoptions = null;// ������
	private PoiDetailSearchOption poidetailsearchoption = null;// ��ϸ����
	@SuppressWarnings("unused")
	private Marker marker = null;// ���
	private boolean isplanningMKinfo = false;// ·�߹滮�����Ϣ

	/**
	 * ·�߹滮
	 */
	private RoutePlanSearch routeplansearch = null;// ��·�滮poi
	private EditText route_city, route_star, route_end;// ���У���㣬�յ㣬
	private Button route_bus_btn, route_walk_btn, route_driv_btn;// �滮����
	private PlanNode SN, EN;// ����յ�
	private TransitRoutePlanOption transitrouteplanoption;// ����
	private WalkingRoutePlanOption walkingrouteplanoption;// ����
	private DrivingRoutePlanOption drivingrouteplanoption;// �ݳ�
	private LinearLayout Route_line;// ��ʾ��ѯ
	/**
	 * �������
	 */
	private GeoCoder geocoder;// �������
	private ImageView getAddress;// ��ȡ����λ��
	private ReverseGeoCodeOption reversegeocodeoption = null;
	/**
	 * ��ʾ������ϸ����
	 */
	private Bundle DetailBundle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.main_activity);
		initView();// ��ʼ����Դ
		funcationListener();// ���ü����¼�

	}

	// Ĭ�ϵĹ��캯��
	public MainActivity() {
		main = MainActivity.this;

	}
	
	

	// ��ʼ����Դ
	@SuppressLint("InflateParams")
	public void initView() {
		mMapView = (MapView) findViewById(R.id.id_bmapView);// mapview
		mMapView.showZoomControls(false);// �������Ű�ť
		mBaiduMap = mMapView.getMap();// ��ȡMapViewͼ��
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));// ����mapview������
		// װ�ص�����Ӧ���� // ����PopupWindow���� ���ô�С // ��������
		popview = MainActivity.this.getLayoutInflater().inflate(R.layout.popwindows, null);
		popupWindow = new PopupWindow(popview, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setAnimationStyle(R.style.ldc_pop_anim);
		// mapview����ӿؼ�
		localicon_line = (LinearLayout) findViewById(R.id.localicon_line);// ��λ
		layericon_line = (LinearLayout) findViewById(R.id.layericon_line);// ��άͼ��
		nearbyicon_line = (LinearLayout) findViewById(R.id.nearbyicon_line);// ��ʾ��ѯ��ͼ
		mainSearch_line = (LinearLayout) findViewById(R.id.mainSearch_line);// ��ѯ��ͼ
		// ��ȡ�ؼ���Դ
		btn1 = (Button) findViewById(R.id.main_btn_1);// ����
		btn2 = (Button) findViewById(R.id.main_btn_2);// ·��
		btn3 = (Button) findViewById(R.id.main_btn_3);// ����
		// ������ѯ
		mainkey = (EditText) findViewById(R.id.main_searchkey);// �ؼ���
		mainsearch = (TextView) findViewById(R.id.main_search);// ��ѯ
		// ��ʼ�� PoiSearch ����
		poisearch = PoiSearch.newInstance();
		nearbySearchOption = new PoiNearbySearchOption();// ��������
		poidetailsearchoption = new PoiDetailSearchOption();// ��ϸ����
		overlayoptions = new MarkerOptions();// ��Ǹ�����
		mCurrentMode = LocationMode.NORMAL;// Ĭ�϶�λģʽΪ��ͨ��λ
		// -------------------------------------------------- ·�߹滮
		// ��ʼ��·�߹滮��������
		routeplansearch = RoutePlanSearch.newInstance();
		// ��ȡ��Դ
		route_bus_btn = (Button) findViewById(R.id.route_gongjiao_btn);// ����
		route_walk_btn = (Button) findViewById(R.id.route_buxing_btn);// ����
		route_driv_btn = (Button) findViewById(R.id.route_car_btn);// �ݳ�
		// ��ȡ����
		route_city = (EditText) findViewById(R.id.route_city);// ����
		route_star = (EditText) findViewById(R.id.route_qd_addr);// ���
		route_end = (EditText) findViewById(R.id.route_zd_addr);// �յ�
		transitrouteplanoption = new TransitRoutePlanOption();// �����滮
		walkingrouteplanoption = new WalkingRoutePlanOption();// ���й滮
		drivingrouteplanoption = new DrivingRoutePlanOption();// �ݳ��滮
		Route_line = (LinearLayout) findViewById(R.id.Route_line);// ·�߽���
		/**
		 * ����жϳ�ʼֵ
		 */
		isplanningMKinfo = false;

		/**
		 * �������
		 */
		geocoder = GeoCoder.newInstance();// ����ʼ���������
		getAddress = (ImageView) findViewById(R.id.get_address);// ��ȡ����λ��
		reversegeocodeoption = new ReverseGeoCodeOption();// ���������
		// ��һ�ζ�λ
		isFristLocation = true;// ��һ�ζ�λ
		init_LDC_Location();// ��ʼ����λ
		init_LDC_Oritation();// ��ʼ��������

	}

	public void funcationListener() {
		// ��Ȥ��
		poisearch.setOnGetPoiSearchResultListener(poiListener);// ���ü�������
		// ����·�߹滮����
		routeplansearch.setOnGetRoutePlanResultListener(planresultlistener);
		// �������
		geocoder.setOnGetGeoCodeResultListener(GeoCodeResultListener);
		// ��ȡ����λ��
		getAddress.setOnClickListener(new SingleClickListener(main));
		// ���ü����¼�
		btn1.setOnClickListener(new SingleClickListener(main));
		btn2.setOnClickListener(new SingleClickListener(main));
		btn3.setOnClickListener(new SingleClickListener(main));
		// 3ά��ͼ
		layericon_line.setOnClickListener(new SingleClickListener(main));
		// ��λ
		localicon_line.setOnClickListener(new SingleClickListener(main));
		// ��ѯ��ʾ
		nearbyicon_line.setOnClickListener(new SingleClickListener(main));
		// ��ѯ
		mainsearch.setOnClickListener(new SingleClickListener(main));
		// ���ù����滮�����¼�
		route_bus_btn.setOnClickListener(new SingleClickListener(main));// ����
		route_walk_btn.setOnClickListener(new SingleClickListener(main));// ����
		route_driv_btn.setOnClickListener(new SingleClickListener(main));// �ݳ�
		// ��ȡ����λ��
		getAddress.setOnClickListener(new SingleClickListener(main));
		// ����
		popview.findViewById(R.id.pop_map0).setOnClickListener(new SingleClickListener(main));
		// ��ͨ��ͼ
		popview.findViewById(R.id.pop_map1).setOnClickListener(new SingleClickListener(main));
		// ��ͨ��ͼ
		popview.findViewById(R.id.pop_map2).setOnClickListener(new SingleClickListener(main));
		// ������ͼ
		popview.findViewById(R.id.pop_map3).setOnClickListener(new SingleClickListener(main));
		// ��������
		popview.findViewById(R.id.pop_about).setOnClickListener(new SingleClickListener(main));
		// ���ߵ�ͼ����
		popview.findViewById(R.id.pop_map4).setOnClickListener(new SingleClickListener(main));
		// ȡ������
		popview.findViewById(R.id.pop_close).setOnClickListener(new SingleClickListener(main));
		// �˳�APP
		popview.findViewById(R.id.pop_exit).setOnClickListener(new SingleClickListener(main));
		// �ٶȵ�ͼ����������¼�
		mBaiduMap.setOnMarkerClickListener(new MarkerListener());

	}

	/**
	 * �����ڲ�������¼�
	 * 
	 * @author LiuDingCheng
	 *
	 */
	class SingleClickListener implements OnClickListener {
		private Context mainlistener;

		public SingleClickListener(Context context) {
			mainlistener = context;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SN = PlanNode.withCityNameAndPlaceName(route_city.getText().toString().trim(),
					route_star.getText().toString().trim());
			EN = PlanNode.withCityNameAndPlaceName(route_city.getText().toString().trim(),
					route_end.getText().toString().trim());
			switch (v.getId()) {
			case R.id.localicon_line:
				// ��λģʽ
				switch (mCurrentMode) {
				case NORMAL:
					Toast.makeText(main, "����ģʽ", Toast.LENGTH_SHORT).show();

					mCurrentMode = LocationMode.FOLLOWING;
					break;
				case FOLLOWING:
					Toast.makeText(main, "����ģʽ", Toast.LENGTH_SHORT).show();
					mCurrentMode = LocationMode.COMPASS;
					break;
				case COMPASS:
					Toast.makeText(main, "��ͨģʽ", Toast.LENGTH_SHORT).show();
					mCurrentMode = LocationMode.NORMAL;
					break;
				}
				// ��λ
				LDCmyLoc();
				break;
			case R.id.layericon_line:
				// 3άͼ��
				LDCmy3D();
				break;

			case R.id.nearbyicon_line:
				// ��ʾ��ѯ��ͼ
				if (isShowSearchView == true) {

					mainSearch_line.setVisibility(View.VISIBLE);// ��ʾ
					Route_line.setVisibility(View.GONE);// ����·�߹滮
					isShowSearchView = false;
				} else if (isShowSearchView == false) {

					mainSearch_line.setVisibility(View.GONE);// ����ʾ
					isShowSearchView = true;
				}

				break;
			case R.id.main_search:
				// ��ѯ����
				LatLng lls = new LatLng(mCurrentLantitude, mCurrentLongitude);
				Log.e("L", "��γ��--------------" + lls);
				mBaiduMap.clear();// ���ͼ��
				isplanningMKinfo=false;
				nearbySearchOption.location(lls);
				nearbySearchOption.sortType(PoiSortType.distance_from_near_to_far);// ����
				nearbySearchOption.pageCapacity(50);// ÿһҳ������
				nearbySearchOption.pageNum(1);// ҳ��
				nearbySearchOption.radius(1000);// �뾶��
				nearbySearchOption.keyword(mainkey.getText().toString());
				poisearch.searchNearby(nearbySearchOption);

				break;
			// -------------------------------·�߹滮
			case R.id.route_gongjiao_btn:
				// �����滮
				mBaiduMap.clear();// ���ͼ��
				isplanningMKinfo = true;
				transitrouteplanoption.city(route_city.getText().toString().trim());// ����
				transitrouteplanoption.from(SN);// ���
				transitrouteplanoption.to(EN);// �յ�
				routeplansearch.transitSearch(transitrouteplanoption);
				Route_line.setVisibility(View.GONE);// ����

				break;
			case R.id.route_buxing_btn:
				// ���й滮
				mBaiduMap.clear();// ���ͼ��
				isplanningMKinfo = true;
				walkingrouteplanoption.from(SN);// ���
				walkingrouteplanoption.to(EN);// �յ�
				routeplansearch.walkingSearch(walkingrouteplanoption);
				Route_line.setVisibility(View.GONE);// ����
				break;
			case R.id.route_car_btn:
				// �ݳ��滮
				mBaiduMap.clear();// ���ͼ��
				isplanningMKinfo = true;
				drivingrouteplanoption.from(SN);// ���
				drivingrouteplanoption.to(EN);// �յ�
				routeplansearch.drivingSearch(drivingrouteplanoption);
				Route_line.setVisibility(View.GONE);// ����
				break;
			case R.id.get_address:
				LatLng pt = new LatLng(mCurrentLantitude, mCurrentLongitude);// ��γ��
				// ��Geo����
				reversegeocodeoption.location(pt);
				geocoder.reverseGeoCode(reversegeocodeoption);
				break;
			// poi
			case R.id.main_btn_1:
				Intent mnearby = new Intent();
				mnearby.setClass(mainlistener, NavigationActivity.class);
				startActivity(mnearby);
				break;
			// ·��
			case R.id.main_btn_2:

				// ��ʾ·��
				if (isShowRoute == true) {

					Route_line.setVisibility(View.VISIBLE);// ��ʾ
					mainSearch_line.setVisibility(View.GONE);// ���ظ�����ѯ
					isShowRoute = false;
				} else if (isShowRoute == false) {

					Route_line.setVisibility(View.GONE);// ����ʾ
					isShowRoute = true;
				}
				break;
			// ����
			case R.id.main_btn_3:

				// ��ʾ����
				popupWindow.showAtLocation(MainActivity.this.findViewById(R.id.main_btn_3), Gravity.BOTTOM, 0, 0);

				break;
			case R.id.pop_map0:
				if (mBaiduMap.isBaiduHeatMapEnabled() == false) {
					mBaiduMap.setBaiduHeatMapEnabled(true);
					Toast.makeText(MainActivity.this, "��������ͼ��", Toast.LENGTH_SHORT).show();
				} else {
					mBaiduMap.setBaiduHeatMapEnabled(false);
					Toast.makeText(MainActivity.this, "�ر�����ͼ��", Toast.LENGTH_SHORT).show();
				}
				popupWindow.dismiss();
				break;
			case R.id.pop_map1:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
				popupWindow.dismiss();
				break;
			case R.id.pop_map2:
				if (mBaiduMap.isTrafficEnabled()) {
					mBaiduMap.setTrafficEnabled(false);
					Toast.makeText(MainActivity.this, "�ر�ʵʱ��ͨ", Toast.LENGTH_SHORT).show();
				} else {
					mBaiduMap.setTrafficEnabled(true);
					Toast.makeText(MainActivity.this, "����ʵʱ��ͨ", Toast.LENGTH_SHORT).show();
				}
				popupWindow.dismiss();

				break;
			case R.id.pop_map3:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				popupWindow.dismiss();

				break;
			case R.id.pop_map4:
				// ���ߵ�ͼ����
				Intent offlinemap = new Intent();
				offlinemap.setClass(mainlistener, OfflineActivity.class);
				startActivity(offlinemap);
				popupWindow.dismiss();

				break;
			case R.id.pop_about:
				Intent it3 = new Intent();
				it3.setClass(main, AboutMe.class);
				startActivity(it3);
				popupWindow.dismiss();
				break;

			case R.id.pop_close:
				popupWindow.dismiss();
				break;
			case R.id.pop_exit:
				Dialog exitDialog = new AlertDialog.Builder(main).setTitle("Android��ܰ��ʾ").setIcon(R.drawable.close)
						.setMessage("�Ƿ��˳�������!").setPositiveButton("��", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								finish();
								java.lang.System.exit(0);
							}
						}).setNegativeButton("��", null).create();
				exitDialog.show();
				break;
			}
			Log.e("L", "����switch----------------------");

		}

	}

	/**
	 * ���������¼� �������ק�¼�
	 * 
	 * @author LiuDingCheng
	 *
	 */
	class MarkerListener implements OnMarkerClickListener {
		/**
		 * ���������¼�
		 */
		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			if (isplanningMKinfo == false) {
				/**
				 * ��ȡ��ϸ��Ϣ
				 */
				Log.e("L", "--------------uid----------------" + arg0.getExtraInfo().getString("uid"));
				poidetailsearchoption.poiUid(arg0.getExtraInfo().getString("uid"));
				poisearch.searchPoiDetail(poidetailsearchoption);

			} else if (isplanningMKinfo == true) {

				// ������������
				reversegeocodeoption.location(arg0.getPosition());
				geocoder.reverseGeoCode(reversegeocodeoption);

			}
			return false;
		}

	}

	/**
	 * ��ͼ�������� ���ðٶ�poi���������¼�
	 */

	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiResult(PoiResult poiresult) {
			// TODO Auto-generated method stub
			List<PoiInfo> info = poiresult.getAllPoi();// ��ȡpoi��Ϣ
			if (poiresult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {

				ToastUtil.CreateToastShow(main, null, "��ܰ��ʾ", "δ�ҵ�����Ҫ�Ľ����");
			} else {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nmarker);
				for (int j = 0; j < info.size(); j++) {
					Log.e("L", "���----------------------" + info.get(j).name);
					MapStatusUpdate search_ll = MapStatusUpdateFactory.newLatLng(info.get(0).location);// ��λ������λ��
					mBaiduMap.animateMapStatus(search_ll);
					// ������
					Bundle poinfo = new Bundle();
					poinfo.putString("uid", info.get(j).uid);// ��ϸ����
					((MarkerOptions) overlayoptions).position(info.get(j).location)// λ��
							.icon(icon).zIndex(10)// ���ö���ͼ��
							.title(info.get(j).name)// ���ñ���
							.extraInfo(poinfo)// uid
							.draggable(true);// ����������ק

					// �ڵ�ͼ�����marker������ʾ
					marker = (Marker) mBaiduMap.addOverlay(overlayoptions);

				}

			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poidetailresult) {
			// TODO Auto-generated method stub
			DetailBundle = new Bundle();
			// �������
			DetailBundle.putString("getName", poidetailresult.getName());
			DetailBundle.putString("getAddress", poidetailresult.getAddress());
			DetailBundle.putString("getDetailUrl", poidetailresult.getDetailUrl());
			DetailBundle.putString("getShopHours", poidetailresult.getShopHours());
			DetailBundle.putString("getTelephone", poidetailresult.getTelephone());
			DetailBundle.putString("getTag", poidetailresult.getTag());
			DetailBundle.putString("getType", poidetailresult.getType());
			DetailBundle.putInt("getCheckinNum", poidetailresult.getCheckinNum());
			DetailBundle.putInt("getCommentNum", poidetailresult.getCommentNum());
			DetailBundle.putDouble("getEnvironmentRating", poidetailresult.getEnvironmentRating());
			DetailBundle.putDouble("getFacilityRating", poidetailresult.getFacilityRating());
			DetailBundle.putDouble("getFavoriteNum", poidetailresult.getFavoriteNum());
			DetailBundle.putDouble("getHygieneRating", poidetailresult.getHygieneRating());
			DetailBundle.putDouble("getImageNum", poidetailresult.getImageNum());
			DetailBundle.putDouble("getOverallRating", poidetailresult.getOverallRating());
			DetailBundle.putDouble("getPrice", poidetailresult.getPrice());
			DetailBundle.putDouble("getServiceRating", poidetailresult.getServiceRating());
			DetailBundle.putDouble("getTasteRating", poidetailresult.getTasteRating());
			DetailBundle.putDouble("getTechnologyRating", poidetailresult.getTechnologyRating());

			// ��ʾ����
			String DatailStr = "����:" + poidetailresult.getName() + "\n��ַ:" + poidetailresult.getAddress() + "\n�۸�:"
					+ poidetailresult.getPrice() + "\nӪҵʱ��:" + poidetailresult.getShopHours() + "\n�绰:"
					+ poidetailresult.getTelephone() + "\n�ۺ�����:" + poidetailresult.getOverallRating() + "\n��ǩ:"
					+ poidetailresult.getTag();
			final String phone = poidetailresult.getTelephone();// �绰
			Log.e("L", "----------------------��ϸ��Ϣ---------------------------" + DatailStr + "\n"
					+ poidetailresult.getDetailUrl());
			Dialog show = new AlertDialog.Builder(main, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setIcon(null)
					.setTitle("������ʾ").setMessage(DatailStr).setNegativeButton("ȡ\t\t��", null)
					.setNeutralButton("�绰��ϵ", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (phone.isEmpty()) {
								ToastUtil.CreateToastShow(main, null, "��ϵ�̼�", "û�е绰��������������");
							} else {
								Intent phonecall = new Intent(Intent.ACTION_DIAL);
								Uri call = Uri.parse("tel:" + phone);
								phonecall.setData(call);
								startActivity(phonecall);
							}

						}
					}).setPositiveButton("��ϸ��Ϣ", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent it4 = new Intent();
							it4.setClass(main, PoiDetailActivity.class);
							it4.putExtras(DetailBundle);
							startActivity(it4);

						}
					}).create();
			show.show();

		}
	};
	/**
	 * ·�߹滮
	 */
	OnGetRoutePlanResultListener planresultlistener = new OnGetRoutePlanResultListener() {
		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult drivingrouteresult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();// ���ͼ�㸲����
			if (drivingrouteresult == null || drivingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "��ܰ��ʾ", "û���ҵ����ʵ�·��");
			}
			if (drivingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (drivingrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingOverlayUtil drivingoverlayutil = new DrivingOverlayUtil(mBaiduMap, main);
				drivingoverlayutil.setData(drivingrouteresult.getRouteLines().get(0));
				drivingoverlayutil.addToMap();
				drivingoverlayutil.zoomToSpan();
			}

		}

		// �����˻�·�߹滮
		@Override
		public void onGetTransitRouteResult(TransitRouteResult transitrouteresult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();// ���ͼ�㸲����
			if (transitrouteresult == null || transitrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "��ܰ��ʾ", "û���ҵ����ʵ�·��");
			}
			if (transitrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (transitrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
				TransitOverlayUtil transitoverlayutil = new TransitOverlayUtil(mBaiduMap, main);
				transitoverlayutil.setData(transitrouteresult.getRouteLines().get(0));// ��ȡ��һ�����
				transitoverlayutil.addToMap();
				transitoverlayutil.zoomToSpan();
			}

		}

		// �ݳ��˻�·�߹滮
		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult walkingrouteresult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();// ���ͼ�㸲����
			if (walkingrouteresult == null || walkingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "��ܰ��ʾ", "û���ҵ����ʵ�·��");
			}
			if (walkingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
				// result.getSuggestAddrInfo()
				return;
			}
			if (walkingrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
				WalkingOverlayUtil walkingoverlayutil = new WalkingOverlayUtil(mBaiduMap, main);
				walkingoverlayutil.setData(walkingrouteresult.getRouteLines().get(0));// ��ȡ��һ�����
				walkingoverlayutil.addToMap();// ��Χ�ڿɼ�ȫ��
				walkingoverlayutil.zoomToSpan();
			}

		}

	};
	/**
	 * �������
	 */
	OnGetGeoCoderResultListener GeoCodeResultListener = new OnGetGeoCoderResultListener() {

		// �ŵ��������
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reversegeocoderesult) {
			// TODO Auto-generated method stub
			if (reversegeocoderesult == null || reversegeocoderesult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "������ʾ��", "�޷���ȡ��ϸ��ַ��Ϣ��");
				return;

			}
			CharSequence address = reversegeocoderesult.getAddress();
			if (isplanningMKinfo == true) {
				ToastUtil.CreateToastShow(main, null, "�����λ��",
						address + "\n����:" + reversegeocoderesult.getLocation().latitude + "\nά��:"
								+ reversegeocoderesult.getLocation().longitude);

			} else if (isplanningMKinfo == false) {
				route_star.setText(address);

			}

		}

		// ���������
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geocoderesult) {
			// TODO Auto-generated method stub

		}
	};

	// ������ؼ�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DoubleClickExit();
			return false;

		}
		return super.onKeyDown(keyCode, event);
	}

	// ˫���˳�APP�����¼�
	public void DoubleClickExit() {

		Timer toExit = null;
		if (isExit == false) {
			isExit = true;// ׼���˳�
			ToastUtil.CreateToastShow(main, null, "ϵͳ��ʾ��", "�ڵ�һ�η��ؼ����뿪������");
			toExit = new Timer();
			toExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2 * 1000);// 2s��û�а��·��ؽ�����������ʱ��ȡ��ִ������
		} else {
			finish();// ������ǰ
			java.lang.System.exit(0);// ������������,���ӳ���
		}

	}

	public boolean onTouchEvent(MotionEvent event) {

		return false;

	};

	/**
	 * �Զ��嶨λ��ʵ�ְٶȶ�λ�����¼�
	 */
	public class LDCLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();// �뾶
			// ���ö�λ���� ��γ��
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);
			mBaiduMap.setMyLocationConfigeration(config);
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
			if (isFristLocation) {
				isFristLocation = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());// ��ͼ��λ
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * ��ʼ�����򴫸���
	 */
	private void init_LDC_Oritation() {
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new liudingcheng.maps.MyOrientationListener.OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;

						// ���춨λ����
						MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
								// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
								.direction(mXDirection).latitude(mCurrentLantitude).longitude(mCurrentLongitude)
								.build();
						// ���ö�λ����
						mBaiduMap.setMyLocationData(locData);
						// �����Զ���ͼ�� null ʹ��Ĭ��ͼ��
						// BitmapDescriptor mCurrentMarker =
						// BitmapDescriptorFactory.fromResource(R.drawable.ldc_map_gps);
						MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);
						mBaiduMap.setMyLocationConfigeration(config);

					}
				});
	}

	/**
	 * ��ʼ����λ��ش���
	 */
	private void init_LDC_Location() {
		// ��λ��ʼ��
		locationClient = new LocationClient(main);// �½�һ����λ�ͻ���
		locationListener = new LDCLocationListener();// ��λ
		locationClient.registerLocationListener(locationListener);// ���ö�λ���������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setAddrType("all");
		option.setScanSpan(2000); // ���ö�λ���ʱ��
		locationClient.setLocOption(option);
	}

	/**
	 * ��ͼ�ƶ����ҵ�λ��,�˴��������·���λ����Ȼ��λ�� ֱ�������һ�ξ�γ�ȣ������ʱ��û�ж�λ�ɹ������ܻ���ʾЧ������
	 */
	private void LDCmyLoc() {

		LatLng ms1 = new LatLng(mCurrentLantitude, mCurrentLongitude);// ��λ
		MapStatus ms2 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();// ��ʾƽ��
		MapStatusUpdate msu1 = MapStatusUpdateFactory.newLatLng(ms1);
		MapStatusUpdate msu2 = MapStatusUpdateFactory.newMapStatus(ms2);
		mBaiduMap.animateMapStatus(msu1);
		mBaiduMap.animateMapStatus(msu2);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16.0f));// ���ñ�����

	}

	/**
	 * ת��3άͼ��
	 */
	private void LDCmy3D() {

		// ��ʾ3ά
		MapStatus ms1 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(-45).build();// 3D
		MapStatusUpdate msu1 = MapStatusUpdateFactory.newMapStatus(ms1);
		MapStatus ms2 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();// ƽ��
		MapStatusUpdate msu2 = MapStatusUpdateFactory.newMapStatus(ms2);
		Log.e("L", "3D-------------" + is3D);
		if ((mBaiduMap.getMapStatus().overlook) <= -19) {
			mBaiduMap.animateMapStatus(msu2);// ƽ��
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(20.0f));// ���ñ�����
		}
		if ((mBaiduMap.getMapStatus().overlook) == 0) {
			mBaiduMap.animateMapStatus(msu1);// 3D
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));// ���ñ�����
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		// ����ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!locationClient.isStarted()) {
			locationClient.start();
		}
		// �������򴫸���
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onStop() {
		// �ر�ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(false);// �رն�λ
		locationClient.stop();// ������λ����
		// �رշ��򴫸���
		myOrientationListener.stop();
		super.onStop();
	}

	// �������ں�mapview����������
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		poisearch.destroy();// ����ʵ��
		routeplansearch.destroy();// ����ʵ��
		geocoder.destroy();// ����ʵ��
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}