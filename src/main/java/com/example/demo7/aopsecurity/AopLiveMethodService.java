package com.example.demo7.aopsecurity;

import org.springframework.stereotype.Service;

@Service
public class AopLiveMethodService {


    public void liveMethodSecured() {

        System.out.println("liveMethodSecured");
    }
}
