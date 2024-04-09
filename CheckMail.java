package com.example.emotiondetection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class CheckMail {
    public static boolean checkMail(String mailid){
        boolean flag=false;
        //regular expression to check format and allowable characters in mail id
        String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
        Pattern p = Pattern.compile(regEx);//compile regular expression for validity
        Matcher m = p.matcher(mailid);//check mail with regular expression
        if(m.find())//if match found return true
            flag=true;
        else
            flag=false;
        return flag;
    }
}
