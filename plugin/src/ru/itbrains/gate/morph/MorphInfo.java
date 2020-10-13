package ru.itbrains.gate.morph;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * Represents result of morphological parsing for one word
 * Contains Part of Speech, and set of grammems for given word-form
 */
public class MorphInfo
{
    private String originalWord;
    private List<Map<String, String>> homonymGrammems=new LinkedList<Map<String, String>>();

    public MorphInfo(String originalWord)
    {
        this.originalWord = originalWord.trim();        
    }

    public String getOriginalWord()
    {
        return originalWord;
    }

    public List<Map<String, String>> getHomonymGrammems()
    {
        return homonymGrammems;
    }

    public void addHomonym(Map<String, String> features)
    {
        homonymGrammems.add(features);
    }
}
