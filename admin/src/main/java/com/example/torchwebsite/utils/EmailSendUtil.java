package com.example.torchwebsite.utils;


import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Random;

//邮件发送类
@Component
@Slf4j
public class EmailSendUtil {
    @Resource
    private JavaMailSenderImpl mailSender;

    private RedisUtil redisUtil;

    private TokenUtil tokenUtil;

    private RedissonClient redissonClient;

    @Autowired
    public EmailSendUtil(RedisUtil redisUtil, TokenUtil tokenUtil, RedissonClient redissonClient) {
        this.redisUtil = redisUtil;
        this.tokenUtil = tokenUtil;
        this.redissonClient = redissonClient;
    }

    /**
     * 简易样式邮箱
     * @param from 发送者
     * @param email 接受者
     * @param subject 邮件标题
     * @param text 文本类容
     */
    public void simpleEmail(String from, String email, String subject, String text){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        mailSender.send(simpleMailMessage);
    }

    public void sendMailVerify(String from, String mail,String cookie){
        String subject = "薪火志愿者邮箱验证码";
        Random r = new Random();
        StringBuffer sb =new StringBuffer();
        for(int i = 0;i < 6;i ++){
            int ran1 = r.nextInt(10);
            sb.append(String.valueOf(ran1));
        }
        String code = sb.toString();
        System.out.println("token+"+tokenUtil);


        String md5 = tokenUtil.generateMd5(mail, cookie,code);
//        ---------------------------,就不用布隆过滤器了吧
        redisUtil.set(mail,md5,60);//将mail和cookie加密的md5上传redis。


        String text = "邮箱验证码："+code+",注意请在60秒内完成注册。";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(mail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        mailSender.send(simpleMailMessage);
    }
    /**
     * html样式邮件(带图片)!
     * @param from 发送者
     * @param subject 标题
     * @param email 接受者
     */
    public void htmlEmailWithPic(String from,String subject,String email,String imgPath,String text){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject(subject);

            String content = "<html><body>" + "<p>"+ text + "</p>" + "<img src=\'cid:" + 1 + "\'></img>" + "</body></html><br>";
            helper.setText(content,true);
            File file = new File(imgPath);// 创建图片文件
            FileSystemResource resource = new FileSystemResource(file);
            helper.addInline("1", resource);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * html样式邮件
     * @param from 发送者
     * @param subject 标题
     * @param email 接受者
     */
    public void htmlEmail(String from,String subject,String email){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject(subject);

            String Text = "HTML文本";

            helper.setText(Text,true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 附件样式邮件
     * @param from 发送者
     * @param subject 标题
     * @param email 接受者
     */
    public void sendAttachmentsMail(String from,String email,String subject, String text,String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message,true);
            messageHelper.setFrom(from);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(text,true);
            //携带附件
            FileSystemResource file = new FileSystemResource(filePath);
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            messageHelper.addAttachment(fileName,file);

            mailSender.send(message);
            log.info("邮件加附件发送成功！");
        } catch (MessagingException e) {
            log.error("发送失败："+e);
        }
    }

}
