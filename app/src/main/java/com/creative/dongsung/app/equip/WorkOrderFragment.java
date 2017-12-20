package com.creative.dongsung.app.equip;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.dongsung.app.R;
import com.creative.dongsung.app.retrofit.Datas;
import com.creative.dongsung.app.retrofit.RetrofitService;
import com.creative.dongsung.app.util.UtilClass;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkOrderFragment extends Fragment {
    private static final String TAG = "WorkOrderFragment";
    private RetrofitService service;
    private String title;

    @Bind(R.id.top_title) TextView textTitle;
    @Bind(R.id.editText1) TextView et_data1;
    @Bind(R.id.button1) Button button1;
    @Bind(R.id.textView1) TextView tv_data1;
    @Bind(R.id.textView2) TextView tv_data2;
    @Bind(R.id.textView3) TextView tv_data3;
    @Bind(R.id.textView4) TextView tv_data4;
    @Bind(R.id.textView5) TextView tv_data5;
    @Bind(R.id.textView7) TextView tv_data7;
    @Bind(R.id.textView8) TextView tv_data8;
    @Bind(R.id.textView9) TextView tv_data9;
    @Bind(R.id.textView10) TextView tv_data10;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wo, container, false);
        ButterKnife.bind(this, view);
        service= RetrofitService.rest_api.create(RetrofitService.class);

        title= getArguments().getString("title");
        textTitle.setText(title);

        return view;
    }//onCreateView

    public void getWorkOrderData(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(dialog);

        Call<Datas> call = service.listData("Equip","workOrderData", et_data1.getText().toString());
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        if(status.equals("success")){
                            if(response.body().getCount()==0){
                                Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                                tv_data1.setText("");
                                tv_data2.setText("");
                                tv_data3.setText("");
                                tv_data4.setText("");
                                tv_data5.setText("");
                                tv_data7.setText("");
                                tv_data8.setText("");
                                tv_data9.setText("");
                            }
                            ArrayList<String> workName= new ArrayList();
                            for(int i=0; i<response.body().getList().size();i++){
                                if(response.body().getList().get(i).containsKey("C008")){
                                    tv_data1.setText(response.body().getList().get(0).get("C008"));
                                    tv_data2.setText(response.body().getList().get(0).get("C012"));
                                    tv_data3.setText(response.body().getList().get(0).get("C029"));
                                    tv_data4.setText(response.body().getList().get(0).get("C028"));
                                    tv_data5.setText(response.body().getList().get(0).get("C032"));
                                    tv_data7.setText(UtilClass.numericZeroCheck(response.body().getList().get(0).get("C033")));
                                    tv_data8.setText(response.body().getList().get(0).get("C023"));
                                    tv_data9.setText("");
                                }else{
                                    workName.add(response.body().getList().get(i).get("C003"));
                                }
                            }
                            String replaceWorkName= workName+"";
                            replaceWorkName= replaceWorkName.replace("[", "");
                            replaceWorkName= replaceWorkName.replace("]", "");
                            tv_data10.setText(replaceWorkName);
                        }

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 WorkOrder 1", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "response isFailed", Toast.LENGTH_SHORT).show();
                }
                if(dialog!=null) dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(dialog!=null) dialog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "onFailure WorkOrder 1",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.button1)
    public void dataSearch() {
        if(et_data1.getText().length()==0){
            Toast.makeText(getActivity(), "W/O를 입력하세요.",Toast.LENGTH_SHORT).show();
        }else{
            getWorkOrderData();
            InputMethodManager imm= (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_data1.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.top_home)
    public void goHome() {
        UtilClass.goHome(getActivity());
    }

}
