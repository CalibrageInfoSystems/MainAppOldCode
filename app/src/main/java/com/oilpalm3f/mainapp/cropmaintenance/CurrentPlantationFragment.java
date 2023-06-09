package com.oilpalm3f.mainapp.cropmaintenance;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.areaextension.UpdateUiListener;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.Uprootment;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.util.LinkedHashMap;

import static com.oilpalm3f.mainapp.cropmaintenance.CommonUtilsNavigation.getKey;

/**
 * Created by CHENCHAIAH on 5/27/2017.
 */

public class CurrentPlantationFragment extends Fragment {
    private TextView Noofsaplingsplanted_text, countoftreespreviousvisit_text, missingtrees_text, noofmissingtrees_text, comments_text, expectedTreecount_visit;
    private EditText counttresscurrentvisitEdt, comment_edit;
    private Spinner reasonformissing;
    private View rootView;
    private Context mContext;
    private Button savebtn;
    private DataAccessHandler dataAccessHandler;
    private UpdateUiListener updateUiListener;
    private LinkedHashMap<String, String> reasonDataMap;
    private Uprootment mUprootmentModel;
    private String treesCount, preCount;
    private int saplingsCount = 0;
    private ActionBar actionBar;
    private String gapFillingTreeCount, expecetedTreesCount;
    private String[] expecetTreeCount;
    private int missingTrees = 0;
    private LinearLayout reasonformissingtreesLL;
//    private int expecetedTreesCount = 0;

    public CurrentPlantationFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.currentplantation_layout, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(getString(R.string.current_plantation));

        mContext = getActivity();
        setHasOptionsMenu(true);

        dataAccessHandler = new DataAccessHandler(getActivity());
        reasonDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getLookUpData("4"));
        treesCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().querySumOfSaplings(CommonConstants.PLOT_CODE));
        try {
            expecetTreeCount = dataAccessHandler.getOnlyTwoValueFromDb(Queries.getInstance().getExpectedTreeCount(CommonConstants.PLOT_CODE)).split("@");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (expecetTreeCount != null) {
            gapFillingTreeCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getGapFillingTreeCount(CommonConstants.PLOT_CODE, expecetTreeCount[1]));
            if (!TextUtils.isEmpty(gapFillingTreeCount)) {
                expecetedTreesCount = String.valueOf(Integer.parseInt(expecetTreeCount[0]) + Integer.parseInt(gapFillingTreeCount));

            } else {
                expecetedTreesCount = expecetTreeCount[0];
            }
        } else {
            expecetedTreesCount = treesCount;
        }


        initViews();
        bindData();
        setViews();
        if (expecetedTreesCount != null)
//            saplingsCount = CommonUtils.convertToBigNumber(treesCount);
            saplingsCount = CommonUtils.convertToBigNumber(expecetedTreesCount);
        return rootView;
    }


    private void bindData() {
        mUprootmentModel = (Uprootment) DataManager.getInstance().getDataFromManager(DataManager.CURRENT_PLANTATION);
        if (mUprootmentModel != null) {
            counttresscurrentvisitEdt.setText("" + mUprootmentModel.getPlamscount());
            noofmissingtrees_text.setText("" + mUprootmentModel.getMissingtreescount());
            missingtrees_text.setText(mUprootmentModel.getIstreesmissing() == 1 ? "yes" : "No");
            reasonformissing.setSelection(mUprootmentModel.getReasontypeid() == null ? 0 : CommonUtilsNavigation.getvalueFromHashMap(reasonDataMap, mUprootmentModel.getReasontypeid()));
            comment_edit.setText("" + mUprootmentModel.getComments());
            preCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().queryGetCountOfPreviousTrees(CommonConstants.PLOT_CODE));
            if (!TextUtils.isEmpty(preCount)) {
                countoftreespreviousvisit_text.setText(preCount);
            } else {
                countoftreespreviousvisit_text.setText(treesCount);
            }
        } else {
            preCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().queryGetCountOfPreviousTrees(CommonConstants.PLOT_CODE));
            if (!TextUtils.isEmpty(preCount)) {
                countoftreespreviousvisit_text.setText(preCount);
            } else {
                countoftreespreviousvisit_text.setText(treesCount);
            }
        }
    }

    private void initViews() {

        Noofsaplingsplanted_text = (TextView) rootView.findViewById(R.id.saplingplanted_text);
        Noofsaplingsplanted_text.setText(treesCount);
        expectedTreecount_visit = rootView.findViewById(R.id.expectedTreecountvisit);
        expectedTreecount_visit.setText(expecetedTreesCount);
        countoftreespreviousvisit_text = (TextView) rootView.findViewById(R.id.countoftreesvisit_text);
        counttresscurrentvisitEdt = (EditText) rootView.findViewById(R.id.counttresscurrentvisitEdt);
        missingtrees_text = (TextView) rootView.findViewById(R.id.missingtrees_text);
        noofmissingtrees_text = (TextView) rootView.findViewById(R.id.no_of_missing_treesTV);
        reasonformissing = (Spinner) rootView.findViewById(R.id.reason_for_missing_treesSpin);
        reasonformissingtreesLL = rootView.findViewById(R.id.reasonformissingtreesLL);
        comment_edit = (EditText) rootView.findViewById(R.id.commentsEdit);
        savebtn = (Button) rootView.findViewById(R.id.SaveBtn);

        reasonformissing.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(mContext, "Select", reasonDataMap));

    }

    private void setViews() {



        counttresscurrentvisitEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String count = s.toString();
                int previusTrees = 0;
                if (!s.toString().equalsIgnoreCase("")) {
                    int currentTrees = CommonUtils.convertToBigNumber(s.toString());

                    if (Integer.parseInt(expecetedTreesCount) > currentTrees) {
                        missingtrees_text.setText("Yes");
                        noofmissingtrees_text.setText("" + (Integer.parseInt(expecetedTreesCount) - currentTrees));
                        reasonformissingtreesLL.setVisibility(View.VISIBLE);
                        missingTrees = (Integer.parseInt(expecetedTreesCount) - currentTrees);
                        Log.v("@@@missing", "" + missingTrees);
                    } else {
                        missingtrees_text.setText("No");
                        noofmissingtrees_text.setText("0");
                        reasonformissingtreesLL.setVisibility(View.GONE);
                        missingTrees = 0;
                        Log.v("@@@missing", "" + missingTrees);
                    }

                } else {
                    missingtrees_text.setText("No");
                    noofmissingtrees_text.setText("0");
                    missingTrees = 0;
                    Log.v("@@@missing", "" + missingTrees);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(counttresscurrentvisitEdt.getText().toString())) {
                    UiUtils.showCustomToastMessage("Please enter count of trees", mContext, 0);
                    return;
                }

                if (missingTrees > 0) {
                    if (TextUtils.isEmpty(comment_edit.getText().toString())) {
                        UiUtils.showCustomToastMessage("Please enter Comments", mContext, 0);
                        return;
                    }

                }
                CommonConstants.CURRENT_TREE = CommonUtils.convertToBigNumber(counttresscurrentvisitEdt.getText().toString());
                mUprootmentModel = new Uprootment();
                mUprootmentModel.setPlamscount(CommonUtils.convertToBigNumber(counttresscurrentvisitEdt.getText().toString()));
                mUprootmentModel.setIstreesmissing(missingtrees_text.getText().toString().contains("Yes") ? 1 : 0);

                if (TextUtils.isEmpty(noofmissingtrees_text.getText().toString())) {
                    mUprootmentModel.setMissingtreescount(0);

                } else {
                    mUprootmentModel.setMissingtreescount(CommonUtils.convertToBigNumber(noofmissingtrees_text.getText().toString()));

                }
                mUprootmentModel.setReasontypeid(reasonformissing.getSelectedItemPosition() == 0 ? null :
                        Integer.parseInt(getKey(reasonDataMap, reasonformissing.getSelectedItem().toString())));
                mUprootmentModel.setComments(comment_edit.getText().toString());


                if (treesCount != null) {
                    mUprootmentModel.setSeedsplanted(Integer.parseInt(treesCount));
                } else {
                    mUprootmentModel.setSeedsplanted(0);
                }
                mUprootmentModel.setExpectedPlamsCount(CommonUtils.convertToBigNumber(expecetedTreesCount));
                DataManager.getInstance().addData(DataManager.CURRENT_PLANTATION, mUprootmentModel);
                CommonUtilsNavigation.hideKeyBoard(getActivity());
                getFragmentManager().popBackStack();
                updateUiListener.updateUserInterface(0);
            }
        });

    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }
}
