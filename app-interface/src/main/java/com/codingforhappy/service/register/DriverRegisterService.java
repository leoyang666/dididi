package com.codingforhappy.service.register;

import org.springframework.stereotype.Service;

@Service("driverRegisterService")
public class DriverRegisterService extends RegisterService {
    @Override
    protected String getTable() {
        return "drivers";
    }
}
