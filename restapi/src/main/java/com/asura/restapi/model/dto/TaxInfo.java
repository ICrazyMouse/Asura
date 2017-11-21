package com.asura.restapi.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by lichuanshun on 2017/11/21.
 */
@Data
@Getter
@Setter
public class TaxInfo {

    private String id;
    private String foreign_id;
    private String source;
    private String item;
    private String tax_money;
    private String period;
    private String levying_department;
    private String tax_date;
    // 缴税义务人 代缴一般为公司kjywr',
    private String tax_unit;
    // '缴税义务人代码kjywrdm',
    private String tax_unit_code;
    // 缴税周期开始日skssqq
    private String period_start;
    // 缴税周期结束日skssqz
    private String period_end;
    // 收入额sre
    private String income;
    // 纳税人识别号nsrsbh
    private String tax_user_code;
    // 征收机关代码zsjgdm
    private String leaving_department_code;
    // 实际金额sjje
    private String real_amount;
    // 税款收缴机构skssjg'
    private String parent_department;
    // 扣缴义务人kjywrdjxh:10013101003000686666
    private String tax_unit_djxh;
    // 扣缴义务人名称
    private String tax_unit_name;
    // 入库日期rkrq
    private String save_date;
    // 纳税人姓名nsrxm
    private String user_name;
    // 身份证件号码sfzjhm
    private String id_card_num;
    // 身份证件类型代码sfzjlxdm
    private String id_card_type_code;
    // 身份证件类型名称sfzjlxmc
    private String id_card_type;
    // ?项目代码sjsdxmdm
    private String sjsdxmdm;

    //ynssde
    private String ynssde;

    // ?扣缴上报kjsb
    private String tax_submit;
    // 项目名称sdxmDm
    private String sdxmdm;

    private String dzlbzdsdm;
    //上报日期sbrq
    private String tax_submit_date;

    private String sdxmmc;


    private String dzsph;

    private String szmc;
    private String szdm;

    private String add_time;

    private String update_time;


}
