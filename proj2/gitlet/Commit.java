package gitlet;


import java.io.File;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Represents a gitlet commit object.
 *
 * @author TODO
 */
public class Commit implements Serializable {

    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;
    private Date timeStamp;
    private String parent1;
    private String parent2;
    private String hash;
    private HashMap<String, String> files;

    public static final File CWD = new File(System.getProperty("user.dir"));

    public Commit(String messageIn, String parent1In, String parent2In) {
        //make a variable that contains header tracker but not in commit constructor
        this.message = messageIn;
        this.parent1 = parent1In;
        this.parent2 = parent2In;
        if (this.parent2 == null) {
            this.timeStamp = new Date(0);
        } else {
            this.timeStamp = new Date();
        }
        files = new HashMap<>();
        // put into files key - value pairings, key is file name and value is file hash
        //the reason for this is that now you know which file name the file hash is associated with
        //so you can go and look it up
    }

    public String getMessage() {
        return this.message;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public String getParent1() {
        return this.parent1;
    }

    public String getParent2() {
        return this.parent2;
    }

    public HashMap<String, String> getFiles() {
        return files;
    }

    public String getFromFiles(String fileName) {
        return files.get(fileName);
    }

    public void addToFiles(String fileName, String fileHash) {
        files.put(fileName, fileHash);
    }

    public void remFromFiles(String fileName) {
        files.remove(fileName);
    }

    public void setFiles(HashMap<String, String> mapIn) {
        this.files = mapIn;
    }

    public boolean contains(String fileName) {
        return files.containsKey(fileName);
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hashIn) {
        this.hash = hashIn;
    }


    public void setParent2(String idIn) {
        this.parent2 = idIn;
    }

}
