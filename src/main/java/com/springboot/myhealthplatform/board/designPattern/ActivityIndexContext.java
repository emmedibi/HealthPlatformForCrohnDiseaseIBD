package com.springboot.myhealthplatform.board.designPattern;

import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;

/**
 * Context dove viene chiamata l'algoritmo di calcolo dell'indice.
 */
public class ActivityIndexContext {
    private CrohnDiseaseActivityData crohnDiseaseActivityData;
    private CrohnDiseaseActivityIndexStrategy crohnDiseaseActivityIndexStrategy;

    public ActivityIndexContext(CrohnDiseaseActivityData crohnDiseaseActivityData){
        this.crohnDiseaseActivityData = crohnDiseaseActivityData;
    }
    public void setCrohnDiseaseActivityIndexStrategy(CrohnDiseaseActivityIndexStrategy crohnDiseaseActivityIndexStrategy) {
        this.crohnDiseaseActivityIndexStrategy = crohnDiseaseActivityIndexStrategy;
    }
    public void setCrohnDiseaseActivityData(CrohnDiseaseActivityData crohnDiseaseActivityData){
        this.crohnDiseaseActivityData = crohnDiseaseActivityData;
    }
    public int executeStrategy(){
        return this.crohnDiseaseActivityIndexStrategy.indexCalculator(crohnDiseaseActivityData);
    };
}
