package com.example.hp.maps;

import android.app.Application;

public class Keeplogin extends Application {

    public static int someVariable;
    public Keeplogin() {
        super();
        }



    public int getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(int someVariable) {
        this.someVariable = someVariable;
    }
}