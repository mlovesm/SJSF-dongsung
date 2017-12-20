package com.creative.dongsung.app.material;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.creative.dongsung.app.R;
import com.creative.dongsung.app.adaptor.BaseAdapter;
import com.creative.dongsung.app.fragment.FragMenuActivity;
import com.creative.dongsung.app.menu.MainFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MaterialWriteFragment extends Fragment {

    private static final String TAG = "MaterialWriteFragment";
    private String url;
    private String title;

    @Bind(R.id.top_title) TextView textTitle;
    @Bind(R.id.webView1) WebView webView;
    private ProgressDialog dialog;

    //검색 다이얼로그
    private Dialog mDialog = null;
    private Spinner search_spi;
    private String search_gubun;	//검색 구분
    private EditText et_search;
    private ListView listView;
    private ArrayList<HashMap<String,String>> materialArray;
    private BaseAdapter mAdapter;
    private Button btn_search;
    private TextView btn_cancel;
    private int dialogGubun;

    private AQuery aq;

    public MaterialWriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.basic_view, container, false);
        ButterKnife.bind(this, view);
        aq = new AQuery(getActivity());

        url= getArguments().getString("url");
        title= getArguments().getString("title");
        textTitle.setText(title);

        view.findViewById(R.id.top_save).setVisibility(View.VISIBLE);
        view.findViewById(R.id.top_home).setVisibility(View.VISIBLE);

        final Context myApp = getActivity();
        //자바스크립트 Alert,confirm 사용
        webView.setWebChromeClient(new WebChromeClient() {
            ProgressBar pb = (ProgressBar)view.findViewById(R.id.progressBar1);

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(myApp)
                        .setTitle("경고")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                // TODO Auto-generated method stub
                //return super.onJsConfirm(view, url, message, result);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("알림")
                        .setMessage(message)
                        .setPositiveButton("네",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("아니오",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }

            public void onProgressChanged(WebView webView, int paramInt) {
                this.pb.setProgress(paramInt);
                if (paramInt == 100)
                {
                    this.pb.setVisibility(View.GONE);
                    return;
                }
                this.pb.setVisibility(View.VISIBLE);
            }
        });//setWebChromeClient 재정의
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //This is the filter
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        ((FragMenuActivity)getActivity()).onBackPressed();
                    }
                    return true;
                }

                return false;
            }
        });

        WebSettings wSetting = webView.getSettings();
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.setWebViewClient(new MyWebViewClient());
        wSetting.setJavaScriptEnabled(true);      // 웹뷰에서 자바 스크립트 사용
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
//        wSetting.setGeolocationEnabled(true);   //Geolocation API 사용
//        wSetting.setGeolocationDatabasePath(getActivity().getFilesDir().getPath());

        webView.addJavascriptInterface(new AndroidBridge(), "android");
        webView.loadUrl(url);

        return view;
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void searchDialog(final String args){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    materialSearchDialog(args);
                }
            });
        }
        @JavascriptInterface
        public void materialAfterWrite(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "저장 되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(),FragMenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("title", "자재불출등록");
                    startActivity(intent);
                }
            });
        }
    }

    @OnClick(R.id.top_save)
    public void getWriteBoard() {
        webView.loadUrl("javascript:fn_material_Write('" + MainFragment.loginSabun + "')");
    }

    @OnClick(R.id.top_home)
    public void goHome() {
        Intent intent = new Intent(getActivity(), MainFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading...");
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d("onReceivedError", "errorCode=" + errorCode);
            switch(errorCode) {
                case ERROR_AUTHENTICATION:              // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL:                     // 잘못된 URL
                case ERROR_CONNECT:                     // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE:     	// SSL handshake 수행 실패
                case ERROR_FILE:                        // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND:              // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP:            		// 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO:                          // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION:    	// 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP:               // 너무 많은 리디렉션
                case ERROR_TIMEOUT:                     // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS:           // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN:                     // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME:  	// 지원되지 않는 인증 체계
                case ERROR_UNSUPPORTED_SCHEME:			// URI가 지원되지 않는 방식

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Error");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            Fragment fm = getFragmentManager().findFragmentByTag("dfdf");
                            getActivity().finish();
                        }
                    });
                    builder.setMessage("네트워크 상태가 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
                    builder.show();

                    break;
            }
        }
    }//MyWebViewClient

    //다이얼로그
    private void materialSearchDialog(String args) {
        final View linear = View.inflate(getActivity(), R.layout.material_search, null);
        mDialog = new Dialog(getActivity());
        mDialog.setTitle("검색");
        search_spi= (Spinner) linear.findViewById(R.id.search_spi);
        et_search= (EditText) linear.findViewById(R.id.et_search);
        listView= (ListView) linear.findViewById(R.id.listView1);

        // Spinner 생성
        Log.d(TAG,"구분="+args);

        int searchArray;
        if(args.equals("1")){
            searchArray= R.array.dialog_search_list;
            dialogGubun=1;
        }else if(args.equals("2")){
            searchArray= R.array.dialog_search_list2;
            dialogGubun=2;
        }else if(args.equals("3")){
            searchArray= R.array.dialog_search_list;
            dialogGubun=3;
        }else{
            searchArray= R.array.dialog_search_list;
            dialogGubun=4;
        }
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), searchArray, android.R.layout.simple_spinner_dropdown_item);
        search_spi.setPrompt("선택하세요.");
        search_spi.setAdapter(adapter);
        search_spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				et_search.setText("position : " + position + parent.getItemAtPosition(position));
//				search_spi.getSelectedItem().toString();
                if(position==0){
                    search_gubun="nm";
                }else if(position==1){
                    search_gubun="cd";
                }else{
                    search_gubun="kind";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDialog.setContentView(linear);

        // Back키 눌렀을 경우 Dialog Cancle 여부 설정
        mDialog.setCancelable(true);

        // Dialog 생성시 배경화면 어둡게 하지 않기
//		mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Dialog 밖을 터치 했을 경우 Dialog 사라지게 하기
        mDialog.setCanceledOnTouchOutside(true);

        btn_search = (Button) linear.findViewById(R.id.button1);
        btn_cancel = (TextView) linear.findViewById(R.id.textButton1);

        btn_search.setOnClickListener(button_click_listener);
        btn_cancel.setOnClickListener(button_click_listener);
        listView.setOnItemClickListener(new ListViewItemClickListener());

        // Dialog Cancle시 Event 받기
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dismissDialog();
            }
        });

        // Dialog Show시 Event 받기
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

            }
        });

        // Dialog Dismiss시 Event 받기
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        mDialog.show();
    }

    private void dismissDialog() {
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    public void async_progress_dialog(String callback){
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Loading...", true, true);
        dialog.setInverseBackgroundForced(false);
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                Log.d(TAG, "onDismissed() ");
//                aq.ajaxCancel();
//            }
//        });

        aq.progress(dialog).ajax(MainFragment.ipAddress+MainFragment.contextPath+"/rest/Jajae/MateSearchDialog/gubun="+search_gubun+"/param="+et_search.getText()+"/dialog="+dialogGubun, JSONObject.class, this, callback);
    }

    public void searchDialogData(String url, JSONObject object, AjaxStatus status) {
        Log.d(TAG, "object= "+object);

        if( object != null) {
            try {
                materialArray = new ArrayList<>();
                materialArray.clear();
                if(object.get("count").equals(0)){
                    Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                for(int i=0; i<object.getJSONArray("datas").length();i++){
                    HashMap<String,String> hashMap = new HashMap<>();
                    if(dialogGubun==1){
                        hashMap.put("jepum_cd",object.getJSONArray("datas").getJSONObject(i).get("jepum_cd").toString());
                        hashMap.put("data1",object.getJSONArray("datas").getJSONObject(i).get("jepum_nm").toString());
                        hashMap.put("data2",object.getJSONArray("datas").getJSONObject(i).get("jepum_kind").toString());
                    }else if(dialogGubun==2){
                        hashMap.put("user_nm",object.getJSONArray("datas").getJSONObject(i).get("user_nm").toString());
                        hashMap.put("data1",object.getJSONArray("datas").getJSONObject(i).get("user_no").toString());
                        hashMap.put("data2",object.getJSONArray("datas").getJSONObject(i).get("user_nm").toString());
                    }else{
                        hashMap.put("code",object.getJSONArray("datas").getJSONObject(i).get("C001").toString());
                        hashMap.put("data1",object.getJSONArray("datas").getJSONObject(i).get("C001").toString());
                        hashMap.put("data2",object.getJSONArray("datas").getJSONObject(i).get("C002").toString());
                    }

                    materialArray.add(hashMap);
                }

                mAdapter = new BaseAdapter(getActivity(), materialArray);
                listView.setAdapter(mAdapter);
            } catch ( Exception e ) {
                Toast.makeText(getActivity(), "에러코드 Material 1", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG,"Data is Null");
            Toast.makeText(getActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener button_click_listener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button1:
                    //검색하면 키보드 내리기
                    InputMethodManager imm= (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                    async_progress_dialog("searchDialogData");
                    break;

                case R.id.textButton1:
                    dismissDialog();
                    break;
            }
        }
    };

    //ListView의 item을 클릭했을 때.
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap = materialArray.get(position);
            ArrayList<String> arr = new ArrayList<>();
            for (Iterator iter = hashMap.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                //String key = (String)entry.getKey();
                arr.add((String) entry.getValue());
            }
//            Log.d(TAG, "?="+arr);
            String reUrl=null;
            if(dialogGubun==1){
                reUrl="javascript:fn_after_searchDialog1('" + materialArray.get(position).get("jepum_cd") + "')";
            }else if(dialogGubun==2){
                reUrl="javascript:fn_after_searchDialog2('" + materialArray.get(position).get("user_nm") + "')";
            }else if(dialogGubun==3){
                reUrl="javascript:fn_after_searchDialog3('" + materialArray.get(position).get("code") + "')";
            }else{
                reUrl="javascript:fn_after_searchDialog4('" + materialArray.get(position).get("code") + "')";
            }
            webView.loadUrl(reUrl);
            InputMethodManager imm= (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
            dismissDialog();
        }
    }
}
