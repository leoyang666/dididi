package com.codingforhappy.service.register;

import org.springframework.stereotype.Service;

@Service("passengerRegisterService")
public class PassengerRegisterService extends RegisterService {
    @Override
    protected String getTable() {
        return "passengers";
    }
}
