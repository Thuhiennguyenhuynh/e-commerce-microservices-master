package com.rainbowforest.emailservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @Value("${mail.from}")
    private String fromEmail;
    
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        javaMailSender.send(message);
    }
    
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        
        javaMailSender.send(message);
    }
    
    // Payment Confirmation Email
    public void sendPaymentConfirmation(String to, String customerName, String orderId, String amount) throws MessagingException {
        String htmlBody = String.format(
            "<html>" +
            "<body>" +
            "<h2>Xác nhận thanh toán</h2>" +
            "<p>Xin chào %s,</p>" +
            "<p>Cảm ơn bạn đã mua hàng. Dưới đây là chi tiết thanh toán:</p>" +
            "<p><strong>Mã đơn hàng:</strong> %s</p>" +
            "<p><strong>Số tiền:</strong> %s VND</p>" +
            "<p>Đơn hàng của bạn sẽ được xử lý trong 24 giờ.</p>" +
            "<p>Cảm ơn!</p>" +
            "</body>" +
            "</html>",
            customerName, orderId, amount
        );
        
        sendHtmlEmail(to, "Xác nhận thanh toán - Đơn hàng #" + orderId, htmlBody);
    }
    
    // Password Reset Email
    public void sendPasswordResetEmail(String to, String resetLink) throws MessagingException {
        String htmlBody = String.format(
            "<html>" +
            "<body>" +
            "<h2>Đặt lại mật khẩu</h2>" +
            "<p>Bạn đã yêu cầu đặt lại mật khẩu. Nhấp vào liên kết dưới đây để đặt lại mật khẩu:</p>" +
            "<p><a href='%s'>Đặt lại mật khẩu</a></p>" +
            "<p>Liên kết này sẽ hết hạn trong 1 giờ.</p>" +
            "<p>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>" +
            "</body>" +
            "</html>",
            resetLink
        );
        
        sendHtmlEmail(to, "Đặt lại mật khẩu", htmlBody);
    }
    
    // Order Status Update Email
    public void sendOrderStatusUpdate(String to, String orderId, String status) throws MessagingException {
        String htmlBody = String.format(
            "<html>" +
            "<body>" +
            "<h2>Cập nhật trạng thái đơn hàng</h2>" +
            "<p>Trạng thái của đơn hàng <strong>#%s</strong> của bạn đã cập nhật:</p>" +
            "<p><strong>Trạng thái mới:</strong> %s</p>" +
            "<p>Cảm ơn bạn đã mua hàng!</p>" +
            "</body>" +
            "</html>",
            orderId, status
        );
        
        sendHtmlEmail(to, "Cập nhật đơn hàng #" + orderId, htmlBody);
    }
    
    // Welcome Email
    public void sendWelcomeEmail(String to, String customerName) throws MessagingException {
        String htmlBody = String.format(
            "<html>" +
            "<body>" +
            "<h2>Chào mừng!</h2>" +
            "<p>Xin chào %s,</p>" +
            "<p>Cảm ơn bạn đã đăng ký tài khoản với chúng tôi.</p>" +
            "<p>Bạn hiện có thể truy cập các sản phẩm, dịch vụ và nhiều hơn nữa.</p>" +
            "<p>Hãy bắt đầu mua sắm ngay hôm nay!</p>" +
            "</body>" +
            "</html>",
            customerName
        );
        
        sendHtmlEmail(to, "Chào mừng đến với cửa hàng của chúng tôi", htmlBody);
    }
}
