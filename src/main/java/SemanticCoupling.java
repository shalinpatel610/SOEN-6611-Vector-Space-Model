import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

public class SemanticCoupling {


    private static String documentsPath;
    private static int maxPrintResults;
    private static String targetFilename;

    private List<JavaClass> javaClasses = new ArrayList<>();
    private Dictionary dictionary;
    // the key is Java file name, and value is the cosine similarity
    private Hashtable<String, Double> documentSimilarities;
    //a list of ranked Java files in descending order. Each entry contains a Java files name and its similarity score
    private List<Map.Entry<String, Double>> rankedDocuments;

    public static void main(String[] args) {
        // accept the user input parameters
        documentsPath = args[0];
        targetFilename = args[1];
        maxPrintResults = Integer.parseInt(args[2]);

        // initialize the StanfordCoreNLP
        Utility.initializeStanfordCoreNLP();

        SemanticCoupling semanticCoupling = new SemanticCoupling();
        semanticCoupling.run();
    }

    public void run() {
        loadDocuments(documentsPath);

        buildDictionary(javaClasses);


        // Calculate TF-IDF for every Java file
        javaClasses.forEach(item -> item.setTfIdfs(item.calculateTfIdfs(dictionary)));
        // Calculate the cosine similarities given the target Java file name.
        documentSimilarities = Utility.getDocumentSimilarities(targetFilename, javaClasses);
        // rank the Java files based on the similarity
        rankedDocuments = Utility.rankSimilarDocuments(documentSimilarities);
        //print the top k Java files based on the similarity score.
        Utility.printTopKSimilarDocuments(rankedDocuments, maxPrintResults);
        // dump the content of dictionary to disk
        Utility.dumpDictionaryToDisk(dictionary);
        //dump the content of the target class
        Utility.dumpJavaClassToDisk(targetFilename, javaClasses);
    }

    /**
     * Load documents from the given path.
     * @param path
     */
    private void loadDocuments(String path) {
        try {
            File dir = new File(path);
            String[] extensions = new String[] { "java"};
            Collection<File> files = FileUtils.listFiles(dir, extensions, true);
            for (File file : files) {
                javaClasses.add(new JavaClass(file.getName(), file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Build a dictionary based on the processed Java files.
     * @param _javaClasses
     */
    private void buildDictionary(List<JavaClass> _javaClasses) {
        dictionary = new Dictionary(_javaClasses);
    }

}
