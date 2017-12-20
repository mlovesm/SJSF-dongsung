package com.creative.dongsung.app.adaptor;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.creative.dongsung.app.R;
import com.creative.dongsung.app.util.CustomBitmapPool;
import com.creative.dongsung.app.util.UtilClass;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class PersonnelAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private ArrayList<HashMap<String,String>> peopleList;
	private ViewHolder viewHolder;
	private Context con;


	public PersonnelAdapter(Context con , ArrayList<HashMap<String,String>> array){
		inflater = LayoutInflater.from(con);
		peopleList = array;
		this.con = con;
	}

	@Override
	public int getCount() {
		return peopleList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, final View convertview, ViewGroup parent) {

		View v = convertview;

		if(v == null){
			viewHolder = new ViewHolder();

			v = inflater.inflate(R.layout.people_list_item, parent,false);
			viewHolder.people_image = (ImageView) v.findViewById(R.id.imageView1);
			viewHolder.people_name = (TextView)v.findViewById(R.id.textView1);
			viewHolder.data2 = (TextView)v.findViewById(R.id.textView2);
			viewHolder.data3 = (TextView)v.findViewById(R.id.textView3);
			viewHolder.data4 = (TextView)v.findViewById(R.id.textView4);
			viewHolder.data5 = (TextView)v.findViewById(R.id.textView5);
			viewHolder.data6 = (TextView)v.findViewById(R.id.textView6);
			viewHolder.data7 = (TextView)v.findViewById(R.id.textView7);
			viewHolder.data8 = (TextView)v.findViewById(R.id.textView8);

			v.setTag(viewHolder);

		}else {
			viewHolder = (ViewHolder)v.getTag();
		}
		UtilClass.dataNullCheckZero(peopleList.get(position));
		byte[] byteArray =  Base64.decode(peopleList.get(position).get("user_pic").toString(), Base64.DEFAULT) ;
//		Bitmap bmp1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		Glide.with(con).load(byteArray)
				.asBitmap()
				.transform(new CropCircleTransformation(new CustomBitmapPool()))
				.error(R.drawable.no_img)
//				.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
				.into(viewHolder.people_image);

		viewHolder.people_name.setText(peopleList.get(position).get("user_nm").toString());
		String user_cell= peopleList.get(position).get("user_cell").toString();

			String work_nm= peopleList.get(position).get("dept_nm2").toString().trim();
			if(work_nm.equals("")||work_nm==null||work_nm.equals(".")){
				work_nm="";
			}else{
				work_nm= "("+ work_nm +")";
			}
			viewHolder.data3.setText(peopleList.get(position).get("user_no").toString());

		viewHolder.data2.setText(peopleList.get(position).get("j_pos").toString());

		viewHolder.data4.setText(peopleList.get(position).get("dept_nm1").toString().trim()+work_nm);
		viewHolder.data5.setText(peopleList.get(position).get("work_nm").toString());
		viewHolder.data6.setText(user_cell);
		viewHolder.data7.setText(peopleList.get(position).get("user_email").toString());
		viewHolder.data8.setText(peopleList.get(position).get("user_addr").toString());

		return v;
	}


	public void setArrayList(ArrayList<HashMap<String,String>> arrays){
		this.peopleList = arrays;
	}

	public ArrayList<HashMap<String,String>> getArrayList(){
		return peopleList;
	}


	/*
	 * ViewHolder
	 */
	class ViewHolder{
		ImageView people_image;
		TextView people_name;
		TextView data2;
		TextView data3;
		TextView data4;
		TextView data5;
		TextView data6;
		TextView data7;
		TextView data8;

	}


}







