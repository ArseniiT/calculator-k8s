package com.gretadevops.calculatord;
import org.springframework.stereotype.Service;

@Service
public class Calculator {
    // sum method with two integer parameters
    int sum(int a, int b) {
        return a + b;
    }
}
