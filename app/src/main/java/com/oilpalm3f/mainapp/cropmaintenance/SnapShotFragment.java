package com.oilpalm3f.mainapp.cropmaintenance;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.Farmer;
import com.oilpalm3f.mainapp.prospectiveFarmers.ProspectivePlotsAdapter;
import com.oilpalm3f.mainapp.prospectiveFarmers.ProspectivePlotsModel;
import com.oilpalm3f.mainapp.ui.BaseFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SnapShotFragment extends Fragment {

    private TextView lastVisitedDate_text, prospectivePlots_text;
    private View rootView;
    private RecyclerView rvplotlist;
    private Farmer selectedFarmer;
    private DataAccessHandler dataAccessHandler = null;
    private List<ProspectivePlotsModel> plotDetailsObjArrayList = new ArrayList<>();
    private com.oilpalm3f.mainapp.prospectiveFarmers.ProspectivePlotsAdapter prospectivePlotsAdapter;

    private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private  DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private ActionBar actionBar;

    public SnapShotFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_snap_shot, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getActivity().getResources().getString(R.string.snap_shot_1));

        rvplotlist = (RecyclerView) rootView.findViewById(R.id.lv_farmerplotdetails);
        initViews();

        selectedFarmer = (Farmer) DataManager.getInstance().getDataFromManager(DataManager.FARMER_PERSONAL_DETAILS);

        bindData();

        return rootView;
    }


    private void initViews() {
        lastVisitedDate_text = (TextView) rootView.findViewById(R.id.lastVisitedDate_text);
        prospectivePlots_text = (TextView) rootView.findViewById(R.id.prospectivePlots_text);
    }

    private void bindData() {
        dataAccessHandler = new DataAccessHandler(getActivity());
        plotDetailsObjArrayList = dataAccessHandler.getProspectivePlotDetails(selectedFarmer.getCode(), 81);
        if (plotDetailsObjArrayList != null && plotDetailsObjArrayList.size() > 0) {
            prospectivePlotsAdapter = new ProspectivePlotsAdapter(getActivity(), plotDetailsObjArrayList);
            rvplotlist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvplotlist.setAdapter(prospectivePlotsAdapter);
        }

        String lastVisitDate = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().queryCropLastVisitDate());



        if (!TextUtils.isEmpty(lastVisitDate)) {

            lastVisitDate = lastVisitDate.replace("T"," ");
            Date date = null;
            try {
                date = inputFormat.parse(lastVisitDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            String outputDate = outputFormat.format(date);
            lastVisitedDate_text.setText(outputDate);
        } else {
            lastVisitedDate_text.setText(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_2));
        }
    }

}
