package kit.edu.kastel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * class Dictionary works as a database for the main program. It realizes
 * all requested features. All datas in thif database is stored by pairs, a pair
 * consists of a word in starting language (word) and a corresponding word in
 * target language (translation).
 * 
 * @author udqch
 * @since 16.12.2025
 */
public class Dictionary {
    HashMap<String, ArrayList<String>> dict = new HashMap<>();

    /**
     * sort all datas by word in starting language in Germans alphabet.
     * 
     * @param list list of word in starting language
     * @return a sorted in order array-list of original words
     */
    public ArrayList<String> sortDict(ArrayList<String> list) {
        ArrayList<String> sortedList = new ArrayList<>(list);

        sortedList.sort(
                String.CASE_INSENSITIVE_ORDER
                        .thenComparing(Comparator.reverseOrder()));

        return sortedList;
    }

    /**
     * add a word and corresponding translation.
     * 
     * @param word        word in starting language
     * @param translation word in target language
     */
    public void add(String word, String translation) {
        // if word does not exist in database
        if (dict.get(word) == null) {
            ArrayList<String> newWord = new ArrayList<>();
            newWord.add(translation);
            dict.put(word, newWord);
        } else {
            if (dict.get(word).contains(translation)) {
                System.out.println("ERROR: This translation have existed. Will not add anymore.");
            } else {
                ArrayList<String> newTrans = dict.get(word);
                newTrans.add(translation);
                newTrans = sortDict(newTrans);
                dict.put(word, newTrans);
            }
        }
    }

    /**
     * remove a word from database.
     * 
     * @param word word in starting language, that needs to be removed
     */
    public void remove(String word) {
        if (dict.get(word) == null) {
            System.out.println("ERROR: The \"" + word + "\" does not exist.");
        } else {
            dict.remove(word);
        }
    }

    /**
     * print all existing pairs of word and tranlation in database.
     */
    public void print() {
        if (dict.isEmpty()) {
            System.out.println(
                    "ERROR: Nothing in dictionary.");
        } else {
            ArrayList<String> words = new ArrayList<>(dict.keySet());
            for (String word : sortDict(words)) {
                System.out.print(word + " - ");
                for (String trans : dict.get(word)) {
                    System.out.print(trans);
                    if (dict.get(word).indexOf(trans) < dict.get(word).size() - 1) {
                        System.out.print(",");
                    }
                }
                System.out.println();
            }
        }
    }

    /**
     * like print but has parameter ch, which means print all pairs of word and
     * translation, if the first letter of word is ch.
     * 
     * @param ch inputed condition
     */
    public void print(char ch) {
        char bigCh = Character.toUpperCase(ch);
        char smallCh = Character.toLowerCase(ch);
        boolean isValid = false;
        ArrayList<String> words = new ArrayList<>(dict.keySet());
        for (String word : sortDict(words)) {
            if (word.charAt(0) == bigCh || word.charAt(0) == smallCh) {
                isValid = true;
                System.out.print(word + " - ");
                for (String trans : dict.get(word)) {
                    System.out.print(trans);
                    if (dict.get(word).indexOf(trans) < dict.get(word).size() - 1) {
                        System.out.print(",");
                    }
                }
                System.out.println();
            }
        }
        if (isValid == false) {
            System.out.println(
                    "ERROR: No word matchs your argument.");
        }
    }

    /**
     * search for all translations of inputed word.
     * 
     * @param word word needs to be translated
     */
    public void translate(String word) {
        if (dict.get(word) == null) {
            System.out.println(
                    "ERROR: The \"" + word
                            + "\" does not exist. Please add translation for this word.");
        } else {
            for (String trans : dict.get(word)) {
                System.out.print(trans);
                if (dict.get(word).indexOf(trans) < dict.get(word).size() - 1) {
                    System.out.print(",");
                }
            }
            System.out.println();
        }
    }

    /**
     * to print all translations, this function use backtracking algorithm to do
     * that.
     * 
     * @param words          all splited words in sentence
     * @param index          index of word in sentence
     * @param curTranslation store translated words
     */
    public void printTranslate(String[] words, int index, List<String> curTranslation) {
        if (index == words.length) {
            System.out.println(String.join(" ", curTranslation));
            return;
        }

        List<String> translations = dict.get(words[index]);
        for (String trans : translations) {
            curTranslation.add(trans);
            printTranslate(words, index + 1, curTranslation);
            curTranslation.remove(curTranslation.size() - 1);
        }
    }

    /**
     * translate word-by-word a whole sentence. If a word in sentence does not exist
     * in database, return error.
     * 
     * @param sentence a whole sentence without any punctations.
     */
    public void translate(String[] sentence) {
        // check if all words in sentence have corresponding translations in database
        boolean isValid = true;
        String invaliWord = new String();
        for (String word : sentence) {
            if (dict.get(word) == null) {
                isValid = false;
                invaliWord = word;
            }
        }
        if (isValid == false) {
            System.out.println(
                    "ERROR: The \"" + invaliWord
                            + "\" does not exist. Please add translation for this word.");
        } else {
            List<String> curTranslation = new ArrayList<>();
            printTranslate(sentence, 0, curTranslation);
        }
    }

    /**
     * execute the valid command.
     * 
     * @param command inputed valid command
     */
    public void execute(String command) {
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "add":
                add(parts[1], parts[2]);
                break;
            case "remove":
                remove(parts[1]);
                break;
            case "print":
                if (parts.length == 1) {
                    print();
                } else {
                    print(parts[1].charAt(0));
                }
                break;
            case "translate":
                if (parts.length == 2) {
                    translate(parts[1]);
                } else {
                    String[] needTrans = java.util.Arrays.copyOfRange(parts, 1, parts.length);
                    translate(needTrans);
                }
                break;
            default:
                break;
        }
    }
}
