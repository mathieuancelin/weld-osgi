package com.sample.osgi.cdi.services.impl;

import com.sample.osgi.cdi.services.DictionaryService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mathieu ANCELIN
 */
public class EnglishDictionaryServiceImpl implements DictionaryService {

    List<String> words = new ArrayList<String>();

    {
        words.add("hello");
        words.add("world");
        words.add("how");
        words.add("are");
        words.add("you");
        words.add("guys");
    };

    @Override
    public boolean checkWord(String word) {
        System.out.println("checking v 1");
        word = word.toLowerCase();
        for (String oneWord : words) {
            if (oneWord.equals(word)) {
                return true;
            }
        }
        return false;
    }
}
