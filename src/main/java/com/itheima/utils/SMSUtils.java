package com.itheima.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 短信发送工具类
 */
@Configuration
@Data
public class SMSUtils {
    /**
     * 公钥
     */

    @Value("${AccessKey-ID}")
    private String AccessKeyID;
    /**
     * 私钥
     */
    @Value("${AccessKey-Secret}")
    private String AccessKeySecret;
    /**
     * 发送地址
     */
    @Value("${SysRegionId}")
    private String SysRegionId;
    /**
     * 签名名称
     */
    @Value("${SignName}")
    private String SignName;
    /**
     *
     * 模版Code
     */
    @Value("${TemplateCode}")
    private String TemplateCode;

    public  boolean sendMessage(String phoneNumbers, String code) {
        DefaultProfile profile = DefaultProfile.getProfile(SysRegionId, AccessKeyID, AccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId(SysRegionId);
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(SignName);
        request.setTemplateCode(TemplateCode);
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功");
            return true;
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
    }
}
