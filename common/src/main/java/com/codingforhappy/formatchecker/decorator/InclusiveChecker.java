package com.codingforhappy.formatchecker.decorator;

import com.codingforhappy.formatchecker.FormatChecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 用于检查其中含有或不含有子串，如是否含有空格等
public class InclusiveChecker extends FormatCheckerDecorator{

    private String regex;

    public InclusiveChecker(FormatChecker checker, String regex) {
        super(checker);
        this.regex = regex;
    }

    @Override
    public boolean checkFormat(String s) {
        if (!checker.checkFormat(s)) return false;
        return checkFormat(s, regex);
    }

    public boolean checkFormat(String s, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    public void resetRegex(String regex){
        this.regex = regex;
    }
}
