package com.codingforhappy.config;

import com.codingforhappy.formatchecker.FormatChecker;
import com.codingforhappy.formatchecker.NullFormatChecker;
import com.codingforhappy.formatchecker.decorator.InclusiveChecker;
import com.codingforhappy.formatchecker.decorator.LengthChecker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class FormatCheckerConfig {

    public static void main(String[] args) {
        FormatChecker lengthChecker = new LengthChecker(new NullFormatChecker(), 9, 15);
        FormatChecker checker = new InclusiveChecker(lengthChecker, "^[A-Za-z0-9]*$");

        Scanner sc = new Scanner(System.in);
        String current = sc.next();
        while (true) {
            System.out.println(current + ":" + checker.checkFormat(current));
            current = sc.next();
        }
    }

    @Bean
    public NullFormatChecker initNullFormatChecker() {
        return new NullFormatChecker();
    }

    @Bean
    @Qualifier("passwordChecker")
    public FormatChecker initPasswordChecker(NullFormatChecker nullFormatChecker) {
        FormatChecker lengthChecker = new LengthChecker(nullFormatChecker, 9, 15);
        return new InclusiveChecker(lengthChecker, "^[A-Za-z0-9]*$");
    }

    @Bean
    @Qualifier("phoneNumChecker")
    public FormatChecker initPhoneNumChecker(NullFormatChecker nullFormatChecker) {
        FormatChecker lengthChecker = new LengthChecker(nullFormatChecker, 11, 11);
        return new InclusiveChecker(lengthChecker, "^[0-9]*$");
    }
}
