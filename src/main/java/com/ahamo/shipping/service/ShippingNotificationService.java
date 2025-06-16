package com.ahamo.shipping.service;

import com.ahamo.shipping.model.ShippingOrder;
import com.ahamo.user.model.User;
import com.ahamo.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.Optional;

@Service
@Slf4j
public class ShippingNotificationService {

    @Autowired
    @Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private UserService userService;

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number:}")
    private String twilioPhoneNumber;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendShippingStatusNotification(ShippingOrder order, String newStatus) {
        try {
            Optional<User> userOpt = userService.findByContractNumber(String.valueOf(order.getContractId()));
            if (userOpt.isEmpty()) {
                log.warn("User not found for contract ID: {}", order.getContractId());
                return;
            }

            User user = userOpt.get();
            String message = createStatusMessage(order, newStatus);

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                sendEmailNotification(user.getEmail(), order.getOrderNumber(), message);
            }

            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                sendSmsNotification(user.getPhone(), message);
            }

        } catch (Exception e) {
            log.error("Failed to send shipping notification for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private String createStatusMessage(ShippingOrder order, String status) {
        String deviceInfo = order.getDeviceId() != null ? order.getDeviceId() : "デバイス";
        
        switch (status) {
            case "SHIPPED":
                return String.format("【ahamo】%sが発送されました。追跡番号: %s", deviceInfo, order.getTrackingNumber());
            case "IN_TRANSIT":
                return String.format("【ahamo】%sが配送中です。まもなくお届け予定です。", deviceInfo);
            case "OUT_FOR_DELIVERY":
                return String.format("【ahamo】%sが配達中です。本日中にお届け予定です。", deviceInfo);
            case "DELIVERED":
                return String.format("【ahamo】%sが配達完了しました。ご利用ありがとうございます。", deviceInfo);
            case "FAILED":
                return String.format("【ahamo】%sの配達に失敗しました。再配達をご希望の場合はお手続きください。", deviceInfo);
            default:
                return String.format("【ahamo】%sの配送状況が更新されました: %s", deviceInfo, status);
        }
    }

    private void sendEmailNotification(String email, String orderNumber, String message) {
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.warn("Email configuration not set, skipping email send");
            return;
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(email);
            mailMessage.setSubject("【ahamo】配送状況のお知らせ");
            mailMessage.setText(message + "\n\n注文番号: " + orderNumber);
            
            mailSender.send(mailMessage);
            log.info("Shipping notification email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send shipping notification email to: {}", email, e);
        }
    }

    private void sendSmsNotification(String phoneNumber, String message) {
        if (twilioAccountSid == null || twilioAccountSid.isEmpty() || 
            twilioAuthToken == null || twilioAuthToken.isEmpty()) {
            log.warn("Twilio configuration not set, skipping SMS send");
            return;
        }

        try {
            Twilio.init(twilioAccountSid, twilioAuthToken);
            
            Message smsMessage = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    message)
                    .create();
            
            log.info("Shipping notification SMS sent with SID: {}", smsMessage.getSid());
        } catch (Exception e) {
            log.error("Failed to send shipping notification SMS", e);
        }
    }
}
