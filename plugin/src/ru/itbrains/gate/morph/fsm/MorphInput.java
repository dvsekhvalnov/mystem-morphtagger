package ru.itbrains.gate.morph.fsm;

import ru.itbrains.gate.morph.MorphInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MorphInput
{
    private String contentLine;
    private String originalWord;
    private List<MorphInfo> annotations;
    private int index = 0;
    private MorphInfo current;

    public MorphInput(List<MorphInfo> annotations)
    {
        this.annotations = annotations;
        index = -1;
    }

    public List<Map<String, String>> getHomonyms()
    {
        return current.getHomonymGrammems();
    }

    public String getOriginalWord()
    {
        return this.originalWord;
    }

    public String getContentLine()
    {
        return contentLine;
    }

    public void next() throws IOException
    {
        index++;
        if (index < annotations.size())
        {
            current = annotations.get(index);
            contentLine = "";
            originalWord = current.getOriginalWord();
        }
        else
            contentLine = null;

    }


    public String toString()
    {
        return "MorphInput{" +
               "contentLine='" + contentLine + '\'' +
               ", originalWord='" + originalWord + '\'' +
               '}';
    }
}
