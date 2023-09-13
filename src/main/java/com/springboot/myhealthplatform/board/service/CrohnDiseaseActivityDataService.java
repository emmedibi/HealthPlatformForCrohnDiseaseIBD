package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.board.bean.CrohnDiseaseActivityData;
import com.springboot.myhealthplatform.board.designPattern.ActivityIndexContext;
import com.springboot.myhealthplatform.board.designPattern.CrohnDiseaseActivityIndexCalculator;
import com.springboot.myhealthplatform.board.designPattern.CrohnDiseaseHarveyBradshawIndexCalculator;
import org.springframework.stereotype.Service;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione dei dati utili al calcolo degli activity index.
 */
@Service
public class CrohnDiseaseActivityDataService {

    /**
     * Il metodo agisce da client per il richiamo dell'algoritmo per il calcolo dell'indice di attività della malattia.
     * Viene richiamato il context all'interno del quale viene settato sia l'oggetto che contiene
     * i dati utili al calcolo (crohnDiseaseActivityIndex) sia la strategia usata per il calcolo
     * (CrohnDiseaseActivityIndexCalculator).
     * @param crohnDiseaseActivityData dati validati provenienti dalla form.
     * @return ritorna il valore calcolato dall'algoritmo selezionato.
     */
    public int calculateCDAI(CrohnDiseaseActivityData crohnDiseaseActivityData){
        ActivityIndexContext activityIndexContext = new ActivityIndexContext(crohnDiseaseActivityData);
        activityIndexContext.setCrohnDiseaseActivityIndexStrategy(new CrohnDiseaseActivityIndexCalculator());
        return activityIndexContext.executeStrategy();
    }

    /**
     * Il metodo agisce da client per il richiamo dell'algoritmo per il calcolo dell'indice di attività della malattia.
     *  Viene richiamato il context all'interno del quale viene settato sia l'oggetto che contiene
     * i dati utili al calcolo (crohnDiseaseActivityIndex) sia la strategia usata per il calcolo
     * (CrohnDiseaseHarveyBradshawIndexCalculator).
     * @param crohnDiseaseActivityData dati validati provenienti dalla form.
     * @return ritorna il valore calcolato dall'algoritmo selezionato.
     */
    public int calculateHBI(CrohnDiseaseActivityData crohnDiseaseActivityData){
        ActivityIndexContext activityIndexContext = new ActivityIndexContext(crohnDiseaseActivityData);
        activityIndexContext.setCrohnDiseaseActivityIndexStrategy(new CrohnDiseaseHarveyBradshawIndexCalculator());
        return activityIndexContext.executeStrategy();
    }
}
