package com.ahamo.auth.service;

import com.ahamo.user.model.User;
import com.ahamo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MfaService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;
    private final UserService userService;

    public MfaService(@Qualifier("stringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                      JavaMailSender mailSender,
                      UserService userService) {
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
        this.userService = userService;
    }

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number:}")
    private String twilioPhoneNumber;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private static final String EMAIL_CODE_PREFIX = "email_code:";
    private static final String SMS_CODE_PREFIX = "sms_code:";
    private static final int CODE_EXPIRY_MINUTES = 10;
    private static final int CODE_LENGTH = 6;

    public String generateAndSendEmailVerification(String email) {
        String code = generateVerificationCode();
        String key = EMAIL_CODE_PREFIX + email;
        
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        try {
            sendEmailVerification(email, code);
            log.info("Email verification code sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email verification to: {}", email, e);
        }
        
        return code;
    }

    public String generateAndSendSmsVerification(String phoneNumber) {
        String code = generateVerificationCode();
        String key = SMS_CODE_PREFIX + phoneNumber;
        
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        try {
            sendSmsVerification(phoneNumber, code);
            log.info("SMS verification code sent to: {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS verification to: {}", phoneNumber, e);
        }
        
        return code;
    }

    public boolean verifyEmailCode(String email, String code) {
        String key = EMAIL_CODE_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key);
            
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                userService.verifyEmail(userOpt.get());
            }
            
            return true;
        }
        
        return false;
    }

    public boolean verifySmsCode(String phoneNumber, String code) {
        String key = SMS_CODE_PREFIX + phoneNumber;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key);
            
            Optional<User> userOpt = userService.findByPhone(phoneNumber);
            if (userOpt.isPresent()) {
                userService.verifyPhone(userOpt.get());
            }
            
            return true;
        }
        
        return false;
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }

    private void sendEmailVerification(String email, String code) {
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.warn("Email configuration not set, skipping email send");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("ahamo 認証コード");
        message.setText("認証コード: " + code + "\n\nこのコードは10分間有効です。");
        
        mailSender.send(message);
    }

    private void sendSmsVerification(String phoneNumber, String code) {
        if (twilioAccountSid == null || twilioAccountSid.isEmpty() || 
            twilioAuthToken == null || twilioAuthToken.isEmpty()) {
            log.warn("Twilio configuration not set, skipping SMS send");
            return;
        }

        try {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    "ahamo 認証コード: " + code + " (10分間有効)")
                    .create();
            
            log.info("SMS sent with SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio", e);
            throw e;
        }
    }
}
