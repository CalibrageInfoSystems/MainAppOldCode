package com.oilpalm3f.mainapp.cropmaintenance;


import android.content.Context;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.areaextension.RegistrationFlowScreen;
import com.oilpalm3f.mainapp.areaextension.UpdateUiListener;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.conversion.PalmDetailsEditListener;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.DataSavingHelper;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.Fertilizer;
import com.oilpalm3f.mainapp.dbmodels.Fertilizer;
import com.oilpalm3f.mainapp.dbmodels.Nutrient;
import com.oilpalm3f.mainapp.dbmodels.RecommndFertilizer;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.oilpalm3f.mainapp.common.CommonUtils.edittextEampty;
import static com.oilpalm3f.mainapp.common.CommonUtils.spinnerSelect;
import static com.oilpalm3f.mainapp.cropmaintenance.CommonUtilsNavigation.getKey;
import static com.oilpalm3f.mainapp.cropmaintenance.CropMaintainanceHistoryFragment.RECOM_FERTILIZER_DATA;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomndFertilizerFragment extends Fragment implements View.OnClickListener, PalmDetailsEditListener, UpdateUiListener {
//    private RecomNDScreen.OnFragmentInteractionListener mListener;
    private Context mContext;
    private View rootView;
    private Toolbar toolbarrecomnd;
    private ActionBar actionBar;
    private Spinner rcmndfertilizerProductNameSpin,rcmnduomSpin;
    private EditText rcmndosageEdt;
    private EditText rcmndedtcmment;
    private RecyclerView rcmndsaveList;
    private Button rcmndsave,historyBtn;
    private DataAccessHandler dataAccessHandler;
    private LinkedHashMap<String, String> fertilizerDataMap, fertilizerTypeDataMap, uomDataMap, frequencyOfApplicationDataMap;
    private ArrayList mFertilizerModelArray;
    private RecmndGenericTypeAdapter fertilizerDataAdapter;
    private Fertilizer  mFertilizerModel;
    private Fertilizer mFertilizerModel1;
    private ArrayList<Nutrient> mmNutrientModelModelArray;
    private String screenrecmnd;
    private String value;
    private UpdateUiListener updateUiListener;



    public RecomndFertilizerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_recomnd_fertilizert, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbarrecomnd = (Toolbar) rootView.findViewById(R.id.toolbar);

        activity.setSupportActionBar(toolbarrecomnd);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Recommended Fertilizer");

        mContext = getActivity();
        setHasOptionsMenu(true);
        initViews();
        setViews();
        bindData();

        return rootView;
    }




    private void initViews() {
        dataAccessHandler = new DataAccessHandler(getActivity());

        rcmndfertilizerProductNameSpin = (Spinner) rootView.findViewById(R.id.rcmndfertilizerProductNameSpin);
        rcmnduomSpin = (Spinner) rootView.findViewById(R.id.rcmnduomSpin);
        rcmndosageEdt=(EditText)rootView.findViewById(R.id.rcmndosageEdt);
        rcmndedtcmment=(EditText)rootView.findViewById(R.id.rcmndedtcmment);
        rcmndsaveList=(RecyclerView)rootView.findViewById(R.id.rcmndsaveList);
        rcmndsave=(Button)rootView.findViewById(R.id.rcmndsave);
        historyBtn = (Button) rootView.findViewById(R.id.historyBtn);

    }
    private void setViews() {
        rcmndsave.setOnClickListener(this);
        historyBtn.setOnClickListener(this);


        fertilizerTypeDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getLookUpData("23"));
        rcmndfertilizerProductNameSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "fertilizer Product Name", fertilizerTypeDataMap));
        uomDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getUOM());
        rcmnduomSpin.setAdapter(CommonUtilsNavigation.adapterSetFromHashmap(getActivity(), "Select UOM", uomDataMap));


    }
    private void bindData() {


            mFertilizerModelArray = (ArrayList<RecommndFertilizer>) DataManager.getInstance().getDataFromManager(DataManager.RECMND_FERTILIZER);
        if (null == mFertilizerModelArray)
            mFertilizerModelArray = new ArrayList<RecommndFertilizer>();

        fertilizerDataAdapter = new RecmndGenericTypeAdapter(getActivity(), mFertilizerModelArray, fertilizerTypeDataMap, uomDataMap, GenericTypeAdapter.TYPE_RECOM_FERTILIZER);

        rcmndsaveList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcmndsaveList.setAdapter(fertilizerDataAdapter);
        fertilizerDataAdapter.setEditClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rcmndsave:
                if (validateUI()) {
                    RecommndFertilizer mFertilizerModel=new RecommndFertilizer();
                        mFertilizerModel.setRecommendFertilizerProviderId((Integer.parseInt(getKey(fertilizerTypeDataMap, rcmndfertilizerProductNameSpin.getSelectedItem().toString()))));
                        mFertilizerModel.setRecommendUOMId(Integer.parseInt(getKey(uomDataMap, rcmnduomSpin.getSelectedItem().toString())));
                        mFertilizerModel.setRecommendDosage(TextUtils.isEmpty(rcmndosageEdt.getText().toString())==true? 0.0:Double.parseDouble(rcmndosageEdt.getText().toString()));

                        mFertilizerModel.setComments(rcmndedtcmment.getText().toString());
                        mFertilizerModelArray.add(mFertilizerModel);
                        DataManager.getInstance().addData(DataManager.RECMND_FERTILIZER, mFertilizerModelArray);


                    clearFields();
                    fertilizerDataAdapter.notifyDataSetChanged();

                }
                CommonUtilsNavigation.hideKeyBoard(getActivity());
                break;

            case R.id.historyBtn:
                CropMaintainanceHistoryFragment newFragment = new CropMaintainanceHistoryFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("screen", RECOM_FERTILIZER_DATA);
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getFragmentManager(), "history");
                break;
        }
    }

    @Override
    public void updateUserInterface(int refreshPosition) {

    }
    private void clearFields() {
        rcmndfertilizerProductNameSpin.setSelection(0);

        rcmnduomSpin.setSelection(0);
        rcmndosageEdt.setText("");
        rcmndedtcmment.setText("");
    }
    @Override
    public void onEditClicked(int position) {

            mFertilizerModelArray.remove(position);



        fertilizerDataAdapter.notifyDataSetChanged();
    }
    private boolean validateUI() {
        return spinnerSelect(rcmndfertilizerProductNameSpin, "Fertilizer product name", mContext)
                 && edittextEampty(rcmndosageEdt, "Dosage given", mContext) && spinnerSelect(rcmnduomSpin, "UOM", mContext);

    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }
}
