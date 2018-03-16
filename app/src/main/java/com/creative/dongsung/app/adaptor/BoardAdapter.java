package com.creative.dongsung.app.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creative.dongsung.app.R;

import java.util.ArrayList;
import java.util.HashMap;


public class BoardAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private ArrayList<HashMap<String,String>> boardList;
	private ViewHolder viewHolder;
	private Context con;
	private String name="";


	public BoardAdapter(Context con , ArrayList<HashMap<String,String>> array){
		inflater = LayoutInflater.from(con);
		boardList = array;
		this.con = con;
	}

	public BoardAdapter(Context con , ArrayList<HashMap<String,String>> array, String name){
		inflater = LayoutInflater.from(con);
		boardList = array;
		this.con = con;
		this.name = name;
	}

	@Override
	public int getCount() {
		return boardList.size();
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

			if(name.equals("NoticeBoard")){
				v = inflater.inflate(R.layout.basic_list_item, parent,false);
				viewHolder.board_data1 = (TextView)v.findViewById(R.id.textView1);
				viewHolder.board_data3 = (TextView)v.findViewById(R.id.textView3);
				viewHolder.board_data4 = (TextView)v.findViewById(R.id.textView4);

			}else if(name.equals("Workers")){
				v = inflater.inflate(R.layout.workers_list_item, parent,false);
				viewHolder.board_data1 = (TextView)v.findViewById(R.id.textView1);
				viewHolder.board_data2 = (TextView)v.findViewById(R.id.textView2);
				viewHolder.board_data3 = (TextView)v.findViewById(R.id.textView3);

			}else if(name.equals("Monthly")){
				v = inflater.inflate(R.layout.monthly_list_item, parent,false);
				viewHolder.board_data1 = (TextView)v.findViewById(R.id.textView1);
				viewHolder.board_data2 = (TextView)v.findViewById(R.id.textView2);
				viewHolder.board_data3 = (TextView)v.findViewById(R.id.textView3);
				viewHolder.board_data4 = (TextView)v.findViewById(R.id.textView4);
				viewHolder.board_data5 = (TextView)v.findViewById(R.id.textView5);

			}else{
				v = inflater.inflate(R.layout.basic_list_item2, parent,false);
				viewHolder.board_data1 = (TextView)v.findViewById(R.id.textView1);
				viewHolder.board_data2 = (TextView)v.findViewById(R.id.textView2);
				viewHolder.board_data3 = (TextView)v.findViewById(R.id.textView3);
				viewHolder.board_data4 = (TextView)v.findViewById(R.id.textView4);
			}

			v.setTag(viewHolder);

		}else {
			viewHolder = (ViewHolder)v.getTag();
		}

		try {
			if(name.equals("NoticeBoard")){
				viewHolder.board_data1.setText(boardList.get(position).get("data1").toString());
				viewHolder.board_data3.setText(boardList.get(position).get("data3").toString());
				viewHolder.board_data4.setText(boardList.get(position).get("data4").toString());

			}else if(name.equals("Workers")){
				viewHolder.board_data1.setText(boardList.get(position).get("data1").toString());
				viewHolder.board_data2.setText(boardList.get(position).get("data2").toString());
				viewHolder.board_data3.setText(boardList.get(position).get("data3").toString());

			}else if(name.equals("Monthly")){
				viewHolder.board_data1.setText(boardList.get(position).get("data1").toString());
				viewHolder.board_data2.setText(boardList.get(position).get("data2").toString());
				viewHolder.board_data3.setText(boardList.get(position).get("data3").toString());
				viewHolder.board_data4.setText(boardList.get(position).get("data4").toString());
				viewHolder.board_data5.setText(boardList.get(position).get("data5").toString());

			}else{
				viewHolder.board_data1.setText(boardList.get(position).get("data1").toString());
				viewHolder.board_data2.setText(boardList.get(position).get("data2").toString());
				viewHolder.board_data3.setText(boardList.get(position).get("data3").toString());
				viewHolder.board_data4.setText(boardList.get(position).get("data4").toString());
			}
		}catch (NullPointerException e){
			e.printStackTrace();

		}

		return v;
	}

	
	public void setArrayList(ArrayList<HashMap<String,String>> arrays){
		this.boardList = arrays;
	}
	
	public ArrayList<HashMap<String,String>> getArrayList(){
		return boardList;
	}
	
	
	/*
	 * ViewHolder
	 */
	class ViewHolder{
		TextView board_data1;
		TextView board_data2;
		TextView board_data3;
		TextView board_data4;
		TextView board_data5;
	}
	

}







