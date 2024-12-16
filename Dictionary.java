/*-----------------------------------------------------------------------------------
 * This is a program that creates an undefined dictionary.
 * You are able to add/manipulate words as you please.
 * You are also able to import a .txt File with proper formatting and export it to
 * another .txt file that shows the word, frequency, and definition.
 * -----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 * AUTHOR: MILES JENNINGS 12/2/24
 * -----------------------------------------------------------------------------------
 * ~ EECS 40; Professor Zhou Lee; University of California Irvine ~

 */
package Dictionary;

import java.io.PrintWriter; //PrintWriter class is a utility in Java used for writing formatted data to text files
import java.util.*; //must import java.util for usage of hashmap and arraylist
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


/*----------------------------------------------------------------------------------------
        Exception handling classes, will utilize in try, throw, catch
        Inherits RuntimeException class
        Must be outside dictionary class
*/
/**
 * Exception thrown for invalid word input.
 */
class InvalidWordError extends RuntimeException {
    public InvalidWordError() {
        super("Invalid Word Error.");
    }
}
/**
 * Exception thrown for duplicate word entries.
 */
class WordDuplicatedError extends RuntimeException {
    public WordDuplicatedError() {
        super("Duplicate Word Error.");
    }
}
/**
 * Exception thrown when a word is not found in the dictionary.
 */
class WordNotFoundError extends RuntimeException {
    public WordNotFoundError() {
        super("Word Not Found Error.");
    }
}
/**
 * Exception thrown when a file is not found during import/export operations.
 */
class FileNotFoundError extends RuntimeException {
    public FileNotFoundError() {
        super("File Not Found Error.");
    }
}
// END OF EXCEPTION CLASSES----------------------------------------------------------------------------------------

/**
 * Defines and creates the connection between actionlisteners and the Swing GUI
 * Consists of Adding, finding, Modifying, Clearing, Removing, Importing, and Exporting
 * Allows one to see search history and words similar to what they are searching for
 */
public class Dictionary {
    //Elements from Swing
    public JButton FINDButton;
    public JButton ADDButton;
    public JButton MODIFYButton;
    public JButton REMOVEButton;
    public JButton CLEARButton;
    public JButton IMPORTButton;
    public JButton EXPORTButton;
    public JTextField TextNewWord;
    public JTextField TextOriginalWord;
    public JTextField TextFreqWord1;
    public JTextField TextFreqWord2;
    public JTextField TextFreqWord3;
    public JTextArea TextArea;
    public JList searchHistoryList;
    public JTextField TextFilePath;
    public JPanel DictionaryPanel;


    //Instance variables to use in various action listeners
    public String word = "";           // Initialize word variable
    public String definition = "";     // Initialize definition as empty
    public String key = "";            // Initializes the string key used in
    public String filepath = "";       //used in import/export
    public String file = "";
    public String originalWord = "";
    public String newWord = "";

    /**
     * Helper Class to hold and store meaning and frequency of a word.
     */
    static class Word_Data {
        String meaning = "";
        int frequency = 0;

        /**
         * Helper method in class Word_Data to set the meaning of the current word to the meaning and initialize frequency,
        */
         public Word_Data(String meaning) {
            this.meaning = meaning;
            this.frequency = 0;
        }
    }


    /**
     * Helper method to check if input is a valid word, used in exception handling for Invalid Word Error
     * @param word The word to be checked.
     */
    public boolean ValidWordCheck(String word) {
        for (char i : word.toCharArray()) {         // Convert the word to a char array and iterate through the word
            if (!Character.isLetter(i)) {           // Checks if each letter is a character
                return false;                       // Return false if any character is not a letter
            }
        }
        return true;                                // Return true if all characters are letters
    } //END OF HELPER METHOD



    //Data Structures used throughout the program
//    DefaultListModel<String> searchHistoryModel = new DefaultListModel<>();

    public final HashMap<String, Word_Data> dictionary = new HashMap<>();
    public final ArrayList<Map.Entry<String, Word_Data>> temp_compare = new ArrayList<>();   //temporary arraylist used to compare prefix to keys in hash in FIND
    public ArrayList<String> searchHistory = new ArrayList<>();     //Utilizes a built-in method to help manage the list used in search history JList
    public final int maxHistorySize = 10; // Maximum entries in search history
    /*HELPER METHOD FOR SEARCH HISTORY */
    /**
     * Updates the search history, ensuring the most recent search is at the top.
     * Ignores null or empty words and removes any words past the maxHistorySize length.
     * If the word being added is already in the search history, it will remove it and add it to the top.
     * @param word The word to add to the search history.
     */
        public void updateSearchHistory(String word) {
            if (word == null || word.isEmpty()) {
                return; // Ignore null or empty words
            }

            if (searchHistory.contains(word)) {
                searchHistory.remove(word);
            }
            searchHistory.add(0, word);

            while (searchHistory.size() > maxHistorySize) {
                searchHistory.remove(searchHistory.size() - 1);
            }

            searchHistoryList.setListData(searchHistory.toArray(new String[0]));
        }

    /**
     * Sorts a list of word entries by their frequency in descending order.
     *
     * @param entries The list of word entries to sort.
     * @return The sorted list of word entries.
     */
    //Helper Method for Sort
    public List<Map.Entry<String, Word_Data>> sortByFrequency(List<Map.Entry<String, Word_Data>> entries) {
        entries.sort((a, b) -> Integer.compare(b.getValue().frequency, a.getValue().frequency));
        return entries;
    }


    /**
     *     Constructor to hold the action listeners for the buttons in swing.
      */
    public Dictionary() {
//        searchHistoryList.setModel(searchHistoryModel);

        /**
         * Method to add words to dictionary.
         * Checks if word is a valid input and/or if the word is repeated.
         */
        ADDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    word = TextNewWord.getText().trim();    //.trim() added to clear all trailing spaces for cleaner formatting
                    definition = TextArea.getText().trim();

                    if (!ValidWordCheck(word)) {        // Use the helper method to check validity
                        throw new InvalidWordError();
                    }
                    if (word.isEmpty()) {
                        throw new InvalidWordError();
                    }
                    if (definition.isEmpty()) {
                        throw new InvalidWordError();
                    }

                    if (dictionary.containsKey(word)) {     //very helpful hashmap utility, checks if the key already exists
                        throw new WordDuplicatedError();
                    }

                    dictionary.put(word, new Word_Data(definition));        //adds the new word to the dictionary
                    TextArea.setText("Success: '" + word + "' has been added to the dictionary\n With definition: " + definition);
                    TextNewWord.setText("");

                }
                catch (InvalidWordError ex) {
                    TextArea.setText( ex.getMessage());
                    System.out.println(ex.getMessage());
                    throw new InvalidWordError();
                } catch (WordDuplicatedError ex) {
                    TextArea.setText( ex.getMessage());
                    System.out.println(ex.getMessage());
                    throw new WordDuplicatedError();
                }
            }
        });/* END OF ADDButton */
        /**
         * Import button implementation to import files to the dictionary.
         */
        IMPORTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //assigns instance variable filepath to the text in TextFilePath
                filepath = TextFilePath.getText().trim();
                File file = new File(filepath);     //gives file the filepath of input
                Scanner fileIn = null;      //declares scanner var
                //initialize scanner
                //from lecture
                // Check if the file exists
                try {
                    if (!file.exists()) {
                        throw new FileNotFoundError();
                    }
                } catch (FileNotFoundError ex) {
                    System.out.println(ex.getMessage());
                    TextArea.setText(ex.getMessage());
                    throw new FileNotFoundError();
                }
                // Try to open the file and read its contents
                try {
                    fileIn = new Scanner(file);
                    ArrayList<String> tempArrList = new ArrayList<>();

                    // Read each line and add non-empty lines to tempArrList
                    while (fileIn.hasNextLine()) {
                        String line = fileIn.nextLine().trim();
                        if (!line.isEmpty()) {
                            tempArrList.add(line);
                        }
                    }

                    // Process the lines in tempArrList
                    for (int i = 0; i < tempArrList.size(); i += 2) {
                        String word = tempArrList.get(i);
                        String meaning = i + 1 < tempArrList.size() ? tempArrList.get(i + 1) : null;

                        // Validate the word
                        if (!ValidWordCheck(word)) {
                            throw new InvalidWordError();
                        }

                        // Check for missing definitions
                        if (meaning == null || meaning.isEmpty()) {
                            throw new InvalidWordError();
                        }

                        // Check for duplicate words
                        if (dictionary.containsKey(word)) {
                            throw new WordDuplicatedError();
                        }

                        // Add the word and its meaning to the dictionary
                        dictionary.put(word, new Word_Data(meaning));
                    }

                    // Success message
                    TextArea.setText("Success: File has been imported successfully.");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }catch (InvalidWordError ex) {
                    TextArea.setText(ex.getMessage());
                    System.out.println(ex.getMessage());
                    throw new InvalidWordError();
                }catch (WordDuplicatedError ex) {
                        TextArea.setText(ex.getMessage());
                        System.out.println(ex.getMessage());
                        throw new WordDuplicatedError();
                    } finally {
                    if (fileIn != null) {
                        fileIn.close(); // Ensure scanner is closed
                    }
                }
            }
        });/* END OF IMPORTButton */
        /**
         * Export Button used to write to a file.
         * Gets each entry of the dictionary and outputs in a format of
         * word, frequency, definition --> Next word(repeat).
         */
        EXPORTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filepath = TextFilePath.getText().trim();
                File file = new File(filepath);
                try {
                    if (!file.exists()) {
                        throw new FileNotFoundError();
                    }
                } catch (FileNotFoundError ex) {
                    System.out.println(ex.getMessage());
                    TextArea.setText(ex.getMessage());
                    return; // Exit the action if file is not found
                }

                try {
                    // Create a PrintWriter to write the sorted dictionary to the file
                    PrintWriter writer = new PrintWriter(new FileOutputStream(file));

                    List<Map.Entry<String, Word_Data>> dictEntries = new ArrayList<>(dictionary.entrySet());
                    // utilize helper sort function
                    List<Map.Entry<String, Word_Data>> sortedEntries = sortByFrequency(dictEntries);

                    //writes the entry to the soon to be exported file
                    for (Map.Entry<String, Word_Data> entry : sortedEntries) {
                        writer.println(entry.getKey()); // Write the word
                        writer.println(entry.getValue().frequency); // Write the frequency
                        writer.println(entry.getValue().meaning); // Write the meaning
                        writer.println(); // Add a blank line for readability
                    }

                    writer.close(); // ALWAYS close writer
                    TextArea.setText("Exported successfully to: " + file);

                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
//                catch (IOException ex) {
//                    throw new RuntimeException("Error: unable to save dictionary to file.");                
//                }
            }
        }); /* END OF EXPORTButton */

        /**
         * Find method used to find words in the dictionary. If any of the words contain the prefix
         * it will add similar words to another hashmap and output those words to frequency fields based on their
         * frequency of being searched.
         * Will output top 3 words similar to your word and their definitions.
         * The 3 words will then be added to search history.
         */
        FINDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                word = TextNewWord.getText().trim();
                temp_compare.clear();       //utilizing instance arraylist

                //if loop to handle if there is an exact match in the dictionary(word data)
                if (dictionary.containsKey(word)) {
                    TextArea.setText("");
                    Word_Data exactMatch = dictionary.get(word); // Get the exact match
                    TextArea.append("Word: " + word + "\nDefinition: " + exactMatch.meaning);

                    // Increment frequency of the exact match
                    exactMatch.frequency++;

                    // Update frequency fields to display only this exactMatch word
                    TextFreqWord1.setText(word);
                    TextFreqWord2.setText(""); // Clear unused fields
                    TextFreqWord3.setText("");

                    // Add the exact match to search history
                    updateSearchHistory(word);
                    return; // Exit since exact match is handled
                }


                //for loop to iterate through each word in the dictionary and compare to FIND word
                for (Map.Entry<String, Word_Data> dictEntry : dictionary.entrySet()) {
                    if (dictEntry.getKey().contains(word)) {    //compares the word making sure to stay case sensitive
                        temp_compare.add(dictEntry);            //adds the similar words to the instance array list
                    }
                }

                //Handles if find is pressed with nothing in textfield
                if (TextNewWord.getText().trim().isEmpty()) {
                    TextArea.setText("No matches found for: '" + word + "'");
                    TextFreqWord1.setText(word);
                    TextFreqWord2.setText("");
                    TextFreqWord3.setText("");
                    return; // Exit if no matches
                }


                // Sorts the test_compare array list by frequency in descending order to get the most frequently searched words first
                //Found sort syntax with help from chatgpt
                // Sort similar entries by frequency in descending order (a = key, b = value) <-- Lambda values
                List<Map.Entry<String, Word_Data>> sortedSimilarWords = sortByFrequency(temp_compare);
                //Gets the top 3 words from the sorted arrayList (used to be output to the text field and searchhistory
                int topThree = Math.min(3, sortedSimilarWords.size());
                // Creates array (of JTextFields) of the 3 frequency text fields, defined explicitly
                // Idea given via stackoverflow - https://stackoverflow.com/questions/26318048/how-can-i-create-a-jtextfield-array
                JTextField[] frequencyTextFieldsArray = {TextFreqWord1, TextFreqWord2, TextFreqWord3};

                //iterate through the array to set the frequencytextfields to the similar words
                for (int i = 0; i < frequencyTextFieldsArray.length; i++) {
                    if (i < temp_compare.size()) {
                        int frequency = temp_compare.get(i).getValue().frequency;   //get the definition of the frequency from the hashmap
                        frequencyTextFieldsArray[i].setText(temp_compare.get(i).getKey()); // sets the textField by going to the index and getting the word from the hashmap
                    } else {
                        frequencyTextFieldsArray[i].setText("");
                    }
                }

            //Outputs to the text area the 3 most frequent words
            if(!sortedSimilarWords.isEmpty()) {
                    // Return the top 3 similar words (with definition) in the TextArea
                    TextArea.setText("For '" + word + "': \n");
                    for (int i = 0; i < topThree; i++) {
                        key = sortedSimilarWords.get(i).getKey();     //retrieves the key from temp_compare ArrayList
                        definition = sortedSimilarWords.get(i).getValue().meaning;       //get the definition of the word from the hashmap
                        if(i < topThree - 1) {        //adds commas for top 3 until the last one
                            TextArea.append("- " + key + "(Definition: " + definition+ ") , \n");
                        }else{
                            TextArea.append("- " + key + "(Definition: " + definition + ")\n");
                        }
                    }
                    TextArea.append("Are Feasible For Your Search.");


                    // Add the top 3 similar/frequently searched to the search history
                    for (int i = topThree - 1; i >= 0; i--) {
                        updateSearchHistory(sortedSimilarWords.get(i).getKey());
                    }

                    // Increment the frequency for the top 3 matches
                    for (int i = 0; i < topThree; i++) {
                        sortedSimilarWords.get(i).getValue().frequency++;
                    }
                } else {
                TextArea.setText("No Word Matched.");
            }
            }
        }); /* END OF FINDButton */
        /**
         * Modify Will change the old word with the new word
         * Must contain exceptions for InvalidWordError, WordNotFound, and WordDuplicatedError
         */
        MODIFYButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Need to obtain the original and new word to modify the word (declared as instance variables above dictionary constructor)
                    newWord = TextNewWord.getText().trim();
                    originalWord = TextOriginalWord.getText().trim();

                    //exception handling
                    if (!ValidWordCheck(newWord)) {
                        throw new InvalidWordError();
                    }
                    if (!dictionary.containsKey(originalWord)) {
                        throw new WordNotFoundError();
                    }
                    if (dictionary.containsKey(newWord)) {
                        throw new WordDuplicatedError();
                    }

                    // Get the value and frequency for the original word from Word_Data class
                    //note: .get(key) gets the meaning and frequency from Word_Data that goes with the key
                    Word_Data originalVF = dictionary.get(originalWord);

                    // Remove the original word and add the new word with the same data
                    dictionary.remove(originalWord);
                    dictionary.put(newWord, originalVF);

                    // Display success message
                    TextArea.setText("Success: '" + originalWord + "' has been replaced with '" + newWord + "'.");
                    TextOriginalWord.setText("");
                    TextNewWord.setText("");
                } catch (InvalidWordError ex) {
                    TextArea.setText(ex.getMessage());
                    System.out.println(ex.getMessage());
                    throw new InvalidWordError();
                }catch (WordNotFoundError ex) {
                    TextArea.setText(ex.getMessage());
                    System.out.println(ex.getMessage());
                    throw new WordNotFoundError();
                }catch (WordDuplicatedError ex) {
                    TextArea.setText(ex.getMessage());
                    System.out.println(ex.getMessage());
                    throw new WordDuplicatedError();
                }
            }
        });/* END OF MODIFYButton */

        /**
         * Remove actionlistener implementation to remove a word from the dictionary.
         */
        REMOVEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    word = TextNewWord.getText().trim();

                    if (!dictionary.containsKey(word)) {
                        throw new WordNotFoundError();
                    }

                    //Removes the word from dictionary along with its definition
                    dictionary.remove(word);
                    TextArea.setText("Success: '" + word + "' has been removed.");
                    TextNewWord.setText("");

                } catch (WordNotFoundError ex) {
                    TextArea.setText(ex.getMessage());
                    System.out.println(ex.getMessage());
                    throw new WordNotFoundError();
                }
            }
        });/* END OF REMOVEButton */

        /**
         * Clears all of the text fields in the swing gui.
         */
        CLEARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea.setText("");
                TextNewWord.setText("");
                TextFilePath.setText("");
                TextOriginalWord.setText("");
                TextFreqWord1.setText("");
                TextFreqWord2.setText("");
                TextFreqWord3.setText("");
                searchHistoryList.clearSelection(); //for SearchHistory
            }
        });
    } /* END OF CLEARButton */

    /**
     * Main function generated to connect Calculator.java with Swing Gui and run the program.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dictionary");
        frame.setContentPane(new Dictionary().DictionaryPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

/*EOF*/