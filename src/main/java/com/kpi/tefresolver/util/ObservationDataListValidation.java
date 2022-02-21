package com.kpi.tefresolver.util;

import com.kpi.tefresolver.model.ObservationData;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Class implement list validation
 * it check if data in list is provided without spaces
 *
 */
@Component
public class ObservationDataListValidation {

    public static boolean checkObservationListValidation(List<ObservationData> list){
        boolean b = true;
        LocalTime counter = LocalTime.MIN;
        for (ObservationData data: list) {
            if(!counter.equals(data.getObservationTime())){
                b = false ;
                break;
            }
            counter = counter.plusMinutes(30);
        }
        return b;
    }

}
