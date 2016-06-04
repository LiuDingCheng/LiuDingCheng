package ToolUtil;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLngBounds;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author LiuDingCheng
 *
 */
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

	BaiduMap mBaiduMap = null;
	private List<OverlayOptions> mOverlayOptionList = null;
	private Context ctx;

	List<Overlay> mOverlayList = null;

	/**
	 * 通过一个BaiduMap 对象构造
	 * 
	 * @param baiduMap
	 */
	public OverlayManager(BaiduMap baiduMap, Context context) {
		mBaiduMap = baiduMap;
		ctx = context;// 上下文
		// mBaiduMap.setOnMarkerClickListener(this);
		if (mOverlayOptionList == null) {
			mOverlayOptionList = new ArrayList<OverlayOptions>();
		}
		if (mOverlayList == null) {
			mOverlayList = new ArrayList<Overlay>();
		}
	}

	/**
	 * 覆写此方法设置要管理的Overlay列表
	 * 
	 * @return 管理的Overlay列表
	 */
	public abstract List<OverlayOptions> getOverlayOptions();

	/**
	 * 将所有Overlay 添加到地图上
	 */
	public final void addToMap() {
		if (mBaiduMap == null) {
			return;
		}

		removeFromMap();
		List<OverlayOptions> overlayOptions = getOverlayOptions();
		if (overlayOptions != null) {
			mOverlayOptionList.addAll(getOverlayOptions());
		}

		for (OverlayOptions option : mOverlayOptionList) {
			mOverlayList.add(mBaiduMap.addOverlay(option));
		}
	}

	/**
	 * 将所有Overlay 从 地图上消除
	 */
	public final void removeFromMap() {
		if (mBaiduMap == null) {
			return;
		}
		for (Overlay marker : mOverlayList) {
			marker.remove();
		}
		mOverlayOptionList.clear();
		mOverlayList.clear();

	}

	/**
	 * 缩放地图，使所有Overlay都在合适的视野内 注： 该方法只对Marker类型的overlay有效
	 * 
	 * 
	 */
	public void zoomToSpan() {
		if (mBaiduMap == null) {
			return;
		}
		if (mOverlayList.size() > 0) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();//范围
			for (Overlay overlay : mOverlayList) {
				// polyline 中的点可能太多，只按marker 缩放
				if (overlay instanceof Marker) {
					builder.include(((Marker) overlay).getPosition());
				}
			}
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
		}
	}

}
