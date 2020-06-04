package com.healthlucid;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;

import java.util.*;

public class Main {

    public static void main(String[] args) {
//        List of Imaging Providers Names:
        String[] acr = new String[] {
                "Sitka Community Hospital",
                "209 Moller Avenue",
                "99835",
                "South Peninsula Hospital",
                "4300 Bartlett Street",
                "99603",
                "Southeast Alaska Regional Health Cons",
                "1200 Salmon Creek Lane",
                "99801",
                "Tanana Valley Clinic",
                "1101 Noble Street Radiology Department",
                "99701"
        };

//        List of Hospitals:
        String[] hospital = new String[]{
                "Providence Alaska Medical Center ",
                "3851 Piper St.Anchorage",
                "99508",
                "Sitka Community Hospital",
                "209 Moller Ave.",
                "99835",
                "Peninsula Hospital",
                "4300 Bartlett St.",
                "99603",
                "North Peninsula Hospital",
                "389 James Street Homer",
                "99603"
        };

        String[] acrOut = getNonHospitalACR(acr, hospital);
        System.out.println("ACR Hospitals removed from list :: " + ((acr.length - acrOut.length)/3));
        System.out.println("Final acr List \n" + Arrays.toString(acrOut));
    }


    static String[] getNonHospitalACR(String[] acr, String[] hospital) {

        HashMap<String, List<String>> acrMap = new HashMap<>();
        //Name Addr ZipCode
        for (int i = 0; i < acr.length / 3; i++) {
            List<String> value = acrMap.getOrDefault(acr[i * 3 + 2], new ArrayList());
            value.add(acr[i * 3]);
            value.add(acr[i * 3 + 1]);
            acrMap.put(acr[i * 3 + 2], value);
        }

        HashMap<String, List<String>> hospitalMap = new HashMap<>();
        for (int i = 0; i < hospital.length / 3; i++) {
            List<String> value = hospitalMap.getOrDefault(hospital[i * 3 + 2], new ArrayList());
            value.add(hospital[i * 3]);
            value.add(hospital[i * 3 + 1]);
            hospitalMap.put(hospital[i * 3 + 2], value);
        }

        String[] keys = acrMap.keySet().stream().toArray(String[]::new);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            List<String> nameAddAcr = acrMap.getOrDefault(key, new ArrayList<>());
            List<String> nameAddHospital = hospitalMap.getOrDefault(key, new ArrayList<>());
            String[] acrNameTokens = nameAddAcr.get(0).split(" ");
            String[] acrAddTokens = nameAddAcr.get(1).split(" ");
            for (int j = 0; j < nameAddHospital.size() / 2; j++) {
                String[] hospitalNameTokens = nameAddHospital.get(j * 2).split(" ");
                String[] hospitalAddTokens = nameAddHospital.get(j * 2 + 1).split(" ");
                double nameMatch = matchStringArray(acrNameTokens, hospitalNameTokens);
                double addMatch = matchStringArray(acrAddTokens, hospitalAddTokens);
                System.out.println("Match for zipCode: " + key + " is " + nameMatch + "(name) " + addMatch + "(address)!!");
                if (nameMatch > 0.33 && addMatch > 0.33 && (nameMatch + addMatch) > 1) {
                    acrMap.remove(key);
                    break;
                }
            }
        }

        List<String> acrNotHospital = new ArrayList<>();
        Iterator<String> itr2 = acrMap.keySet().iterator();
        while (itr2.hasNext()) {
            String key = itr2.next();
            acrNotHospital.add(acrMap.get(key).get(0));
            acrNotHospital.add(acrMap.get(key).get(1));
            acrNotHospital.add(key);
        }
        return acrNotHospital.stream().toArray(String[]::new);
    }

     static Double matchStringArray(String[] strArray1, String[] strArray2) {
        TextSimilarityMeasure measure = new WordNGramJaccardMeasure(1);
         double match = 0;
         try {
             match = measure.getSimilarity(strArray1, strArray2);
         } catch (SimilarityException e) {
             e.printStackTrace();
         } finally {
             return match;
         }
    }

}
