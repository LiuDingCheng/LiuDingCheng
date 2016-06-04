package digitalking_for_ldc.singlemap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
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
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import java.util.ArrayList;
import java.util.List;

import digitalking_for_ldc.singlemap.ToolsUtil.DrivingOverlayUtil;
import digitalking_for_ldc.singlemap.ToolsUtil.MyOrientationListener;
import digitalking_for_ldc.singlemap.ToolsUtil.ToastUtil;
import digitalking_for_ldc.singlemap.ToolsUtil.TransitOverlayUtil;
import digitalking_for_ldc.singlemap.ToolsUtil.WalkingOverlayUtil;

/**
 * Created by Administrator on 2016/5/22.
 */
public class NavigationActivity extends Activity {
    private LocationClient N_locationclient;// 定位客户端
    public N_bdlocationlistener N_bdlocationlistener;// 定位监听器
    private MyOrientationListener myOrientationListener;// 方向传感器的监听器
    private double N_lat;// 当前的精度
    private double N_lng;// 当前维度
    private volatile boolean Nisfristlocation = true;// 第一次定位
    private int Nxdirection;// 方向传感器X方向的值
    private float Ncurrentaccracy;// 当前的精度
    private MyLocationConfiguration.LocationMode Ncurrentmode;// 定位模式
    private static Context navigation;
    private TextView searchTv, showresultnumber;
    private EditText searchWord, searchcity;
    private Bundle mbundle;
    private Vibrator vibrator = null;// 手机振动
    private DialogRecognitionListener DRL = null;// 语音监听事件
    private BaiduASRDigitalDialog BAD = null;// 语音对话框
    // 申请的百度语音key
    private final String API_KEY = "4FiPLcmDkdSy8j67DXBEEdSfeZy0aouN";// 地图/语音识别
    private final String SECRET_KEY = "wDBgqOL6adQiV6Dd40BsKLMUePyKtlD0";// 语音识别
    private PoiSearch poisearch = null;// 什么百度poi检索
    private MapView nearby_mapview;// 地图
    private BaiduMap nearby_baidumap;// 百度图层
    // 定位
    private LinearLayout N_loc_btn;// 定位
    private LinearLayout N_getaddr_btn;// 获取位置
    // 添加覆盖物
    private MarkerOptions N_markeroptions;
    private Marker N_marker = null;
    private TextView walktv, bustv, cartv;
    private View N_view;// 视图
    private PopupWindow N_popupwindow = null;// 导航弹窗
    /**
     * 路线
     */
    private RoutePlanSearch N_routeplansearch = null;// 线路规划poi
    private PlanNode N_SN, N_EN;// 起点终点
    private TransitRoutePlanOption N_transitrouteplanoption;// 公交
    private WalkingRoutePlanOption N_walkingrouteplanoption;// 不行
    private DrivingRoutePlanOption N_drivingrouteplanoption;// 驾车
    private RouteLine<?> N_routeline = null;// 路线信息
    // 后退
    private TextView backtv;
    /**
     * 语音导航
     */
    private LinearLayout N_NNaviline;// 打开导航
    private NaviParaOption N_naviparaoption = null;
    private boolean isNavi = true;// 是否开启语音导航
    private TextView N_changeNavi;// 显示导航开关
    private TextView N_nearby_changelook;// 查看详情
    /**
     * 获取地址
     */
    private GeoCoder N_geocoder;// 地理编码
    private ReverseGeoCodeOption N_reversegeocodeoption = null;// 反编码
    private boolean isMyaddress = true;// 我的地址判断
    private LinearLayout N_showinfo_line;// 显示数据
    private TextView N_showdatainfotv;// 显示数据
    private LinearLayout N_showdatail_line;// 显示布局
    private boolean isShowDatail = true;

    /**
     * 地理基本信息
     */
    private String N_currentaddress = null;// 当前的地理位置
    private String N_targetaddress = null;// 目标地理位置
    private LatLng N_currentlatlng = null;// 当前的地理经纬度
    private LatLng N_targetlatlng = null;// 目标地理经纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.nearby_activity);
        initView();
        funcationListener();

    }

    public NavigationActivity() {
        navigation = NavigationActivity.this;
    }

    // 初始化界面
    public void initView() {
        /**
         * 路线规划
         */
        N_routeplansearch = RoutePlanSearch.newInstance();
        N_walkingrouteplanoption = new WalkingRoutePlanOption();
        N_transitrouteplanoption = new TransitRoutePlanOption();
        N_drivingrouteplanoption = new DrivingRoutePlanOption();
        // 初始化编码实例
        N_geocoder = GeoCoder.newInstance();
        // 反编码 初始化实例
        N_reversegeocodeoption = new ReverseGeoCodeOption();
        /**
         * 语音选项初始化
         */
        N_naviparaoption = new NaviParaOption();
        N_NNaviline = (LinearLayout) findViewById(R.id.N_Naviline);
        /**
         * 初始化覆盖物
         */
        N_markeroptions = new MarkerOptions();

        // 获取地图
        nearby_mapview = (MapView) findViewById(R.id.nearby_mapView);
        // 获取图层
        nearby_baidumap = nearby_mapview.getMap();
        nearby_baidumap.setTrafficEnabled(true);// 开启交通视图
        nearby_baidumap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));// 设置mapview比例尺
        // nearby_baidumap.setMyLocationEnabled(true);// 开启定位图层
        // 获取系统服务
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);// 震动
        // 获取资源
        searchTv = (TextView) findViewById(R.id.nearby_search_tv);// 查询
        searchWord = (EditText) findViewById(R.id.nearby_searchword_et);// 关键字
        searchcity = (EditText) findViewById(R.id.nearby_searccity_et);// 城市
        showresultnumber = (TextView) findViewById(R.id.resultnumber_);// 搜索条
        // 显示列表
        backtv = (TextView) findViewById(R.id.back_nearby);

        // 初始化 PoiSearch 对象
        poisearch = PoiSearch.newInstance();// 之后设置监听
        // 获取定位按键
        N_loc_btn = (LinearLayout) findViewById(R.id.N_locline);
        // 获取地址
        N_getaddr_btn = (LinearLayout) findViewById(R.id.N_getaddr);
        /**
         * 获取弹窗
         */
        N_view = NavigationActivity.this.getLayoutInflater().inflate(R.layout.nearby_routeview, null);
        N_popupwindow = new PopupWindow(N_view, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        walktv = (TextView) N_view.findViewById(R.id.nearby_walk);// 步行
        cartv = (TextView) N_view.findViewById(R.id.nearby_car);// 开车
        bustv = (TextView) N_view.findViewById(R.id.nearby_bus);// 公交
        /**
         * 显示详情
         */
        N_showinfo_line = (LinearLayout) findViewById(R.id.nearby_showinfo_line);
        N_showdatainfotv = (TextView) findViewById(R.id.nearby_datainfo_tv);// 显示数据
        N_showdatail_line = (LinearLayout) findViewById(R.id.N_showdatail_line);
        N_changeNavi = (TextView) findViewById(R.id.nearby_changeNavi);// 显示导航开关
        N_nearby_changelook = (TextView) findViewById(R.id.nearby_changelook);// 详情开关

        // 默认不导航
        isNavi = true;
        /**
         * 显示判读啊
         */
        isShowDatail = true;
        // 定位模式
        Ncurrentmode = MyLocationConfiguration.LocationMode.NORMAL;// 默认普通模式
        // 第一次定位
        Nisfristlocation = true;
        // 初始化定位
        N_initLocation();
        // 初始化方向传感器
        N_initOritation();

    }

    /**
     * 设置监听事件
     */
    public void funcationListener() {
        /**
         * 路线规划
         */
        // 设置路线规划监听
        N_routeplansearch.setOnGetRoutePlanResultListener(N_planresultlistener);
        // 设置监听
        walktv.setOnClickListener(new SingleListener());
        cartv.setOnClickListener(new SingleListener());
        bustv.setOnClickListener(new SingleListener());
        nearby_baidumap.setOnMarkerClickListener(new NMarkerListener());// 标记物监听事件
        // 地理编码
        N_geocoder.setOnGetGeoCodeResultListener(N_GeoCodeResultListener);
        poisearch.setOnGetPoiSearchResultListener(poiListener);// 设置检索监听
        backtv.setOnClickListener(new SingleListener());// 返回
        N_loc_btn.setOnClickListener(new SingleListener());// 设置定位监听事件
        N_getaddr_btn.setOnClickListener(new SingleListener());
        /**
         * 显示数据
         */
        N_showdatail_line.setOnClickListener(new SingleListener());
        /**
         * 设置语音导航监听
         */
        N_NNaviline.setOnClickListener(new SingleListener());// 单击监听
        searchTv.setOnClickListener(new SingleListener());// 搜索
        // 长按事件监听
        searchTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(new long[] { 50, 100 }, -1);
                YYDialog();
                return false;
            }
        });

    }

    // 内部类监听事件
    class SingleListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // 起点
            N_SN = PlanNode.withCityNameAndPlaceName(searchcity.getText().toString().trim(), N_currentaddress);
            // 终点
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
                    N_Loc();// 定位事件
                    break;
                case R.id.N_getaddr:
                    // 当前地理位置
                    isMyaddress = true;
                    LatLng pLL = new LatLng(N_lat, N_lng);
                    N_reversegeocodeoption.location(pLL);

                    N_geocoder.reverseGeoCode(N_reversegeocodeoption);
                    break;
                case R.id.nearby_walk:
                    /**
                     * 步行
                     */
                    N_NNaviline.setVisibility(View.VISIBLE);
                    N_walkingrouteplanoption.from(N_SN);// 起点
                    N_walkingrouteplanoption.to(N_EN);// 终点
                    N_routeplansearch.walkingSearch(N_walkingrouteplanoption);
                    N_popupwindow.dismiss();
                    break;
                case R.id.nearby_car:
                    /**
                     * 开车
                     */
                    N_NNaviline.setVisibility(View.VISIBLE);
                    N_drivingrouteplanoption.from(N_SN);// 起点
                    N_drivingrouteplanoption.to(N_EN);// 终点
                    N_routeplansearch.drivingSearch(N_drivingrouteplanoption);
                    N_popupwindow.dismiss();
                    break;
                case R.id.nearby_bus:
                    /**
                     * 公交
                     */
                    N_NNaviline.setVisibility(View.VISIBLE);
                    N_transitrouteplanoption.city(searchcity.getText().toString().trim());// 城市
                    N_transitrouteplanoption.from(N_SN);// 起点
                    N_transitrouteplanoption.to(N_EN);// 终点
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
                    Log.e("L", "isShowDatail 显示----" + isShowDatail);
                    if (isShowDatail == true) {
                        N_showinfo_line.setVisibility(View.VISIBLE);
                        N_nearby_changelook.setText("隐藏详情");
                        isShowDatail = false;
                    } else if (isShowDatail == false) {
                        Log.e("L", "isShowDatail 不显示----" + isShowDatail);
                        N_showinfo_line.setVisibility(View.GONE);
                        N_nearby_changelook.setText("查看详情");
                        isShowDatail = true;
                    }
                    break;
                case R.id.back_nearby:
                    Ncurrentmode = MyLocationConfiguration.LocationMode.NORMAL;// 设置为普通模式
                    finish();
                    break;

                default:

                    break;
            }

        }

    }

    /**
     * 标记物监听事件
     */
    class NMarkerListener implements BaiduMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker arg0) {
            // TODO Auto-generated method stub
            // 获取数据
            N_popupwindow.showAtLocation(findViewById(R.id.nearby_mapView),
                    Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,((int) nearby_mapview.getX()),
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
     * 语音导航 调用百度app 为什么没有开发版呢，百度不给力
     */
    public void TTSRoute(boolean en) {
        Log.e("en", "----------------------------------------" + en);
        if (en) {
            Ncurrentmode = MyLocationConfiguration.LocationMode.COMPASS;

            try {

                N_naviparaoption.startName(N_currentaddress).startPoint(N_currentlatlng).endPoint(N_targetlatlng)
                        .endName(N_targetaddress);
                BaiduMapNavigation.setSupportWebNavi(false);
                // 驾车
                BaiduMapNavigation.openBaiduMapNavi(N_naviparaoption, navigation);// 开车
            } catch (BaiduMapAppNotSupportNaviException e) {
                Toast.makeText(navigation, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
            N_changeNavi.setText("关闭导航");

        } else if (!en) {
            Ncurrentmode = MyLocationConfiguration.LocationMode.NORMAL;
            BaiduMapNavigation.finish(navigation);// 结束导航
            N_changeNavi.setText("开启导航");
        }

    }

    /**
     * 路线规划
     */
    OnGetRoutePlanResultListener N_planresultlistener = new OnGetRoutePlanResultListener() {
        private StringBuffer SB = new StringBuffer();

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingrouteresult) {
            // TODO Auto-generated method stub
            nearby_baidumap.clear();// 清除图层覆盖物
            N_showdatainfotv.setText("");
            SB.delete(0, SB.length());
            if (drivingrouteresult == null || drivingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
                ToastUtil.CreateToastShow(navigation, null, "温馨提示", "没有找到合适的路线");
            }
            if (drivingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (drivingrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
                N_routeline = drivingrouteresult.getRouteLines().get(0);// 第一方案
                SB.append("起点\t\t" + N_currentaddress).append("\t\t\t----\t\t\t").append("终点\t\t" + N_targetaddress)
                        .append("\n\n");
                for (int i = 0; i < N_routeline.getAllStep().size(); i++) {

                    DrivingRouteLine.DrivingStep step = (DrivingRouteLine.DrivingStep) N_routeline.getAllStep().get(i);

                    SB.append(i + "\t在\t:" + step.getEntranceInstructions() + "\t\t\t")
                            .append(i + "\t在\t:" + step.getExitInstructions() + "\t\t\t");
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
            nearby_baidumap.clear();// 清除图层覆盖物
            N_showdatainfotv.setText("");
            SB.delete(0, SB.length());
            if (transitrouteresult == null || transitrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
                ToastUtil.CreateToastShow(navigation, null, "温馨提示", "没有找到合适的路线");
            }
            if (transitrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (transitrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {

                N_routeline = transitrouteresult.getRouteLines().get(0);// 第一方案
                SB.append("起点\t\t" + N_currentaddress).append("\t\t\t----\t\t\t").append("终点\t\t" + N_targetaddress)
                        .append("\n\n");
                for (int i = 0; i < N_routeline.getAllStep().size(); i++) {
                    TransitRouteLine.TransitStep step = (TransitRouteLine.TransitStep) N_routeline.getAllStep().get(i);
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
            nearby_baidumap.clear();// 清除图层覆盖物
            N_showdatainfotv.setText("");
            SB.delete(0, SB.length());
            if (walkingrouteresult == null || walkingrouteresult.error != SearchResult.ERRORNO.NO_ERROR) {
                ToastUtil.CreateToastShow(navigation, null, "温馨提示", "没有找到合适的路线");
            }
            if (walkingrouteresult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (walkingrouteresult.error == SearchResult.ERRORNO.NO_ERROR) {
                N_routeline = walkingrouteresult.getRouteLines().get(0);// 第一方案
                Log.e("L", "显示步行");

                SB.append("起点\t\t" + N_currentaddress).append("\t\t\t----\t\t\t").append("终点\t\t" + N_targetaddress)
                        .append("\n\n");
                for (int i = 0; i < N_routeline.getAllStep().size(); i++) {
                    WalkingRouteLine.WalkingStep step = (WalkingRouteLine.WalkingStep) N_routeline.getAllStep().get(i);
                    SB.append(i + "\t入口\t:" + step.getEntranceInstructions()).append("\t\t\t")
                            .append(i + "\t出口\t:" + step.getExitInstructions()).append("\t\t\t");
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
     * 地理编码
     */
    OnGetGeoCoderResultListener N_GeoCodeResultListener = new OnGetGeoCoderResultListener() {

        // 放地理编码结果
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reversegeocoderesult) {
            // TODO Auto-generated method stub
            if (reversegeocoderesult == null || reversegeocoderesult.error != SearchResult.ERRORNO.NO_ERROR) {
                ToastUtil.CreateToastShow(navigation, null, "错误提示！", "无法获取详细地址信息！");
                return;

            }

            if (Nisfristlocation) {

            }
            if (isMyaddress == true) {
                N_currentaddress = reversegeocoderesult.getAddress();
                N_currentlatlng = reversegeocoderesult.getLocation();
                ToastUtil.CreateToastShow(navigation, null, "我的位置",
                        N_currentaddress + "\n精度:" + N_currentlatlng.latitude + "\n维度:" + N_currentlatlng.longitude);
            } else if (isMyaddress == false) {
                N_targetaddress = reversegeocoderesult.getAddress();
                N_targetlatlng = reversegeocoderesult.getLocation();
                ToastUtil.CreateToastShow(navigation, null, "目的地位置",
                        N_targetaddress + "\n精度:" + N_targetlatlng.latitude + "\n维度:" + N_targetlatlng.longitude);
            }

        }

        // 地理编码结果
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geocoderesult) {
            // TODO Auto-generated method stub

        }
    };

    /**
     * 定位啊
     */
    public void N_Loc() {
        LatLng NLL = new LatLng(N_lat, N_lng);// 获取经纬度
        MapStatusUpdate nll = MapStatusUpdateFactory.newLatLng(NLL);
        nearby_baidumap.animateMapStatus(nll);

    }

    /**
     * 设置百度poi检索监听事件
     */
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        @SuppressWarnings("null")
        @SuppressLint("InflateParams")
        @Override
        public void onGetPoiResult(PoiResult poiresult) {
            // TODO Auto-generated method stub
            List<PoiInfo> infos = new ArrayList<PoiInfo>();
            infos = poiresult.getAllPoi();// 获取poi信息

            if (poiresult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                showresultnumber.setText("共找到 0 条信息");

                ToastUtil.CreateToastShow(navigation, null, "温馨提示", "未找到你想要的结果！");
            } else {
                BitmapDescriptor Nicon = BitmapDescriptorFactory.fromResource(R.drawable.nmarker);
                showresultnumber.setText("共找到" + infos.size() + "条信息");
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
                ToastUtil.CreateToastShow(navigation, null, "错误提示", "没有找到信息信息");
            } else {
                // 检索成功

            }

        }
    };

    /**
     * 初始化方向传感器
     */
    private void N_initOritation() {
        myOrientationListener = new MyOrientationListener(getApplicationContext());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        Nxdirection = (int) x;

                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder().accuracy(Ncurrentaccracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(Nxdirection).latitude(N_lat).longitude(N_lng).build();
                        // 设置定位数据
                        nearby_baidumap.setMyLocationData(locData);
                        // 设置自定义图标
                        MyLocationConfiguration config = new MyLocationConfiguration(Ncurrentmode, true, null);
                        nearby_baidumap.setMyLocationConfigeration(config);

                    }
                });
    }

    /**
     * 自定义定位类实现百度定位监听事件
     */
    public class N_bdlocationlistener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            // map view 销毁后不在处理新接收的位置
            if (location == null || nearby_mapview == null)
                return;
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(Nxdirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            Ncurrentaccracy = location.getRadius();// 半径
            // 设置定位数据 经纬度
            nearby_baidumap.setMyLocationData(locData);
            N_lat = location.getLatitude();// 获取精度
            N_lng = location.getLongitude();// 获取纬度
            MyLocationConfiguration config = new MyLocationConfiguration(Ncurrentmode, true, null);
            nearby_baidumap.setMyLocationConfigeration(config);
            // 第一次定位时，将地图位置移动到当前位置
            if (Nisfristlocation) {// 如果为真
                Nisfristlocation = true;
                // 定位
                LatLng Nll = new LatLng(location.getLatitude(), location.getLongitude());//
                Log.e("L", "附近   ---经纬度" + Nll);
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
     * 初始化定位相关代码
     */
    private void N_initLocation() {
        // 定位初始化 nearby_locationListener
        N_locationclient = new LocationClient(navigation);// 新建一个定位客户端
        N_bdlocationlistener = new N_bdlocationlistener();// 定位监听器
        N_locationclient.registerLocationListener(N_bdlocationlistener);// 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        //gcj02  bd09ll
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setAddrType("all");
        N_locationclient.setLocOption(option);
    }

    // 语音识别对话框
    public void YYDialog() {
        // 设置语音
        mbundle = new Bundle();
        mbundle.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);// 设置Key
        mbundle.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);// 设置Key
        mbundle.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);// 设置语音识别对话框为蓝色高亮主题
        BAD = new BaiduASRDigitalDialog(navigation, mbundle);
        DRL = new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle mResults) {
                // 获取是被字符转
                ArrayList<String> rs = mResults != null ? mResults.getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                    searchWord.setText(rs.get(0));
                }

            }

        };
        BAD.setDialogRecognitionListener(DRL);
        // 设置语音识别模式为输入模式
        BAD.setSpeechMode(BaiduASRDigitalDialog.SPEECH_MODE_INPUT);
        // 禁用语义识别
        BAD.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE, true);
        BAD.show();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        nearby_baidumap.setMyLocationEnabled(true); // 开启图层定位
        if (!N_locationclient.isStarted()) {
            N_locationclient.start();// 开始定位
        }
        // 开启方向传感器
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
        nearby_baidumap.setMyLocationEnabled(false);// 关闭定位
        N_locationclient.stop();// 结束定位请求
        myOrientationListener.stop();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // 释放实例
        N_routeplansearch.destroy();
        nearby_mapview.onDestroy();// 销毁实例
        poisearch.destroy();// 销毁实例
        super.onDestroy();
    }
}
