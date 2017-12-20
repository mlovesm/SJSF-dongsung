package com.creative.dongsung.app.government;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.dongsung.app.R;
import com.creative.dongsung.app.adaptor.WorkAdapter;
import com.creative.dongsung.app.fragment.WebFragment;
import com.creative.dongsung.app.util.UtilClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GovernmentFragment extends Fragment {
    private static final String TAG = "GovernmentFragment";

    private ArrayList<HashMap<String,String>> arrayList;
    private WorkAdapter mAdapter;
    private String[] mGroupList = null;
    private String[] mChildListUrl = null;

    @Bind(R.id.listView1) ListView listView;
    @Bind(R.id.top_title) TextView textTitle;

    @Override
    public void onStart() {
        super.onStart();
    }//onStart

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_work, container, false);
        ButterKnife.bind(this, view);

        textTitle.setText(getArguments().getString("title"));
        view.findViewById(R.id.top_home).setVisibility(View.VISIBLE);

        getGovernmentMenu();

        listView.setOnItemClickListener(new ListViewItemClickListener());

        return view;
    }

    public void getGovernmentMenu(){
        try {
            mGroupList = new String[4];
            mGroupList[0] = "출퇴근현황";
            mGroupList[1] = "일근태";
            mGroupList[2] = "년차조회";
            mGroupList[3] = "급여명세서";

            mChildListUrl = new String[] { "commute_list.do",  "commute_oneday_list.do",  "commute_search.do",  "pay_list.do" };

            arrayList = new ArrayList<>();
            arrayList.clear();
            for(int i=0; i<mGroupList.length;i++){
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("title", mGroupList[i]);
                hashMap.put("url", mChildListUrl[i]);
                arrayList.add(hashMap);
            }

            mAdapter = new WorkAdapter(getActivity(), arrayList);
            listView.setAdapter(mAdapter);
        } catch ( Exception e ) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "에러코드 Government 1", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.top_home)
    public void goHome() {
        UtilClass.goHome(getActivity());
    }

    public void pdfTest() {
        String fileName= Environment.getExternalStorageDirectory()+"/Download/Jersey Framework.pdf";
        Log.d(TAG, "fileName="+fileName);
        if(fileName.length()>0){
            openPDF(fileName.trim());
        }else{
            Toast.makeText(getActivity(), "PDF 파일명을 입력하시오.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openPDF(String contentsPath) {
        File file = new File(contentsPath);

        if(file.exists()) {
            Uri path = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex){
                Toast.makeText(getActivity(), "PDF 파일을 보기 위한 앱이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "PDF 파일이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFragment(Fragment fragment, Bundle bundle,String title){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentReplace, fragment);
        fragmentTransaction.addToBackStack(title);

        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    //ListView의 item을 클릭했을 때.
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Fragment fragment = new WebFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title",arrayList.get(position).get("title").toString());
            bundle.putString("url",arrayList.get(position).get("url").toString());
            onFragment(fragment,bundle,"근태/급여상세");
        }
    }

}
