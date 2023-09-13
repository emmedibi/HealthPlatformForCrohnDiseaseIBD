package com.springboot.myhealthplatform.board.designPattern;

import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;

public interface CrohnDiseaseActivityIndexStrategy {

    public default int indexCalculator(CrohnDiseaseActivityData crohnDiseaseActivityData){
        int sum = 0;
        return sum;
    };
}
