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
 * 主视图
 * 
 * @author LiuDingCheng
 *
 */
public class MainActivity extends Activity {

	private MapView mMapView;// 百度地图控件
	private BaiduMap mBaiduMap;// 地图实例
	private LocationClient locationClient;// 定位客户端
	public LDCLocationListener locationListener;// 定位监听器
	private MyOrientationListener myOrientationListener;// 方向传感器的监听器
	private LocationMode mCurrentMode;// 定位模式
	private volatile boolean isFristLocation = true;// 是否是第一次定位
	// 最新一次的经纬度 经纬度
	private double mCurrentLantitude;// 当前位置的经度
	private double mCurrentLongitude;// 当前位置的维度
	private float mCurrentAccracy;// 当前的精度
	private int mXDirection;// 方向传感器X方向的值
	private Button btn1, btn2, btn3;// 附近，路线，更多
	private LinearLayout localicon_line;// 定位
	private LinearLayout layericon_line;// 三维
	private LinearLayout nearbyicon_line;// 附近覆盖物查询显示
	private LinearLayout mainSearch_line;// 查询试图
	private EditText mainkey;// 关键字
	private TextView mainsearch;// 查询数据
	// 弹窗布局加载
	private View popview;// 弹窗布局
	PopupWindow popupWindow = null;// 泡泡视图
	private Context main;
	private static boolean isExit = false;
	private boolean isShowSearchView = true;// 显示查询试图？
	private boolean isShowRoute = true;// 显示路线规划
	private boolean is3D = true;// 判断是否为3D
	// poi查询
	private PoiSearch poisearch = null;// 什么百度poi检索
	private PoiNearbySearchOption nearbySearchOption = null;// poi附近检索检索
	private OverlayOptions overlayoptions = null;// 覆盖物
	private PoiDetailSearchOption poidetailsearchoption = null;// 详细检索
	@SuppressWarnings("unused")
	private Marker marker = null;// 标记
	private boolean isplanningMKinfo = false;// 路线规划标记信息

	/**
	 * 路线规划
	 */
	private RoutePlanSearch routeplansearch = null;// 线路规划poi
	private EditText route_city, route_star, route_end;// 城市，起点，终点，
	private Button route_bus_btn, route_walk_btn, route_driv_btn;// 规划类型
	private PlanNode SN, EN;// 起点终点
	private TransitRoutePlanOption transitrouteplanoption;// 公交
	private WalkingRoutePlanOption walkingrouteplanoption;// 不行
	private DrivingRoutePlanOption drivingrouteplanoption;// 驾车
	private LinearLayout Route_line;// 显示查询
	/**
	 * 地理编码
	 */
	private GeoCoder geocoder;// 地理编码
	private ImageView getAddress;// 获取地理位置
	private ReverseGeoCodeOption reversegeocodeoption = null;
	/**
	 * 显示弹窗详细数据
	 */
	private Bundle DetailBundle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.main_activity);
		initView();// 初始化资源
		funcationListener();// 设置监听事件

	}

	// 默认的构造函数
	public MainActivity() {
		main = MainActivity.this;

	}
	
	

	// 初始化资源
	@SuppressLint("InflateParams")
	public void initView() {
		mMapView = (MapView) findViewById(R.id.id_bmapView);// mapview
		mMapView.showZoomControls(false);// 隐藏缩放按钮
		mBaiduMap = mMapView.getMap();// 获取MapView图层
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));// 设置mapview比例尺
		// 装载弹窗对应布局 // 创建PopupWindow对象 设置大小 // 设置主题
		popview = MainActivity.this.getLayoutInflater().inflate(R.layout.popwindows, null);
		popupWindow = new PopupWindow(popview, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setAnimationStyle(R.style.ldc_pop_anim);
		// mapview上添加控件
		localicon_line = (LinearLayout) findViewById(R.id.localicon_line);// 定位
		layericon_line = (LinearLayout) findViewById(R.id.layericon_line);// 三维图层
		nearbyicon_line = (LinearLayout) findViewById(R.id.nearbyicon_line);// 显示查询试图
		mainSearch_line = (LinearLayout) findViewById(R.id.mainSearch_line);// 查询视图
		// 获取控件资源
		btn1 = (Button) findViewById(R.id.main_btn_1);// 附件
		btn2 = (Button) findViewById(R.id.main_btn_2);// 路线
		btn3 = (Button) findViewById(R.id.main_btn_3);// 更多
		// 附近查询
		mainkey = (EditText) findViewById(R.id.main_searchkey);// 关键字
		mainsearch = (TextView) findViewById(R.id.main_search);// 查询
		// 初始化 PoiSearch 对象
		poisearch = PoiSearch.newInstance();
		nearbySearchOption = new PoiNearbySearchOption();// 附近搜索
		poidetailsearchoption = new PoiDetailSearchOption();// 详细检索
		overlayoptions = new MarkerOptions();// 标记覆盖物
		mCurrentMode = LocationMode.NORMAL;// 默认定位模式为普通定位
		// -------------------------------------------------- 路线规划
		// 初始化路线规划检索对象
		routeplansearch = RoutePlanSearch.newInstance();
		// 获取资源
		route_bus_btn = (Button) findViewById(R.id.route_gongjiao_btn);// 公交
		route_walk_btn = (Button) findViewById(R.id.route_buxing_btn);// 步行
		route_driv_btn = (Button) findViewById(R.id.route_car_btn);// 驾乘
		// 获取数据
		route_city = (EditText) findViewById(R.id.route_city);// 城市
		route_star = (EditText) findViewById(R.id.route_qd_addr);// 起点
		route_end = (EditText) findViewById(R.id.route_zd_addr);// 终点
		transitrouteplanoption = new TransitRoutePlanOption();// 公交规划
		walkingrouteplanoption = new WalkingRoutePlanOption();// 步行规划
		drivingrouteplanoption = new DrivingRoutePlanOption();// 驾车规划
		Route_line = (LinearLayout) findViewById(R.id.Route_line);// 路线界面
		/**
		 * 标记判断初始值
		 */
		isplanningMKinfo = false;

		/**
		 * 地理编码
		 */
		geocoder = GeoCoder.newInstance();// 长初始化地理编码
		getAddress = (ImageView) findViewById(R.id.get_address);// 获取地理位置
		reversegeocodeoption = new ReverseGeoCodeOption();// 反地理编码
		// 第一次定位
		isFristLocation = true;// 第一次定位
		init_LDC_Location();// 初始化定位
		init_LDC_Oritation();// 初始化传感器

	}

	public void funcationListener() {
		// 兴趣点
		poisearch.setOnGetPoiSearchResultListener(poiListener);// 设置检索监听
		// 设置路线规划监听
		routeplansearch.setOnGetRoutePlanResultListener(planresultlistener);
		// 地理编码
		geocoder.setOnGetGeoCodeResultListener(GeoCodeResultListener);
		// 获取地理位置
		getAddress.setOnClickListener(new SingleClickListener(main));
		// 设置监听事件
		btn1.setOnClickListener(new SingleClickListener(main));
		btn2.setOnClickListener(new SingleClickListener(main));
		btn3.setOnClickListener(new SingleClickListener(main));
		// 3维视图
		layericon_line.setOnClickListener(new SingleClickListener(main));
		// 定位
		localicon_line.setOnClickListener(new SingleClickListener(main));
		// 查询显示
		nearbyicon_line.setOnClickListener(new SingleClickListener(main));
		// 查询
		mainsearch.setOnClickListener(new SingleClickListener(main));
		// 设置公交规划监听事件
		route_bus_btn.setOnClickListener(new SingleClickListener(main));// 公交
		route_walk_btn.setOnClickListener(new SingleClickListener(main));// 步行
		route_driv_btn.setOnClickListener(new SingleClickListener(main));// 驾车
		// 获取地理位置
		getAddress.setOnClickListener(new SingleClickListener(main));
		// 热力
		popview.findViewById(R.id.pop_map0).setOnClickListener(new SingleClickListener(main));
		// 普通视图
		popview.findViewById(R.id.pop_map1).setOnClickListener(new SingleClickListener(main));
		// 交通视图
		popview.findViewById(R.id.pop_map2).setOnClickListener(new SingleClickListener(main));
		// 卫星视图
		popview.findViewById(R.id.pop_map3).setOnClickListener(new SingleClickListener(main));
		// 关于作者
		popview.findViewById(R.id.pop_about).setOnClickListener(new SingleClickListener(main));
		// 离线地图下载
		popview.findViewById(R.id.pop_map4).setOnClickListener(new SingleClickListener(main));
		// 取消弹窗
		popview.findViewById(R.id.pop_close).setOnClickListener(new SingleClickListener(main));
		// 退出APP
		popview.findViewById(R.id.pop_exit).setOnClickListener(new SingleClickListener(main));
		// 百度地图点击覆盖物事件
		mBaiduMap.setOnMarkerClickListener(new MarkerListener());

	}

	/**
	 * 设置内部类监听事件
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
				// 定位模式
				switch (mCurrentMode) {
				case NORMAL:
					Toast.makeText(main, "跟随模式", Toast.LENGTH_SHORT).show();

					mCurrentMode = LocationMode.FOLLOWING;
					break;
				case FOLLOWING:
					Toast.makeText(main, "罗盘模式", Toast.LENGTH_SHORT).show();
					mCurrentMode = LocationMode.COMPASS;
					break;
				case COMPASS:
					Toast.makeText(main, "普通模式", Toast.LENGTH_SHORT).show();
					mCurrentMode = LocationMode.NORMAL;
					break;
				}
				// 定位
				LDCmyLoc();
				break;
			case R.id.layericon_line:
				// 3维图层
				LDCmy3D();
				break;

			case R.id.nearbyicon_line:
				// 显示查询试图
				if (isShowSearchView == true) {

					mainSearch_line.setVisibility(View.VISIBLE);// 显示
					Route_line.setVisibility(View.GONE);// 隐藏路线规划
					isShowSearchView = false;
				} else if (isShowSearchView == false) {

					mainSearch_line.setVisibility(View.GONE);// 不显示
					isShowSearchView = true;
				}

				break;
			case R.id.main_search:
				// 查询附近
				LatLng lls = new LatLng(mCurrentLantitude, mCurrentLongitude);
				Log.e("L", "经纬度--------------" + lls);
				mBaiduMap.clear();// 清楚图层
				isplanningMKinfo=false;
				nearbySearchOption.location(lls);
				nearbySearchOption.sortType(PoiSortType.distance_from_near_to_far);// 排序
				nearbySearchOption.pageCapacity(50);// 每一页的容量
				nearbySearchOption.pageNum(1);// 页数
				nearbySearchOption.radius(1000);// 半径米
				nearbySearchOption.keyword(mainkey.getText().toString());
				poisearch.searchNearby(nearbySearchOption);

				break;
			// -------------------------------路线规划
			case R.id.route_gongjiao_btn:
				// 公交规划
				mBaiduMap.clear();// 清楚图层
				isplanningMKinfo = true;
				transitrouteplanoption.city(route_city.getText().toString().trim());// 城市
				transitrouteplanoption.from(SN);// 起点
				transitrouteplanoption.to(EN);// 终点
				routeplansearch.transitSearch(transitrouteplanoption);
				Route_line.setVisibility(View.GONE);// 隐藏

				break;
			case R.id.route_buxing_btn:
				// 步行规划
				mBaiduMap.clear();// 清楚图层
				isplanningMKinfo = true;
				walkingrouteplanoption.from(SN);// 起点
				walkingrouteplanoption.to(EN);// 终点
				routeplansearch.walkingSearch(walkingrouteplanoption);
				Route_line.setVisibility(View.GONE);// 隐藏
				break;
			case R.id.route_car_btn:
				// 驾车规划
				mBaiduMap.clear();// 清楚图层
				isplanningMKinfo = true;
				drivingrouteplanoption.from(SN);// 起点
				drivingrouteplanoption.to(EN);// 终点
				routeplansearch.drivingSearch(drivingrouteplanoption);
				Route_line.setVisibility(View.GONE);// 隐藏
				break;
			case R.id.get_address:
				LatLng pt = new LatLng(mCurrentLantitude, mCurrentLongitude);// 经纬度
				// 反Geo搜索
				reversegeocodeoption.location(pt);
				geocoder.reverseGeoCode(reversegeocodeoption);
				break;
			// poi
			case R.id.main_btn_1:
				Intent mnearby = new Intent();
				mnearby.setClass(mainlistener, NavigationActivity.class);
				startActivity(mnearby);
				break;
			// 路线
			case R.id.main_btn_2:

				// 显示路线
				if (isShowRoute == true) {

					Route_line.setVisibility(View.VISIBLE);// 显示
					mainSearch_line.setVisibility(View.GONE);// 隐藏附近查询
					isShowRoute = false;
				} else if (isShowRoute == false) {

					Route_line.setVisibility(View.GONE);// 不显示
					isShowRoute = true;
				}
				break;
			// 更多
			case R.id.main_btn_3:

				// 显示弹窗
				popupWindow.showAtLocation(MainActivity.this.findViewById(R.id.main_btn_3), Gravity.BOTTOM, 0, 0);

				break;
			case R.id.pop_map0:
				if (mBaiduMap.isBaiduHeatMapEnabled() == false) {
					mBaiduMap.setBaiduHeatMapEnabled(true);
					Toast.makeText(MainActivity.this, "开启热力图层", Toast.LENGTH_SHORT).show();
				} else {
					mBaiduMap.setBaiduHeatMapEnabled(false);
					Toast.makeText(MainActivity.this, "关闭热力图层", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(MainActivity.this, "关闭实时交通", Toast.LENGTH_SHORT).show();
				} else {
					mBaiduMap.setTrafficEnabled(true);
					Toast.makeText(MainActivity.this, "开启实时交通", Toast.LENGTH_SHORT).show();
				}
				popupWindow.dismiss();

				break;
			case R.id.pop_map3:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				popupWindow.dismiss();

				break;
			case R.id.pop_map4:
				// 离线地图下载
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
				Dialog exitDialog = new AlertDialog.Builder(main).setTitle("Android温馨提示").setIcon(R.drawable.close)
						.setMessage("是否退出本程序!").setPositiveButton("是", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								finish();
								java.lang.System.exit(0);
							}
						}).setNegativeButton("否", null).create();
				exitDialog.show();
				break;
			}
			Log.e("L", "跳出switch----------------------");

		}

	}

	/**
	 * 标记物监听事件 标记物推拽事件
	 * 
	 * @author LiuDingCheng
	 *
	 */
	class MarkerListener implements OnMarkerClickListener {
		/**
		 * 标记物监听事件
		 */
		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			if (isplanningMKinfo == false) {
				/**
				 * 获取详细信息
				 */
				Log.e("L", "--------------uid----------------" + arg0.getExtraInfo().getString("uid"));
				poidetailsearchoption.poiUid(arg0.getExtraInfo().getString("uid"));
				poisearch.searchPoiDetail(poidetailsearchoption);

			} else if (isplanningMKinfo == true) {

				// 反地理编码检索
				reversegeocodeoption.location(arg0.getPosition());
				geocoder.reverseGeoCode(reversegeocodeoption);

			}
			return false;
		}

	}

	/**
	 * 地图附近检索 设置百度poi检索监听事件
	 */

	OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiResult(PoiResult poiresult) {
			// TODO Auto-generated method stub
			List<PoiInfo> info = poiresult.getAllPoi();// 获取poi信息
			if (poiresult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {

				ToastUtil.CreateToastShow(main, null, "温馨提示", "未找到你想要的结果！");
			} else {
				BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.nmarker);
				for (int j = 0; j < info.size(); j++) {
					Log.e("L", "结果----------------------" + info.get(j).name);
					MapStatusUpdate search_ll = MapStatusUpdateFactory.newLatLng(info.get(0).location);// 定位到搜索位置
					mBaiduMap.animateMapStatus(search_ll);
					// 覆盖物
					Bundle poinfo = new Bundle();
					poinfo.putString("uid", info.get(j).uid);// 详细检索
					((MarkerOptions) overlayoptions).position(info.get(j).location)// 位置
							.icon(icon).zIndex(10)// 设置躲在图层
							.title(info.get(j).name)// 设置标题
							.extraInfo(poinfo)// uid
							.draggable(true);// 可以手势推拽

					// 在地图上添加marker，并显示
					marker = (Marker) mBaiduMap.addOverlay(overlayoptions);

				}

			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poidetailresult) {
			// TODO Auto-generated method stub
			DetailBundle = new Bundle();
			// 添加数据
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

			// 显示数据
			String DatailStr = "名称:" + poidetailresult.getName() + "\n地址:" + poidetailresult.getAddress() + "\n价格:"
					+ poidetailresult.getPrice() + "\n营业时间:" + poidetailresult.getShopHours() + "\n电话:"
					+ poidetailresult.getTelephone() + "\n综合评价:" + poidetailresult.getOverallRating() + "\n标签:"
					+ poidetailresult.getTag();
			final String phone = poidetailresult.getTelephone();// 电话
			Log.e("L", "----------------------详细信息---------------------------" + DatailStr + "\n"
					+ poidetailresult.getDetailUrl());
			Dialog show = new AlertDialog.Builder(main, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setIcon(null)
					.setTitle("数据显示").setMessage(DatailStr).setNegativeButton("取\t\t消", null)
					.setNeutralButton("电话联系", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (phone.isEmpty()) {
								ToastUtil.CreateToastShow(main, null, "联系商家", "没有电话啊啊啊啊啊啊！");
							} else {
								Intent phonecall = new Intent(Intent.ACTION_DIAL);
								Uri call = Uri.parse("tel:" + phone);
								phonecall.setData(call);
								startActivity(phonecall);
							}

						}
					}).setPositiveButton("详细信息", new DialogInterface.OnClickListener() {

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
	 * 路线规划
	 */
	OnGetRoutePlanResultListener planresultlistener = new OnGetRoutePlanResultListener() {
		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult drivingrouteresult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();// 清除图层覆盖物
			if (drivingrouteresult == null || drivingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "温馨提示", "没有找到合适的路线");
			}
			if (drivingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
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

		// 公交乘换路线规划
		@Override
		public void onGetTransitRouteResult(TransitRouteResult transitrouteresult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();// 清除图层覆盖物
			if (transitrouteresult == null || transitrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "温馨提示", "没有找到合适的路线");
			}
			if (transitrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (transitrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
				TransitOverlayUtil transitoverlayutil = new TransitOverlayUtil(mBaiduMap, main);
				transitoverlayutil.setData(transitrouteresult.getRouteLines().get(0));// 获取第一条结果
				transitoverlayutil.addToMap();
				transitoverlayutil.zoomToSpan();
			}

		}

		// 驾车乘换路线规划
		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult walkingrouteresult) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();// 清除图层覆盖物
			if (walkingrouteresult == null || walkingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "温馨提示", "没有找到合适的路线");
			}
			if (walkingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (walkingrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
				WalkingOverlayUtil walkingoverlayutil = new WalkingOverlayUtil(mBaiduMap, main);
				walkingoverlayutil.setData(walkingrouteresult.getRouteLines().get(0));// 获取第一条结果
				walkingoverlayutil.addToMap();// 范围内可见全部
				walkingoverlayutil.zoomToSpan();
			}

		}

	};
	/**
	 * 地理编码
	 */
	OnGetGeoCoderResultListener GeoCodeResultListener = new OnGetGeoCoderResultListener() {

		// 放地理编码结果
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reversegeocoderesult) {
			// TODO Auto-generated method stub
			if (reversegeocoderesult == null || reversegeocoderesult.error != SearchResult.ERRORNO.NO_ERROR) {
				ToastUtil.CreateToastShow(main, null, "错误提示！", "无法获取详细地址信息！");
				return;

			}
			CharSequence address = reversegeocoderesult.getAddress();
			if (isplanningMKinfo == true) {
				ToastUtil.CreateToastShow(main, null, "这里的位置",
						address + "\n精度:" + reversegeocoderesult.getLocation().latitude + "\n维度:"
								+ reversegeocoderesult.getLocation().longitude);

			} else if (isplanningMKinfo == false) {
				route_star.setText(address);

			}

		}

		// 地理编码结果
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geocoderesult) {
			// TODO Auto-generated method stub

		}
	};

	// 点击返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DoubleClickExit();
			return false;

		}
		return super.onKeyDown(keyCode, event);
	}

	// 双击退出APP程序事件
	public void DoubleClickExit() {

		Timer toExit = null;
		if (isExit == false) {
			isExit = true;// 准备退出
			ToastUtil.CreateToastShow(main, null, "系统提示！", "在点一次返回键，离开本程序！");
			toExit = new Timer();
			toExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2 * 1000);// 2s内没有按下返回建，则启动定时器取消执行任务
		} else {
			finish();// 结束当前
			java.lang.System.exit(0);// 清除虚拟机任务,更加彻底
		}

	}

	public boolean onTouchEvent(MotionEvent event) {

		return false;

	};

	/**
	 * 自定义定位类实现百度定位监听事件
	 */
	public class LDCLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();// 半径
			// 设置定位数据 经纬度
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);
			mBaiduMap.setMyLocationConfigeration(config);
			// 第一次定位时，将地图位置移动到当前位置
			if (isFristLocation) {
				isFristLocation = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());// 地图定位
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
	 * 初始化方向传感器
	 */
	private void init_LDC_Oritation() {
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new liudingcheng.maps.MyOrientationListener.OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;

						// 构造定位数据
						MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.direction(mXDirection).latitude(mCurrentLantitude).longitude(mCurrentLongitude)
								.build();
						// 设置定位数据
						mBaiduMap.setMyLocationData(locData);
						// 设置自定义图标 null 使用默认图标
						// BitmapDescriptor mCurrentMarker =
						// BitmapDescriptorFactory.fromResource(R.drawable.ldc_map_gps);
						MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);
						mBaiduMap.setMyLocationConfigeration(config);

					}
				});
	}

	/**
	 * 初始化定位相关代码
	 */
	private void init_LDC_Location() {
		// 定位初始化
		locationClient = new LocationClient(main);// 新建一个定位客户端
		locationListener = new LDCLocationListener();// 定位
		locationClient.registerLocationListener(locationListener);// 设置定位的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setAddrType("all");
		option.setScanSpan(2000); // 设置定位间隔时间
		locationClient.setLocOption(option);
	}

	/**
	 * 地图移动到我的位置,此处可以重新发定位请求，然后定位； 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
	 */
	private void LDCmyLoc() {

		LatLng ms1 = new LatLng(mCurrentLantitude, mCurrentLongitude);// 定位
		MapStatus ms2 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();// 显示平面
		MapStatusUpdate msu1 = MapStatusUpdateFactory.newLatLng(ms1);
		MapStatusUpdate msu2 = MapStatusUpdateFactory.newMapStatus(ms2);
		mBaiduMap.animateMapStatus(msu1);
		mBaiduMap.animateMapStatus(msu2);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16.0f));// 设置比例尺

	}

	/**
	 * 转换3维图层
	 */
	private void LDCmy3D() {

		// 显示3维
		MapStatus ms1 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(-45).build();// 3D
		MapStatusUpdate msu1 = MapStatusUpdateFactory.newMapStatus(ms1);
		MapStatus ms2 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();// 平面
		MapStatusUpdate msu2 = MapStatusUpdateFactory.newMapStatus(ms2);
		Log.e("L", "3D-------------" + is3D);
		if ((mBaiduMap.getMapStatus().overlook) <= -19) {
			mBaiduMap.animateMapStatus(msu2);// 平面
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(20.0f));// 设置比例尺
		}
		if ((mBaiduMap.getMapStatus().overlook) == 0) {
			mBaiduMap.animateMapStatus(msu1);// 3D
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));// 设置比例尺
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!locationClient.isStarted()) {
			locationClient.start();
		}
		// 开启方向传感器
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
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);// 关闭定位
		locationClient.stop();// 结束定位请求
		// 关闭方向传感器
		myOrientationListener.stop();
		super.onStop();
	}

	// 生命周期和mapview的生命周期
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		poisearch.destroy();// 销毁实例
		routeplansearch.destroy();// 销毁实例
		geocoder.destroy();// 销毁实例
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}