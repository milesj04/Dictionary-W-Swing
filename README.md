# Dictionary Program
Overview
This Java program implements a dictionary system where users can add, modify, remove, and find words along with their definitions. The program supports importing and exporting words to/from a .txt file, displaying words with their frequency of use, and handling exceptions for invalid or duplicated words. The program features a graphical user interface (GUI) built using Swing.

Features
Add Word: Add new words with definitions to the dictionary.
Modify Word: Modify an existing word's definition or replace it with a new word.
Remove Word: Remove a word from the dictionary.
Find Word: Search for words based on a prefix or exact match, and display the top 3 most frequently searched words.
Import/Export: Import words and definitions from a .txt file and export the dictionary to a .txt file.
Search History: Keeps track of the last 10 searched words.
Frequency Sorting: The dictionary displays words sorted by frequency of use.
Exception Handling
The program includes custom exceptions for:

Invalid word input.
Duplicate word entries.
Word not found in the dictionary.
File not found during import/export operations.
Usage
Running the Program
Compile the Dictionary.java class.
Run the program. The Swing GUI will appear, where you can interact with the dictionary.
Available Actions
Add Word: Enter the word and definition in the corresponding text fields, then click "Add".
Modify Word: Enter the original word and the new word in the corresponding fields, then click "Modify".
Remove Word: Enter the word to remove and click "Remove".
Find Word: Enter a word or prefix and click "Find" to search for it. The program will display similar words, sorted by frequency.
Import: Provide a .txt file path containing word-definition pairs and click "Import".
Export: Provide a file path to save the dictionary to a .txt file.
Clear: Clear all text fields.
Installation
Ensure you have JDK 21 installed (recommended for smoother execution of the Swing program).
Clone or download the repository.
Compile the Dictionary.java file using javac Dictionary.java.
Run the program with java Dictionary.
Requirements
JDK 21 (Recommended for compatibility and smooth operation)
Swing library for GUI components
Java 8 or higher
Author
Miles Jennings
12/2/24
EECS 40; Professor Zhou Lee; University of California Irvine
