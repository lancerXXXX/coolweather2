package com.lancer.coolweeather2.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lancer.coolweeather2.android.db.cityWeatherString;
import com.lancer.coolweeather2.android.gson.Weatherr;
import com.lancer.coolweeather2.android.util.HttpUtil;
import com.lancer.coolweeather2.android.util.ItemTouchHelperAdapter;
import com.lancer.coolweeather2.android.util.MyRecyclerViewAdapter;
import com.lancer.coolweeather2.android.util.MyScrollView;
import com.lancer.coolweeather2.android.util.MypagerAdapter;
import com.lancer.coolweeather2.android.util.RecyclerViewItemListener;
import com.lancer.coolweeather2.android.util.Utility;
import com.lancer.coolweeather2.android.util.VpSwipeRefreshLayout;
import com.lancer.coolweeather2.android.util.myItemTouchHelperCallBack;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Weather2 extends AppCompatActivity implements View.OnClickListener {

    public ViewPager weatherViewpager;
    public List<View> viewList;
    public List<String> cityNameList;
    public List<cityWeatherString> cityWeaherStringList;
    public MypagerAdapter pagerAdapter;
    public MyRecyclerViewAdapter myRecyclerViewAdapter;
    private TextView title_city;
    private Button cityChoose;
    private VpSwipeRefreshLayout swipeRefresh;
    private int height;
    private RelativeLayout mrlay;
    private DrawerLayout drawerLayout;
    private RecyclerView myRecyclerView;
    private RelativeLayout title_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather2);
        initView();
        height = getAndroiodScreenProperty();

        //初始化
        viewList = new ArrayList<View>();
        cityNameList = new ArrayList<String>();
        cityWeaherStringList = new ArrayList<cityWeatherString>();

        //ViewPager适配
        pagerAdapter = new MypagerAdapter(viewList);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.main_weather, null);
        viewList.add(view);
        weatherViewpager.setAdapter(pagerAdapter);

        //RecyclerView适配
        myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myRecyclerView.setLayoutManager(layout);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(cityNameList,cityWeaherStringList,viewList);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerViewAdapter.setMrecyclerViewItemListener(new RecyclerViewItemListener() {
            @Override
            public void onClick(View view, int position) {
                weatherViewpager.setCurrentItem(position+1);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        //CardView回调
        ItemTouchHelper.Callback callback=new myItemTouchHelperCallBack(new ItemTouchHelperAdapter() {
            @Override
            public void onItemMove(int fromPostion, int toPosition) {
                //交换位置
                for (View view1:viewList){
                    Log.e("flag",view1.toString());
                }
                Log.e("flag","                                   ");
                Collections.swap(myRecyclerViewAdapter.viewList,fromPostion+1,toPosition+1);
                for (View view1:viewList){
                    Log.e("flag",view1.toString());
                }
                Collections.swap(myRecyclerViewAdapter.cityWeatherStrings,fromPostion,toPosition);
                Collections.swap(myRecyclerViewAdapter.data,fromPostion,toPosition);
                myRecyclerViewAdapter.notifyItemMoved(fromPostion,toPosition);
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemDelete(int position) {
                myRecyclerViewAdapter.viewList.remove(position+1);
                myRecyclerViewAdapter.cityWeatherStrings.remove(position);
                myRecyclerViewAdapter.data.remove(position);
                myRecyclerViewAdapter.notifyItemRemoved(position);
                pagerAdapter.notifyDataSetChanged();
            }
        });
        ItemTouchHelper touchHelper=new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(myRecyclerView);


        //读取缓存
        List<cityWeatherString> Temp_weatherList = LitePal.findAll(cityWeatherString.class);
        for (int i = 0; i < Temp_weatherList.size(); i++) {
            cityWeaherStringList.add(Temp_weatherList.get(i));
            View view1 = inflater.inflate(R.layout.activity_weather, null);
            viewList.add(view1);
            Weatherr www = Utility.handleWeatherResponse(cityWeaherStringList.get(i).getResponse());
            cityNameList.add(www.getBasic().getLocation());
            showWeatherInfo(view1, www);
            pagerAdapter.notifyDataSetChanged();
            myRecyclerViewAdapter.notifyDataSetChanged();
        }

        for (int i=0;i<cityWeaherStringList.size();i++){
            View view1=viewList.get(i+1);
            Weatherr www=Utility.handleWeatherResponse(cityWeaherStringList.get(i).getResponse());
            refresh(view1,www.getBasic().getCid());
            pagerAdapter.notifyDataSetChanged();
        }


        if (Temp_weatherList.size() == 0) {
            weatherViewpager.setCurrentItem(0);
            myRecyclerView.setVisibility(View.VISIBLE);
            //Log.e("flag","huancun==0");
        } else {
            myRecyclerView.setVisibility(View.INVISIBLE);
            cityChoose.setVisibility(View.INVISIBLE);
            title_city = (TextView) findViewById(R.id.title_city);
            title_city.setText(cityNameList.get(0).toString());
            weatherViewpager.setCurrentItem(1);
            //Log.e("flag","huancun>0");
        }


        //城市选择按钮监听
        cityChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

            }
        });


        //ViewPager监听
        weatherViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                weatherViewpager.setTag(i);
                title_city = (TextView) findViewById(R.id.title_city);
                View titleView = inflater.inflate(R.layout.title, null);
                if (i == 0) {
                    title_city.setText("");
                    cityChoose.setVisibility(View.VISIBLE);
                    myRecyclerView.setVisibility(View.VISIBLE);

                } else {
                    title_city.setText(cityNameList.get(i - 1));
                    cityChoose.setVisibility(View.INVISIBLE);
                    myRecyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    //布局初始化
    private void initView() {
        weatherViewpager = (ViewPager) findViewById(R.id.weather_view_page);
        cityChoose = (Button) findViewById(R.id.city_choose);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);

        title_layout = (RelativeLayout) findViewById(R.id.title_layout);
    }












    /**
     * 根据天气id请求天气信息
     */
    public String requestWeather(final View v, final String weatherId) {
        String weatherUrl = "https://free-api.heweather.net/s6/weather?key=e297a7e1bfa441b18c2776f196c89d58&location=" + weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Weather2.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final cityWeatherString temp = new cityWeatherString();
                temp.setResponse(responseText);
                final Weatherr weatherr = Utility.handleWeatherResponse(responseText);
                cityNameList.add(weatherr.getBasic().getLocation());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myRecyclerViewAdapter.notifyDataSetChanged();
                        if (weatherr != null && "ok".equals(weatherr.getStatus())) {
                            int flag = 0;
                            int line = 0;
                            for (cityWeatherString wea : cityWeaherStringList) {
                                Weatherr ww = Utility.handleWeatherResponse(wea.getResponse());
                                if (ww.getBasic().getCid().toString().equals(weatherr.getBasic().getCid().toString())) {
                                    flag = 1;
                                    break;
                                }
                                line++;
                            }
                            if (flag == 1) {
                                cityWeaherStringList.add(temp);
                                Toast.makeText(Weather2.this,"已经添加过该城市",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Weather2.this,"添加该城市",Toast.LENGTH_SHORT).show();
                                cityWeaherStringList.add(temp);
                            }
                            showWeatherInfo(v, weatherr);

                        } else {
                            Toast.makeText(Weather2.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        return weatherId;
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(View v, Weatherr weatherr) {
        TextView titleUpdataTime = (TextView) v.findViewById(R.id.title_updata_time);
        TextView degreeText = (TextView) v.findViewById(R.id.degree_text);
        TextView weatherInfoText = (TextView) v.findViewById(R.id.weather_info_text);
        LinearLayout forecastLayout = (LinearLayout) v.findViewById(R.id.forecast_layout);
        LinearLayout buyzhidao = (LinearLayout) v.findViewById(R.id.LinearLayout);
        RelativeLayout nowLayout = (RelativeLayout) v.findViewById(R.id.Ralativelaout);
        TextView apiText = (TextView) v.findViewById(R.id.api_text);
        TextView pm25Text = (TextView) v.findViewById(R.id.pm25_text);
        TextView comfortText = (TextView) v.findViewById(R.id.comfort_text);
        TextView carWashText = (TextView) v.findViewById(R.id.car_wash_text);
        TextView sportText = (TextView) v.findViewById(R.id.sport_text);
        MyScrollView weatherLayout = (MyScrollView) v.findViewById(R.id.weather_layout);
        TextView comfortSuggestion = (TextView) v.findViewById(R.id.comfort_suggestion);
        TextView carWashSuggestion = (TextView) v.findViewById(R.id.car_wash_suggestion);
        TextView sportSuggestion = (TextView) v.findViewById(R.id.sport_suggestion);
        if (weatherr != null && "ok".equals(weatherr.getStatus())) {
            String updataTime = weatherr.getUpdate().getLoc().split(" ")[1];
            String degree = weatherr.getNow().getTmp();
            String weatherInfo = weatherr.getNow().getCond_txt();
            degreeText.setText(degree);
            titleUpdataTime.setText("更新时间：" + updataTime + " ");
            weatherInfoText.setText(weatherInfo + " ");
            forecastLayout.removeAllViews();
            for (Weatherr.DailyForecastBean forecast : weatherr.getDaily_forecast()) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dataText = view.findViewById(R.id.data_text);
                TextView infoText = view.findViewById(R.id.info_text);
                TextView maxText = view.findViewById(R.id.max_text);
                TextView minText = view.findViewById(R.id.min_text);
                dataText.setText(forecast.getDate());
                infoText.setText(forecast.getCond_txt_d());
                maxText.setText(forecast.getTmp_max());
                minText.setText(forecast.getTmp_min());
                forecastLayout.addView(view);
            }
            //////////之后添加空气质量&建议
            //apiText.setText(weatherr.getBasic().getLocation());
            comfortText.setText("舒 适 度: " + weatherr.getLifestyle().get(0).getBrf());
            comfortSuggestion.setText(weatherr.getLifestyle().get(0).getTxt());
            carWashText.setText("洗车指数：" + weatherr.getLifestyle().get(6).getBrf());
            carWashSuggestion.setText(weatherr.getLifestyle().get(6).getTxt());
            sportText.setText("运动建议：" + weatherr.getLifestyle().get(3).getBrf());
            sportSuggestion.setText(weatherr.getLifestyle().get(3).getTxt() + '\n');
            weatherLayout.setVisibility(View.VISIBLE);
            buyzhidao = (LinearLayout) v.findViewById(R.id.LinearLayout);
            RelativeLayout.LayoutParams params_1 = (RelativeLayout.LayoutParams) buyzhidao.getLayoutParams();
            params_1.height = height;
            buyzhidao.setLayoutParams(params_1);
        } else {
            Toast.makeText(Weather2.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
    }
    public String refresh(final View v, final String weatherId) {
        String weatherUrl = "https://free-api.heweather.net/s6/weather?key=e297a7e1bfa441b18c2776f196c89d58&location=" + weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Weather2.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final cityWeatherString temp = new cityWeatherString();
                temp.setResponse(responseText);
                final Weatherr weatherr = Utility.handleWeatherResponse(responseText);
                //cityNameList.add(weatherr.getBasic().getLocation());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myRecyclerViewAdapter.notifyDataSetChanged();
                        if (weatherr != null && "ok".equals(weatherr.getStatus())) {
                            int flag = 0;
                            int line = 0;
                            for (cityWeatherString wea : cityWeaherStringList) {
                                Weatherr ww = Utility.handleWeatherResponse(wea.getResponse());
                                if (ww.getBasic().getCid().toString().equals(weatherr.getBasic().getCid().toString())) {
                                    flag = 1;
                                    break;
                                }
                                line++;
                            }
                            if (flag == 1) {
                                //cityWeaherStringList.add(temp);
                                Toast.makeText(Weather2.this,"刷新城市天气",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Weather2.this,"刷新城市天气",Toast.LENGTH_SHORT).show();
                                //cityWeaherStringList.add(temp);
                            }
                            showWeatherInfo(v, weatherr);

                        } else {
                            Toast.makeText(Weather2.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        return weatherId;
    }

    /*
    获取屏幕高度返回
     */
    public int getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;// 屏幕宽度（像素）
        float density = dm.density;// 屏幕密度（0.75 / 1.0 / 1.5）
        int screenHeight = (int) (height / density);
        int statusBarHeight1 = 0;//获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        return height - statusBarHeight1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_choose:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int original =LitePal.findAll(cityWeatherString.class).size();
        Log.e("change","yuanlai "+original);
        int now=cityWeaherStringList.size();
        Log.e("change","now"+now);
        if (now<=original){
            int i=0;
            for(;i<now;i++){
                cityWeatherString temp=cityWeaherStringList.get(i);
                temp.update(i+1);
                Log.e("change","update 1 "+temp.getResponse().substring(75,90));
            }
            int delete=i+1;
            if (delete<original){
                for (;i<original;i++){
                    LitePal.delete(cityWeatherString.class,delete);
                }
            }
        }
        else {
            int i=0;
            for (;i<original;i++){
                cityWeatherString temp=cityWeaherStringList.get(i);
                temp.update(i+1);
                Log.e("change","update 2 "+temp.getResponse().substring(75,90));
            }
            for (;i<now;i++){
                cityWeatherString temp=cityWeaherStringList.get(i);
                temp.save();
                Log.e("change","save 2 "+temp.getResponse().substring(75,90));
            }
        }
    }
}
