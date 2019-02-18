package com.lancer.coolweeather2.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lancer.coolweeather2.android.gson.Weatherr;
import com.lancer.coolweeather2.android.service.AutoUpdataService;
import com.lancer.coolweeather2.android.util.HttpUtil;
import com.lancer.coolweeather2.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView titleCity;
    private TextView titleUpdataTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView apiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private NestedScrollView weatherLayout;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private Button myButton;
    private TextView comfortSuggestion;
    private TextView carWashSuggestion;
    private TextView sportSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        initView();
        swipeRefresh.setColorSchemeResources(R.color.balack);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weatherr weatherr = Utility.handleWeatherResponse(weatherString);
            weatherId = weatherr.getBasic().getCid();
            showWeatherInfo(weatherr);
        } else {
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherString = prefs.getString("weather", null);
                Weatherr weatherr = Utility.handleWeatherResponse(weatherString);
                String qqq = weatherr.getBasic().getCid();
                requestWeather(qqq);
            }
        });

    }

    /**
     * 根据天气id请求天气信息
     */
    public String requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.net/s6/weather?key=e297a7e1bfa441b18c2776f196c89d58&location=" + weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weatherr weatherr = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherr != null && "ok".equals(weatherr.getStatus())) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weatherr);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        return weatherId;
    }
    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weatherr weatherr) {
        if (weatherr != null && "ok".equals(weatherr.getStatus())) {
            String cityName = weatherr.getBasic().getLocation();
            String updataTime = weatherr.getUpdate().getLoc().split(" ")[1];
            String degree = weatherr.getNow().getTmp();
            String weatherInfo = weatherr.getNow().getCond_txt();
            Log.e("city", weatherInfo);
            titleCity.setText(cityName);
            titleUpdataTime.setText(updataTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
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
            comfortText.setText("舒 适 度: " + weatherr.getLifestyle().get(0).getBrf());
            comfortSuggestion.setText(weatherr.getLifestyle().get(0).getTxt());
            carWashText.setText("洗车指数：" + weatherr.getLifestyle().get(6).getBrf());
            carWashSuggestion.setText(weatherr.getLifestyle().get(6).getTxt());
            sportText.setText(  "运动建议：" + weatherr.getLifestyle().get(3).getBrf());
            sportSuggestion.setText(weatherr.getLifestyle().get(3).getTxt()+'\n');
            weatherLayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, AutoUpdataService.class);
            startService(intent);
        } else {
            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdataTime = (TextView) findViewById(R.id.title_updata_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        apiText = (TextView) findViewById(R.id.api_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        weatherLayout = (NestedScrollView) findViewById(R.id.weather_layout);
        //bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        //swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //navButton = (Button) findViewById(R.id.nav_button);
        comfortSuggestion = (TextView) findViewById(R.id.comfort_suggestion);
        carWashSuggestion = (TextView) findViewById(R.id.car_wash_suggestion);
        sportSuggestion = (TextView) findViewById(R.id.sport_suggestion);
        //myButton=(Button)findViewById(R.id.my_button);
    }

}








