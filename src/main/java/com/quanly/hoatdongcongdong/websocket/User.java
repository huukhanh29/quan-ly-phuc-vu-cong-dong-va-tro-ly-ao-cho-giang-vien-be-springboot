package com.quanly.hoatdongcongdong.websocket;


import java.security.Principal;
import java.util.ArrayList;

public class User implements Principal {

    private String name;

    public User(String name) {
        this.name = name;
    }


    @Override
    public String getName() {
        return name;
    }

}