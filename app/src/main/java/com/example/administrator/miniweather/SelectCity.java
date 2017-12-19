package com.example.administrator.miniweather;
import com.example.administrator.app.MyApplication;
import com.example.administrator.bean.City;
import com.example.administrator.app.ListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ListView listCity;
    private ImageView mBackBtn;
    private MyApplication mApplication;



    private List<City> cList;
    private List<City> newlist = new ArrayList<City>();      //显示搜索后数据
    private ListAdapter adapter;
    private TextView currentCityNameTv;
    private EditText mEditText;
    private String cityName="北京";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        init();

        Intent intent = getIntent();
        String currentCityName = intent.getStringExtra("cityName");
        if(currentCityName==null){
            currentCityName = "";
        }
        currentCityNameTv.setText(getResources().getString(R.string.current_city_name)+currentCityName);

        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
            cityName=cList.get(position).getCity();
                Log.d("城市名称",cityName);
            String cityNumber = getNumFromName(cityName);      //根据cityName获取城市number
            String number_name = cityNumber+":"+cityName;
            Intent i = new Intent();
            i.putExtra("cityCode", cityNumber);
            setResult(RESULT_OK, i);
            finish();
        }
    });

        listCity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            final View view1 = view;
            new AlertDialog.Builder(SelectCity.this)
                    .setTitle("")          //添加为默认城市
                    .setMessage("添加为默认城市?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                                    int defaultCityCount = sharedPreferences.getInt("defaultCityCount", -1);     //获取当前城市的个数

                                    TextView text = (TextView) view1.findViewById(R.id.tvData);
                                    cityName = (String) text.getText();

                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    editor.putString("defaultCity_"+(defaultCityCount+1),cityName);
                                    editor.putInt("defaultCityCount", defaultCityCount + 1);     //默认城市个数加1
                                    editor.commit();        //提交当前数据
                                    Toast.makeText(SelectCity.this, "添加成功", Toast.LENGTH_SHORT).show();

                                    ArrayList arr = new ArrayList();
                                    int a = sharedPreferences.getInt("defaultCityCount", -1);
                                    if(a!=-1) {
                                        for (int j = 0; j <= defaultCityCount; j++) {
                                            String defaultCity = sharedPreferences.getString("defaultCity_" + j, "");
                                            arr.add(defaultCity);
                                        }
                                    }

                                    Log.d("defaultCityCount",defaultCityCount+"");
                                    Log.d("defaultCityCount List",arr.toString());

                                }
                            }).setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    //Toast.makeText(SelectCity.this, "已取消", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
            return false;
        }
    });
    }

    //根据城市名称，获取城市编号
    private String getNumFromName(String cityname) {
        for (int i = 0; i < cList.size(); i++) {
            City city = cList.get(i);
            if (city.getCity() == cityname) {
                return city.getNumber();
            }
        }
        return "";
    }

    private void init(){

        currentCityNameTv = (TextView)findViewById(R.id.title_name);
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mEditText = (EditText)findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher);

        mApplication = (MyApplication)getApplication();
        listCity = (ListView) findViewById(R.id.city_list);
        cList =  mApplication.getCityList();      //获取城市列表
        Log.d("size...:", cList.size()+"");
        adapter = new ListAdapter(this, cList);
        listCity.setAdapter(adapter);      //为ListView设置Adapter来绑定数据

    }

    //当Editetext变化时调用的方法，来判断所输入是否包含在所属数据中
    private List<City> getNewData(String input_info) {
        String upCase = input_info.toUpperCase();
        for (int i = 0; i < cList.size(); i++) {
            City city = cList.get(i);
            //汉字检测
            if (city.getCity().contains(input_info)) {
                //将遍历到的元素重新组成一个list
                City city2 = new City();
                city2.setCity(city.getCity());
                city2.setAllFristPY(city.getAllFristPY());
                city2.setAllPY(city.getAllPY());
                city2.setFirstPY(city.getFirstPY());
                city2.setNumber(city.getNumber());
                city2.setProvince(city.getProvince());
                if(newlist.contains(city2) == false)
                    newlist.add(city2);
            }else{
                //手拼音检测
                if(city.getAllFristPY().startsWith(upCase) == true){
                    City city2 = new City();
                    city2.setCity(city.getCity());
                    city2.setAllFristPY(city.getAllFristPY());
                    city2.setAllPY(city.getAllPY());
                    city2.setFirstPY(city.getFirstPY());
                    city2.setNumber(city.getNumber());
                    city2.setProvince(city.getProvince());
                    if(newlist.contains(city2) == false)
                        newlist.add(city2);
                }
            }
        }
        //全拼检测
        for (int i = 0; i < cList.size(); i++) {
            City city = cList.get(i);
            if(city.getAllPY().startsWith(upCase) == true){
                City city2 = new City();
                city2.setCity(city.getCity());
                city2.setAllFristPY(city.getAllFristPY());
                city2.setAllPY(city.getAllPY());
                city2.setFirstPY(city.getFirstPY());
                city2.setNumber(city.getNumber());
                city2.setProvince(city.getProvince());
                if(newlist.contains(city2) == false)
                    newlist.add(city2);
            }
        }
        return newlist;
    }

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            newlist.clear();
            if (mEditText.getText() != null) {
                String input_info = mEditText.getText().toString();  //获取 “编辑框” 的内容
                Log.d("EditText Change", input_info);
                //根据编辑框的内容，getNewData()获取新的newlist
                newlist = getNewData(input_info);
                //重新绑定adapter
                adapter = new ListAdapter(SelectCity.this, newlist);
                listCity.setAdapter(adapter);
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

   //点击“返回”键，关闭当前Activity
    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.title_back){
            finish();
        }
    }
}



