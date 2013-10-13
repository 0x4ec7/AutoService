package edu.bistu.hich.autoservice.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import edu.bistu.hich.autoservice.R;
import edu.bistu.hich.autoservice.entity.Spot;
import edu.bistu.hich.autoservice.services.SpotDbService;
import edu.bistu.hich.autoservice.utils.MapUtil;
import edu.bistu.hich.autoservice.utils.Util;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CollectionActivity extends SlidingFragmentActivity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {
	private List<Spot> spots = null;
	private Spot currentSpot = null;
	private PopupWindow popupWindow;
	private ListView list;
	private int position;
	ArrayList<HashMap<String, String>> data = null;
	SimpleAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		getSupportActionBar().setTitle("收藏管理");
		getSupportActionBar().setLogo(R.drawable.image_actionbar_home_button_back);
		getSupportActionBar().setHomeButtonEnabled(true);

		setContentView(R.layout.activity_collection);
		setBehindContentView(R.layout.menu_container);

		list = (ListView) findViewById(R.id.list_collection);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		SpotDbService service = new SpotDbService(this);
		spots = service.getScrollData();

		if (spots.size() == 0) {
			Util.shortToast(this, "您还没有收藏任何地点！");
		} else {
			data = new ArrayList<HashMap<String, String>>();
			for (Spot spot : spots) {
				final HashMap<String, String> map = new HashMap<String, String>();
				map.put("spot_name", spot.getSpotName());
				if (spot.getSpotAddr().equals("")) {
					map.put("spot_addr", "该地点 暂无地址信息！");
				} else {
					map.put("spot_addr", spot.getSpotAddr());
				}
				if (spot.getSpotAddr().equals("")) {
					map.put("spot_addr", "该地点 暂无地址信息！");
				} else {
					map.put("spot_addr", spot.getSpotAddr());
				}
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				map.put("add_time", format.format(spot.getAddTime()));
				data.add(map);

				adapter = new SimpleAdapter(CollectionActivity.this, data, R.layout.layout_collection_list, new String[] { "spot_name", "add_time", "spot_addr" }, new int[] { R.id.spot_name,
						R.id.add_time, R.id.spot_addr });
				list.setAdapter(adapter);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.list_collection) {
			LayoutInflater inflater = LayoutInflater.from(this);
			final WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = 0.5f;
			// lp.dimAmount = 0.5f;
			getWindow().setAttributes(lp);
			View view = inflater.inflate(R.layout.popupwindow_collection, null);
			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popupWindow.setFocusable(true);
			popupWindow.setBackgroundDrawable(new PaintDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.activity_collection_content), Gravity.CENTER, 0, 0);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					lp.alpha = 1.0f;
					getWindow().setAttributes(lp);
				}
			});
			popupWindow.update();

			TextView textName = (TextView) view.findViewById(R.id.collection_text_name);
			TextView textAddr = (TextView) view.findViewById(R.id.collection_text_addr);
			TextView textPhone = (TextView) view.findViewById(R.id.collection_text_phone);
			Button deleteButton = (Button) view.findViewById(R.id.collection_button_delete);
			Button contactButton = (Button) view.findViewById(R.id.collection_button_contact);
			Button routeButton = (Button) view.findViewById(R.id.collection_button_route);
			deleteButton.setOnClickListener(this);
			contactButton.setOnClickListener(this);
			routeButton.setOnClickListener(this);

			currentSpot = spots.get(arg2);
			position = arg2;
			String nameText = currentSpot.getSpotName();
			for (int i = nameText.length(); i <= 15; i++) {
				nameText += "  ";
			}
			textName.setText(nameText);

			if (!currentSpot.getSpotAddr().equals("")) {
				textAddr.setText(currentSpot.getSpotAddr());
			} else {
				textAddr.setText(getString(R.string.no_address));
			}

			if (!currentSpot.getSpotPhone().equals("")) {
				textPhone.setText(currentSpot.getSpotPhone());
			} else {
				textPhone.setText(getString(R.string.no_phone));
			}

		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.collection_button_delete) {
			SpotDbService service = new SpotDbService(this);
			if (currentSpot != null) {
				System.out.println(currentSpot.getSpotPguid());
				System.out.println(position);
				if (service.deleteSpot(currentSpot.getSpotPguid())) {
					Util.shortToast(this, "删除成功");
					data.remove(position);
					spots.remove(position);
					adapter.notifyDataSetChanged();
					popupWindow.dismiss();
					// list.invalidate();
				} else {
					Util.shortToast(this, "删除失败");
				}
			}
		} else if (v.getId() == R.id.collection_button_contact) {
			String phone = currentSpot.getSpotPhone();
			if (phone.equals("")) {
				Util.shortToast(this, getString(R.string.no_phone_toast));
				return;
			}
			final String[] phones = phone.split(";");

			new AlertDialog.Builder(this).setTitle("选择电话：").setItems(phones, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phones[which]));
					startActivity(intent);
				}
			}).setNegativeButton("取消", null).show();
		} else if (v.getId() == R.id.collection_button_route) {
			popupWindow.dismiss();
			MapUtil.navi(this, currentSpot.getSpotPguid(), currentSpot.getSpotLat(), currentSpot.getSpotLat());
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.list_collection) {
			currentSpot = spots.get(arg2);
			position = arg2;
			new AlertDialog.Builder(this).setMessage("是否删除“" + currentSpot.getSpotName() + "”？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					SpotDbService service = new SpotDbService(CollectionActivity.this);
					if (currentSpot != null) {
						if (service.deleteSpot(currentSpot.getSpotPguid())) {
							Util.shortToast(CollectionActivity.this, "删除成功");
							data.remove(position);
							spots.remove(position);
							adapter.notifyDataSetChanged();
						} else {
							Util.shortToast(CollectionActivity.this, "删除失败");
						}
					}
				}
			}).setNegativeButton("取消", null).show();
		}
		return false;
	}
}
