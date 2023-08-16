package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

/** Commit class for Gitlet.
 *  @author Miles Green
 */
public class Commit implements Serializable {

    /** Commit message. */
    private String message;

    /** Parent of the commmit. */
    private String parent;

    /** Time commit was made. */
    private String time;

    /** id of the commit. */
    private String id;

    /** TreeMap of the files of the commit. */
    private TreeMap<String, String> files;

    /** Constructor for Commit object.
     * @param m is message
     * @param p is parent */
    public Commit(String m, String p) {
        files = new TreeMap<>();
        Date date;
        if (!p.equals("")) {
            date = new Date();
        } else {
            date = new Date(0);
        }
        String pat = "EEE MMM d HH:mm:ss yyyy Z";
        SimpleDateFormat temp = new SimpleDateFormat(pat);
        String sTemp = temp.format(date);
        String s29 = "sssssssssssssssssssssssssssss";
        String s17 = "sssssssssssssssss";
        if (sTemp.length() == s29.length()) {
            sTemp = sTemp.substring(0, s17.length()) + "0"
                    + sTemp.substring(s17.length(), s29.length());
        }
        time = sTemp;
        message = m;
        parent = p;
        id = Utils.sha1(parent, time);
    }

    /** Returns the id of the Commit. */
    public String getID() {
        return id;
    }

    /** Returns the message of the Commit. */
    public String getMessage() {
        return message;
    }

    /** Returns the parent of the Commit as a String (id of parent). */
    public String getParent() {
        return parent;
    }

    /** Returns the time the Commit was created. */
    public String getTime() {
        return time;
    }

    /** If the Commit is tracking the file with the name passed into it, it
     * returns the id of that file. If it's not being tracked it returns "".
     * @param fileName is the name of the file */
    public String getFileID(String fileName) {
        if (files.containsKey(fileName)) {
            return files.get(fileName);
        } else {
            return "";
        }
    }

    /** Takes the name of a file and the id of that file and adds
     * it to the tree of tracked files for this Commit.
     * @param fileName is the name of the file
     * @param fileID is the id of the file */
    public void addFile(String fileName, String fileID) {
        files.put(fileName, fileID);
    }

    /** Removes the file with the name passed to the method from the
     * tree of tracked files, returning the id of that file if it's
     * in the file tree. If the file is not in the file tree, the
     * method returns "".
     * @param fileName is the name of the file */
    public String removeFile(String fileName) {
        if (files.containsKey(fileName)) {
            return files.remove(fileName);
        } else {
            return "";
        }
    }

    /** Returns the file tree of this Commit. */
    public TreeMap<String, String> getTree() {
        return files;
    }
}
