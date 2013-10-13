package edu.bistu.hich.autoservice.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

public class MapUtil {

	public static void setZoomLevel(AMap aMap, LatLng location, float level) {
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, level));
	}

	/**
	 * AmapLocation to LatLng
	 * @param location
	 * @return
	 */
	public static LatLng locToLatLng(AMapLocation location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}
	
	/**
	 * AmapLocation to LatLonPoint
	 * @param location
	 * @return
	 */
	public static LatLonPoint loc2LatLonPoint(AMapLocation location){
		return new LatLonPoint(location.getLatitude(), location.getLongitude());
	}
	
	/**
	 * LatLng to LatLonPoint
	 * @param location
	 * @return
	 */
	public static LatLonPoint latLng2LatLonPoint(LatLng location){
		return new LatLonPoint(location.latitude, location.longitude);
	}
	
	/**
	 * LatLonPoint to LatLng
	 * @param location
	 * @return
	 */
	public static LatLng latLonPoint2LatLng(LatLonPoint location){
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

	public static void drawCircle(Circle circle, LatLng center, int radius) {
		if (circle == null) {
			return;
		}
		int fillColor = Color.argb(50, Color.red(Color.BLUE), Color.green(Color.BLUE), Color.blue(Color.BLUE));
		circle.setCenter(center);
		circle.setRadius(radius);
		circle.setFillColor(fillColor);
		circle.setStrokeColor(Color.BLUE);
		circle.setStrokeWidth(2);
	}
	
	public static void navi(Activity activity, String spotPguid, double spot_lat, double spot_lon){
		try {
			Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://navi?sourceApplication=autoservice&poiid=" + spotPguid + "&lat=" + spot_lat + "&lon=" + spot_lon + "&dev=1&style=2"));
			intent.setPackage("com.autonavi.minimap");
			activity.startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Util.longToast(activity, "导航功能需要您首先安装高德地图！");
		}
	}
}
