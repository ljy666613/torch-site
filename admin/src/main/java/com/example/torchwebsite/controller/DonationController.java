package com.example.torchwebsite.controller;

import com.example.torchwebsite.service.DonationService;
import com.example.torchwebsite.utils.EmailSendUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.pojo.Donation;
import com.example.api.pojo.vo.Donation.DonationInfo;
import com.example.torchwebsite.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/donation")
// 筹款功能 该功能独立于其他模块，用于协会资金筹款
public class DonationController {
    @Resource
    private DonationService donationService;

    @Resource
    private EmailSendUtil emailSendUtil;

    @Value("${spring.mail.username}")
    private String MAIL;

    @Value("${path.mailPic}")
    private String mailPath;
    //    查询捐款
    @GetMapping
    public R<?> selectDonations(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(defaultValue = "") String search){

        QueryWrapper<Donation> wrapper = new QueryWrapper<>();
        wrapper.like("name",search);
        Page<Donation> donations = donationService.getDonations(wrapper,new Page<>(pageNum, pageSize));
        List<Donation> records = donations.getRecords();
        long total = donations.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("records",records);
        jsonObject.set("total",total);

        return R.ok().detail(jsonObject);
    }

    //删除捐款
    @DeleteMapping("/{donation_id}")
    public R<?> deleteDonation(@PathVariable Integer donation_id,HttpServletRequest request, HttpServletResponse response){

        int res = donationService.getBaseMapper().deleteById(donation_id);
        if (res == 0){
            return R.error().message("数据库删除失败");
        }
        return R.ok().message("");
    }

    //    更改捐款信息
    @PutMapping("/{donation_id}")
    public R<?> updateDonation(@RequestBody DonationInfo donationInfo, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer donation_id){

//        数据库可能已有的内容
        QueryWrapper<Donation> wrapper = new QueryWrapper<>();
        wrapper.eq("name",donationInfo.getName());
        wrapper.ne("id",donation_id);
        Donation selectedDonation = donationService.getBaseMapper().selectOne(wrapper);
        if (selectedDonation != null){
            return R.error().message("捐款名已存在");
        }
        Donation newDonation = new Donation();

        newDonation.setId(donation_id);
        newDonation.setName(donationInfo.getName());
        newDonation.setEmail(donationInfo.getEmail());
        newDonation.setTel(donationInfo.getTel());
        newDonation.setMessage(donationInfo.getMessage());
        newDonation.setAmount(donationInfo.getAmount());
        newDonation.setWay(donationInfo.getWay());
        newDonation.setIsAnonymous(donationInfo.getIsAnonymous());
        if (donationInfo.getIsAnonymous() == 1){
            newDonation.setAnonymity(donationInfo.getAnonymity());
        }

        int res = donationService.getBaseMapper().update(newDonation,new QueryWrapper<Donation>().eq("id",donation_id));
        if (res == 0){
            return R.error().message("更改信息失败");
        }
        return R.ok().message("");
    }

    //添加捐款
    @PostMapping
    public R<?> insertDonation(@RequestBody DonationInfo donationInfo, HttpServletRequest request, HttpServletResponse response){

//        QueryWrapper<Donation> wrapper = new QueryWrapper<>();
//        wrapper.eq("name",donationInfo.getName());
//        Donation selectedDonation = donationService.getBaseMapper().selectOne(wrapper);
//        if (selectedDonation != null){
//            return R.error().message("捐款名已存在");
//        }

        Donation newDonation = new Donation();

        newDonation.setName(donationInfo.getName());
        newDonation.setEmail(donationInfo.getEmail());
        newDonation.setTel(donationInfo.getTel());
        newDonation.setMessage(donationInfo.getMessage());
        newDonation.setAmount(donationInfo.getAmount());
        newDonation.setWay(donationInfo.getWay());
        newDonation.setIsAnonymous(donationInfo.getIsAnonymous());
        if (donationInfo.getIsAnonymous() == 1){
            newDonation.setAnonymity(donationInfo.getAnonymity());
        }

        int res = donationService.getBaseMapper().insert(newDonation);

        if (res == 0){

            return R.error().message("数据库添加失败");
        }
        return R.ok().message("");
    }

    @GetMapping("/{donation_id}")
    public R<?> getDonation(@PathVariable Integer donation_id){
        Donation donation = donationService.getBaseMapper().selectById(donation_id);
        return R.ok().detail(donation);
    }

//    @Scheduled(cron = "0 0 12 1 * ?")
    @Scheduled(cron = "*/10 * * * * ?")
    public void inform(){
        List<Donation> donations = donationService.getBaseMapper().selectList(null);
        for (Donation donation : donations) {
//            emailSendUtil.simpleEmail(MAIL,donation.getEmail(),"捐款通知","......");
//            emailSendUtil.sendAttachmentsMail(MAIL, donation.getEmail(),"捐款通知", "......",
//                    "C:\\Users\\Lenovo\\Desktop\\ljy\\picture\\mmexport1662011188809.jpg");
            emailSendUtil.htmlEmailWithPic(MAIL,"捐款",donation.getEmail(),
                    mailPath);

        }

    }

}
