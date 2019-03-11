package com.codingforhappy.formatchecker.decorator;

import com.codingforhappy.formatchecker.FormatChecker;

public class ExclusiveChecker extends InclusiveChecker {

    public ExclusiveChecker(FormatChecker checker, String regex) {
        super(checker, regex);
    }

    @Override
    public boolean checkFormat(String s) {
        return !super.checkFormat(s);
    }

    @Override
    public boolean checkFormat(String s, String regex) {
        return !super.checkFormat(s, regex);
    }
}
