package com.asura.restapi.mapper;

import com.asura.restapi.model.dto.TaxInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lichuanshun on 2017/11/21.
 */
@Mapper
public interface TaxInfoMapper {

    int saveTaxInfo(TaxInfo taxInfo);

    List<TaxInfo> queryTaxByUid(@Param("uid") String uid);

    List<TaxInfo> queryTaxUnitAndMoenyByUid(@Param("uid") String uid);
}
