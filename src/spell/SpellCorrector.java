package spell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class SpellCorrector implements ISpellCorrector {
    private Trie data;

    public SpellCorrector() {
        this.data = new Trie();
        candidates_Lv2 = new TreeSet<String>();
    }
    private SortedSet<String> candidates_Lv2;

    final char[] ALPHABET = {'a','b','c','d','e','f','g','h','i','j','k',
            'l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        BufferedReader in = new BufferedReader(
                new FileReader(dictionaryFileName));
        while (in.ready()) {
            String temp = in.readLine();
            String[] tempArray = temp.split(" ");

            for (String word : tempArray) {
                data.add(word);
            }
        }
        in.close();
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        String result = "";

        INode found = data.find(inputWord);
        SortedSet<String> possibles = new TreeSet<String>();

        //if not in there then do crazy stuff
        if (found == null) {
            SortedSet<String> results = new TreeSet<String>();

            // Level 1 distance calls!
            result = deletionDist(inputWord, true);
            if (result != null) {
                results.add(result);
            }
            result = insertionDist(inputWord, true);
            if (result != null) {
                results.add(result);
            }
            result = alterationDist(inputWord, true);
            if (result != null) {
                results.add(result);
            }
            result = transpositionDist(inputWord, true);
            if (result != null) {
                results.add(result);
            }

            // redundant but it gets me what I want
            Set<Node> inDict = checkCandidates(results);
            // to iterate easier
            Node[] dictArray = inDict.toArray(new Node[inDict.size()]);


            if (dictArray.length > 0) {
                int highestPt = 0;
                int highestCount = 0;
                // get the one with highest word count
                for (int i = dictArray.length - 1; i >= 0; i--) {
                    if (dictArray[i].getValue() > highestCount) {
                        highestCount = dictArray[i].getValue();
                        highestPt = i;
                    }
                }

                result = data.getWord(dictArray[highestPt]);
            }

            if (result != null) {
                return result;
            }

            // Level Up! Starting level 2 distance calls
            for (String word : candidates_Lv2) {
                result = callDistances(word);
                if (result != null) {
                    possibles.add(result);
//                    return result;
                }
            }
        } else {
            result = data.getWord((Node)found);
        }

        if (!possibles.isEmpty()) {
            // redundant but it gets me what I want
            Set<Node> inDict = checkCandidates(possibles);
            // to iterate easier
            Node[] dictArray = inDict.toArray(new Node[inDict.size()]);

            int highestPt = 0;
            int highestCount = 0;
            // get the one with highest word count
            for (int i = dictArray.length - 1; i >= 0; i--) {
                if (dictArray[i].getValue() > highestCount) {
                    highestCount = dictArray[i].getValue();
                    highestPt = i;
                }
            }
            result = data.getWord(dictArray[highestPt]);
        }

        return result;
    }

    private String callDistances(String word) {
        String result = "";
        SortedSet<String> results = new TreeSet<String>();

        result = deletionDist(word, false);
        if (result != null) {
            results.add(result);
        }
        result = insertionDist(word, false);
        if (result != null) {
            results.add(result);
        }
        result = alterationDist(word, false);
        if (result != null) {
            results.add(result);
        }
        result = transpositionDist(word, false);
        if (result != null) {
            results.add(result);
        }

        if (results.isEmpty()) {
            return null;
        }

        // redundant but it gets me what I want
        Set<Node> inDict = checkCandidates(results);
        // to iterate easier
        Node[] dictArray = inDict.toArray(new Node[inDict.size()]);

        int highestPt = 0;
        int highestCount = 0;
        // get the one with highest word count
        for (int i = dictArray.length - 1; i >= 0; i--) {
            if (dictArray[i].getValue() > highestCount) {
                highestCount = dictArray[i].getValue();
                highestPt = i;
            }
        }

        result = data.getWord(dictArray[highestPt]);

        return result;
    }



    // is given candidate words and it spits out the right ones to suggest
    private String wordFromCandidates(SortedSet<String> candidates) {
        // put in alphabetical order
        Set<Node> inDict = checkCandidates(candidates);

        // only if they found things
        if (inDict.isEmpty()) {
            return null;
        }

        // to iterate easier
        Node[] dictArray = inDict.toArray(new Node[inDict.size()]);

        int highestPt = 0;
        int highestCount = 0;
        // get the one with highest word count
        for (int i = 0; i < dictArray.length; i++) {
            if (dictArray[i].getValue() > highestCount) {
                highestCount = dictArray[i].getValue();
                highestPt = i;
            }
        }

        // return highest word counted word
        return data.getWord(dictArray[highestPt]);
    }

    //Returns which "words" are in the Dictionary
    private Set<Node> checkCandidates(SortedSet<String> candidates) {
        Set<Node> inDict = new HashSet<Node>();
        Node result;
        for (String word : candidates) {
            result = (Node)data.find(word);
            if (result != null) {
                inDict.add(result);
            }
        }
        return inDict;
    }

    //** Deletion Distance
    // iterates through word and deletes each letter then
    // checks to see if that's in the dictionary.
    // returns new word is so and "" if not.
    private String deletionDist(String word, boolean isLv1) {
        SortedSet<String> candidates = new TreeSet<String>();

        // The actual deletion
        for (int i = 0; i < word.length(); i++) {
            StringBuilder s = new StringBuilder(word);
            s.deleteCharAt(i);
            candidates.add(s.toString());
            if (isLv1) {
                candidates_Lv2.add(s.toString());
            }
        }

        return wordFromCandidates(candidates);
    }

    //** Transposition Distance
    private String transpositionDist(String word, boolean isLv1) {
        SortedSet<String> candidates = new TreeSet<String>();

        // actual transposition here
        for (int i = 0, j = 1; i < word.length() - 1; i++, j++) {
            StringBuilder s = new StringBuilder(word);
            char original = s.charAt(i);

            s.setCharAt(i, s.charAt(j));
            s.setCharAt(j, original);

            candidates.add(s.toString());
            if (isLv1) {
                candidates_Lv2.add(s.toString());
            }
        }

        return wordFromCandidates(candidates);

    }

    //** Alteration Distance
    // go through and replace each char with any other
    private String alterationDist(String word, boolean isLv1) {
        SortedSet<String> candidates = new TreeSet<String>();

        // actual insertion here
        for (int i = 0; i < word.length(); i++) {
            for (char letter : ALPHABET) {
                StringBuilder s = new StringBuilder(word);
                s.setCharAt(i, letter);
                candidates.add(s.toString());
                if (isLv1) {
                    candidates_Lv2.add(s.toString());
                }
            }
        }

        return wordFromCandidates(candidates);
    }


    //** Insertion Distance
    // go through each position in the word and at every letter
    private String insertionDist(String word, boolean isLv1) {
        SortedSet<String> candidates = new TreeSet<String>();

        // actual insertion here
        for (int i = 0; i <= word.length(); i++) {
            for (char letter : ALPHABET) {
                StringBuilder s = new StringBuilder(word);
                s.insert(i, letter);
                candidates.add(s.toString());
                if (isLv1) {
                    candidates_Lv2.add(s.toString());
                }
            }
        }

        return wordFromCandidates(candidates);
    }
}
