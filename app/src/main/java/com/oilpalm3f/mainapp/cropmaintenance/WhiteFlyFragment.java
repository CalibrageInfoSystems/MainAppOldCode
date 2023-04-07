package com.oilpalm3f.mainapp.cropmaintenance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.areaextension.UpdateUiListener;
import com.oilpalm3f.mainapp.conversion.PalmDetailsEditListener;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.WhiteFlyAssessment;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class WhiteFlyFragment extends Fragment implements View.OnClickListener, WhiteFieldFragment.onDataSelectedListener, PalmDetailsEditListener, UpdateUiListener {
    private UpdateUiListener updateUiListener;
    private ActionBar actionBar;
    Button eighteenBtn, ninteenBtn, currentBtn, saveBtn;
    public ArrayList<WhiteFlyAssessment> whiteFlyList18 = new ArrayList<>();
    public ArrayList<WhiteFlyAssessment> whiteFlyList19 = new ArrayList<>();
    public ArrayList<WhiteFlyAssessment> whiteFlyList = new ArrayList<>();
    public int year = 0;

    public WhiteFlyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_white_fly, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("WhiteFly");

        eighteenBtn = (Button) view.findViewById(R.id.eighteenBtn);
        ninteenBtn = (Button) view.findViewById(R.id.ninteenBtn);
        currentBtn = (Button) view.findViewById(R.id.currentBtn);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);

        DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());
        String cnt2018 = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().get2018WhiteCount());
        String cnt2019 = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().get2019WhiteCount());
        if(cnt2018.length()>0){
            if(Integer.parseInt(cnt2018)>0){
                eighteenBtn.setEnabled(false);
                eighteenBtn.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }

        if(cnt2019.length()>0){
            if(Integer.parseInt(cnt2019)>0){
                ninteenBtn.setEnabled(false);
                ninteenBtn.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }


        eighteenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = 2018;
               // whiteFlyList18.clear();
                WhiteFieldFragment multiEntryDialogFragment = new WhiteFieldFragment();
                multiEntryDialogFragment.setOnDataSelectedListener(WhiteFlyFragment.this);
                Bundle inpuptBundle = new Bundle();
                inpuptBundle.putInt("type", 2018);
                multiEntryDialogFragment.setArguments(inpuptBundle);
                FragmentManager mFragmentManager = getChildFragmentManager();
                multiEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");
            }
        });

        ninteenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = 2019;
              //  whiteFlyList19.clear();
                WhiteFieldFragment multiEntryDialogFragment = new WhiteFieldFragment();
                multiEntryDialogFragment.setOnDataSelectedListener(WhiteFlyFragment.this);
                Bundle inpuptBundle = new Bundle();
                inpuptBundle.putInt("type", 2019);
                multiEntryDialogFragment.setArguments(inpuptBundle);
                FragmentManager mFragmentManager = getChildFragmentManager();
                multiEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");

            }
        });

        currentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = 2020;
              //  whiteFlyList.clear();
                WhiteFieldFragment multiEntryDialogFragment = new WhiteFieldFragment();
                multiEntryDialogFragment.setOnDataSelectedListener(WhiteFlyFragment.this);
                Bundle inpuptBundle = new Bundle();
                inpuptBundle.putInt("type", 2020);
                multiEntryDialogFragment.setArguments(inpuptBundle);
                FragmentManager mFragmentManager = getChildFragmentManager();
                multiEntryDialogFragment.show(mFragmentManager, "fragment_edit_name");

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               whiteFlyList18 = (List<WhiteFlyAssessment>) DataManager.getInstance().getDataFromManager(DataManager.WHITE_FLY);
//                whiteFlyList19 = (List<WhiteFlyAssessment>) DataManager.getInstance().getDataFromManager(DataManager.WHITE_FLY_18);
//                 whiteFlyList = (List<WhiteFlyAssessment>) DataManager.getInstance().getDataFromManager(DataManager.WHITE_FLY_19);
                // if (year == 2018 && whiteFlyList18.size()>0){
                if ( whiteFlyList18.size()>0){
                    DataManager.getInstance().addData(DataManager.WHITE_FLY_18, whiteFlyList18);
                    // }else if(year == 2019 && whiteFlyList19.size()>0){
                }
                if(whiteFlyList19.size()>0){
                    DataManager.getInstance().addData(DataManager.WHITE_FLY_19, whiteFlyList19);
                    // }else if(year == 2020 && whiteFlyList.size()>0){
                }
                if(whiteFlyList.size()>0){
                    DataManager.getInstance().addData(DataManager.WHITE_FLY, whiteFlyList);
                }
                updateUiListener.updateUserInterface(0);
                getFragmentManager().popBackStack();
            }
        });
        return view;
    }


    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void updateUserInterface(int refreshPosition) {

    }

    @Override
    public void onEditClicked(int position) {

    }

    @Override
    public void onDataSelected(int type, Bundle bundle) {
        if (whiteFlyList == null || whiteFlyList.isEmpty()) {
            whiteFlyList = new ArrayList<>();
        }

        if (whiteFlyList18 == null || whiteFlyList18.isEmpty()) {
            whiteFlyList18 = new ArrayList<>();
        }

        if (whiteFlyList19 == null || whiteFlyList19.isEmpty()) {
            whiteFlyList19 = new ArrayList<>();
        }


        if (bundle.getInt("Year") == 2018 && bundle != null){
           // whiteFlyList18.clear();
            whiteFlyList18.add(new WhiteFlyAssessment(bundle.getString("Question"),
                    bundle.getString("Answer"),
                    bundle.getString("Value"),
                    bundle.getInt("Year")));
        }else if (bundle.getInt("Year") == 2019 && bundle != null){
           // whiteFlyList19.clear();
            whiteFlyList19.add(new WhiteFlyAssessment(bundle.getString("Question"),
                    bundle.getString("Answer"),
                    bundle.getString("Value"),
                    bundle.getInt("Year")));
        }else if (bundle != null){
           // whiteFlyList.clear();
            whiteFlyList.add(new WhiteFlyAssessment(bundle.getString("Question"),
                    bundle.getString("Answer"),
                    bundle.getString("Value"),
                    bundle.getInt("Year")));
        }

        saveBtn.setVisibility(View.VISIBLE);
    }
}
