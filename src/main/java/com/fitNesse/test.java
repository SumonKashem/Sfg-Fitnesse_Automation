package com.fitNesse;

public class test {

    public static void main(String[] args){

        value(0,"a","b");

    }

    public static void value(int count, String... values){
        System.out.println("value is " + values[count].toString());

    }
}
