package com.example.demo7.aopsecurity;

import org.springframework.stereotype.Service;

@Service
public class AopMethodService {


    public void methodSecured() {
        System.out.println("methodSecured");
    }
}
