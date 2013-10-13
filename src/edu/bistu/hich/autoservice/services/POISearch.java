package edu.bistu.hich.autoservice.services;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.overlay.PoiOverlay;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;

import edu.bistu.hich.autoservice.R;
import edu.bistu.hich.autoservice.entity.Spot;
import edu.bistu.hich.autoservice.utils.MapUtil;
import edu.bistu.hich.autoservice.utils.Util;
import edu.bistu.hich.autoservice.views.MainActivity;

public class POISearch implements Runnable, OnMarkerClickListener, InfoWindowAdapter, OnItemSelectedListener, OnPoiSearchListener, OnMapClickListener, OnInfoWindowClickListener, OnClickListener {
	// private Context context;
	private MainActivity activity;
	private AMap aMap;
	private AMapLocation location;
	private QueryCriteria criteria;

	private ProgressDialog progDialog = null;
	private PoiSearch.Query query;
	private PoiSearch poiSearch;
	private PoiOverlay poiOverlay;
	private List<PoiItem> poiItems;
	private Marker detailMarker;

	String[] boundStrings;
	int[] boundIntegers;
	String[] typeName;
	String[] typeQuery;

	PoiItem currentItem;

	public POISearch(MainActivity activity, AMap aMap, AMapLocation location, QueryCriteria criteria) {
		this.activity = activity;
		this.aMap = aMap;
		this.location = location;
		this.criteria = criteria;
		boundStrings = activity.getResources().getStringArray(R.array.bound_strings);
		boundIntegers = activity.getResources().getIntArray(R.array.bound_integers);
		typeName = activity.getResources().getStringArray(R.array.type_name);
		typeQuery = activity.getResources().getStringArray(R.array.type_query);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(activity);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage(activity.getString(R.string.searching) + boundStrings[criteria.getBound()] + activity.getString(R.string.within)
				+ activity.getResources().getStringArray(R.array.type_name)[UserPrefs.getInstance(activity).getType()]);
		progDialog.show();
	}

	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	private void registerListener() {
		aMap.setOnMapClickListener(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);
	}

	public void doSearchQuery() {
		showProgressDialog();
		registerListener();
		query = new PoiSearch.Query(criteria.getKeyword(), typeName[criteria.getType()], "");
		query.setPageNum(0);
		query.setPageSize(30);

		poiSearch = new PoiSearch(activity, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.setBound(new SearchBound(MapUtil.loc2LatLonPoint(location), boundIntegers[criteria.getBound()]));
		poiSearch.searchPOIAsyn();
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		System.out.println("onPoiSearched");
		
		// TODO Auto-generated method stub
		dissmissProgressDialog();
		if (rCode == 0) {
			aMap.clear();
			
			//my location
			MyLocationStyle myLocationStyle = new MyLocationStyle();
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
			myLocationStyle.strokeColor(Color.BLUE);
			myLocationStyle.strokeWidth(1);
			int fillColor = Color.argb(50, Color.red(Color.BLUE), Color.green(Color.BLUE), Color.blue(Color.BLUE));
			myLocationStyle.radiusFillColor(fillColor);
			aMap.setMyLocationStyle(myLocationStyle);
			aMap.setLocationSource(activity);
			
			if (result != null) {
				if (result.getPageCount() > 1) {
					Util.shortToast(activity, activity.getString(R.string.outnumber));
				}

				poiItems = result.getPois();
				
				if (poiItems != null && poiItems.size() > 0) {
					poiOverlay = new PoiOverlay(aMap, poiItems);
					poiOverlay.removeFromMap();
					poiOverlay.addToMap();
					poiOverlay.zoomToSpan();
				} else {
					Util.shortToast(activity, activity.getString(R.string.no_result));
				}
			} else {
				Util.shortToast(activity, activity.getString(R.string.no_result));
			}
		} else {
			Util.shortToast(activity, activity.getString(R.string.network_error));
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		System.out.println("onMarkerClick");

		if (poiOverlay != null && poiItems != null && poiItems.size() > 0) {
			detailMarker = marker;
			doSearchPoiDetail(poiItems.get(poiOverlay.getPoiIndex(marker)).getPoiId());
		}
		return false;
	}

	public void onInfoWindowClick(Marker marker) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.popupwindow_poidetail, null);
		PopupWindow popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new PaintDrawable());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(activity.findViewById(R.id.activity_fragment_content), Gravity.CENTER | Gravity.BOTTOM, 0, 0);
		popupWindow.update();

		TextView textName = (TextView) view.findViewById(R.id.text_name);
		TextView textAddr = (TextView) view.findViewById(R.id.text_addr);
		TextView textPhone = (TextView) view.findViewById(R.id.text_phone);
		Button collectButton = (Button) view.findViewById(R.id.button_collect);
		Button contactButton = (Button) view.findViewById(R.id.button_contact);
		Button routeButton = (Button) view.findViewById(R.id.button_route);

		currentItem = poiItems.get(poiOverlay.getPoiIndex(marker));

		ButtonClickListener listener = new ButtonClickListener();
		collectButton.setOnClickListener(listener);
		contactButton.setOnClickListener(listener);
		routeButton.setOnClickListener(listener);

		textName.setText(currentItem.getTitle());
		if (!currentItem.getSnippet().equals("")) {
			textAddr.setText(currentItem.getSnippet());
		} else {
			textAddr.setText(activity.getString(R.string.no_address));
		}

		if (!currentItem.getTel().equals("")) {
			textPhone.setText(currentItem.getTel());
		} else {
			textPhone.setText(activity.getString(R.string.no_phone));
		}

	}

	public void doSearchPoiDetail(String poiId) {
		System.out.println("doSearchPoiDetail");
		if (poiSearch != null && poiId != null) {
			poiSearch.searchPOIDetailAsyn(poiId);
		}
	}

	public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {
		System.out.println("onPoiItemDetailSearched");
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null) {
				if (detailMarker != null) {
					StringBuffer sb = new StringBuffer("");
					if (!result.getSnippet().equals("")) {
						sb.append(activity.getString(R.string.address) + result.getSnippet());
					}
					if (!result.getTel().equals("")) {
						sb.append("\n" + activity.getString(R.string.phone_number) + result.getTel());
					}
					detailMarker.setSnippet(sb.toString());
				}
				detailMarker.showInfoWindow();
			} else {
				Util.shortToast(activity, activity.getString(R.string.no_result));
			}
		} else {
			Util.shortToast(activity, activity.getString(R.string.network_error));
		}
	}

	final class ButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.button_collect) {
				SpotDbService service = new SpotDbService(activity);
				Spot spot = new Spot(-1, currentItem.getPoiId(), currentItem.getTitle(), currentItem.getSnippet(), currentItem.getLatLonPoint().getLatitude(), currentItem.getLatLonPoint().getLongitude(), currentItem.getTel(), null);
				
				if (service.isAdded(currentItem.getPoiId())) {
					Util.shortToast(activity, "您已经收藏过了该地点！");
				} else {
					if(service.insertSpot(spot)){
						Util.shortToast(activity, "收藏成功！");
					} else {
						Util.shortToast(activity, "收藏失败！");
					}
				}
			} else if (v.getId() == R.id.button_contact) {
				if (currentItem.getTel().equals("")) {
					Util.shortToast(activity, activity.getString(R.string.no_phone_toast));
				} else {
					String phone = currentItem.getTel();
					final String[] phones = phone.split(";");

					new AlertDialog.Builder(activity).setTitle("选择电话：").setItems(phones, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phones[which]));
							activity.startActivity(intent);
						}
					}).setNegativeButton("取消", null).show();
				}
			} else if (v.getId() == R.id.button_route) {
				MapUtil.navi(activity, currentItem.getPoiId(), currentItem.getLatLonPoint().getLatitude(), currentItem.getLatLonPoint().getLongitude());
			}
		}
	}

	public void onMapClick(LatLng latng) {

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
