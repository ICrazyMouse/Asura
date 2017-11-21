package com.asura.restapi.mapper;

import com.asura.restapi.model.dto.TaxInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by lichuanshun on 2017/11/21.
 */
@Mapper
public interface TaxInfoMapper {

    int saveTaxInfo(TaxInfo taxInfo);
}
