package com.creative.dongsung.app.common;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.creative.dongsung.app.R;
import com.creative.dongsung.app.menu.MainFragment;
import com.creative.dongsung.app.retrofit.Datas;
import com.creative.dongsung.app.retrofit.RetrofitService;
import com.creative.dongsung.app.util.UtilClass;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlyWriteFragment extends Fragment {
    private static final String TAG = "MonthlyWriteFragment";
    private ProgressDialog pDlalog = null;
    private RetrofitService service;

    private String mode="";
    private String idx="";
    private String dataSabun;
    private boolean userAuth= false;
    private boolean isFirstTime=false;
    private boolean isFirstDate=false;

    @Bind(R.id.top_title) TextView textTitle;
    @Bind(R.id.date_button1) TextView tvDate1;
    @Bind(R.id.date_button2) TextView tvDate2;
    @Bind(R.id.stime_button) TextView tvTime1;
    @Bind(R.id.etime_button) TextView tvTime2;
    @Bind(R.id.editText1) EditText et_title;
    @Bind(R.id.editText2) EditText et_location;
    @Bind(R.id.editText3) EditText et_memo;
    @Bind(R.id.textView1) TextView tv_writerName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.monthly_write, container, false);
        ButterKnife.bind(this, view);
        service= RetrofitService.rest_api.create(RetrofitService.class);

        mode= getArguments().getString("mode");
        view.findViewById(R.id.top_save).setVisibility(View.VISIBLE);

        getUserAuth();

        if(mode.equals("insert")){
            dataSabun= MainFragment.loginSabun;
            view.findViewById(R.id.linear2).setVisibility(View.GONE);
            textTitle.setText("월중행사계획표 작성");
            tvDate1.setText(UtilClass.getCurrentDate(1, "."));
            tvDate2.setText(UtilClass.getCurrentDate(1, "."));
            tvTime1.setText(UtilClass.getCurrentDate(4, ""));
            tvTime2.setText(UtilClass.getCurrentDate(4, ""));
            tv_writerName.setText(MainFragment.loginName);
        }else{
            textTitle.setText("월중행사계획표 수정");
            idx= getArguments().getString("idx");
            async_progress_dialog();
        }

        return view;
    }//onCreateView

    public void getUserAuth(){
        Call<Datas> call = service.listData("Common","userAuth", "1", MainFragment.loginSabun);
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        if(response.body().getCount()==0){
                            userAuth= false;
                        }else{
                            userAuth= true;
                        }

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 Monthly 3", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "response isFailed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "onFailure Board",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void async_progress_dialog(){
        final ProgressDialog pDlalog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(pDlalog);

        Call<Datas> call = service.listData("Common","monthlyDetail", idx);
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        dataSabun= response.body().getList().get(0).get("writer_sabun");
                        if(MainFragment.loginSabun.equals(dataSabun)){
                        }else{
                            et_location.setFocusableInTouchMode(false);
                            et_memo.setFocusableInTouchMode(false);
                        }
                        tvDate1.setText(response.body().getList().get(0).get("plan_sdate"));
                        tvDate2.setText(response.body().getList().get(0).get("plan_edate"));
                        tvTime1.setText(response.body().getList().get(0).get("plan_stime"));
                        tvTime2.setText(response.body().getList().get(0).get("plan_etime"));
                        et_title.setText(response.body().getList().get(0).get("plan_title"));
                        et_location.setText(response.body().getList().get(0).get("plan_locate"));
                        et_memo.setText(response.body().getList().get(0).get("plan_content"));
                        tv_writerName.setText(response.body().getList().get(0).get("writer_nm"));

                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 Monthly 2", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "response isFailed", Toast.LENGTH_SHORT).show();
                }
                if(pDlalog!=null) pDlalog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(pDlalog!=null) pDlalog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "onFailure Board",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //날짜설정
    @OnClick(R.id.date_button1)
    public void getDateDialog1() {
        isFirstDate=true;
        getDialog("D");
    }

    @OnClick(R.id.date_button2)
    public void getDateDialog2() {
        isFirstDate=false;
        getDialog("D");
    }

    //시간설정
    @OnClick(R.id.stime_button)
    public void getTimeDialog() {
        isFirstTime=true;
        getDialog("ST");
    }
    @OnClick(R.id.etime_button)
    public void getTimeDialog2() {
        isFirstTime=false;
        getDialog("ET");
    }

    public void getDialog(String gubun) {
        if(gubun.equals("D")){
            TextView textView;
            if(isFirstDate){
                textView= tvDate1;
            }else{
                textView= tvDate2;
            }
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), date_listener, UtilClass.dateAndTimeChoiceList(textView, "D").get(0)
                    , UtilClass.dateAndTimeChoiceList(textView, "D").get(1)-1, UtilClass.dateAndTimeChoiceList(textView, "D").get(2));
            dialog.show();
        }else{
            TextView textView;
            if(isFirstTime){
                textView= tvTime1;
            }else{
                textView= tvTime2;
            }
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), time_listener, UtilClass.dateAndTimeChoiceList(textView, "T").get(0)
                    , UtilClass.dateAndTimeChoiceList(textView, "T").get(1), false);
            dialog.show();
        }

    }

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            Toast.makeText(getActivity(), year + "년" + (monthOfYear+1) + "월" + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
            String month= UtilClass.addZero(monthOfYear+1);
            String day= UtilClass.addZero(dayOfMonth);
            if(isFirstDate){
                tvDate1.setText(year+"."+month+"."+day);
            }else{
                tvDate2.setText(year+"."+month+"."+day);
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener time_listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
//            Toast.makeText(getActivity(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
            String hour= UtilClass.addZero(hourOfDay);
            String min= UtilClass.addZero(minute);
            if(isFirstTime){
                tvTime1.setText(hour+":"+min);
            }else{
                tvTime2.setText(hour+":"+min);
            }
        }
    };

    @OnClick(R.id.top_home)
    public void goHome() {
        UtilClass.goHome(getActivity());
    }

    @OnClick({R.id.textButton1, R.id.top_save})
    public void alertDialogSave(){
        if(MainFragment.loginSabun.equals(dataSabun)){
            if(userAuth){
                alertDialog("S");
            }else{
                Toast.makeText(getActivity(),"해당 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(),"작성자만 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.textButton2})
    public void alertDialogDelete(){
        if(MainFragment.loginSabun.equals(dataSabun)){
            alertDialog("D");
        }else{
            Toast.makeText(getActivity(),"작성자만 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void alertDialog(final String gubun){
        final AlertDialog.Builder alertDlg = new AlertDialog.Builder(getActivity());
        alertDlg.setTitle("알림");
        if(gubun.equals("S")){
            alertDlg.setMessage("작성하시겠습니까?");
        }else if(gubun.equals("D")){
            alertDlg.setMessage("삭제하시겠습니까?");
        }else{
            alertDlg.setMessage("전송하시겠습니까?");
        }
        // '예' 버튼이 클릭되면
        alertDlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(gubun.equals("S")){
                    postData();
                }else if(gubun.equals("D")){
                    deleteData();
                }else{

                }
            }
        });
        // '아니오' 버튼이 클릭되면
        alertDlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // AlertDialog를 닫는다.
            }
        });
        alertDlg.show();
    }

    //삭제
    public void deleteData() {
        pDlalog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(pDlalog);

        Call<Datas> call = service.deleteData("Common","monthlyDelete", idx);

        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    handleResponse(response);
                }else{
                    Toast.makeText(getActivity(), "작업에 실패하였습니다.",Toast.LENGTH_LONG).show();
                }
                if(pDlalog!=null) pDlalog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(pDlalog!=null) pDlalog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "handleResponse Monthly",Toast.LENGTH_LONG).show();
            }
        });
    }

    //작성,수정
    public void postData() {
        if (et_title.equals("") || et_title.length()==0) {
            Toast.makeText(getActivity(), "빈칸을 채워주세요.",Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> map = new HashMap();
        map.put("writer_sabun", MainFragment.loginSabun);
        map.put("writer_name", MainFragment.loginName);
        map.put("plan_sdate",tvDate1.getText());
        map.put("plan_edate",tvDate2.getText());
        map.put("plan_stime",tvTime1.getText());
        map.put("plan_etime",tvTime2.getText());
        map.put("plan_title", et_title.getText());
        map.put("plan_locate", et_location.getText());
        map.put("plan_content",et_memo.getText());

        pDlalog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(pDlalog);

        Call<Datas> call= null;
        if(mode.equals("insert")){
            call = service.insertData("Common","monthlyInsert", map);
        }else{
            call = service.updateData("Common","monthlyModify", map);
            map.put("idx",idx);
        }

        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    try {
                        handleResponse(response);
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 Monthly 1", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "response isFailed", Toast.LENGTH_SHORT).show();
                }
                if(pDlalog!=null) pDlalog.dismiss();
            }

            @Override
            public void onFailure(Call<Datas> call, Throwable t) {
                if(pDlalog!=null) pDlalog.dismiss();
                UtilClass.logD(TAG, "onFailure="+call.toString()+", "+t);
                Toast.makeText(getActivity(), "onFailure Monthly",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //작성 완료
    public void handleResponse(Response<Datas> response) {
        try {
            String status= response.body().getStatus();
            if(status.equals("success")){
                getActivity().onBackPressed();
            }else{
                Toast.makeText(getActivity(), "실패하였습니다.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "작업에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }

    }

}
