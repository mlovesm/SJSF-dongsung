package com.creative.dongsung.app.equip;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.dongsung.app.R;
import com.creative.dongsung.app.adaptor.BoardAdapter;
import com.creative.dongsung.app.retrofit.Datas;
import com.creative.dongsung.app.retrofit.RetrofitService;
import com.creative.dongsung.app.util.UtilClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.creative.dongsung.app.R.id.et_search;

public class WorkerPositionFragment extends Fragment {
    private static final String TAG = "WorkerPositionFragment";
    private RetrofitService service;
    private String title;

    private ArrayList<HashMap<String,String>> arrayList;
    private BoardAdapter mAdapter;
    @Bind(R.id.listView1) ListView listView;
    @Bind(R.id.top_title) TextView textTitle;
    @Bind(R.id.textButton1) TextView tv_button1;
    @Bind(R.id.textButton2) TextView tv_button2;

    @Bind(R.id.editText1) EditText et_search;

    private boolean isSdate=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workers_list, container, false);
        ButterKnife.bind(this, view);
        service= RetrofitService.rest_api.create(RetrofitService.class);

        title= getArguments().getString("title");
        textTitle.setText(title);
        view.findViewById(R.id.top_write).setVisibility(View.VISIBLE);

        tv_button1.setText(UtilClass.getCurrentDate(2, "."));
        tv_button2.setText(UtilClass.getCurrentDate(1, "."));

        async_progress_dialog();

        listView.setOnItemClickListener(new ListViewItemClickListener());

        return view;
    }//onCreateView

    public void async_progress_dialog(){
        final ProgressDialog pDlalog = new ProgressDialog(getActivity());
        UtilClass.showProcessingDialog(pDlalog);

        Call<Datas> call = service.listDataQ("Equip","workersPositionList",tv_button1.getText().toString(), tv_button2.getText().toString(), et_search.getText().toString());
        call.enqueue(new Callback<Datas>() {
            @Override
            public void onResponse(Call<Datas> call, Response<Datas> response) {
                UtilClass.logD(TAG, "response="+response);
                if (response.isSuccessful()) {
                    UtilClass.logD(TAG, "isSuccessful="+response.body().toString());
                    String status= response.body().getStatus();
                    try {
                        if(response.body().getCount()==0){
                            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        arrayList = new ArrayList<>();
                        arrayList.clear();
                        for(int i=0; i<response.body().getList().size();i++){
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("key",response.body().getList().get(i).get("work_key"));
                            hashMap.put("data1",response.body().getList().get(i).get("work_date"));
                            hashMap.put("data2",response.body().getList().get(i).get("worker_nm"));
                            hashMap.put("data3",response.body().getList().get(i).get("work_loc"));
                            arrayList.add(hashMap);
                        }

                        mAdapter = new BoardAdapter(getActivity(), arrayList, "Workers");
                        listView.setAdapter(mAdapter);
                    } catch ( Exception e ) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "에러코드 Workers 1", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "onFailure Workers",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.top_home)
    public void goHome() {
        UtilClass.goHome(getActivity());
    }

    @OnClick(R.id.top_write)
    public void getWriteBoard() {
        Fragment frag = new WorkersPositionWriteFragment();
        Bundle bundle = new Bundle();

        bundle.putString("mode","insert");
        frag.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentReplace, frag);
        fragmentTransaction.addToBackStack(title+"작성");
        fragmentTransaction.commit();
    }

    //해당 검색값 데이터 조회
    @OnClick(R.id.imageView1)
    public void onSearchColumn() {
        //검색하면 키보드 내리기
        InputMethodManager imm= (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);

        if(et_search.getText().toString().length()==0){
            Toast.makeText(getActivity(), "작업자를 입력하세요.", Toast.LENGTH_SHORT).show();
        }else{
            async_progress_dialog();
        }

    }

    //날짜설정
    @OnClick(R.id.textButton1)
    public void getDateDialog() {
        getDialog("SD");
        isSdate=true;
    }
    @OnClick(R.id.textButton2)
    public void getDateDialog2() {
        getDialog("ED");
        isSdate=false;
    }

    public void getDialog(String gubun) {
        int year, month, day;

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);

        if(gubun.equals("SD")){
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), date_listener, year, month, 1);
            dialog.show();
        }else{
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), date_listener, year, month, day);
            dialog.show();
        }

    }

    private DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month= UtilClass.addZero(monthOfYear+1);
            String day= UtilClass.addZero(dayOfMonth);
            String date= year+"."+month+"."+day;

            if(isSdate){
                tv_button1.setText(date);
            }else{
                tv_button2.setText(date);
            }
            async_progress_dialog();

        }
    };

    //ListView의 item (상세)
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Fragment frag = null;
            Bundle bundle = new Bundle();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentReplace, frag = new WorkersPositionWriteFragment());
            bundle.putString("title",title+"상세");
            String key= arrayList.get(position).get("key");
            bundle.putString("idx", key);
            bundle.putString("mode", "update");

            frag.setArguments(bundle);
            fragmentTransaction.addToBackStack(title+"상세");
            fragmentTransaction.commit();
        }
    }

}
