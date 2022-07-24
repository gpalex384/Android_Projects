package com.algapo.myratings.util;

import com.algapo.myratings.model.Rating;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static final String RATEABLE_THING_STORAGE_FILE = "rateablethings.dat";
    public static final String RATING_STORAGE_FILE = "ratings.dat";


    public static boolean existe_archivo(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f]))
                return true;
        return false;
    }


}
