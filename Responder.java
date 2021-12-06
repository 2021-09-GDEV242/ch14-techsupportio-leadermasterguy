import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author Nicholas Trilone
 * @version 2021.12.06
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    // The name of the file containing the keys and responses used for the response map.
    private static final String FILE_OF_RESPONSE_MAP = "responses.txt";
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_RESPONSE_MAP);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            BufferedReader reader2=Files.newBufferedReader(path, charset);
            String line = reader.readLine();
            String ahead = reader2.readLine();
            ahead = reader2.readLine();

            //filler variable for counting
            int i=0;

            //variable for iterating through totalWordArray
            int word = 0;

            //current response
            String response = "";

            //array of keys for an individual response
            String[] wordArray={};

            //whether or not program is on the first line of the response
            boolean first = true;
            while(line!=null&&ahead!=null) {  
                if(first==false){
                    response=response+line;
                }
                if(first==true&&(line!=null||line!="")){
                    wordArray = line.split(",");
                    i=wordArray.length;
                    first=false;
                }
                if(ahead.equals("")){
                    line = reader.readLine();
                    response=response+" "+line;
                    word=0;
                    while(word<wordArray.length){
                        responseMap.put(wordArray[word].trim(), 
                            response);
                        word++;
                    } 
                    first=true;
                }
                line = reader.readLine();
                ahead = reader2.readLine();
            }
            if(ahead==null){
                line = reader.readLine();
                    response=response+" "+line;
                    word=0;
                    while(word<wordArray.length){
                        responseMap.put(wordArray[word].trim(), 
                            response);
                        word++;
                    } 
                    first=true;
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_RESPONSE_MAP);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                FILE_OF_RESPONSE_MAP);
        }
        System.out.println(responseMap);
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            BufferedReader reader2=Files.newBufferedReader(path, charset);
            String response = reader.readLine();

            //string that will hold the response one line ahead of the current one
            String ahead = reader2.readLine();

            //string that will hold in-progress response combinations
            String combination = "";

            ahead = reader2.readLine();
            while(response != null&&ahead!=null) {  
                if(combination==""&&response.equals("")==false&&ahead.equals("")){
                    defaultResponses.add(response);
                }
                else if(combination!=""&&ahead.equals("")){
                    defaultResponses.add(combination);
                    combination="";
                }
                else if(response.equals("")==false&&ahead.equals("")==false){
                    if(combination==""){
                        combination=response+ahead;
                    }
                    else{
                        combination=combination+ahead;
                    }
                }
                response = reader.readLine();
                ahead = reader2.readLine();
            }
            if(response.equals("")==false){
                defaultResponses.add(response);
                System.out.println(response);
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                FILE_OF_DEFAULT_RESPONSES);
        }
        // Make sure we have at least one response.
        if(defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
