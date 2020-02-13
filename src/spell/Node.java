package spell;

import java.util.ArrayList;
import java.util.HashMap;

public class Node implements INode {

    private int wordCount;
    private Node parent;
    private char letter;

    public HashMap<Character, Node> children;


    public char getLetter() {
        return letter;
    }

    // for root Node
    public Node() {
        this.wordCount = 0;
        this.parent = null;
        this.children = new HashMap<Character, Node>();
        this.letter = ' '; // default will be a space
    }

    // normal Node
    public Node(Node parent, char letter) {
        this.wordCount = 0;
        this.parent = parent;
        this.children = new HashMap<Character, Node>();
        this.letter = letter;
    }



    @Override
    public boolean equals(Object o) {
        // check for null
        if (o == null) {
            return false;
        }

        Node n = (Node) o;
        if (wordCount == n.wordCount
                && letter == n.letter
                && children.equals(n.children)
                && ((parent == null && n.parent == null)
                    || (parent.letter == n.parent.letter))) {
            return true;
        } else {
            return false;
        }
    }

    // works as the wordCount getter
    @Override
    public int getValue() {
        return wordCount;
    }
    public Node getParent() {
        return parent;
    }

    // Setters
    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void incWordCounter() {
        wordCount++;
    }

    public boolean isRoot() {
        if (parent == null) {
            return true;
        } else {
            return false;
        }
    }

    //returns the sum has of the word count and letter
    @Override
    public int hashCode() {
        return Integer.hashCode(wordCount) + Character.hashCode(letter);
    }
}
