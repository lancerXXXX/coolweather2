package com.lancer.coolweeather2.android.db;

import org.litepal.crud.LitePalSupport;

public class cityWeatherString extends LitePalSupport {
    public String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
