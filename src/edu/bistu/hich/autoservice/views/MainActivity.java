package edu.bistu.hich.autoservice.views;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.bistu.hich.autoservice.services.POISearch;
import edu.bistu.hich.autoservice.services.QueryCriteria;
import edu.bistu.hich.autoservice.services.UserPrefs;
import edu.bistu.hich.autoservice.utils.Constant;

import edu.bistu.hich.autoservice.R;
import edu.bistu.hich.autoservice.utils.Global;
import edu.bistu.hich.autoservice.utils.Util;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

public class MainActivity extends BaseActivity {
	// private static final String DEBUG = "FragmentContentActivity";
	private Fragment content;
	private SlidingMenu menu;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private POISearch poiSearch;
	private AMapLocation location;

	private MenuItem mi;
	UserPrefs prefs;
	String[] boundStrings;
	int[] boundIntegers;
	int boundIndex;
	int typeIndex;
	String[] typeName;
	String[] typeQuery;

	QueryCriteria criteria = QueryCriteria.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			content = getSupportFragmentManager().getFragment(savedInstanceState, "content");
		}

		boundStrings = getResources().getStringArray(R.array.bound_strings);
		boundIntegers = getResources().getIntArray(R.array.bound_integers);
		typeName = getResources().getStringArray(R.array.type_name);
		typeQuery = getResources().getStringArray(R.array.type_query);

		// init query criteria
		prefs = UserPrefs.getInstance(this);
		boundIndex = prefs.getBound();
		typeIndex = prefs.getType();

		criteria.setType(typeIndex);
		criteria.setBound(boundIndex);

		// enable action bar home button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(typeName[typeIndex]);
		getSupportActionBar().setLogo(R.drawable.image_actionbar_home_button);

		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.menu_container);

		initSlidingMenu();
		// initActionbar();

		Util.longToast(this, getString(R.string.locating));
		initMap();
	}

	private void initSlidingMenu() {
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_container, new MenuFragment()).commit();
		menu = getSlidingMenu();
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidth(0);

		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);

		menu.setBehindOffset((int) (display.widthPixels * 0.382));
		menu.setFadeDegree(0.35f);
	}

	private void initActionbar() {
		// add tabs
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab mapTab = getSupportActionBar().newTab();
		ActionBar.Tab listTab = getSupportActionBar().newTab();
		mapTab.setText(getString(R.string.map));
		listTab.setText(getString(R.string.list));
		mapTab.setTabListener(this);
		listTab.setTabListener(this);
		getSupportActionBar().addTab(mapTab);
		getSupportActionBar().addTab(listTab);
	}

	/*
	 * @Override protected void onSaveInstanceState(Bundle outState) { // TODO
	 * Auto-generated method stub super.onSaveInstanceState(outState); //
	 * getSupportFragmentManager().putFragment(outState, "content", // content);
	 * }
	 */

	private void initMap() {
		if (aMap == null) {
			aMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		setUpMap();
	}

	private void setUpMap() {
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
		myLocationStyle.strokeColor(Color.BLUE);
		myLocationStyle.strokeWidth(1);
		int fillColor = Color.argb(50, Color.red(Color.BLUE), Color.green(Color.BLUE), Color.blue(Color.BLUE));
		myLocationStyle.radiusFillColor(fillColor);
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);
		aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.setMyLocationEnabled(true);

		// circle = aMap.addCircle(new CircleOptions());
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		location = aLocation;
		if (mListener != null) {
			mListener.onLocationChanged(aLocation);

			if (!Global.isLocated) {
				Util.shortToast(this, getString(R.string.locating_success));
				Global.isLocated = true;

				doSearch(criteria);
			}
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		activateLoc();
	}

	@Override
	public void deactivate() {
		// mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	// activate locating
	public void activateLoc() {
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
		}
	}

	// deactivate locating
	public void deactivateLoc() {
		deactivate();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		activateLoc();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		deactivateLoc();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		SubMenu subMenu = menu.addSubMenu(boundStrings[boundIndex]);
		subMenu.add(0, Constant.MY_ITEM_ID_BASE + 0, 0, boundStrings[0]);
		subMenu.add(0, Constant.MY_ITEM_ID_BASE + 1, 0, boundStrings[1]);
		subMenu.add(0, Constant.MY_ITEM_ID_BASE + 2, 0, boundStrings[2]);
		subMenu.add(0, Constant.MY_ITEM_ID_BASE + 3, 0, boundStrings[3]);
		subMenu.add(0, Constant.MY_ITEM_ID_BASE + 4, 0, boundStrings[4]);

		MenuItem distanceItem = subMenu.getItem();
		distanceItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() != android.R.id.home) {
			if (Global.isLocated == false) {
				Util.shortToast(this, "正在确定您的位置，请稍候再进行搜索！");
				activateLoc();
				return false;
			}
		}
		switch (item.getItemId()) {
		case android.R.id.home:
			if (!menu.isMenuShowing()) {
				menu.showMenu();
			}
			break;
		case 0:
			this.mi = item;
			break;
		case Constant.MY_ITEM_ID_BASE:
			this.mi.setTitle(boundStrings[0]);
			prefs.savePrefs(prefs.getType(), 0);
			criteria.setBound(0);
			doSearch(criteria);
			break;
		case Constant.MY_ITEM_ID_BASE + 1:
			this.mi.setTitle(boundStrings[1]);
			prefs.savePrefs(prefs.getType(), 1);
			criteria.setBound(1);
			doSearch(criteria);
			break;
		case Constant.MY_ITEM_ID_BASE + 2:
			this.mi.setTitle(boundStrings[2]);
			prefs.savePrefs(prefs.getType(), 2);
			criteria.setBound(2);
			doSearch(criteria);
			break;
		case Constant.MY_ITEM_ID_BASE + 3:
			this.mi.setTitle(boundStrings[3]);
			prefs.savePrefs(prefs.getType(), 3);
			criteria.setBound(3);
			doSearch(criteria);
			break;
		case Constant.MY_ITEM_ID_BASE + 4:
			this.mi.setTitle(boundStrings[4]);
			prefs.savePrefs(prefs.getType(), 4);
			criteria.setBound(4);
			doSearch(criteria);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!menu.isMenuShowing()) {
				menu.toggle();
				deactivateLoc();
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (menu.isMenuShowing()) {
				
			} else {
				if (System.currentTimeMillis() - exitTime > 2000) {
					Util.shortToast(this, getString(R.string.exit));
					exitTime = System.currentTimeMillis();
				} else {
					finish();
					System.exit(0);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void doSearch(QueryCriteria criteria) {
		if (menu.isMenuShowing()) {
			menu.toggle();
		}

		if (poiSearch == null) {
			poiSearch = new POISearch(this, aMap, location, criteria);
		}
		poiSearch.doSearchQuery();
	}

	public void switchActivity() {
		Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
		startActivity(intent);
		if (menu.isMenuShowing()) {
			menu.toggle();
		}
	}
}
