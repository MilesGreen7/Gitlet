package gitlet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

/** Command class for Gitlet.
 *  @author Miles Green
 */
public class Commands {

    /** Sets up our .gitlet directory if one does not exist. */
    public void init() {
        File a = new File(".gitlet");
        if (!a.exists()) {
            a.mkdir();
            File b = new File(".gitlet/Commits");
            b.mkdir();
            File d = new File(".gitlet/Stage");
            d.mkdir();
            File e = new File(".gitlet/Commits/head.txt");
            File x = new File(".gitlet/Commits/currBranch.txt");
            File y = new File(".gitlet/counter.txt");
            try {
                e.createNewFile();
                x.createNewFile();
                y.createNewFile();
            } catch (IOException excp) {
                System.out.println(excp);
            }
            Utils.writeContents(y, "0");
            Commit com = new Commit("initial commit", "");
            Utils.writeContents(e, com.getID());
            Utils.writeContents(x, "master");
            File g = Utils.join(b, com.getID());
            Utils.writeObject(g, com);
            TreeMap<String, String> branches = new TreeMap<>();
            branches.put("master", com.getID());
            File v = Utils.join(b, "BRANCHES");
            Utils.writeObject(v, branches);
            TreeMap<String, String> adds = new TreeMap<>();
            File z = Utils.join(d, "ADDS");
            Utils.writeObject(z, adds);
            TreeMap<String, String> removes = new TreeMap<>();
            File k = Utils.join(d, "REMOVES");
            Utils.writeObject(k, removes);
        } else {
            System.out.println("A Gitlet version-control system already"
                    + " exists in the current directory.");
        }
    }

    /** Returns the last commit of the current branch. */
    public Commit getCurrBranch() {
        String branchName = getCurrBranchName();
        File b = new File(".gitlet/Commits/BRANCHES");
        TreeMap<String, String> temp = new TreeMap<>();
        @SuppressWarnings("unchecked")
        TreeMap<String, String> branches = Utils.readObject(b, temp.getClass());
        String id = branches.get(branchName);
        File fCommit = new File(".gitlet/Commits/" + id);
        Commit t = new Commit("r", "r");
        Commit com = Utils.readObject(fCommit, t.getClass());
        return com;
    }

    /** Returns the name of the current branch. */
    public String getCurrBranchName() {
        File f = new File(".gitlet/Commits/currBranch.txt");
        return Utils.readContentsAsString(f);
    }

    /** This method takes the id of a commit and the name of a branch.
     * It sets the branch pointer of the passed branch to point to the commit
     * with the id passed to the method.
     * @param id is the branch id
     * @param branch is the name of the branch */
    public void setBranchPointer(String id, String branch) {
        File b = new File(".gitlet/Commits/BRANCHES");
        TreeMap<String, String> temp = new TreeMap<>();
        @SuppressWarnings("unchecked")
        TreeMap<String, String> branches = Utils.readObject(b, temp.getClass());
        branches.remove(branch);
        branches.put(branch, id);
        b.delete();
        Utils.writeObject(b, branches);
    }

    /** This sets the current branch.
     * @param s is the name of the branch */
    public void setCurrBranch(String s) {
        File f = new File(".gitlet/Commits/currBranch.txt");
        f.delete();
        Utils.writeContents(f, s);
    }

    /** This sets the head commit to the commit with the id passed to the
     * method.
     * @param id is the id of the head */
    public void setHead(String id) {
        File f = new File(".gitlet/Commits/head.txt");
        Utils.writeContents(f, id);
    }

    /** This returns the current head commit. */
    public Commit getHeadCommit() {
        File fCommit = new File(".gitlet/Commits/head.txt");
        String cID = Utils.readContentsAsString(fCommit);
        fCommit = new File(".gitlet/Commits/" + cID);
        Commit t = new Commit("r", "r");
        Commit com = Utils.readObject(fCommit, t.getClass());
        return com;
    }

    /** This returns the commit that has the id passed to the method
     * and if no such commit exists, it returns null. Also works
     * for abbreviated ids.
     * @param id is the id of the commit */
    public Commit getCommit(String id) {
        File f = new File(".gitlet/Commits/" + id);
        if (!f.exists()) {
            return getSubstringCommit(id);
        }
        Commit t = new Commit("r", "r");
        Commit com = Utils.readObject(f, t.getClass());
        return com;
    }

    /** This returns the commit that has the abbreviated id passed to the method
     * and if no such commit exists, it returns null.
     * @param id is the id of the commit */
    public Commit getSubstringCommit(String id) {
        String foundID = "";
        ArrayList<String> commits = getAllCommits();
        for (int i = 0; i < commits.size(); i++) {
            if (commits.get(i).contains(id)) {
                if ((foundID.equals(""))) {
                    foundID = commits.get(i);
                } else {
                    return null;
                }
            }
        }
        if (foundID.equals("")) {
            return null;
        } else {
            return getCommit(foundID);
        }
    }

    /** This returns the ADDS tree of the staging area. */
    public TreeMap<String, String> getAddTree() {
        File fAdds = new File(".gitlet/Stage/ADDS");
        TreeMap<String, String> temp = new TreeMap<>();
        @SuppressWarnings("unchecked")
        TreeMap<String, String> adds = Utils.readObject(fAdds, temp.getClass());
        return adds;
    }

    /** This returns the REMOVES tree of the staging area. */
    @SuppressWarnings("unchecked")
    public TreeMap<String, String> getRemoveTree() {
        File fRemoves = new File(".gitlet/Stage/REMOVES");
        TreeMap<String, String> temp = new TreeMap<>();
        TreeMap<String, String> removes;
        removes = Utils.readObject(fRemoves, temp.getClass());
        return removes;
    }

    /** This returns the BRANCHES tree for the commits. */
    @SuppressWarnings("unchecked")
    public TreeMap<String, String> getBranchTree() {
        File fBranches = new File(".gitlet/Commits/BRANCHES");
        TreeMap<String, String> temp = new TreeMap<>();
        TreeMap<String, String> branches;
        branches = Utils.readObject(fBranches, temp.getClass());
        return branches;
    }

    /** This returns an ArrayList containing all ids of every commit. */
    public ArrayList<String> getAllCommits() {
        ArrayList<String> visited = new ArrayList<String>();
        TreeMap<String, String> branches = getBranchTree();
        for (Map.Entry<String, String> entry : branches.entrySet()) {
            Commit c = getCommit(entry.getValue());
            while (true) {
                if (visited.contains(c.getID())) {
                    break;
                }
                visited.add(c.getID());
                if (c.getParent().equals("")) {
                    break;
                }
                c = getCommit(c.getParent());
            }
        }
        return visited;
    }

    /** Return the branch name containing the commit id.
     * @param id is the passed id */
    public String findBranch(String id) {
        ArrayList<String> visited = new ArrayList<String>();
        TreeMap<String, String> branches = getBranchTree();
        for (Map.Entry<String, String> entry : branches.entrySet()) {
            Commit c = getCommit(entry.getValue());
            while (true) {
                if (visited.contains(c.getID())) {
                    break;
                }
                visited.add(c.getID());
                if (c.getID().contains(id)) {
                    return entry.getKey();
                }
                if (c.getParent().equals("")) {
                    break;
                }
                c = getCommit(c.getParent());
            }
        }
        return "";
    }

    /** Returns the branch names in alphabetical order. */
    public ArrayList<String> alphabeticalBranchNames() {
        TreeMap<String, String> branches = getBranchTree();
        ArrayList<String> names = new ArrayList<>(branches.keySet());
        Collections.sort(names);
        return names;
    }

    /** Returns the names of files staged for addition
     * in alphabetical order. */
    public ArrayList<String> alphabeticalAddNames() {
        TreeMap<String, String> adds = getAddTree();
        ArrayList<String> names = new ArrayList<>(adds.keySet());
        Collections.sort(names);
        return names;
    }

    /** Returns the names of files staged for removal
     * in alphabetical order. */
    public ArrayList<String> alphabeticalRemoveNames() {
        TreeMap<String, String> removes = getRemoveTree();
        ArrayList<String> names = new ArrayList<>(removes.keySet());
        Collections.sort(names);
        return names;
    }

    /** Returns the number in counter.txt as a String and WITHOUT .txt appended
     * (just the number). */
    public String getCount() {
        File f = new File(".gitlet/counter.txt");
        return Utils.readContentsAsString(f);
    }

    /** Increases the number in counter.txt by 1. */
    public void incrCount() {
        File f = new File(".gitlet/counter.txt");
        int x = Integer.parseInt(getCount()) + 1;
        Utils.writeContents(f, String.valueOf(x));
    }

    /** This is for the add command and it adds a file to the staging
     * area.
     * @param s is the string array */
    public void add(String[] s) {
        Commit com = getHeadCommit();
        TreeMap<String, String> adds = getAddTree();
        TreeMap<String, String> removes = getRemoveTree();
        for (int i = 1; i < s.length; i++) {
            File f = new File(s[i]);
            if (!f.exists()) {
                System.out.println("File does not exist.");
                return;
            }
            if (removes.containsKey(s[i])) {
                removes.remove(s[i]);
            }
            String cID = com.getFileID(s[i]);
            File b = new File(s[i]);
            String fContent = Utils.readContentsAsString(b);
            File a = new File(".gitlet/Commits/" + cID);
            if (cID != "" && fContent.equals(Utils.readContentsAsString(a))) {
                if (adds.containsKey(s[i])) {
                    String addID = adds.remove(s[i]);
                    File c = new File(".gitlet/Stage/" + addID);
                    c.delete();
                }
            } else if (adds.containsKey(s[i])) {
                String addID = adds.get(s[i]);
                File d = new File(".gitlet/Stage/" + addID);
                Utils.writeContents(d, fContent);
            } else {
                String sCount = getCount() + ".txt";
                incrCount();
                File h = new File(".gitlet/Stage/" + sCount);
                try {
                    h.createNewFile();
                } catch (IOException excp) {
                    System.out.println(excp);
                }
                Utils.writeContents(h, fContent);
                adds.put(s[i], sCount);
            }
        }
        File aFile = new File(".gitlet/Stage/ADDS");
        File rFile = new File(".gitlet/Stage/REMOVES");
        aFile.delete();
        rFile.delete();
        Utils.writeObject(aFile, adds);
        Utils.writeObject(rFile, removes);
    }

    /** This is for the commit command and it adds a new commit to the
     * commit tree setting the head commit to this new commit.
     * @param m is the message of the commit */
    public void commit(String m) {
        TreeMap<String, String> adds = getAddTree();
        TreeMap<String, String> removes = getRemoveTree();
        if (adds.isEmpty() && removes.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit oldHead = getHeadCommit();
        Commit newHead = new Commit(m, oldHead.getID());
        setBranchPointer(newHead.getID(), getCurrBranchName());
        setHead(newHead.getID());
        Map<String, String> oldTree = oldHead.getTree();
        for (Map.Entry<String, String> entry : oldTree.entrySet()) {
            newHead.addFile(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : adds.entrySet()) {
            newHead.removeFile(entry.getKey());
            newHead.addFile(entry.getKey(), entry.getValue());
            File a = new File(".gitlet/Stage/" + entry.getValue());
            File b = new File(".gitlet/Commits/" + entry.getValue());
            try {
                b.createNewFile();
            } catch (IOException excp) {
                System.out.println(excp);
            }
            String aContent = Utils.readContentsAsString(a);
            Utils.writeContents(b, aContent);
            a.delete();
        }
        for (Map.Entry<String, String> entry : removes.entrySet()) {
            newHead.removeFile(entry.getKey());
            File e = new File(".gitlet/Stage/" + entry.getValue());
            if (e.exists()) {
                e.delete();
            }
        }
        File f = new File(".gitlet/Commits/" + newHead.getID());
        Utils.writeObject(f, newHead);
        adds.clear();
        removes.clear();
        File aFile = new File(".gitlet/Stage/ADDS");
        File rFile = new File(".gitlet/Stage/REMOVES");
        aFile.delete();
        rFile.delete();
        Utils.writeObject(aFile, adds);
        Utils.writeObject(rFile, removes);
    }

    /** This is for the rm command and it removes a file from the staging
     * area. If the file is in the current commit, it's removed from the
     * working directory and staged for removal.
     * @param removeFile is the file to be removed */
    public void rm(String[] removeFile) {
        TreeMap<String, String> adds = getAddTree();
        TreeMap<String, String> removes = getRemoveTree();
        Commit head = getHeadCommit();
        for (int i = 1; i < removeFile.length; i++) {
            boolean hasError = true;
            if (adds.containsKey(removeFile[i])) {
                hasError = false;
                adds.remove(removeFile[i]);
            }
            if (head.getTree().containsKey(removeFile[i])) {
                hasError = false;
                removes.put(removeFile[i], head.getFileID(removeFile[i]));
                File f = new File(removeFile[i]);
                if (f.exists()) {
                    f.delete();
                }
            }
            if (hasError) {
                System.out.println("No reason to remove the file.");
            }
        }
        File aFile = new File(".gitlet/Stage/ADDS");
        File rFile = new File(".gitlet/Stage/REMOVES");
        aFile.delete();
        rFile.delete();
        Utils.writeObject(aFile, adds);
        Utils.writeObject(rFile, removes);
    }

    /** This takes the file in the head commit with the name passed
     * to the method and overwrites the contents of the file with
     * the same name in the working directory.
     * @param args is the arguments */
    public void checkout1(String[] args) {
        if (!args[1].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        Commit head = getHeadCommit();
        String fileID = head.getFileID(args[2]);
        if (fileID.equals("")) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File f = new File(".gitlet/Commits/" + fileID);
        String s = Utils.readContentsAsString(f);
        File r = new File(args[2]);
        if (!r.exists()) {
            try {
                r.createNewFile();
            } catch (IOException excp) {
                System.out.println(excp);
            }
        }
        Utils.writeContents(r, s);
    }

    /** This takes the name of the file in the commit specified by the
     * id passed and overwrites the contents of the file with
     * the same name in the working directory.
     * @param args is the argument array */
    public void checkout2(String[] args) {
        if (!args[2].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        Commit com = getCommit(args[1]);
        if (com == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        String fileID = com.getFileID(args[3]);
        if (fileID.equals("")) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File f = new File(".gitlet/Commits/" + fileID);
        String s = Utils.readContentsAsString(f);
        File r = new File(args[3]);
        if (!r.exists()) {
            try {
                r.createNewFile();
            } catch (IOException excp) {
                System.out.println(excp);
            }
        }
        Utils.writeContents(r, s);
    }

    /** This will checkout the branch passed to the method.
     * @param args is the argument array */
    public void checkout3(String[] args) {
        TreeMap<String, String> branches = getBranchTree();
        if (!(branches.containsKey(args[1]))) {
            System.out.println("No such branch exists.");
        } else if (getCurrBranchName().equals(args[1])) {
            System.out.println("No need to checkout the current branch.");
        } else {
            Commit bHead = getCommit(branches.get(args[1]));
            Commit cHead = getHeadCommit();
            TreeMap<String, String> adds = getAddTree();
            String sTemp = System.getProperty("user.dir");
            List<String> tempFiles = Utils.plainFilenamesIn(sTemp);
            for (int i = 0; i < tempFiles.size(); i++) {
                if (!(cHead.getTree().containsKey(tempFiles.get(i)))) {
                    File p = new File(tempFiles.get(i));
                    String cwdContent = Utils.readContentsAsString(p);
                    String path = ".gitlet/Commits/";
                    path += bHead.getFileID(tempFiles.get(i));
                    if (bHead.getFileID(tempFiles.get(i)).equals("")) {
                        continue;
                    }
                    File r = new File(path);
                    String bContent = Utils.readContentsAsString(r);
                    if (!(cwdContent.equals(bContent))) {
                        String s = "There is an untracked file in the way; "
                                + "delete it, or add and commit it first.";
                        System.out.println(s);
                        return;
                    }
                }
            }
            TreeMap<String, String> fileTreeB = bHead.getTree();
            TreeMap<String, String> fileTreeH = cHead.getTree();
            for (Map.Entry<String, String> entry : fileTreeB.entrySet()) {
                String[] cs = {"", bHead.getID(), "--", entry.getKey()};
                checkout2(cs);
            }
            for (Map.Entry<String, String> entry : fileTreeH.entrySet()) {
                if (!(fileTreeB.containsKey(entry.getKey()))) {
                    File w = new File(entry.getKey());
                    if (w.exists()) {
                        w.delete();
                    }
                }
            }
            setHead(bHead.getID());
            setCurrBranch(args[1]);
            adds.clear();
            TreeMap<String, String> removes = new TreeMap<>();
            File aFile = new File(".gitlet/Stage/ADDS");
            File rFile = new File(".gitlet/Stage/REMOVES");
            aFile.delete();
            rFile.delete();
            Utils.writeObject(aFile, adds);
            Utils.writeObject(rFile, removes);
        }
    }

    /** This prints out the information of commits starting
     * from the head and moving strictly backwards to the
     * initial commit. */
    public void log() {
        Commit com = getHeadCommit();
        String sLog = "";
        while (true) {
            sLog += "===\ncommit ";
            sLog += com.getID();
            sLog += "\nDate: " + com.getTime() + "\n";
            sLog += com.getMessage() + "\n\n";
            if (com.getParent().equals("")) {
                break;
            }
            com = getCommit(com.getParent());
        }
        System.out.print(sLog);
    }

    /** This prints out all the Commit ids that contain
     * the message passed into this method.
     * @param m is the message to find */
    public void find(String m) {
        ArrayList<String> ids = getAllCommits();
        boolean error = true;
        for (int i = 0; i < ids.size(); i++) {
            Commit c = getCommit(ids.get(i));
            if (c.getMessage().equals(m)) {
                error = false;
                System.out.println(c.getID());
            }
        } if (error) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** This creates a new branch with the name passed to the method.
     * @param b is the name of the branch */
    public void branch(String b) {
        TreeMap<String, String> branches = getBranchTree();
        if (branches.containsKey(b)) {
            System.out.println("A branch with that name already exists.");
        } else {
            branches.put(b, getHeadCommit().getID());
            File f = new File(".gitlet/Commits/BRANCHES");
            f.delete();
            Utils.writeObject(f, branches);
        }
    }

    /** This removes a branch with the name passed to the method.
     * @param b is the name of the branch */
    public void rmBranch(String b) {
        TreeMap<String, String> branches = getBranchTree();
        if (!branches.containsKey(b)) {
            System.out.println("A branch with that name does not exist.");
        } else if (getCurrBranchName().equals(b)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            branches.remove(b);
        }
        File f = new File(".gitlet/Commits/BRANCHES");
        f.delete();
        Utils.writeObject(f, branches);
    }

    /** This prints out the information of all commits. */
    public void globalLog() {
        String sLog = "";
        ArrayList<String> commits = getAllCommits();
        for (int i = 0; i < commits.size(); i++) {
            Commit c = getCommit(commits.get(i));
            sLog += "===\ncommit ";
            sLog += c.getID();
            sLog += "\nDate: " + c.getTime() + "\n";
            sLog += c.getMessage() + "\n\n";
        }
        System.out.print(sLog);
    }

    /** This prints out the current gitlet status. */
    public void status() {
        ArrayList<String> branchNames = alphabeticalBranchNames();
        ArrayList<String> addNames = alphabeticalAddNames();
        ArrayList<String> removeNames = alphabeticalRemoveNames();
        String s = "=== Branches ===\n";
        for (int i = 0; i < branchNames.size(); i++) {
            if (branchNames.get(i).equals(getCurrBranchName())) {
                s += "*";
            }
            s += branchNames.get(i) + "\n";
        }
        s += "\n=== Staged Files ===\n";
        for (int k = 0; k < addNames.size(); k++) {
            s += addNames.get(k) + "\n";
        }
        s += "\n=== Removed Files ===\n";
        for (int j = 0; j < removeNames.size(); j++) {
            s += removeNames.get(j) + "\n";
        }
        s += "\n=== Modifications Not Staged For Commit ===\n";
        s += "\n=== Untracked Files ===";
        System.out.println(s);
    }

    public void reset(String id) {
        TreeMap<String, String> branches = getBranchTree();
        String s = "0";
        while (branches.containsKey(s)) {
            s += "0";
        }
        Commit c = getSubstringCommit(id);
        if (c == null) {
            System.out.println("No commit with that id exists.");
        } else {
            branches.put(s, c.getID());
            File f = new File(".gitlet/Commits/BRANCHES");
            f.delete();
            Utils.writeObject(f, branches);
            String[] temp = {"", s};
            checkout3(temp);
            branches.remove(s);
            f.delete();
            Utils.writeObject(f, branches);
            setCurrBranch(findBranch(c.getID()));
        }
    }
}
