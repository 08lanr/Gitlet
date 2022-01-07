package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** stage file.
 * @author Ruobin
 */
public class Stage implements Serializable {
    /** working dir. */
    private HashMap<String, String> stageAdded;
    /** working dir. */
    private static HashMap<String, String> stageRemoved;
    /** working dir. */
    private static HashMap<String, String> modified1;
    /** working dir. */
    private static HashMap<String, String> deleted1;
    /** working dir. */
    private static HashMap<String, String> notAdded1;
    /** working dir. */
    private static File main = new File(System.getProperty("user.dir"));
    /** working dir. */
    private static File staging = Utils.join(main, "stagingArea");
    /** working dir. */
    private static File removed = Utils.join(main, "removeFiles");
    /** working dir. */
    private static File modified = Utils.join(main, "modifiedFiles");
    /** working dir. */
    private static File branch = Utils.join(main, "branches");
    /** working dir. */
    private static File branches = Utils.join(branch, "branches");
    /** working dir. */
    private static File untrackedFile = Utils.join(main, "untrackedFiles");
    /** working dir. */
    private static File permaBan = Utils.join(main, "rmCommitted");
    /** working dir. */
    private static File blobs = Utils.join(main, ".gitlet.blobs");
    /** working dir. */
    private static List<String> untracked;
    /** Constructor.*/
    Stage() {
        this.stageAdded = new HashMap<>();
        this.stageRemoved = new HashMap<>();
        this.modified1 = new HashMap<>();
        deleted1 = new HashMap<>();
        notAdded1 = new HashMap<>();
        this.untracked = new ArrayList<>();
    }
    /** find modified. */
    public static void findModified() {
        CommitTree branch1 = Utils.readObject(branches, CommitTree.class);
        Commit thisCommit = branch1.getBranches().get(branch1.getBranch());
        List<String> allFiles = Utils.plainFilenamesIn(main);
        List<String> fileNames = new ArrayList<>();
        for (File file : staging.listFiles()) {
            String name = file.getName();
            fileNames.add(name);
        }
        for (File file : removed.listFiles()) {
            String name = file.getName();
            fileNames.add(name);
        }
        for (Commit commit : branch1.getTreeBranch().values()) {
            for (File file : main.listFiles()) {
                if (file.isFile() && !fileNames.contains(file.getName())) {
                    String fileName = file.getName();
                    File mainFile = Utils.join(main, fileName);
                    File changed = Utils.join(modified, fileName);
                    String serial = Utils.sha1(Utils.readContents(file));
                    if (!changed.exists()) {
                        try {
                            changed.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                    if (commit.getBlob() == null) {
                        changed.delete();
                    } else if (commit.getBlob().containsKey(fileName)
                            && (!commit.getBlob().get(fileName).equals(serial)
                            && changed.exists())) {
                        Utils.writeContents(changed,
                                Utils.readContents(mainFile));
                    } else {
                        changed.delete();
                    }
                }
            }
        }
    }

    /** find untracked files. */
    public static void findUntracked() {
        CommitTree branch1 = Utils.readObject(branches, CommitTree.class);
        Commit thisCommit = branch1.getHead();
        List<String> fileNames = new ArrayList<>();
        findModified();
        ArrayList<String> permaed = new ArrayList<>();
        ArrayList<String> modifiedFiles = new ArrayList<>(
                Utils.plainFilenamesIn(modified));
        for (File file : staging.listFiles()) {
            String name = file.getName();
            fileNames.add(name);
        }
        for (File file : removed.listFiles()) {
            String name = file.getName();
            fileNames.add(name);
        }
        for (Commit commit : branch1.getTreeBranch().values()) {
            if (commit.getBlob() != null) {
                for (String name : commit.getBlob().keySet()) {
                    fileNames.add(name);
                }
            }
        }
        for (File file : main.listFiles()) {
            if (file.isFile() && !fileNames.contains(file.getName())) {
                String fileName = file.getName();
                File newUntracked = Utils.join(untrackedFile, fileName);
                if (permaBan.listFiles() != null) {
                    for (String string : Utils.plainFilenamesIn(permaBan)) {
                        permaed.add(string);
                    }
                }
                if (!fileNames.contains(fileName)
                        || permaed.contains(fileName)) {
                    if (!newUntracked.exists()) {
                        try {
                            newUntracked.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Utils.writeContents(newUntracked, Utils.readContents(file));
                }
            }
        }
    }

    /** constructor.
     *
     * @param stage yep
     */
    public Stage(Stage stage) {
        this.stageAdded = stage.getStageAdded();
    }
    /** findstage added.
     * @return hashmap*/
    public HashMap<String, String> getStageAdded() {
        return stageAdded;
    }
    /** find removed.
     * @return hash,ap*/
    public HashMap<String, String> getStageRemoved() {
        return stageRemoved;
    }

    /** Addstage added.
     *
     * @param stage this
     * @param name this
     * @param iD this
     */
    public void addStage(Stage stage, String name, String iD) {
        stageAdded.put(name, iD);
    }
    /** helper. */
    public void clear() {
        for (File file: staging.listFiles()) {
            file.delete();
        }
    }
    /** helper. */
    public void saveStage() {
        try {
            for (String keys : stageAdded.keySet()) {
                File stage = Utils.join(staging, keys);
                stage.createNewFile();
                Utils.writeContents(stage, stageAdded.get(keys));
            }
        } catch (IOException | ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    /** helper dir.
     * @return directory */
    public File getStaging() {
        return staging;
    }
    /** helper dir.
     * @return directory */
    public File getRemoval() {
        return removed;
    }
    /** helper dir.
     * @return directory */
    public File getModded() {
        return modified;
    }
    /** helper dir.
     * @return directory */
    public File getUntracking() {
        return untrackedFile;
    }
    /** helper dir.
     * @return directory */
    public File getPermaed() {
        return permaBan;
    }
}
