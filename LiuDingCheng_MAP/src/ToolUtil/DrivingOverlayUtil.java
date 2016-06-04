package ToolUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author LiuDingCheng
 *
 */
public class DrivingOverlayUtil extends OverlayManager {

	private DrivingRouteLine mRouteLine = null;
	boolean focus = false;

	/**
	 * ���캯��
	 * 
	 * @param baiduMap
	 *            ��DrivingRouteOvelray���õ� BaiduMap
	 */
	public DrivingOverlayUtil(BaiduMap baiduMap, Context context) {
		super(baiduMap, context);
	}

	@Override
	public final List<OverlayOptions> getOverlayOptions() {
		if (mRouteLine == null) {
			return null;
		}

		List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();
		// step node
		if (mRouteLine.getAllStep() != null && mRouteLine.getAllStep().size() > 0) {

			for (DrivingRouteLine.DrivingStep step : mRouteLine.getAllStep()) {
				Bundle b = new Bundle();
				b.putInt("index", mRouteLine.getAllStep().indexOf(step));
				if (step.getEntrance() != null) {
					overlayOptionses.add((new MarkerOptions()).position(step.getEntrance().getLocation())
							.anchor(0.5f, 0.5f).zIndex(10).rotate((360 - step.getDirection())).extraInfo(b)
							.icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_line_node.png")));
				}
				// ���·�λ��Ƴ��ڵ�
				if (mRouteLine.getAllStep().indexOf(step) == (mRouteLine.getAllStep().size() - 1)
						&& step.getExit() != null) {
					overlayOptionses.add((new MarkerOptions()).position(step.getExit().getLocation()).anchor(0.5f, 0.5f)
							.zIndex(10).icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_line_node.png")));

				}
			}
		}

		if (mRouteLine.getStarting() != null) {
			overlayOptionses
					.add((new MarkerOptions())
							.position(mRouteLine.getStarting().getLocation()).icon(getStartMarker() != null
									? getStartMarker() : BitmapDescriptorFactory.fromAssetWithDpi("Icon_start.png"))
							.zIndex(10));
		}
		if (mRouteLine.getTerminal() != null) {
			overlayOptionses.add((new MarkerOptions())
					.position(mRouteLine.getTerminal().getLocation()).icon(getTerminalMarker() != null
							? getTerminalMarker() : BitmapDescriptorFactory.fromAssetWithDpi("Icon_end.png"))
					.zIndex(10));
		}
		// poly line
		if (mRouteLine.getAllStep() != null && mRouteLine.getAllStep().size() > 0) {

			List<DrivingStep> steps = mRouteLine.getAllStep();
			int stepNum = steps.size();

			List<LatLng> points = new ArrayList<LatLng>();
			ArrayList<Integer> traffics = new ArrayList<Integer>();
			int totalTraffic = 0;
			for (int i = 0; i < stepNum; i++) {
				if (i == stepNum - 1) {
					points.addAll(steps.get(i).getWayPoints());
				} else {
					points.addAll(steps.get(i).getWayPoints().subList(0, steps.get(i).getWayPoints().size() - 1));
				}

				totalTraffic += steps.get(i).getWayPoints().size() - 1;
				if (steps.get(i).getTrafficList() != null && steps.get(i).getTrafficList().length > 0) {
					for (int j = 0; j < steps.get(i).getTrafficList().length; j++) {
						traffics.add(steps.get(i).getTrafficList()[j]);
					}
				}
			}

			// Bundle indexList = new Bundle();
			// if (traffics.size() > 0) {
			// int raffic[] = new int[traffics.size()];
			// int index = 0;
			// for (Integer tempTraff : traffics) {
			// raffic[index] = tempTraff.intValue();
			// index++;
			// }
			// indexList.putIntArray("indexs", raffic);
			// }
			boolean isDotLine = false;

			if (traffics != null && traffics.size() > 0) {
				isDotLine = true;
			}
			PolylineOptions option = new PolylineOptions().points(points).textureIndex(traffics).width(7)
					.dottedLine(isDotLine).focus(true)
					.color(getLineColor() != 0 ? getLineColor() : Color.argb(178, 0, 78, 255)).zIndex(0);
			
			overlayOptionses.add(option);
		}
		return overlayOptionses;
	}

	/**
	 * ����·������
	 * 
	 * @param routeLine
	 *            ·������
	 */
	public void setData(DrivingRouteLine routeLine) {
		this.mRouteLine = routeLine;
	}

	/**
	 * ��д�˷����Ըı�Ĭ�����ͼ��
	 * 
	 * @return ���ͼ��
	 */
	public BitmapDescriptor getStartMarker() {
		return null;
	}

	/**
	 * ��д�˷����Ըı�Ĭ�ϻ�����ɫ
	 * 
	 * @return ����ɫ
	 */
	public int getLineColor() {
		return 0;
	}

	
	/**
	 * ��д�˷����Ըı�Ĭ���յ�ͼ��
	 * 
	 * @return �յ�ͼ��
	 */
	public BitmapDescriptor getTerminalMarker() {
		return null;
	}

	/**
	 * ��д�˷����Ըı�Ĭ�ϵ������
	 * 
	 * @param i
	 *            ��·�ڵ�� index
	 * @return �Ƿ����˸õ���¼�
	 */
	public boolean onRouteNodeClick(int i) {
		if (mRouteLine.getAllStep() != null && mRouteLine.getAllStep().get(i) != null) {
			Log.i("baidumapsdk", "DrivingRouteOverlay onRouteNodeClick");
		}
		return false;
	}

	@Override
	public final boolean onMarkerClick(Marker marker) {
		for (Overlay mMarker : mOverlayList) {
			if (mMarker instanceof Marker && mMarker.equals(marker)) {
				if (marker.getExtraInfo() != null) {
					onRouteNodeClick(marker.getExtraInfo().getInt("index"));
				}
			}
		}
		return true;
	}

	@Override
	public boolean onPolylineClick(Polyline polyline) {
		boolean flag = false;
		for (Overlay mPolyline : mOverlayList) {
			if (mPolyline instanceof Polyline && mPolyline.equals(polyline)) {
				// ѡ��
				flag = true;
				break;
			}
		}
		setFocus(flag);
		return true;
	}

	public void setFocus(boolean flag) {
		focus = flag;
		for (Overlay mPolyline : mOverlayList) {
			if (mPolyline instanceof Polyline) {
				// ѡ��
				((Polyline) mPolyline).setFocus(flag);

				break;
			}
		}

	}
}
