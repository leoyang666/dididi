package com.codingforhappy.formatchecker;

public class NullFormatChecker extends FormatChecker {
    @Override
    public boolean checkFormat(String s) {
        return s != null;
    }
}
