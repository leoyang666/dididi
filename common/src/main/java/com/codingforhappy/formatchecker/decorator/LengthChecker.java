package com.codingforhappy.formatchecker.decorator;

import com.codingforhappy.formatchecker.FormatChecker;

public class LengthChecker extends FormatCheckerDecorator {

    private int shortest;

    private int longest;

    public LengthChecker(FormatChecker checker, int shortest, int longest) {
        super(checker);
        this.shortest = shortest;
        this.longest = longest;
    }

    @Override
    public boolean checkFormat(String s) {
        if (!checker.checkFormat(s)) return false;
        return checkFormat(s, shortest, longest);
    }

    public boolean checkFormat(String s, int shortest, int longest){
        int length = s.length();
        return (length >= shortest) && (length <= longest);
    }

    public void resetLength(int shortest, int longest){
        this.shortest = shortest;
        this.longest = longest;
    }

}
