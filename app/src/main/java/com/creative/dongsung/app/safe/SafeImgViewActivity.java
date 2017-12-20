package com.creative.dongsung.app.safe;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.creative.dongsung.app.R;
import com.creative.dongsung.app.menu.MainFragment;
import com.creative.dongsung.app.util.CustomBitmapPool;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SafeImgViewActivity extends AppCompatActivity {
    private static final String TAG = "SafeImgViewActivity";
    private String url;

    @Bind(R.id.imageView1) ImageView imgView;
    private String imgData;

    private ProgressDialog dialog;
    private AQuery aq = new AQuery( this );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safe_image_view);
        ButterKnife.bind(this);

        async_progress_dialog("imageView");

    }//onCreate

    public void async_progress_dialog(String callback){
        ProgressDialog dialog = ProgressDialog.show(SafeImgViewActivity.this, "", "Loading...", true, false);
        dialog.setInverseBackgroundForced(false);

        String large_cd= getIntent().getStringExtra("large_cd");
        String mid_cd= getIntent().getStringExtra("mid_cd");
        String chk_cd= getIntent().getStringExtra("chk_cd");

        url= MainFragment.ipAddress+ MainFragment.contextPath+"/rest/Safe/safeWriteTipImage/large_cd/"+large_cd+"/mid_cd/"+mid_cd+"/chk_cd/"+chk_cd;

        aq.progress(dialog).ajax(url, JSONObject.class, this, callback);
    }

    public void imageView(String url, JSONObject object, AjaxStatus status) throws JSONException {
        if( object != null) {
            try {
                JSONObject jsonObject= (JSONObject) object.get("datas");
                imgData= jsonObject.get("chk_img").toString();

                byte[] byteArray =  Base64.decode(imgData, Base64.DEFAULT);

                Glide.with(SafeImgViewActivity.this).load(byteArray)
                        .asBitmap()
                        .error(R.drawable.no_img)
                        .into(imgView);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"에러코드  SafeImg 1",Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d(TAG,"Data is Null");
        }
    }

    class ImageViewTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(SafeImgViewActivity.this, "", "Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... unsued) {
//            aq.id(imgView).image("http://119.202.60.144/img.asp?"+paramURL);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... unsued) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            if (dialog.isShowing()) dialog.dismiss();
            byte[] byteArray =  Base64.decode(imgData, Base64.DEFAULT) ;
            Glide.with(SafeImgViewActivity.this).load(byteArray)
                    .asBitmap()
                    .transform(new CropCircleTransformation(new CustomBitmapPool()))
                    .error(R.drawable.no_img)
//				.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(imgView);
        }
    }

//    final String url = "http://119.202.60.144/img.asp?"+paramURL;
//    String imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".png";
//    File storageDir = new File(Environment.getExternalStorageDirectory(), imageFileName);
//
//    @OnLongClick(R.id.imageView1) boolean onLongClick() {
//        aq.download( url, storageDir, new AjaxCallback<File>(){
//            @Override
//            public void callback(String url, File object, AjaxStatus status ) {
//                if( object != null)
//                    Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT ).show();
//                else
//                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT ).show();
//            }
//        });
//        return true;
//    }

}
