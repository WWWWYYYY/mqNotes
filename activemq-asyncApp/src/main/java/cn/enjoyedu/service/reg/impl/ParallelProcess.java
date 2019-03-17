package cn.enjoyedu.service.reg.impl;

import cn.enjoyedu.service.busi.SaveUser;
import cn.enjoyedu.service.busi.SendEmail;
import cn.enjoyedu.service.busi.SendSms;
import cn.enjoyedu.service.reg.IUserReg;
import cn.enjoyedu.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * <p>
 * 更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 * <p>
 * 类说明：并行的实现
 */
@Service
@Qualifier("para")
public class ParallelProcess implements IUserReg {

    @Autowired
    private SaveUser saveUser;
    @Autowired
    private SendEmail sendEmail;
    @Autowired
    private SendSms sendSms;

    private static class EmailProcess implements Runnable {

        private SendEmail sendEmail;
        private String email;

        public EmailProcess(SendEmail sendEmail, String email) {
            this.sendEmail = sendEmail;
            this.email = email;
        }

        public void run() {
            sendEmail.sendEmail(email);
            System.out.println("SendEmailThread send mail to " + email);
        }
    }

    private static class SmsProcess implements Runnable {

        private SendSms sendSms;
        private String phoneNumber;

        public SmsProcess(SendSms sendSms, String phoneNumber) {
            this.sendSms = sendSms;
            this.phoneNumber = phoneNumber;
        }

        public void run() {
            sendSms.sendSms(phoneNumber);
            System.out.println("SendSmsThread send sms to " + phoneNumber);
        }
    }

    public boolean userRegister(User user) {
        Thread sendEmailThread =
                new Thread(new EmailProcess(sendEmail, user.getEmail()));
        Thread sendSmsThread =
                new Thread(new SmsProcess(sendSms, user.getPhoneNumber()));
        try {
            saveUser.saveUser(user);
            new Thread(sendEmailThread).start();
            new Thread(sendSmsThread).start();
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }
}
