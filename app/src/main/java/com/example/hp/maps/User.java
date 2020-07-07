package com.example.hp.maps;

import java.util.Map;

public class User extends MapActivity {
    String string;
    Map hm ;
    User(String string){
        this.string = string;
        MapActivity mapActivity = new MapActivity();
        this.hm = mapActivity.hm1;
    }

    public Map get() {
        hm = hm1;
        return hm;
    }

}
