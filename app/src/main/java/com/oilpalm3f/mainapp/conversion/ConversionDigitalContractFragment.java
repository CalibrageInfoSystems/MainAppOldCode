package com.oilpalm3f.mainapp.conversion;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.areaextension.UpdateUiListener;
import com.oilpalm3f.mainapp.cloudhelper.Config;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.cropmaintenance.CommonUtilsNavigation;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.dbmodels.DigitalContract;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.LongFunction;

/**
 * Created by skasam on 1/9/2017.
 */
public class ConversionDigitalContractFragment extends BaseFragment implements OnPageChangeListener, OnLoadCompleteListener {

    private static final String LOG_TAG = ConversionDigitalContractFragment.class.getName();
    public static boolean isContractAgreed = false;
    private LinearLayout parentLayout;
    private PDFView dataView;
    private Button saveBtn;
    private CheckBox agreeChbk;
    private DataAccessHandler dataAccessHandler;
    private UpdateUiListener updateUiListener;
    private DigitalContract digitalContract;
    private File fileToDownLoad = null;
    private File rootDirectory;
    String plotcode;

    public ConversionDigitalContractFragment() {

    }

    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View parentView = inflater.inflate(R.layout.frag_conversion_digitalcontract, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getActivity().getResources().getString(R.string.digitalcontracttitle));
        dataAccessHandler = new DataAccessHandler(getActivity());

        plotcode = CommonConstants.PLOT_CODE;
        Log.d("Plotcode is",plotcode + "");

        if (plotcode.startsWith("AP")){

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getAPDigitalContract(), 0);
        }else if(plotcode.startsWith("TE")){

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getTEDigitalContract(), 0);
        }
        else if(plotcode.startsWith("CK")){

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getCKDigitalContract(), 0);
        }
        else if(plotcode.startsWith("AR")){

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getARDigitalContract(), 0);
        }
        else if(plotcode.startsWith("CG")){

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getCGDigitalContract(), 0);
        }
        else if(plotcode.startsWith("NK")){

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getNKDigitalContract(), 0);
        }
        else if(plotcode.startsWith("SK")){

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getSKDigitalContract(), 0);
        }
        else{

            digitalContract = (DigitalContract) dataAccessHandler.getDigitalContractData(Queries.getInstance().getAPDigitalContract(), 0);
        }



        saveBtn = (Button) parentView.findViewById(R.id.digitalSaveBtn);
        agreeChbk = (CheckBox) parentView.findViewById(R.id.agreedView);

        parentLayout = (LinearLayout) parentView.findViewById(R.id.parent_layout);
        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CommonUtilsNavigation.hideKeyBoard(getActivity());
                return false;
            }
        });

        dataView = (PDFView) parentView.findViewById(R.id.agreementView);

        agreeChbk.setChecked(isContractAgreed);

        if (!isContractAgreed) {
            saveBtn.setEnabled(false);
            saveBtn.setFocusable(false);
            saveBtn.setClickable(false);
            saveBtn.setAlpha(0.5f);
        } else {
            saveBtn.setEnabled(true);
            saveBtn.setFocusable(true);
            saveBtn.setClickable(true);
            saveBtn.setAlpha(1.0f);
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUiListener.updateUserInterface(0);
                getFragmentManager().popBackStack();
            }
        });

        agreeChbk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveBtn.setAlpha(1.0f);
                } else {
                    saveBtn.setAlpha(0.5f);
                }

                isContractAgreed = isChecked;
                saveBtn.setEnabled(isChecked);
                saveBtn.setFocusable(isChecked);
                saveBtn.setClickable(isChecked);
            }
        });


        rootDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_DigitalContract/");
        if (null != digitalContract) {
            fileToDownLoad = new File(rootDirectory + digitalContract.getFILENAME() + digitalContract.getFileExtension());
            if (null != fileToDownLoad && fileToDownLoad.exists()) {
                dataView.fromFile(fileToDownLoad)
                        .defaultPage(0)
                        .enableAnnotationRendering(true)
                        .onPageChange(this)
                        .onLoad(this)
                        .scrollHandle(new DefaultScrollHandle(getActivity()))
                        .load();
            } else {
                String url = Config.image_url + "/" + digitalContract.getFileLocation() + "/" + digitalContract.getFILENAME() + digitalContract.getFileExtension();
                new DownloadFileFromURL().execute(url);
            }
        }

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    public UpdateUiListener getUpdateUiListener() {
        return updateUiListener;
    }

    public void setUpdateUiListener(UpdateUiListener updateUiListener) {
        this.updateUiListener = updateUiListener;
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        public boolean downloadSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                OutputStream output = new FileOutputStream(rootDirectory + digitalContract.getFILENAME() + digitalContract.getFileExtension());

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                downloadSuccess = true;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                downloadSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (downloadSuccess && !getActivity().isFinishing()) {
                fileToDownLoad = new File(rootDirectory + digitalContract.getFILENAME() + digitalContract.getFileExtension());
                if (null != fileToDownLoad && fileToDownLoad.exists()) {
                    dataView.fromFile(fileToDownLoad)
                            .defaultPage(0)
                            .enableAnnotationRendering(true)
                            .onPageChange(ConversionDigitalContractFragment.this)
                            .onLoad(ConversionDigitalContractFragment.this)
                            .scrollHandle(new DefaultScrollHandle(getActivity()))
                            .load();
                } else {
                    UiUtils.showCustomToastMessage("File not exist", getActivity(), 1);
                }
            }

        }
    }

}
