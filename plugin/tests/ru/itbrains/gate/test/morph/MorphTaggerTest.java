package ru.itbrains.gate.test.morph;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import ru.itbrains.gate.morph.MorphTagger;
import ru.itbrains.gate.test.GateUnitTest;

import java.util.List;

import static gate.Factory.newDocument;

public class MorphTaggerTest extends GateUnitTest
{
    protected void setUp() throws Exception
    {
        super.setUp();
        addToPipeline(createSimpleTokenizer());
        MorphTagger processor = new MorphTagger();
        addToPipeline(processor);
    }

    public void testExecute() throws Exception
    {
        Document doc = newDocument("1. Руководителем проекта, назначается");

        executePipeline(doc);

        AnnotationSet an = doc.getAnnotations().get("Morph");


        Annotation word1 = annotation(an, 3, 16);
        Annotation word2 = annotation(an, 17, 24);
        Annotation word3 = annotation(an, 27, 39);

        assertEquals(3, an.size());

        assertBaseForm(word1, "руководитель", 3, 16);
        assertBaseForm(word2, "проект", 17, 24);
        assertBaseForm(word3, "назначаться", 26, 37);

    }

    public void testExecuteWithSlashes() throws Exception
    {
        Document doc = newDocument("раз-два- четыре пять");
        executePipeline(doc);
        printAnnotationsInfo(doc, "Morph");

        AnnotationSet an = doc.getAnnotations().get("Morph");

        Annotation word1 = annotation(an, 0, 3);
        Annotation word2 = annotation(an, 4, 7);
        Annotation word3 = annotation(an, 8, 14);
        Annotation word4 = annotation(an, 15, 19);

        assertEquals("There should be 14 morph annotations", 14, an.size());

        assertBaseForm(word1, "раз", 0, 3);
        assertBaseForm(word2, "два", 4, 7);
        assertBaseForm(word3, "четыре", 9, 15);
        assertBaseForm(word4, "пять", 16, 20);
    }

    public void testComplexExecution2() throws Exception
    {
        Document doc = newDocument("Номер проекта:\n" +
                                   "05-4-Н1-0022\n" +
                                   "Название");
        executePipeline(doc);
        printAnnotationsInfo(doc, "Morph");
        printAnnotationsInfo(doc, "Token");
        AnnotationSet an = doc.getAnnotations().get("Morph");
        Annotation word1 = annotation(an, 0, 5);
        Annotation word2 = annotation(an, 6, 13);
        Annotation word3 = annotation(an, 28, 36);

        assertEquals("There should be 5 annotations", 5, an.size());
        assertBaseForm(word1, "номер", 0, 5);
        assertBaseForm(word2, "проект", 6, 13);
        assertBaseForm(word3, "название", 28, 36);
    }

    public void testComplexExecution3() throws Exception
    {
        Document doc = newDocument("2 - Северо-Западный федеральный округ");
        executePipeline(doc);
        printAnnotationsInfo(doc, "Morph");

    }

    public void testSlash2() throws Exception
    {
        Document doc = newDocument("цена–качество");
        executePipeline(doc);
        printAnnotationsInfo(doc, "Morph");
        assertEquals(3, noOfAnnotations(doc, "Morph"));

        Annotation word1 = firstAnnotation(doc, "Morph", of("цена"));
        Annotation word2 = firstAnnotation(doc, "Morph", of("качество"));

        assertRange(of("цена"), word1);
        assertRange(of("качество"), word2);


    }

    public void testSlash3() throws Exception
    {
        Document doc = newDocument("Должность:\n" +
                                   "Инженер- программист");
        executePipeline(doc);
        printAnnotationsInfo(doc, "Morph");

        Annotation word1 = firstAnnotation(doc, "Morph", of("Должность"));
        Annotation word2 = firstAnnotation(doc, "Morph", of("Инженер"));
        Annotation word3 = firstAnnotation(doc, "Morph", of("программист"));

        assertEquals("There should be 4 annotations", 4, noOfAnnotations(doc, "Morph"));

        assertBaseForm(word1, "должность");
        assertBaseForm(word2, "инженер");
        assertBaseForm(word3, "программист");

        assertRange(of("Должность"), word1);
        assertRange(of("Инженер"), word2);
        assertRange(of("программист"), word3);
    }

    public void testSlash4() throws Exception
    {
        Document doc = newDocument("cвободно-сыпучие гранулы");
        executePipeline(doc);
        printAnnotationsInfo(doc, "Morph");
        assertEquals(6, noOfAnnotations(doc, "Morph"));


        Annotation word1 = firstAnnotation(doc, "Morph", of("сыпучие"));
        Annotation word2 = firstAnnotation(doc, "Morph", of("гранулы"));


        assertBaseForm(word1, "сыпучий");
        assertBaseForm(word2, "гранула");


        assertRange(of("сыпучие"), word1);
        assertRange(of("гранулы"), word2);

    }

    public void testComplexExecution() throws Exception
    {
        Document doc = newDocument("ФОРМА 1. ДАННЫЕ О ПРОЕКТЕ \n" +
                                   "Номер проекта: \n" +
                                   "05-2-Н4-0128 \n" +
                                   "Информация об участниках");
        executePipeline(doc);

        AnnotationSet an = doc.getAnnotations().get("Morph");


        Annotation word1 = annotation(an, 0, 5);
        Annotation word2 = annotation(an, 9, 15);
        Annotation word3 = annotation(an, 16, 17);
        Annotation word4 = annotation(an, 18, 25);
        Annotation word5 = annotation(an, 26, 31);
        Annotation word6 = annotation(an, 33, 40);
        Annotation word7 = annotation(an, 57, 67);
        Annotation word8 = annotation(an, 68, 70);
        Annotation word9 = annotation(an, 71, 81);

        assertEquals(28, an.size());
        assertBaseForm(word1, "форма", 0, 5);
        assertBaseForm(word2, "давать", 9, 15);
        assertBaseForm(word3, "о", 16, 17);
        assertBaseForm(word4, "проект", 18, 25);
        assertBaseForm(word5, "номер", 27, 32);
        assertBaseForm(word6, "проект", 33, 40);
        assertBaseForm(word7, "информация", 57, 67);
        assertBaseForm(word8, "об", 68, 70);
        assertBaseForm(word9, "участник", 71, 81);

    }

    public void testUnknownFeature() throws Exception
    {
        Document doc = newDocument("прибора для определнния концентрации");
        try
        {
            executePipeline(doc);
        }
        catch (NullPointerException e)
        {
            fail("There should not be any exceptions");
        }


    }

    public void testNoBastardFollowingWithSlash() throws Exception
    {

        Document doc = newDocument("000 \"М-Гелиос\" (Москва) существует более 4 лет.");
        executePipeline(doc);

        printAnnotationsInfo(doc, "Morph");

        AnnotationSet an = doc.getAnnotations().get("Morph");

        Annotation word1 = annotation(an, 5, 6);
        Annotation word2 = annotation(an, 7, 13);
        Annotation word3 = annotation(an, 16, 22);
        Annotation word4 = annotation(an, 24, 34);
        Annotation word5 = annotation(an, 35, 40);
        Annotation word6 = annotation(an, 43, 46);


        assertEquals("There should be 36 annotations", 21, an.size());
        assertBaseForm(word1, "м", 5, 6);
        assertBaseForm(word2, "гелиос", 7, 13);
        assertBaseForm(word3, "москва", 16, 22);
        assertBaseForm(word4, "существовать", 24, 34);
        assertBaseForm(word5, "много", 35, 40);
        assertBaseForm(word6, "лет", 43, 46);

    }

    public void testNoBastardFollowingBySlashWithDuplicates() throws Exception
    {
        Document doc = newDocument("(ФМ–ТИ–ФМ), и может быть");
        executePipeline(doc);
        printAnnotationsInfo(doc, "Morph");

        List<Annotation> test = allAnnotations(doc, "Morph");

        assertEquals(26, test.size());
        assertBaseForm(test.get(0), "ФМ");
        assertBaseForm(test.get(1), "ти");
        assertBaseForm(test.get(2), "ти");
        assertBaseForm(test.get(3), "ти");
        assertBaseForm(test.get(4), "ти");
        assertBaseForm(test.get(5), "ти");
        assertBaseForm(test.get(6), "ти");
        assertBaseForm(test.get(7), "ФМ");
        assertBaseForm(test.get(8), "и");
        assertBaseForm(test.get(23), "может");
        assertBaseForm(test.get(24), "мочь");
        assertBaseForm(test.get(25), "быть");


    }

    private Annotation annotation(AnnotationSet anSet, long start, long end)
    {
        return (Annotation) anSet.get(start, end).iterator().next();
    }

    private void assertBaseForm(Annotation word1, String expectedBaseForm)
    {
        assertEquals(expectedBaseForm, word1.getFeatures().get("baseForm"));
    }

    private void assertBaseForm(Annotation word1, String expectedBaseForm, int expectedStart, int expectedEnd)
    {
        assertEquals(expectedBaseForm, word1.getFeatures().get("baseForm"));
        assertEquals(expectedStart, (long) word1.getStartNode().getOffset());
        assertEquals(expectedEnd, (long) word1.getEndNode().getOffset());
    }

}
