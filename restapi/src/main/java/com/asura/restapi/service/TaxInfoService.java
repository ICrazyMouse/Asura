package com.asura.restapi.service;

import com.asura.restapi.mapper.TaxInfoMapper;
import com.asura.restapi.model.dto.TaxInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lichuanshun on 2017/11/21.
 */
@Service
public class TaxInfoService {


    @Autowired
    TaxInfoMapper taxInfoMapper;


    public int saveTaxInfo(TaxInfo taxInfo){
        return taxInfoMapper.saveTaxInfo(taxInfo);
    }

    public List<TaxInfo> queryTaxInfoByUid(String uid){
        return taxInfoMapper.queryTaxByUid(uid);
    }

    public List<TaxInfo> queryTaxUnitAndMoenyByUid(String uid){
        return taxInfoMapper.queryTaxUnitAndMoenyByUid(uid);
    }
}
