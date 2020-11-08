import java.util.Scanner;
import java.io.*;
import java.util.*;

public class HangMan {
    public char[] man = { ' ', ' ', ' ', ' ', ' ', ' ' };
    public char[] manFilled = { 'O', '-', '|', '-', '/', '\\' };
    public int spot;
    public char blank = ' ';
    public Scanner sc = new Scanner(System.in);
    public int counter = 0;
    public ArrayList<Character> wrongLetters;
    public ArrayList<String> wordList;
    // public char player;
    public static String currentWord = "";
    public String currentWordShown = "";

    public HashMap<Integer, Character> mapOfWord;

    /*
     * Main function: the game runs within a while loop until the player no longer
     * wishes to play - which then will terminate the game.
     */

    public static void main(String args[]) {
        String ch;
        HangMan man = new HangMan();
        do {

            man.newBoard();
            man.playAI();
            System.out.println("Would you like to play again (Enter 'yes')? ");
            Scanner in = new Scanner(System.in);
            ch = in.nextLine();
            System.out.println("ch value is " + ch);
        } while (ch.equals("yes"));

    }

    /*
     * This function is the base case for the AI. It picks a random vowel first.
     */
    public void AIChooseFirstChar() {
        boolean charTrue = true;

        do {
            char[] vowels = { 'a', 'e', 'i', 'o', 'u' };
            char charGuessed = ' ';
            boolean letterUsed = true;

            while (letterUsed) {
                charGuessed = vowels[(int) (Math.random() * 5)];
                if (!currentWordShown.contains(Character.toString(charGuessed)) && !wrongLetters.contains(charGuessed))
                    letterUsed = false;
            }

            System.out.println("Is there a letter \"" + charGuessed + "\" in your word?");
            System.out.print("Y = yes/ N = no:");

            char response = sc.next().charAt(0);
            if (response == 'Y') {// If the letter is correct, fill the currentWordShown with the correct letters
                                  // in the correct indexes.
                char[] charWord = currentWord.toCharArray();
                char[] charWordShown = currentWordShown.toCharArray();
                for (int i = 0; i < charWord.length; i++) {
                    if (charGuessed == charWord[i]) {
                        charWordShown[i] = charGuessed;
                        currentWordShown = currentWordShown.substring(0, i) + charGuessed
                                + currentWordShown.substring(i + 1);
                    }
                }
                currentBoard();
                charTrue = false;
            } else { // If no, add the letter to wrong
                man[counter] = manFilled[counter];
                counter++;
                wrongLetters.add(charGuessed);
                currentBoard();
                continue;
            }
        } while (charTrue);
    }

    // This funtion uses an AI to pick the rest of the characters.
    public void AIBlowThePlayersMind() {

        do {

            System.out.println("AI guessing...");

            char aiMove = AImove();

            System.out.println("Is there a letter \"" + aiMove + "\" in your word?");
            System.out.print("Y = yes/ N = no:");
            // System.out.println();
            char yesOrNo = sc.next().charAt(0);
            if (yesOrNo == 'Y') {
                char[] charWord = currentWord.toCharArray();
                // char[] charWordShown = currentWordShown.toCharArray();
                for (int i = 0; i < charWord.length; i++) {
                    if (aiMove == charWord[i]) {
                        // charWordShown[i] = charGuessed;
                        currentWordShown = currentWordShown.substring(0, i) + aiMove
                                + currentWordShown.substring(i + 1);
                    }
                }
                currentBoard();
                // charTrue = false;
            } else if (yesOrNo == 'N') {
                man[counter] = manFilled[counter];
                counter++;
                wrongLetters.add(aiMove);
                currentBoard();
                // continue;
            }

        } while (checkWinner() == false);
    }

    /*
     * This function initiates a new game with starting variables.
     */
    public void newBoard() {
        char positiondef[] = { ' ', ' ', ' ', ' ', ' ', ' ' };
        wrongLetters = new ArrayList<>();
        wordList = new ArrayList<>();
        mapOfWord = new HashMap<>();
        counter = 0;
        newWordList();
        for (int i = 0; i < positiondef.length; i++)
            man[i] = positiondef[i];
        currentBoard();
    }

    /*
     * This function creates the wordList ArrayList that the AI uses when guessing
     * the next letter.
     */
    public void newWordList() {
        try {
            System.out.println("AI getting WordsForHangmanAI...");
            FileReader f = new FileReader("WordsForHangmanAI.txt");
            BufferedReader reader = new BufferedReader(f);
            String line = reader.readLine();
            while (line != null) {
                line.toLowerCase();
                wordList.add(line);
                line = reader.readLine();

            }
            System.out.println("AI done getting words.");
            reader.close();
        } catch (IOException x) {
            System.err.format("IOException: %s\n", x);
        }
    }

    /*
     * This function displays the current game board when called on.
     */
    public void currentBoard() {
        System.out.println("    ______ ");
        System.out.println("    |    | ");
        System.out.println("    " + man[0] + "    | ");
        System.out.println("   " + man[1] + man[2] + man[3] + "   | ");
        System.out.println("   " + man[4] + " " + man[5] + "   | ");
        System.out.println("  _______| ");
        System.out.println();
        System.out.println("Word: " + currentWordShown);
        System.out.println("Incorrect letters: " + "" + new String(wrongLetters.toString()));
    }

    // This function chooses the next letter most likely to be in the word
    public char AImove() {

        ArrayList<String> specialWords = filteredArray(wordList); // Filters out all the words that are not similar to
                                                                  // currentWordShown.
        specialWords = removeWordsWithWrongLetters(specialWords); // FIlters out all words that contains wrong letters
        // System.out.println(specialWords); //comment out when finished
        int oneLetterLeft = currentWordShown.length() - currentWordShown.replaceAll("\\*", "").length();
        char chosenChar = ' ';
        if (oneLetterLeft == 1) { // If there is one letter left to guess, find the index of that last letter,
                                  // choose a random string in the ArrayList, and return the char at the randomly
                                  // chosen string.
            Random rand = new Random();
            int idxOfLastLetter = currentWordShown.indexOf('*');
            boolean letterTaken = true;
            while (letterTaken) {
                String chosenWord = specialWords.get(rand.nextInt(specialWords.size()));
                chosenChar = chosenWord.charAt(idxOfLastLetter);
                // System.out.println("random c:" + chosenChar);
                if (!currentWordShown.contains(Character.toString(chosenChar)) && !wrongLetters.contains(chosenChar))
                    letterTaken = false;
            }

            return chosenChar;
        } else if (specialWords.size() != 1) { // If there is more than 1 string in the specialWords ArrayList, then run
                                               // the probibility function.

            chosenChar = probibility(specialWords);
            return chosenChar;

        } else { // If there is only one string left in the specialWords ArrayList, then choose a
                 // random character from the string.
            Random rand = new Random();
            String onlyCharWord = specialWords.get(0);
            char c = ' ';
            boolean letterTaken = true;
            while (letterTaken) {
                c = onlyCharWord.charAt(rand.nextInt(onlyCharWord.length()));
                // System.out.println("random c:" + c);
                if (!currentWordShown.contains(Character.toString(c)) && !wrongLetters.contains(c))
                    letterTaken = false;
            }
            return c;
        }
    }

    /*
     * filters all the words that contain letters from the wrongLetters ArrayList.
     */
    public ArrayList<String> removeWordsWithWrongLetters(ArrayList<String> arr) {
        for (char w : wrongLetters) {
            arr.removeIf(word -> word.contains(Character.toString(w)));
        }
        return arr;
    }

    /*
     * This function takes the wordList the AI uses and filters it out.
     * 
     * By looking at all the letters guessed in the currentWordShown string, this
     * function takes the index of each letter guesses in the string and compares it
     * to every string in wordList, only returning the strings that have the same
     * guessed letters in the same index's as the currentWordShown.
     */
    public ArrayList<String> filteredArray(ArrayList<String> wList) {

        char[] charWord = currentWordShown.toCharArray();
        for (int i = 0; i < charWord.length; i++) {
            if (charWord[i] != '*') {
                mapOfWord.put(i, charWord[i]);
            }
        }

        ArrayList<String> specialWords = new ArrayList<>();
        for (String wordx : wList) {
            StringBuilder newWord = new StringBuilder();
            String word = wordx.toLowerCase();
            charWord = word.toCharArray();
            for (int i = 0; i < charWord.length; i++) {
                // int j = 0;
                newWord.append("*");
                for (Map.Entry<Integer, Character> entry : mapOfWord.entrySet()) {

                    if (charWord[i] == entry.getValue() && i == entry.getKey()) {
                        newWord.setLength(newWord.length() - 1);
                        newWord.append(charWord[i]);
                    }
                }
            }
            if (newWord.toString().equalsIgnoreCase(currentWordShown)
                    && newWord.length() == currentWordShown.length()) {
                specialWords.add(word);
            }
        }
        return specialWords;
    }

    /*
     * This function iterates through all the characters in the specialWords
     * ArrayList and returns the character that it iterates over the most.
     */
    public char probibility(ArrayList<String> array) {
        HashMap<Character, Integer> map = new HashMap<>();
        for (String word : array) {
            char[] charArray = word.toCharArray();
            for (char c : charArray) {
                if (map.containsKey(c) && !currentWordShown.contains(Character.toString(c))) {
                    int newKey = map.get(c) + 1;
                    map.put(c, newKey);
                } else if (!currentWordShown.contains(Character.toString(c))) {
                    map.put(c, 1);
                }
            }
        }
        Map.Entry<Character, Integer> maxEntry = null;
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }

    /*
     * This function returns a boolean that checks to see if the AI has either won
     * or lost. If either, then it returns true. Else it returns false.
     */
    public boolean checkWinner() {
        boolean Winner = false;
        if (currentWordShown.equalsIgnoreCase(currentWord) || wrongLetters.size() == 6) {
            Winner = true;
            System.out.println("GAME OVER");
        }

        return Winner;
    }

    /*
     * This Function initiates game play vs. AI.
     */
    public void playAI() {
        System.out.print("Enter your word: ");
        String wordTyped = sc.next();
        currentWord = wordTyped;
        currentWordShown = "*".repeat(currentWord.length());

        currentBoard();
        System.out.println("AI guessing...");

        AIChooseFirstChar();

        AIBlowThePlayersMind(); // chooses rest of characters
    }
}
