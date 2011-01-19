package com.sample.osgi.cdi.services.impl;

import com.sample.osgi.cdi.services.DictionaryService;
import com.sample.osgi.cdi.services.SpellCheckerService;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Mathieu ANCELIN
 */
public class SpellCheckerServiceImpl implements SpellCheckerService {

    private DictionaryService dictionaryService;

    public DictionaryService getDictionaryService() {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @Override
    public List<String> check(String passage) {
        if ((passage == null) || (passage.length() == 0)) { 
            return null;
        }
        List<String> errorList = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(passage, " ,.!?;:");
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            if (! dictionaryService.checkWord(word)) {
                errorList.add(word);
            }
        }
        if (errorList.isEmpty()) { 
            return null;
        }
        System.out.println("Wrong words:" + errorList);
        return errorList;
    }
}

