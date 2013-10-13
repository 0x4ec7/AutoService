package edu.bistu.hich.autoservice.views;

import edu.bistu.hich.autoservice.R;
import edu.bistu.hich.autoservice.services.QueryCriteria;
import edu.bistu.hich.autoservice.services.UserPrefs;
import edu.bistu.hich.autoservice.utils.Global;
import edu.bistu.hich.autoservice.utils.Util;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends Fragment implements OnItemClickListener {
	private QueryCriteria criteria;
	private UserPrefs prefs;

	String[] typeName;
	String[] personal;
	String[] general;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		criteria = QueryCriteria.getInstance();
		prefs = UserPrefs.getInstance(getActivity());
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView listType = (ListView) getView().findViewById(R.id.list_type);
		ListView listPersonal = (ListView) getView().findViewById(R.id.list_personal);
		ListView listGeneral = (ListView)getView().findViewById(R.id.list_general);

		typeName = getResources().getStringArray(R.array.type_name);
		personal = getResources().getStringArray(R.array.personal);
		general = getResources().getStringArray(R.array.general);
		ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_item_list, R.id.list_item_text, typeName);
		ArrayAdapter<String> personalAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_item_list, R.id.list_item_text, personal);
		ArrayAdapter<String> generalAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_item_list, R.id.list_item_text, general);
		
		listType.setOnItemClickListener(this);
		listType.setAdapter(typeAdapter);
		Util.setListViewHeightBasedOnChildren(listType);

		listPersonal.setOnItemClickListener(this);
		listPersonal.setAdapter(personalAdapter);
		Util.setListViewHeightBasedOnChildren(listPersonal);
		
		listGeneral.setOnItemClickListener(this);
		listGeneral.setAdapter(generalAdapter);
		Util.setListViewHeightBasedOnChildren(listGeneral);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		return inflater.inflate(R.layout.menu_content_layout, container, false);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		// TODO Auto-generated method stub

		if (arg0.getId() == R.id.list_type) {
			criteria.setType(position);
			prefs.setType(position);
			if (getActivity() == null) {
				return;
			}
			if (getActivity() instanceof MainActivity) {
				MainActivity activity = (MainActivity) getActivity();
				if (Global.isLocated == false) {
					Util.shortToast(activity, "正在确定您的位置，请稍候再进行搜索！");
					activity.activateLoc();
					return;
				}
				activity.getSupportActionBar().setTitle(typeName[criteria.getType()]);
				activity.doSearch(criteria);
			}
		} else if (arg0.getId() == R.id.list_personal) {
			if (getActivity() == null) {
				return;
			}
			if (getActivity() instanceof MainActivity) {
				MainActivity activity = (MainActivity)getActivity();
				activity.switchActivity();
			}
		} else if (arg0.getId() == R.id.list_general) {
			if (position == 0) {
				if (getActivity() == null) {
					return;
				}
				if (getActivity() instanceof MainActivity) {
					new AlertDialog.Builder(getActivity()).setTitle(R.string.about).setPositiveButton("返回", null).setMessage(Html.fromHtml(getString(R.string.about_msg))).show();
				}
			} else if (position == 1) {
				System.exit(0);
			}
		}
	}
}
