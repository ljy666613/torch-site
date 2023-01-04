package com.example.api.pojo.vo.Donation;

import lombok.Data;

@Data
public class DonationInfo {
    private String name;
    private String amount;
    private String tel;
    private String email;
    private String message;
    private Integer way;
    private Integer isAnonymous;
    private String anonymity;
}
