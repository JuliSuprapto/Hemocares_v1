package com.example.hemocare_v1.server;

public class BaseURL {

//    public static String baseUrl = "http://192.168.18.7:5050/";
    public static String baseUrl = "http://192.168.18.253:5050/";
    //public static String baseUrl = "http://192.168.43.81:5050/";
    public static String login = baseUrl + "access/login";
    public static String register = baseUrl + "access/registrasi";
    public static String showUser = baseUrl + "access/getdataUser";
    public static String completeUser = baseUrl + "access/completeData/";
    public static String updateUser = baseUrl + "access/updateUser/";

}
