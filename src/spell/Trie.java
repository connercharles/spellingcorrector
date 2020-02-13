package spell;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Trie implements ITrie {
    private Node root;
    private int nodeCounter;
    private int wordCounter;

    public Trie() {
        this.root = new Node();
        this.wordCounter = 0;
        this.nodeCounter = 1;
    }


    // recursively called to get to the end of the word/node path
    // used for both adding and finding:
    // isAdd = True --> adding, isAdd = False --> finding
    private Node checkChildren(Node currentNode, CharacterIterator it, boolean isAdd) {
        // for finding
        Node result = null;

        // check if done
        if (it.current() != CharacterIterator.DONE) {
            // check if exists
            if (currentNode.children.containsKey(it.current())) {
                // get child
                Node child = currentNode.children.get(it.current());
                it.next();
                // recursion
                result = checkChildren(child, it, isAdd);
            } else if (isAdd) {
                // if not--create one
                currentNode.children.put(it.current(), new Node(currentNode, it.current()));
                // increment counter
                nodeCounter++;
                // get new child
                Node child = currentNode.children.get(it.current());
                it.next();
                // recursion
                result = checkChildren(child, it, isAdd);
            }
        } else {
            if (isAdd) {
                // don't count for duplicates
                if (currentNode.getValue() == 0) {
                    wordCounter++;
                }
                // (adding) finished!! increment word count
                currentNode.incWordCounter();
            } else {
                // (finding)
                // if actually the end of the word
                if (currentNode.getValue() > 0) {
                    return currentNode;
                } else {
                    return result;
                }
            }
        }
        return result;
    }

    @Override
    public void add(String word) {
        // make lowercase
        word = word.toLowerCase();
        CharacterIterator it = new StringCharacterIterator(word);

        // start with root
        checkChildren(root, it, true);
    }

    @Override
    public INode find(String word) {
        // make lowercase
        word = word.toLowerCase();
        CharacterIterator it = new StringCharacterIterator(word);
        Node result;

        // start with root
        result = checkChildren(root, it, false);
        return result;
    }

    // traverses through the Trie and spits out the word based on the Node
    public String getWord(Node end) {
        String word = "";
        // traverse backwards to get word
        while (!end.isRoot()) {
            // get char then move up
            word += end.getLetter();
            end = end.getParent();
        }

        //word is backwards right now so let's switch it
        word = new StringBuilder(word).reverse().toString();

        return word;
    }

    // Counts things within the Trie
    // isWord = True --> counting words, isWord = False --> counting nodes
    private int countTrie(int counter, Node currentNode, boolean isWord) {
        if (isWord && currentNode.getValue() > 0) {
            counter++;
        }

        // go through each child
        for (HashMap.Entry<Character, Node> child : currentNode.children.entrySet()) {
            counter = countTrie(counter, child.getValue(), isWord);
        }
        // count each node
        if (!isWord) {
            counter++;
        }

        return counter;
    }

    // counts the number of UNIQUE words in the Trie
    @Override
    public int getWordCount() {
//        int counter = 0;
//
//        counter = countTrie(counter, root, true);
//
//        return counter;
        return wordCounter;
    }

    @Override
    public int getNodeCount() {
//        int counter = 0;
//
//        counter = countTrie(counter, root, false);
//
//        return counter;
        return nodeCounter;
    }


    // goes through each node in the Trie and checks if they are equal
    private boolean checkChildren(Node node1, Node node2) {
        boolean isEqual = true;

        // check if it's a dead end
        if (node1.children.isEmpty() && node2.children.isEmpty()) {
            return node1.equals(node2);
        }

        // this is very round about but all well.
        Iterator it1 = node1.children.entrySet().iterator();
        Iterator it2 = node2.children.entrySet().iterator();

        Map.Entry pair1 = (Map.Entry)it1.next();
        Map.Entry pair2 = (Map.Entry)it2.next();

        // get first element
        Node nextNode1 =  (Node) pair1.getValue();
        Node nextNode2 =  (Node) pair2.getValue();

        while (it1.hasNext() && it2.hasNext()) {
            // go through each child and check
            isEqual = checkChildren(nextNode1, nextNode2);
            // if at any point there's a false leave!
            if (!isEqual) {
                return false;
            }
            // go to the next one
            pair1 = (Map.Entry)it1.next();
            pair2 = (Map.Entry)it2.next();
            nextNode1 =  (Node) pair1.getValue();
            nextNode2 =  (Node) pair2.getValue();
        }

        // check these two nodes themselves
        if (node1.equals(node2)) {
            return true;
        } else {
            return false;
        }
    }

    // equals factor for one Trie to another
    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        Trie comparing;

        boolean isEqual = true;
        try {
            comparing = (Trie) o;
        } catch (Exception e) {
            return false;
        }

        // right off the bat, it everything else doesn't match no need
        // to traverse if not necessary
        if (this.root.equals(comparing.root)
            && this.nodeCounter == comparing.nodeCounter
            && this.wordCounter == comparing.wordCounter) {
            // call recursive function to go through each node
            isEqual = checkChildren(this.root, comparing.root);
        } else {
            return false;
        }

        return isEqual;
    }

    // edits the output and returns whether it found things or not
    private String getWordList(String output, Node currentNode) {
        String result = "";

        // check if word if so add word to list
        if (currentNode.getValue() > 0) {
            result =  getWord(currentNode);
            if (output == "") {
                output = result;
            } else {
                output =  String.join("\n", output, result);
            }
        }

        // go through each child
        for (HashMap.Entry<Character, Node> child : currentNode.children.entrySet()) {
            output = getWordList(output, child.getValue());
        }

        return output;
    }

    @Override
    public String toString() {
        String output = "";

        output = getWordList(output, root);

        return output;
    }


    // traverse and get every Node's hashcode
    private int getHashes(int hashSum, Node currentNode) {
        hashSum += currentNode.hashCode();

        for (HashMap.Entry<Character, Node> child : currentNode.children.entrySet()) {
            hashSum += getHashes(hashSum, child.getValue());
        }

        return hashSum;
    }

    @Override
    public int hashCode() {
        int hashSum = 0;
        hashSum = getHashes(hashSum, root);
        return hashSum;
    }
}
