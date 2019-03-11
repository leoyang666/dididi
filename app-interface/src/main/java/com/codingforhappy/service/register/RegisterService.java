package com.codingforhappy.service.register;

import com.codingforhappy.dao.sql.UsersDao;
import com.codingforhappy.formatchecker.FormatChecker;
import com.codingforhappy.model.CheckableUser;
import com.codingforhappy.sms.service.SMSResponese;
import com.codingforhappy.sms.service.ShortMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class RegisterService {

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private ShortMessageService sms;

    @Autowired
    @Qualifier("passwordChecker")
    private FormatChecker passwordChecker;

    @Autowired
    @Qualifier("phoneNumChecker")
    private FormatChecker phoneNumChecker;

    protected abstract String getTable();

    public boolean checkDataFormat(CheckableUser user) {
        return passwordChecker.checkFormat(user.getPassword())
                && phoneNumChecker.checkFormat(user.getPhoneNum());
    }

    public boolean hasExisted(String phoneNum) {
        return usersDao.hasExisted(phoneNum, getTable());
    }

    public SMSResponese authSMSCode(String phoneNum, String code) {
        return sms.authenticateVerificationCode(phoneNum, code, ShortMessageService.VerificationCodeType.REGISTER);
    }

    @Transactional
    public boolean addUser(CheckableUser user) {
        boolean existed = usersDao.hasExisted(user.getPhoneNum(), getTable());
        if (existed)
            return false;
        usersDao.addUser(user, getTable());
        return true;
    }

//    public APIResponse register(CheckableUser user, String code) {
//        // 获取明文密码
//        if (!Base64.isBase64(user.getPassword()))
//            return new APIResponse(APIResponse.Event.WRONG_FORMAT, APIResponse.Message.WRONG_FORMAT);
//
//        byte[] base64DecodedPassword = Base64.decodeBase64(user.getPassword());
//        try {
//            PrivateKey privateKey = RSAUtils.getPrivateKey();
//            String decryptedPassword = new String(RSAUtils.decrypt(base64DecodedPassword, privateKey));
//            user.setPassword(decryptedPassword);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new APIResponse(APIResponse.Event.WRONG_FORMAT, APIResponse.Message.WRONG_FORMAT);
//        }
//
//        // 信息格式验证
//        if (!checkDataFormat(user))
//            return new APIResponse(APIResponse.Event.WRONG_FORMAT, APIResponse.Message.WRONG_FORMAT);
//
//        // 验证短信验证码
//        SMSResponese smsResponese = authSMSCode(user.getPhoneNum(),code);
//        if (smsResponese.getEvent().equals(SMSResponese.Event.VERIFICATION_FAILED))
//            return APIResponse.fromSMSResponse(smsResponese);
//
//        // 密码加盐
//        String salted = PasswordUtils.getSaltedPassword(user.getPassword());
//        user.setPassword(salted);
//
//        // 尝试存入数据库
//        if (addUser(user))
//            return new APIResponse(APIResponse.Event.OK, APIResponse.Message.OK);
//        else
//            return new APIResponse(APIResponse.Event.USER_HAS_EXISTED, APIResponse.Message.USER_HAS_EXISTED);
//    }
}
