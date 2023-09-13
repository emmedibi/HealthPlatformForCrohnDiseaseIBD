package com.springboot.myhealthplatform.board.designPattern;

import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;
import com.springboot.myhealthplatform.board.enumeration.AbdominalMass;
import com.springboot.myhealthplatform.board.enumeration.AbdominalPainScale;
import com.springboot.myhealthplatform.board.enumeration.GeneralWellBeing;

/**
 * Classe che implementa l'interfaccia CrohnDiseaseActivityIndexStrategy e produce l'algoritmo
 * utile al calcolo del HBI (HarveyBradshawIndex)
 */
public class CrohnDiseaseHarveyBradshawIndexCalculator implements CrohnDiseaseActivityIndexStrategy{
    AbdominalPainScale abdominalPainScale;
    GeneralWellBeing generalWellBeing;
    AbdominalMass abdominalMass;

    private static final int MULTIPLIERS_PER_LIQUID_STOOL_DAY_BEFORE = 1;
    private static final int CONST_VALUE_FOR_COMPLICATIONS = 1;
    /**
     * Il calcolo di HBI è dato dalla somma totale delle variabili raccolte in crohnDiseaseActivityData.
     * Non vengono usate le variabili averagePastSevenDaysWellBeing e averagePastSevenDaysAbdominalPain.
     * Alcuni di questi attributi hanno dei pesi che vanno sommati alla variabile. I pesi sono le
     * costanti sopra indicate.
     * @param crohnDiseaseActivityData oggetto che contiene tutti i dati recuperati dalla form
     * @return un valore int che rappresenta l'indice di attività della malattia di Crohn.
     */

    @Override
    public int indexCalculator(CrohnDiseaseActivityData crohnDiseaseActivityData) {
        // Definizione delle variabili ENUM
        abdominalPainScale = AbdominalPainScale.values()[crohnDiseaseActivityData.getYesterdayAbdominalPain()];
        generalWellBeing = GeneralWellBeing.values()[crohnDiseaseActivityData.getYesterdayWellBeing()];
        abdominalMass = AbdominalMass.values()[crohnDiseaseActivityData.getAbdominalMass()];
        // Inizializzazione della variabile somma
        int sum = 0;
        // Inizializzazione dell'array booleans che contiene tutti i valori booleani utili al calcolo, ottenuti dalla form.
        boolean[] booleans = crohnDiseaseActivityData.getAllBooleanValues();
        // Calcolo somma dei valori booleani
        // Se il valore è uguale a TRUE, allora la somma aumenta di 1 punto.
        for (int i = 0; i < booleans.length; i++){
            if(booleans[i]){
                sum+=CONST_VALUE_FOR_COMPLICATIONS;
            }
        }
        switch (abdominalPainScale) {
            case NONE:
                sum += 0;
                break;
            case MILD:
                sum += 1;
                break;
            case MODERATE:
                sum += 2;
                break;
            case SEVERE:
                sum += 3;
        }
        switch (generalWellBeing) {
            case WELL:
                sum += 0;
                break;
            case SLIGHTLY_UNDER_PAR:
                sum += 1;
                break;
            case POOR:
                sum += 2;
                break;
            case VERY_POOR:
                sum += 3;
                break;
            case TERRIBLE:
                sum += 4;
        }
        switch(abdominalMass){
            case NO:
                sum+= 0;
                break;
            case QUESTIONABLE:
                sum+=1;
                break;
            case DEFINITE:
                sum+=2;
                break;
            case DEFINITE_AND_TENDER:
                sum+=30;
        }
        // Somma del numero di accessi al bagno con feci liquide nel giorno precedente alla visita
        sum += crohnDiseaseActivityData.getSoftStoolDayBefore()*MULTIPLIERS_PER_LIQUID_STOOL_DAY_BEFORE;
    return sum;
    }
}
