package com.springboot.myhealthplatform.board.designPattern;

import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;
import com.springboot.myhealthplatform.board.enumeration.AbdominalMass;
import com.springboot.myhealthplatform.board.enumeration.AbdominalPainScale;
import com.springboot.myhealthplatform.board.enumeration.GeneralWellBeing;

/**
 * Classe che implementa l'interfaccia CrohnDiseaseActivityIndexStrategy e produce l'algoritmo
 * utile al calcolo del CDAI (CrohnDiseaseActivityIndex)
 */
public class CrohnDiseaseActivityIndexCalculator implements CrohnDiseaseActivityIndexStrategy{
    AbdominalPainScale abdominalPainScale;
    GeneralWellBeing generalWellBeing;
    AbdominalMass abdominalMass;

    private static final int MULTIPLIERS_PER_LIQUID_STOOL_IN_THE_LAST_SEVEN_DAYS = 14;
    private static final int CONST_VALUE_FOR_ANTI_DIARRHEA_DRUG_USE = 30;
    private static final int CONST_VALUE_FOR_COMPLICATIONS = 20;

    /**
     * Il calcolo di CDAI è dato dalla somma totale delle variabili raccolte in crohnDiseaseActivityData.
     * Non vengono usate le variabili yesterdayAbdominalPain e yesterdayWellBeing.
     * Alcuni di questi attributi hanno dei pesi che vanno sommati alla variabile. I pesi sono le
     * costanti sopra indicate.
     * @param crohnDiseaseActivityData oggetto che contiene tutti i dati recuperati dalla form
     * @return un valore int che rappresenta l'indice di attività della malattia di Crohn.
     */
    @Override
    public int indexCalculator(CrohnDiseaseActivityData crohnDiseaseActivityData) {
        abdominalPainScale = AbdominalPainScale.values()[crohnDiseaseActivityData.getAveragePastSevenDaysAbdominalPain()];
        generalWellBeing = GeneralWellBeing.values()[crohnDiseaseActivityData.getAveragePastSevenDaysWellBeing()];
        abdominalMass = AbdominalMass.values()[crohnDiseaseActivityData.getAbdominalMass()];
        int sum = 0;
        sum += crohnDiseaseActivityData.getTotalNumberOfLiquidStoolInTheLastSevenDays() * MULTIPLIERS_PER_LIQUID_STOOL_IN_THE_LAST_SEVEN_DAYS;
        switch (abdominalPainScale) {
            case NONE:
                sum += 0;
                break;
            case MILD:
                sum += 35;
                break;
            case MODERATE:
                sum += 70;
                break;
            case SEVERE:
                sum += 105;
        }
        switch (generalWellBeing) {
            case WELL:
                sum += 0;
                break;
            case SLIGHTLY_UNDER_PAR:
                sum += 49;
                break;
            case POOR:
                sum += 98;
                break;
            case VERY_POOR:
                sum += 147;
                break;
            case TERRIBLE:
                sum += 196;
        }
        switch(abdominalMass){
            case NO:
                sum+= 0;
                break;
            case QUESTIONABLE:
                sum+=20;
                break;
            case DEFINITE:
                sum+=50;
                break;
            case DEFINITE_AND_TENDER:
                sum+=50;
        }
        // Per ogni valore booleano, se true somma al totale la costante di riferimento per quella
        // variabile.
        if (crohnDiseaseActivityData.isAntiDiarrheaDrugUse()) {
            sum += CONST_VALUE_FOR_ANTI_DIARRHEA_DRUG_USE;
        } else if (crohnDiseaseActivityData.isArthralgias()) {
            sum += CONST_VALUE_FOR_COMPLICATIONS;
        } else if (crohnDiseaseActivityData.isUveitis()) {
            sum += CONST_VALUE_FOR_COMPLICATIONS;
        } else if (crohnDiseaseActivityData.isErythemaNodosum() || crohnDiseaseActivityData.isPyodermaGangrenosum() || crohnDiseaseActivityData.isApthousStomatitis()) {
            sum += CONST_VALUE_FOR_COMPLICATIONS;
        } else if (crohnDiseaseActivityData.isFistula() || crohnDiseaseActivityData.isAnalFissure() || crohnDiseaseActivityData.isAbscess()) {
            sum += CONST_VALUE_FOR_COMPLICATIONS;
        } else if (crohnDiseaseActivityData.isOtherFistula()) {
            sum += CONST_VALUE_FOR_COMPLICATIONS;
        } else if(crohnDiseaseActivityData.isFever()){
            sum+=CONST_VALUE_FOR_COMPLICATIONS;
        }
        return sum;
    }
}
