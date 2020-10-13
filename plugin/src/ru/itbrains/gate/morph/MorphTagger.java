package ru.itbrains.gate.morph;

import gate.*;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.util.InvalidOffsetException;
import gate.util.OffsetComparator;
import ru.itbrains.gate.morph.fsm.GateInput;
import ru.itbrains.gate.morph.fsm.MorphInput;

import java.io.IOException;
import java.util.*;


/**
 * Russian morphology tagger based on Yandex Mystem morphological analyzer
 */
public class MorphTagger extends AbstractLanguageAnalyser
{
    private FeatureMap WORD_CONSTRAINT;

    private String nativeFolder;
    private String encoding = "utf-8";

    private enum States
    {
        START, ORDINARY_WORD, SLASH_WORD, REVERSE_SLASH_WORD, NO_WORD, PARTIAL_MATCH, END_WORD_MATCH, END;

    }

    public MorphTagger()
    {
        WORD_CONSTRAINT = Factory.newFeatureMap();
        WORD_CONSTRAINT.put("kind", "word");
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public void setNativeFolder(String folder)
    {
        this.nativeFolder = folder;
    }

    public String getNativeFolder()
    {
        return nativeFolder;
    }

    public void execute() throws ExecutionException
    {
        Document doc = getDocument();

        String content = doc.getContent().toString();

        List<MorphInfo> morphData;

        try
        {
            MorphParser parser = new MorphParser(nativeFolder);
            morphData = parser.runParser(content, encoding);
        }
        catch (IOException e)
        {
            throw new ExecutionException(e);
        }

        AnnotationSet inputAS = doc.getAnnotations();
        AnnotationSet wordSet = inputAS.get("Token", WORD_CONSTRAINT);
        List<Annotation> words = new LinkedList<Annotation>(wordSet);
        Collections.sort(words, new OffsetComparator());

        Annotation[] tokens = new Annotation[words.size()];
        words.toArray(tokens);

        try
        {
            GateInput gateInput = new GateInput(tokens);
            MorphInput mystemInput = new MorphInput(morphData);

            nextMorphData(mystemInput);

            States curState = States.START;
            boolean done = false;

            while (gateInput.getAnnotationIndex() < tokens.length && !done)
            {

                curState = nextState(gateInput, mystemInput, curState);

                switch (curState)
                {
                    case END:
                        done = true;
                        break;
                    case ORDINARY_WORD:
                    {
                        annotate(gateInput, mystemInput, inputAS);

                        nextMorphData(mystemInput);
                        nextAnnotation(gateInput, tokens);
                        break;

                    }

                    case NO_WORD:
                    {
                        nextAnnotation(gateInput, tokens);
                        break;
                    }
                    case PARTIAL_MATCH:
                    {
                        joinAnnotation(gateInput, tokens);
                        break;
                    }
                    case REVERSE_SLASH_WORD:
                    {
                        subAnnotation(gateInput, tokens);
                        nextAnnotation(gateInput, tokens);
                        break;
                    }
                    case END_WORD_MATCH:
                    {
                        annotate(gateInput, mystemInput, inputAS);
                        nextAnnotation(gateInput, tokens);
                        nextMorphData(mystemInput);
                        break;
                    }


                    case SLASH_WORD:
                    {

                        annotate(gateInput, mystemInput, inputAS);

                        String word = gateInput.getProcessingWord()
                                               .replaceFirst(mystemInput.getOriginalWord(), "")
                                               .substring(1);

                        gateInput.updateProcessingWord(word);
                        nextMorphData(mystemInput);
                        break;
                    }


                }
            }
        }

        catch (InvalidOffsetException e)
        {
            throw new ExecutionException(e);

        }
        catch (IOException e)
        {
            throw new ExecutionException(e);
        }
    }

    private void annotate(GateInput input, MorphInput morphInput, AnnotationSet inputAS) throws InvalidOffsetException
    {
        Long start = input.start(morphInput);
        Long end = input.end(morphInput);

        List<FeatureMap> homonyms = parseMorphContent(morphInput);

        for (FeatureMap featureMap : homonyms)
        {
            inputAS.add(start,
                        end,
                        "Morph",
                        featureMap);
        }


    }

    private List<FeatureMap> parseMorphContent(MorphInput input)
    {
        List<Map<String, String>> homonyms = input.getHomonyms();
        List<FeatureMap> result = new LinkedList<FeatureMap>();

        for (Map<String, String> homonym : homonyms)
            result.add(newFeatures(homonym));

        return result;
    }

    private void nextAnnotation(GateInput input, Annotation[] tokens)
    {
        input.next();
    }

    private void joinAnnotation(GateInput input, Annotation[] tokens)
    {
        input.nextToken();
    }

    private void subAnnotation(GateInput input, Annotation[] tokens)
    {
        input.substractPrevious();
    }


    private void nextMorphData(MorphInput input) throws IOException
    {
        input.next();
    }


    private States nextState(GateInput input, MorphInput mystemInput, States currentState)
    {

        if (mystemInput.getContentLine() == null || input.getCurrentToken() == null)
            return States.END;

        String tokenWord = input.getProcessingWord();
        String outputWord = mystemInput.getOriginalWord();

        if (tokenWord.trim().length() == 0)
            return States.NO_WORD;
        if (tokenWord.equals(outputWord))
            return States.ORDINARY_WORD;
        else if (currentState == MorphTagger.States.PARTIAL_MATCH)
            return States.REVERSE_SLASH_WORD;
        else if (tokenWord.startsWith(outputWord))
            return States.SLASH_WORD;
        else if (outputWord.startsWith(tokenWord))
            return States.PARTIAL_MATCH;
        else if (tokenWord.endsWith(outputWord))
            return States.END_WORD_MATCH;
        else
            return States.NO_WORD;
    }


    private FeatureMap newFeatures(Map<String, String> features)
    {
        FeatureMap morphFeatures = Factory.newFeatureMap();
        morphFeatures.putAll(features);

        return morphFeatures;
    }


}
