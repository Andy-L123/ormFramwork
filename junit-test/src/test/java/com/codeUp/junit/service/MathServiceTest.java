package com.codeUp.junit.service;

import org.junit.Assert;
import org.junit.Test;

public class MathServiceTest {

    @Test
    public void testSum(){
        MathService mathService = new MathService();
        int actual=mathService.add(1, 2);
        int expected=3;
        Assert.assertEquals(expected,actual) ;
    }
    @Test
    public void testMinus(){

    }
}
