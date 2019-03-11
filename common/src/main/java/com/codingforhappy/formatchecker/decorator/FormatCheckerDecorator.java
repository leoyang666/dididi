package com.codingforhappy.formatchecker.decorator;

import com.codingforhappy.formatchecker.FormatChecker;

public abstract class FormatCheckerDecorator extends FormatChecker {

    protected FormatChecker checker;

    public FormatCheckerDecorator(FormatChecker checker) {
        this.checker = checker;
    }

}
