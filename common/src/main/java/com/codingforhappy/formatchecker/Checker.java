package com.codingforhappy.formatchecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 为了节省空间，希望只有一个 Checker 对象，但这样的话并发时，因为 result 会被
// 许多格式检验所修改，会出问题。
public class Checker {

    private boolean result = true;

    private String s;

    public boolean getResult(){
        return result;
    }

    public Checker setString(String s){
        this.s = s;
        return this;
    }

    public Checker notNull(){
        result = result && (s != null);
        return this;
    }

    public Checker length(int shortest, int longest){
        int length = s.length();
        result = result && length >= shortest && length <= longest;
        return this;
    }

    public Checker inclusive(String regex){
        result = result && match(regex);
        return this;
    }

    public Checker exclusive(String regex){
        result = result && !match(regex);
        return this;
    }

    public boolean match(String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
}
