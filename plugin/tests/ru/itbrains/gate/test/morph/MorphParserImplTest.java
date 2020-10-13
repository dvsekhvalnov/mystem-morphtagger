package ru.itbrains.gate.test.morph;

import junit.framework.TestCase;
import ru.itbrains.gate.morph.MorphInfo;
import ru.itbrains.gate.morph.MorphParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MorphParserImplTest extends TestCase
{
    private MorphParser parser;

    public void setUp() throws Exception
    {
        parser = new MorphParser();
    }

    public void testRunParser() throws Exception
    {


        List<MorphInfo> result = parser.runParser("ФОРМА\n" +
                                                  "ДАННЫЕ\n" +
                                                  "О\n" +
                                                  "ПРОЕКТЕ\n" +
                                                  "Номер\n" +
                                                  "проекта\n" +
                                                  "Название\n" +
                                                  "проекта","utf-8");
        MorphInfo word1 = result.get(0);
        MorphInfo word2 = result.get(1);
        MorphInfo word3 = result.get(2);
        MorphInfo word4 = result.get(3);
        MorphInfo word5 = result.get(4);
        MorphInfo word6 = result.get(5);
        MorphInfo word7 = result.get(6);
        MorphInfo word8 = result.get(7);

        assertEquals(result.size(), 8);
        assertEquals("ФОРМА", word1.getOriginalWord());
        assertEquals("ДАННЫЕ", word2.getOriginalWord());
        assertEquals("О", word3.getOriginalWord());
        assertEquals("ПРОЕКТЕ", word4.getOriginalWord());
        assertEquals("Номер", word5.getOriginalWord());
        assertEquals("проекта", word6.getOriginalWord());
        assertEquals("Название", word7.getOriginalWord());
        assertEquals("проекта", word8.getOriginalWord());

    }

    public void testRun() throws Exception
    {

        assertEquals("и{и=INTJ=|и=PART=|и=S,сокр=им,ед|=S,сокр=им,мн|=S,сокр=род,ед|=S,сокр=род,мн|=S,сокр=дат,ед|=S,сокр=дат,мн|=S,сокр=вин,ед|=S,сокр=вин,мн|=S,сокр=твор,ед|=S,сокр=твор,мн|=S,сокр=пр,ед|=S,сокр=пр,мн|и=CONJ=}\n" +
                     "и{и=INTJ=|и=PART=|и=S,сокр=им,ед|=S,сокр=им,мн|=S,сокр=род,ед|=S,сокр=род,мн|=S,сокр=дат,ед|=S,сокр=дат,мн|=S,сокр=вин,ед|=S,сокр=вин,мн|=S,сокр=твор,ед|=S,сокр=твор,мн|=S,сокр=пр,ед|=S,сокр=пр,мн|и=CONJ=}\n" +
                     "и{и=INTJ=|и=PART=|и=S,сокр=им,ед|=S,сокр=им,мн|=S,сокр=род,ед|=S,сокр=род,мн|=S,сокр=дат,ед|=S,сокр=дат,мн|=S,сокр=вин,ед|=S,сокр=вин,мн|=S,сокр=твор,ед|=S,сокр=твор,мн|=S,сокр=пр,ед|=S,сокр=пр,мн|и=CONJ=}",
                     parser.run("и" +
                                "\nи" +
                                "\nи","utf-8").trim());
    }

    public void testParseAdjectiveDifferentGenders()
    {

        Map<String, String> expectedFeaturesFem = expectedFeature("adjective",
                                                                  "nominative",
                                                                  "feminine",
                                                                  "singular",
                                                                  "голубой", null, null, null, null, null, null);

        Map<String, String> expectedFeaturesMasc = expectedFeature("adjective",
                                                                   "nominative",
                                                                   "masculine",
                                                                   "singular",
                                                                   "желтый", null, null, null, null, null, null);

        Map<String, String> expectedFeaturesNeut = expectedFeature("adjective",
                                                                   "nominative",
                                                                   "neuter",
                                                                   "singular",
                                                                   "красный", null, null, null, null, null, null);

        Map testFeaturesFem = parser.parseMorphAnnotation("голубая","голубой=A=им,ед,жен").getHomonymGrammems().get(0);
        Map testFeaturesMasc = parser.parseMorphAnnotation("желтый","желтый=A=им,ед,муж").getHomonymGrammems().get(0);
        Map testFeaturesNeut = parser.parseMorphAnnotation("красное","красный=A=им,ед,сред").getHomonymGrammems().get(0);

        assertEquals(expectedFeaturesFem, testFeaturesFem);
        assertEquals(expectedFeaturesMasc, testFeaturesMasc);
        assertEquals(expectedFeaturesNeut, testFeaturesNeut);
    }

    public void testParseAdjectiveDifferentDegrees() throws Exception
    {
        Map<String, String> expectedSuper = expectedFeature("adjective",
                                                            "nominative",
                                                            "masculine",
                                                            "singular",
                                                            "глубокий",
                                                            null,
                                                            "superlative", null, null, null, null);

        Map<String, String> expectedCompar = expectedFeature("adjective",
                                                             null,
                                                             null,
                                                             null,
                                                             "глубокий",
                                                             null,
                                                             "comparative", null, null, null, null);

        Map testSuper = parser.parseMorphAnnotation("глубочайший","глубокий=A=им,ед,прев,муж").getHomonymGrammems().get(0);
        Map testComrap = parser.parseMorphAnnotation("глубже","глубокий=A=срав").getHomonymGrammems().get(0);

        assertEquals(expectedSuper, testSuper);
        assertEquals(expectedCompar, testComrap);

    }

    public void testParseAdjectiveForms() throws Exception
    {
        Map<String, String> expectedBrief = expectedFeature("adjective",
                                                            null,
                                                            "masculine",
                                                            "singular",
                                                            "высокий",
                                                            null,
                                                            null,
                                                            "brief", null, null, null);

        Map testBrief = parser.parseMorphAnnotation("высок","высокий=A=ед,кр,муж").getHomonymGrammems().get(0);

        assertEquals(expectedBrief, testBrief);
    }

    public void testParseSubstantiveDifferentCases() throws Exception
    {
        Map<String, String> expectedFeaturesNom = expectedFeature("substantive",
                                                                  "nominative",
                                                                  "feminine",
                                                                  "singular",
                                                                  "земля",
                                                                  "inanimate", null, null, null, null, null);

        Map<String, String> expectedFeaturesGen = expectedFeature("substantive",
                                                                  "genitive",
                                                                  "feminine",
                                                                  "singular",
                                                                  "земля",
                                                                  "inanimate", null, null, null, null, null);

        Map<String, String> expectedFeaturesDat = expectedFeature("substantive",
                                                                  "dative",
                                                                  "feminine",
                                                                  "singular",
                                                                  "земля",
                                                                  "inanimate", null, null, null, null, null);

        Map<String, String> expectedFeaturesAck = expectedFeature("substantive",
                                                                  "accusative",
                                                                  "feminine",
                                                                  "singular",
                                                                  "земля",
                                                                  "inanimate", null, null, null, null, null);

        Map<String, String> expectedFeaturesInst = expectedFeature("substantive",
                                                                   "instrumental",
                                                                   "feminine",
                                                                   "singular",
                                                                   "земля",
                                                                   "inanimate", null, null, null, null, null);

        Map<String, String> expectedFeaturesPart = expectedFeature("substantive",
                                                                   "partitive",
                                                                   "masculine",
                                                                   "singular",
                                                                   "чай",
                                                                   "inanimate", null, null, null, null, null);

        Map<String, String> expectedFeaturesAbl = expectedFeature("substantive",
                                                                  "ablative",
                                                                  "feminine",
                                                                  "singular",
                                                                  "земля",
                                                                  "inanimate", null, null, null, null, null);

        Map<String, String> expectedFeaturesLoc = expectedFeature("substantive",
                                                                  "locative",
                                                                  "masculine",
                                                                  "singular",
                                                                  "снег",
                                                                  "inanimate", null, null, null, null, null);

        Map testFeaturesNom = parser.parseMorphAnnotation("земля","земля=S,жен,неод=им,ед").getHomonymGrammems().get(0);
        Map testFeaturesGen = parser.parseMorphAnnotation("земли","земля=S,жен,неод=род,ед").getHomonymGrammems().get(0);
        Map testFeaturesPart = parser.parseMorphAnnotation("чаю","чай=S,муж,неод=парт,ед").getHomonymGrammems().get(0);
        Map testFeaturesDat = parser.parseMorphAnnotation("земле","земля=S,жен,неод=дат,ед").getHomonymGrammems().get(0);
        Map testFeaturesAck = parser.parseMorphAnnotation("землю","земля=S,жен,неод=вин,ед").getHomonymGrammems().get(0);
        Map testFeaturesInst = parser.parseMorphAnnotation("землей","земля=S,жен,неод=твор,ед").getHomonymGrammems().get(0);
        Map testFeaturesAbl = parser.parseMorphAnnotation("о земле","земля=S,жен,неод=пр,ед").getHomonymGrammems().get(0);
        Map testFeaturesLoc = parser.parseMorphAnnotation("на снегу","снег=S,муж,неод=местн,ед").getHomonymGrammems().get(0);

        assertEquals(expectedFeaturesNom, testFeaturesNom);
        assertEquals(expectedFeaturesGen, testFeaturesGen);
        assertEquals(expectedFeaturesPart, testFeaturesPart);
        assertEquals(expectedFeaturesDat, testFeaturesDat);
        assertEquals(expectedFeaturesAck, testFeaturesAck);
        assertEquals(expectedFeaturesInst, testFeaturesInst);
        assertEquals(expectedFeaturesAbl, testFeaturesAbl);
        assertEquals(expectedFeaturesLoc, testFeaturesLoc);

    }

    public void testParseNounDifferentAnimations() throws Exception
    {
        Map<String, String> expectedAnimated = expectedFeature("substantive",
                                                               "nominative",
                                                               "masculine",
                                                               "singular",
                                                               "утопленник",
                                                               "animated", null, null, null, null, null);

        Map<String, String> expectedInanimated = expectedFeature("substantive",
                                                                 "nominative",
                                                                 "feminine",
                                                                 "singular",
                                                                 "культура",
                                                                 "inanimate", null, null, null, null, null);

        Map testAnimated = parser.parseMorphAnnotation("утопленник","утопленник=S,муж,од=им,ед").getHomonymGrammems().get(0);
        Map testInanimated = parser.parseMorphAnnotation("культура","культура=S,жен,неод=им,ед").getHomonymGrammems().get(0);

        assertEquals(expectedAnimated, testAnimated);
        assertEquals(expectedInanimated, testInanimated);
    }

    public void testParseVerbDifferentAspectAndVoice() throws Exception
    {

        Map<String, String> expectedFeatureImperf = verbFeature("verb",
                                                                "imperfect",
                                                                null,
                                                                "singular",
                                                                "читать",
                                                                null, null, null, null, null, null, null, null, null);

        Map<String, String> expectedFeaturePerf = verbFeature("verb",
                                                              "perfect",
                                                              null,
                                                              "singular",
                                                              "прочитывать",
                                                              null, null, null, null, null, null, null, null, null);

        Map<String, String> expectedFeaturePassive = verbFeature("verb",
                                                                 "perfect",
                                                                 null,
                                                                 "singular",
                                                                 "сделать",
                                                                 "passive", null, null, null, null,
                                                                 "participle", "brief", null, null);

        Map testFeaturesImperf = parser.parseMorphAnnotation("читаю","читать=V,несов,ед").getHomonymGrammems().get(0);
        Map testFeaturesPerf = parser.parseMorphAnnotation("прочитал","прочитывать=V,ед,сов").getHomonymGrammems().get(0);
        Map testFeaturesPassive = parser.parseMorphAnnotation("сделана","сделать=V,сов,ед,прич,кр,страд").getHomonymGrammems().get(0);

        assertEquals(expectedFeatureImperf, testFeaturesImperf);
        assertEquals(expectedFeaturePerf, testFeaturesPerf);
        assertEquals(expectedFeaturePassive, testFeaturesPassive);
    }

    public void testParseVerbDifferentPerson() throws Exception
    {

        Map<String, String> expectedFeaturePer1 = verbFeature("verb",
                                                              "imperfect",
                                                              "person1",
                                                              "singular",
                                                              "читать",
                                                              null, null, null, null, null, null, null, null, null);

        Map<String, String> expectedFeaturePer2 = verbFeature("verb",
                                                              "imperfect",
                                                              "person2",
                                                              "singular",
                                                              "читать",
                                                              null, null, null, null, null, null, null, null, null);

        Map<String, String> expectedFeaturePer3 = verbFeature("verb",
                                                              "imperfect",
                                                              "person3",
                                                              "plural",
                                                              "читать",
                                                              null, null, null, null, null, null, null, null, null);

        Map testFeaturesPer1 = parser.parseMorphAnnotation("читаю","читать=V,несов,ед,1-л").getHomonymGrammems().get(0);
        Map testFeaturesPer2 = parser.parseMorphAnnotation("читаешь","читать=V,несов,ед,2-л").getHomonymGrammems().get(0);
        Map testFeaturesPer3 = parser.parseMorphAnnotation("читают","читать=V,несов,мн,3-л").getHomonymGrammems().get(0);

        assertEquals(expectedFeaturePer1, testFeaturesPer1);
        assertEquals(expectedFeaturePer2, testFeaturesPer2);
        assertEquals(expectedFeaturePer3, testFeaturesPer3);
    }

    public void testParseVerbDifferentTense() throws Exception
    {

        Map<String, String> expectedFeaturePresent = verbFeature("verb",
                                                                 "imperfect",
                                                                 "person3",
                                                                 "plural",
                                                                 "быть",
                                                                 null,
                                                                 "present", null, null, null, null, null, null, null);

        Map<String, String> expectedFeatureNoPast = verbFeature("verb",
                                                                "imperfect",
                                                                "person1",
                                                                "singular",
                                                                "читать",
                                                                null,
                                                                "nopast", null, null, null, null, null, null, null);

        Map<String, String> expectedFeaturePast = verbFeature("verb",
                                                              "imperfect",
                                                              null,
                                                              "singular",
                                                              "читать",
                                                              null,
                                                              "past", null, null, null, null, null, null, null);

        Map testFeaturesPresent = parser.parseMorphAnnotation("есть","быть=V=наст,мн,3-л,несов").getHomonymGrammems().get(0);
        Map testFeaturesNoPast = parser.parseMorphAnnotation("читаю","читать=V,несов=непрош,ед,1-л").getHomonymGrammems().get(0);
        Map testFeaturesPast = parser.parseMorphAnnotation("читал","читать=V,несов=прош,ед").getHomonymGrammems().get(0);

        assertEquals(expectedFeaturePresent, testFeaturesPresent);
        assertEquals(expectedFeatureNoPast, testFeaturesNoPast);
        assertEquals(expectedFeaturePast, testFeaturesPast);

    }

    public void testParseVerbDifferentMood() throws Exception
    {
        Map<String, String> expectedIndicative = verbFeature("verb",
                                                             "imperfect",
                                                             "person3",
                                                             "plural",
                                                             "читать",
                                                             null,
                                                             "nopast",
                                                             "indicative", null, null, null, null, null, null);

        Map<String, String> expectedImperative = verbFeature("verb",
                                                             "imperfect",
                                                             "person2",
                                                             "singular",
                                                             "украшать",
                                                             null,
                                                             null,
                                                             "imperative", null, null, null, null, null, null);

        Map testIndicative = parser.parseMorphAnnotation("читают","читать=V,несов=непрош,мн,изъяв,3-л").getHomonymGrammems().get(0);
        Map testImperative = parser.parseMorphAnnotation("украшай","украшать=V=ед,пов,2-л,несов").getHomonymGrammems().get(0);

        assertEquals(expectedIndicative, testIndicative);
        assertEquals(expectedImperative, testImperative);
    }

    public void testParseVerbDifferentRepresentation() throws Exception
    {
        //читающий{читать=V,несов=непрош,им,ед,прич,муж}
        //читая{читать=V,несов=непрош,деепр}
        //украшать{украшать=V=инф,несов}
        Map<String, String> expectedParticiple = verbFeature("verb",
                                                             "imperfect",
                                                             null,
                                                             "singular",
                                                             "читать",
                                                             null,
                                                             "nopast",
                                                             null,
                                                             "nominative",
                                                             "masculine",
                                                             "participle", null, null, null);

        Map<String, String> expectedGerund = verbFeature("verb",
                                                         "imperfect",
                                                         null,
                                                         null,
                                                         "читать",
                                                         null,
                                                         "nopast",
                                                         null,
                                                         null,
                                                         null,
                                                         "gerund", null, null, null);

        Map<String, String> expectedInf = verbFeature("verb",
                                                      "imperfect",
                                                      null, null,
                                                      "украшать",
                                                      null, null, null, null, null,
                                                      "infinitive", null, null, null);

        Map testParticiple = parser.parseMorphAnnotation("читающий","читать=V,несов=непрош,им,ед,прич,муж").getHomonymGrammems().get(0);
        Map testGerund = parser.parseMorphAnnotation("читая","читать=V,несов=непрош,деепр").getHomonymGrammems().get(0);
        Map testInf = parser.parseMorphAnnotation("украшать","украшать=V=инф,несов").getHomonymGrammems().get(0);

        assertEquals(expectedParticiple, testParticiple);
        assertEquals(expectedGerund, testGerund);
        assertEquals(expectedInf, testInf);
    }

    public void testParsePreposition() throws Exception
    {

        Map<String, String> expected = expectedFeature("preposition", null, null,
                                                       null, "кроме", null, null, null, null, null, null);

        Map<String, String> test = parser.parseMorphAnnotation("кроме","кроме=PR=").getHomonymGrammems().get(0);

        assertEquals(expected, test);
    }

    public void testParseAdverb() throws Exception
    {
        //жаль{жалить=V,несов=ед,пов,2-л|жаль=ADV,прдк,вводн=}
        Map<String, String> expected = expectedFeature("adverb", null, null, null,
                                                       "по-дружески", null, null, null, null, null, null);

        Map<String, String> expectedPraedicParent = expectedFeature("adverb",
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    "жаль",
                                                                    null, null, null, "predicate-noun", "parenthetical", null);

        Map<String, String> test = parser.parseMorphAnnotation("по-дружески","по-дружески=ADV=").getHomonymGrammems().get(0);
        Map<String, String> testPraedicParent = parser.parseMorphAnnotation("жаль","жаль=ADV,прдк,вводн=").getHomonymGrammems().get(0);

        assertEquals(expected, test);
        assertEquals(expectedPraedicParent, testPraedicParent);
    }

    public void testParseParticle() throws Exception
    {
        Map<String, String> expected = expectedFeature("particle", null, null, null,
                                                       "и", null, null, null, null, null, null);
        Map<String, String> test = parser.parseMorphAnnotation("и","и=PART=").getHomonymGrammems().get(0);

        assertEquals(expected, test);

    }

    public void testParseNumeral() throws Exception
    {
        //восьмидесятый{восьмидесятый=ANUM=им,ед,муж}
        Map<String, String> expected = expectedFeature("numeral", "nominative", null, null,
                                                       "шестьсот", null, null, null, null, null, null);

        Map<String, String> expectedAdjNumeral = expectedFeature("a-numeral",
                                                                 "nominative",
                                                                 "masculine",
                                                                 "singular",
                                                                 "восьмидесятый",
                                                                 null, null, null, null, null, null);

        Map<String, String> test = parser.parseMorphAnnotation("шестьсот","шестьсот=NUM=им").getHomonymGrammems().get(0);
        Map<String, String> testAdjNumeral = parser.parseMorphAnnotation("восьмидесятый","восьмидесятый=ANUM=им,ед,муж").getHomonymGrammems().get(0);

        assertEquals(expected, test);
        assertEquals(expectedAdjNumeral, testAdjNumeral);
    }

    public void testParseConjuction() throws Exception
    {
        //что{что=CONJ=}
        Map<String, String> expected = expectedFeature("conjunction", null, null, null,
                                                       "что", null, null, null, null, null, null);
        Map<String, String> test = parser.parseMorphAnnotation("что","что=CONJ=").getHomonymGrammems().get(0);

        assertEquals(expected, test);
    }

    public void testParseInterjuction() throws Exception
    {
        Map<String, String> expected = expectedFeature("interjection", null, null, null,
                                                       "и", null, null, null, null, null, null);
        Map<String, String> test = parser.parseMorphAnnotation("и","и=INTJ=").getHomonymGrammems().get(0);

        assertEquals(expected, test);
    }

    public void testParsePronoun() throws Exception
    {
        //они{они=SPRO,мн,од=им}
        //что{что=ADVPRO=}
        //твой{твой=APRO=им,ед,муж}
        Map<String, String> expectedSPronoun = expectedFeature("s-pronoun",
                                                               "nominative",
                                                               null,
                                                               "plural",
                                                               "они", null, null, null, null, null, null);

        Map<String, String> expectedADVPronoun = expectedFeature("adv-pronoun",
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 "что", null, null, null, null, null, null);

        Map<String, String> expectedAPronoun = expectedFeature("a-pronoun",
                                                               "nominative",
                                                               "masculine",
                                                               "singular",
                                                               "твой", null, null, null, null, null, null);

        Map<String, String> testSPronoun = parser.parseMorphAnnotation("они","они=SPRO,мн=им").getHomonymGrammems().get(0);
        Map<String, String> testADVPronoun = parser.parseMorphAnnotation("что","что=ADVPRO=").getHomonymGrammems().get(0);
        Map<String, String> testAPronoun = parser.parseMorphAnnotation("твой","твой=APRO=им,ед,муж").getHomonymGrammems().get(0);

        assertEquals(expectedSPronoun, testSPronoun);
        assertEquals(expectedADVPronoun, testADVPronoun);
        assertEquals(expectedAPronoun, testAPronoun);

    }


    public void testParseNoBastard() throws Exception
    {


        Map<String, String> expectedNoBastart = expectedFeature(null, null, null,
                                                                null, "Д", null,
                                                                null, null, null,
                                                                null, "true");
        Map<String, String> testNoBastard = parser.parseMorphAnnotation("Д","Д??").getHomonymGrammems().get(0);

        assertEquals(expectedNoBastart, testNoBastard);
    }

    public void testRunParserNoBastardFollowingNoBastard() throws Exception
    {

        List<MorphInfo> test = parser.runParser("ФМ ТИ","utf-8");

        assertEquals(2, test.size());
        assertEquals("ФМ", test.get(0).getOriginalWord());
        assertEquals("ФМ", test.get(0).getHomonymGrammems().get(0).get("baseForm"));

        assertEquals("ТИ", test.get(1).getOriginalWord());
        assertEquals("ти", test.get(1).getHomonymGrammems().get(0).get("baseForm"));
    }

    public void testParsePredictForm() throws Exception
    {

        Map<String, String> expectedPredict = verbFeature("verb",
                                                          "imperfect",
                                                          null,
                                                          "singular",
                                                          "динафить",
                                                          null,
                                                          "past",
                                                          "indicative",
                                                          null,
                                                          "masculine",
                                                          null,
                                                          null,
                                                          "true", null);
        Map<String, String> testPredict = parser.parseMorphAnnotation("Динафил","динафить?=V,несов=прош,ед,изъяв,муж").getHomonymGrammems().get(0);
        assertEquals(expectedPredict, testPredict);
    }

    public void testParseEnglish() throws Exception
    {
        //finish{finish}
        Map<String, String> expectedEnglish = expectedFeature(null, null, null, null,
                                                              "finish",
                                                              null, null, null, null,
                                                              null, null);

        Map<String, String> testEnglish = parser.parseMorphAnnotation("finish","finish").getHomonymGrammems().get(0);
        assertEquals(expectedEnglish, testEnglish);
    }

    public void testParseHomonyms() throws Exception
    {
    //стали{становиться=V,нп=прош,мн,изъяв,сов|сталь=S,жен,неод=им,мн|=S,жен,неод=род,ед|=S,жен,неод=дат,ед|=S,жен,неод=вин,мн|=S,жен,неод=пр,ед}


        List<MorphInfo> test= parser.parseMorphAnnotations("стали{становиться=V,нп=прош,мн,изъяв,сов|сталь=S,жен,неод=им,мн|=S,жен,неод=род,ед|=S,жен,неод=дат,ед|=S,жен,неод=вин,мн|=S,жен,неод=пр,ед}");

        List<Map<String, String>> testHomonyms=test.get(0).getHomonymGrammems();

        for (Map<String, String> homonym : testHomonyms)
        {
            System.out.println(homonym);
        }


        assertEquals("There should be 6 homonyms", 6, testHomonyms.size());
        assertEquals(features("aspect","perfect",
                              "tense","past",
                              "mood","indicative",
                              "transitivity","nontransitive",
                              "baseForm","становиться",
                              "multiplicity","plural",
                              "pos","verb"), testHomonyms.get(0));

        assertEquals(features("animation", "inanimate",
                              "gender", "feminine",
                              "baseForm", "сталь",
                              "multiplicity", "plural",
                              "case", "nominative",
                              "pos", "substantive"), testHomonyms.get(1));

        assertEquals(features("animation", "inanimate",
                              "gender", "feminine",
                              "baseForm", "сталь",
                              "multiplicity", "singular",
                              "case", "genitive",
                              "pos", "substantive"), testHomonyms.get(2));

        assertEquals(features("animation", "inanimate",
                              "gender", "feminine",
                              "baseForm", "сталь",
                              "multiplicity", "singular",
                              "case", "dative",
                              "pos", "substantive"), testHomonyms.get(3));

        assertEquals(features("animation", "inanimate",
                              "gender", "feminine",
                              "baseForm", "сталь",
                              "multiplicity", "plural",
                              "case", "accusative",
                              "pos", "substantive"), testHomonyms.get(4));

        assertEquals(features("animation", "inanimate",
                              "gender", "feminine",
                              "baseForm", "сталь",
                              "multiplicity", "singular",
                              "case", "ablative",
                              "pos", "substantive"), testHomonyms.get(5));

    }

    public void testParseSameBaseFormHomonyms() throws Exception
    {
        //Название{название=S,сред,неод=им,ед|=S,сред,неод=вин,ед}
    }

    private Map<String, String> verbFeature(String partOfSpeech, String aspect, String person,
                                            String multiplicity, String baseForm, String voice,
                                            String tense, String mood, String posCase, String gender,
                                            String representative, String form, String prediction, String transitivity)

    {

        Map<String, String> expectedFeatures = new HashMap<String, String>();

        expectedFeatures.put("pos", partOfSpeech);
        expectedFeatures.put("aspect", aspect);

        addNotNull("person", person, expectedFeatures);

        addNotNull("multiplicity", multiplicity, expectedFeatures);
        expectedFeatures.put("baseForm", baseForm);

        addNotNull("voice", voice, expectedFeatures);
        addNotNull("tense", tense, expectedFeatures);
        addNotNull("mood", mood, expectedFeatures);
        addNotNull("case", posCase, expectedFeatures);
        addNotNull("gender", gender, expectedFeatures);
        addNotNull("representation", representative, expectedFeatures);
        addNotNull("form", form, expectedFeatures);
        addNotNull("prediction", prediction, expectedFeatures);
        addNotNull("transitivity", transitivity, expectedFeatures);

        return expectedFeatures;

    }

    private void addNotNull(String key, String value, Map<String, String> expectedFeatures)
    {
        if (value != null)
            expectedFeatures.put(key, value);
    }

    private Map<String, String> expectedFeature(String partOfSpeech, String posCase, String gender,
                                                String multiplicity, String baseForm, String animation,
                                                String comprasion, String form, String praedic,
                                                String parenthetical, String noBastards)
    {
        Map<String, String> expectedFeatures = new HashMap<String, String>();

        addNotNull("pos", partOfSpeech, expectedFeatures);
        addNotNull("case", posCase, expectedFeatures);
        addNotNull("gender", gender, expectedFeatures);
        addNotNull("multiplicity", multiplicity, expectedFeatures);
        addNotNull("baseForm", baseForm, expectedFeatures);
        addNotNull("animation", animation, expectedFeatures);
        addNotNull("degree", comprasion, expectedFeatures);
        addNotNull("form", form, expectedFeatures);
        addNotNull("predicate-noun", praedic, expectedFeatures);
        addNotNull("parenthetical", parenthetical, expectedFeatures);
        addNotNull("no-bastard", noBastards, expectedFeatures);

        return expectedFeatures;
    }

    public  static <T> Map<T, T> features(T... pairs)
    {
        if ((pairs.length % 2) != 0)
        {
            throw new IllegalArgumentException("Array should contain odd number of members;");
        }

        Map<T, T> res = new HashMap<T,T>();
        for (int i = 0; i < pairs.length; i += 2)
        {
            T key = pairs[i];
            T value = pairs[i + 1];

            res.put(key, value);
        }

        return res;
    }
}

