package ru.itbrains.gate.morph;

import ru.itbrains.system.OS;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Table-token lookup version
 */
public class MorphParser
{
    /**
     * Map lexical marker to appropriate target grammem feature
     */
    private Map<String, Feature> lookupFeatureTable = new HashMap<String, Feature>();

    private static final String NL = "\n";
    private static final String APP_WORK_FOLDER = ".morphtagger";
    private static final String MORPH_TAGGER_PARAMETERS = "-ni";
    private static final String MORPH_TAGGER_ENCODING = "-e";
    private static final String SEPARATOR = System.getProperty("file.separator");
    private String MORPH_TAGGER_COMMAND;
    private String MORPH_TAGGER_LOCATION;

    private Pattern tokenizer;
    private Pattern splitHomonyms;
    private Pattern entrySplit;

    public MorphParser()
    {
        this(null);
    }

    public MorphParser(String nativeFolder)
    {
        initNative(nativeFolder);

        //Helper regexes
        tokenizer = Pattern.compile("[,=]");
        splitHomonyms = Pattern.compile("\\|");
        entrySplit = Pattern.compile("[{}]");

        //parts-of-speech
        lookupFeatureTable.put("A", new Feature("pos", "adjective"));
        lookupFeatureTable.put("ADV", new Feature("pos", "adverb"));
        lookupFeatureTable.put("INTJ", new Feature("pos", "interjection"));
        lookupFeatureTable.put("NUM", new Feature("pos", "numeral"));
        lookupFeatureTable.put("S", new Feature("pos", "substantive"));
        lookupFeatureTable.put("V", new Feature("pos", "verb"));
        lookupFeatureTable.put("PR", new Feature("pos", "preposition"));
        lookupFeatureTable.put("PART", new Feature("pos", "particle"));
        lookupFeatureTable.put("CONJ", new Feature("pos", "conjunction"));
        lookupFeatureTable.put("SPRO", new Feature("pos", "s-pronoun"));
        lookupFeatureTable.put("ADVPRO", new Feature("pos", "adv-pronoun"));
        lookupFeatureTable.put("APRO", new Feature("pos", "a-pronoun"));
        lookupFeatureTable.put("ANUM", new Feature("pos", "a-numeral"));
        lookupFeatureTable.put("COM", new Feature("pos", "composite"));

        //cases
        lookupFeatureTable.put("им", new Feature("case", "nominative"));
        lookupFeatureTable.put("род", new Feature("case", "genitive"));
        lookupFeatureTable.put("дат", new Feature("case", "dative"));
        lookupFeatureTable.put("вин", new Feature("case", "accusative"));
        lookupFeatureTable.put("твор", new Feature("case", "instrumental"));
        lookupFeatureTable.put("пр", new Feature("case", "ablative"));
        lookupFeatureTable.put("парт", new Feature("case", "partitive"));
        lookupFeatureTable.put("местн", new Feature("case", "locative"));
        lookupFeatureTable.put("зват", new Feature("case", "vocative"));

        //multiplicity
        lookupFeatureTable.put("ед", new Feature("multiplicity", "singular"));
        lookupFeatureTable.put("мн", new Feature("multiplicity", "plural"));

        //gender
        lookupFeatureTable.put("жен", new Feature("gender", "feminine"));
        lookupFeatureTable.put("муж", new Feature("gender", "masculine"));
        lookupFeatureTable.put("сред", new Feature("gender", "neuter"));

        //animation
        lookupFeatureTable.put("неод", new Feature("animation", "inanimate"));
        lookupFeatureTable.put("од", new Feature("animation", "animated"));

        //degree
        lookupFeatureTable.put("прев", new Feature("degree", "superlative"));
        lookupFeatureTable.put("срав", new Feature("degree", "comparative"));

        //adjective form
        lookupFeatureTable.put("кр", new Feature("form", "brief"));
        lookupFeatureTable.put("полн", new Feature("form", "full"));
        lookupFeatureTable.put("притяж", new Feature("form", "possessive"));

        //predicative
        lookupFeatureTable.put("прдк", new Feature("predicate-noun", "predicate-noun"));

        //parenthetical
        lookupFeatureTable.put("вводн", new Feature("parenthetical", "parenthetical"));

        //verb aspect
        lookupFeatureTable.put("несов", new Feature("aspect", "imperfect"));
        lookupFeatureTable.put("сов", new Feature("aspect", "perfect"));

        //verb person
        lookupFeatureTable.put("1-л", new Feature("person", "person1"));
        lookupFeatureTable.put("2-л", new Feature("person", "person2"));
        lookupFeatureTable.put("3-л", new Feature("person", "person3"));

        //verb voice
        lookupFeatureTable.put("страд", new Feature("voice", "passive"));
        lookupFeatureTable.put("действ", new Feature("voice", "active"));

        //verb tense
        lookupFeatureTable.put("наст", new Feature("tense", "present"));
        lookupFeatureTable.put("непрош", new Feature("tense", "nopast"));
        lookupFeatureTable.put("прош", new Feature("tense", "past"));

        //verb mood
        lookupFeatureTable.put("изъяв", new Feature("mood", "indicative"));
        lookupFeatureTable.put("пов", new Feature("mood", "imperative"));

        //verb representative
        lookupFeatureTable.put("прич", new Feature("representation", "participle"));
        lookupFeatureTable.put("деепр", new Feature("representation", "gerund"));
        lookupFeatureTable.put("инф", new Feature("representation", "infinitive"));

        //verb transitivity
        lookupFeatureTable.put("пе", new Feature("transitivity", "transitive"));
        lookupFeatureTable.put("нп", new Feature("transitivity", "nontransitive"));


        //2nd name
        lookupFeatureTable.put("фам", new Feature("surname", "true"));
        lookupFeatureTable.put("имя", new Feature("first-name", "true"));
        lookupFeatureTable.put("отч", new Feature("last-name", "true"));

        //other
        lookupFeatureTable.put("гео", new Feature("geo", "true"));
        lookupFeatureTable.put("затр", new Feature("impede", "true"));
        lookupFeatureTable.put("искаж", new Feature("distorted", "true"));
        lookupFeatureTable.put("мж", new Feature("common-gender", "true"));
        lookupFeatureTable.put("разг", new Feature("colloquial", "true"));
        lookupFeatureTable.put("редк", new Feature("rare", "true"));
        lookupFeatureTable.put("сокр", new Feature("abbreviation", "true"));
        lookupFeatureTable.put("устар", new Feature("archaic", "true"));
        lookupFeatureTable.put("обсц", new Feature("obscene", "true"));
    }

    private void initNative(String nativeFolder)
    {
        if(nativeFolder==null)
            nativeFolder="";

        if (OS.isWindows())
        {
            MORPH_TAGGER_COMMAND = "mystem.exe";

            MORPH_TAGGER_LOCATION = nativeFolder + "native" + SEPARATOR + "win32" + SEPARATOR;
        }

        else if (OS.isMac())
        {
            MORPH_TAGGER_COMMAND = "mystem";
            MORPH_TAGGER_LOCATION = nativeFolder+"native" + SEPARATOR + "osx" + SEPARATOR;
        }

        else if (OS.isUnix())
        {
            MORPH_TAGGER_COMMAND = "mystem";
            MORPH_TAGGER_LOCATION = nativeFolder+"native" + SEPARATOR + "linux" + SEPARATOR;
        }

        else if (OS.isFreeBSD())
        {
            MORPH_TAGGER_COMMAND = "mystem";
            MORPH_TAGGER_LOCATION = nativeFolder+"native" + SEPARATOR + "freebsd" + SEPARATOR;
        }

        else if (OS.isSolaris())
        {
            throw new UnsupportedOperationException("Mystem is not supported on Solaris platform.");
        }
    }

    public List<MorphInfo> runParser(String inputText, String encoding) throws IOException
    {

        String output = run(inputText,encoding);

        return parseMorphAnnotations(output);
    }

    public List<MorphInfo> parseMorphAnnotations(String output)
    {
        List<MorphInfo> result = new LinkedList<MorphInfo>();

        String[] annotationTokens = entrySplit.split(output);

        for (int i = 0; i < annotationTokens.length - 1; i += 2)
        {
            result.add(parseMorphAnnotation(annotationTokens[i], annotationTokens[i + 1]));
        }

        return result;
    }

    public MorphInfo parseMorphAnnotation(String originalWord, String contentLine)
    {
        MorphInfo info = new MorphInfo(originalWord);

        boolean noBastard = contentLine.endsWith("??");

        if (noBastard)
        {
            Map<String, String> features = new HashMap<String, String>();
            features.put("no-bastard", "true");
            features.put("baseForm", originalWord.trim());
            info.addHomonym(features);
        }
        else
        {

            String[] homonymAnnotations = splitHomonyms.split(contentLine);

            String currentBaseForm = null;
            for (int j = 0; j < homonymAnnotations.length; j++)
            {
                String homonym = homonymAnnotations[j];
                Map<String, String> homonymFeatures = new HashMap<String, String>();
                parseAnnotation(homonym, homonymFeatures, currentBaseForm);
                info.addHomonym(homonymFeatures);
                currentBaseForm = homonymFeatures.get("baseForm");
            }
        }

        return info;
    }


    private void parseAnnotation(String annotation, Map<String, String> result, String currentBaseForm)
    {
        String[] matches = tokenizer.split(annotation);

        String baseForm = matches[0];

        if (baseForm.length() == 0)
            baseForm = currentBaseForm;

        int lastCharIndex = baseForm.length() - 1;

        if (baseForm.charAt(lastCharIndex) == '?')
        {
            result.put("baseForm", baseForm.substring(0, lastCharIndex));
            result.put("prediction", "true");
        }
        else
            result.put("baseForm", baseForm);

        for (int i = 1; i < matches.length; i++)
        {
            Feature feature = lookupFeatureTable.get(matches[i]);
            //unknown feature
            if (feature == null)
                System.out.println("Unknown feature detected: " + matches[i]);
            else
                result.put(feature.getName(), feature.getValue());
        }

    }

    public String run(String inputText,String encoding) throws IOException
    {
        File outputPath = new File(createTempFolder(),
                                   outputFilename());
        File inputPath = new File(createTempFolder(),
                                  inputFilename());

        saveToFile(inputPath, inputText,encoding);

        execNative(inputPath, outputPath,encoding);

        return readFile(outputPath,encoding);

    }

    private String readFile(File outputPath,String encoding) throws IOException
    {
        FileInputStream in=new FileInputStream(outputPath);
        InputStreamReader isr = new InputStreamReader(in, encoding);
        BufferedReader reader = new BufferedReader(isr, 4096);


        String line;
        StringBuffer result = new StringBuffer();

        while ((line = reader.readLine()) != null)
        {
            result.append(line);
            result.append(NL);
        }

        reader.close();
        isr.close();
        in.close();

        return result.toString();
    }

    private void execNative(File inputPath, File outputPath,String encoding)
    {
        try
        {
            String[] shellCommand = new String[]
                    {
                            morphTaggerExecutable(),
                            MORPH_TAGGER_PARAMETERS,
                            MORPH_TAGGER_ENCODING,
                            encoding,
                            quotePath(inputPath),
                            quotePath(outputPath)
                    };

            Process morphParser = Runtime.getRuntime().exec(shellCommand);
            morphParser.waitFor();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String inputFilename()
    {
        return "morph_tagger.input." + generateTimestamp() + ".txt";
    }

    private String outputFilename()
    {
        return "morph_tagger.output." + generateTimestamp() + ".txt";
    }

    private String generateTimestamp()
    {
        Date now = new Date();
        DateFormat dateFormater = new SimpleDateFormat("dd-mm-HH-MM");
        return dateFormater.format(now);
    }

    private String morphTaggerExecutable()
    {
        File morphTaggerExecutable=new File(MORPH_TAGGER_LOCATION+MORPH_TAGGER_COMMAND);

        if(!morphTaggerExecutable.isAbsolute())
            morphTaggerExecutable = new File(System.getProperty("user.dir"),
                                              MORPH_TAGGER_LOCATION + MORPH_TAGGER_COMMAND);

        if (!morphTaggerExecutable.exists())
            throw new RuntimeException(morphTaggerExecutable.getAbsolutePath() + " not found.");

        return morphTaggerExecutable.getAbsolutePath();
    }


    private String quotePath(File path)
    {
        if (OS.isWindows())
            return "\"" + path.getAbsolutePath() + "\"";
        else
            return path.getAbsolutePath();
    }

    /**
     * Creates working folder under user home directory
     */
    private File createTempFolder()
    {
        String userHome = System.getProperty("user.home");
        File workDir = new File(userHome, APP_WORK_FOLDER);

        if (!workDir.exists())
            workDir.mkdir();

        return workDir;
    }

    private void saveToFile(File pathToFile, String content, String encoding) throws IOException
    {
        FileOutputStream out = new FileOutputStream(pathToFile);

        Writer writer = new OutputStreamWriter(out, encoding);
        writer.write(content);

        writer.close();
        out.close();
    }

}
