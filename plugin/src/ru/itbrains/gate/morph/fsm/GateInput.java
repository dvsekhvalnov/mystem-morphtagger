package ru.itbrains.gate.morph.fsm;

import gate.Annotation;
import gate.FeatureMap;

/**
 *
 */
public class GateInput {

    public int annotationIndex = 0;

    private Annotation currentToken;
    private Annotation nextToken;
    private String processingWord;
    private int positionWithinProcessingWord;


    private Long start;
    private Long end;
    private Annotation[] tokens;

    public GateInput() {
    }

    public GateInput(Annotation[] tokens) {
        this.tokens = tokens;
        this.annotationIndex = 0;
        this.currentToken = tokens[0];
        this.processingWord = annotatedWord();
        this.positionWithinProcessingWord = 0;
    }

    private Long startOf(Annotation an) {
        return an.getStartNode().getOffset();
    }

    public Long start(MorphInput input) {
        String originalWord = input.getOriginalWord();
        //long posWithinAnnotation = annotatedWord().indexOf(originalWord);
        long posWithinAnnotation = processingWord.indexOf(originalWord)+positionWithinProcessingWord;
        return startOf(currentToken) + posWithinAnnotation;
    }


    public Long end(MorphInput input) {
        String originalWord = input.getOriginalWord();
        //int posWithinAnnotation = annotatedWord().indexOf(originalWord);
        long posWithinAnnotation = processingWord.indexOf(originalWord)+positionWithinProcessingWord;
        return startOf(currentToken) + posWithinAnnotation + (long) originalWord.length();
    }

    private String annotatedWord() {
        return String.valueOf(currentToken.getFeatures().get("string"));
    }

    public String getProcessingWord() {
        return this.processingWord;
    }


    public int getAnnotationIndex() {
        return annotationIndex;
    }

    public void updateProcessingWord(String word) {
        //this.positionWithinProcessingWord += annotatedWord().indexOf(processingWord) + positionWithinProcessingWord;
        this.positionWithinProcessingWord += processingWord.indexOf(word);
        this.processingWord = word;
    }

    public void setCurrentToken(Annotation newToken) {
        this.currentToken = newToken;
        this.processingWord = annotatedWord();
        this.positionWithinProcessingWord=0;
    }

    public void next() {
        annotationIndex++;
        setCurrentToken(tokens[Math.min(annotationIndex, tokens.length - 1)]);
    }

    public void nextToken() {
        annotationIndex++;
        setNextToken(tokens[Math.min(annotationIndex, tokens.length - 1)]);
    }


    public Annotation getCurrentToken() {
        return currentToken;
    }

    public Annotation getNextToken()
    {
        return nextToken;
    }

    public void setNextToken(Annotation nextToken) {

        this.nextToken = nextToken;
        FeatureMap features = currentToken.getFeatures();

        String nextWord = String.valueOf(nextToken.getFeatures().get("string"));
        String currentWord = String.valueOf(currentToken.getFeatures().get("string"));
        features.put("string", currentWord.concat(nextWord));
        processingWord = processingWord.concat(nextWord);

    }


    public String toString() {
        return "GateInput{" +
                "processingWord='" + processingWord + '\'' +
                ", nextToken=" + nextToken +
                ", currentToken=" + currentToken +
                '}';
    }

    public void substractPrevious()
    {
        FeatureMap features = nextToken.getFeatures();
        String current = String.valueOf(nextToken.getFeatures().get("string"));

        features.put("string", current);

        processingWord=current;
        annotationIndex--;
    }
}
