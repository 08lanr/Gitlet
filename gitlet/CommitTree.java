package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/** commit tree class.
 * @author Ruobin
 */
public class CommitTree implements Serializable {
    /**
     * linked hashmap.
     */
    private LinkedHashMap<String, Commit> treeBranch = new LinkedHashMap<>();
    /**
     * linked hashmap.
     */
    private HashMap<String, Commit> branches = new HashMap<>();
    /** mainstage. */
    private static Stage mainstage = new Stage();
    /**
     * user directory.
     */
    private String directory = System.getProperty("user.dir");
    /**
     * working dir.
     */
    private static String file1 = System.getProperty("user.dir");
    /**
     * user directory.
     */
    private static File file2 = new File(System.getProperty("user.dir"));
    /**
     * user directory.
     */
    private static File branchFile = Utils.join(file2, "branches");
    /**
     * working dir.
     */
    private static File branches1 = Utils.join(branchFile, "branches");
    /**
     * working dir blobs.
     */
    private static Path blobpath = Paths.get(file1, ".gitlet", "blobs");
    /**
     * working dir.
     */
    private static File blobs1 = new File(blobpath.toString());
    /**
     * working dir.
     */
    private Commit head;
    /**
     * branch.
     */
    private String branch = new String();
    /**
     * branch.
     */
    private String headBranch;
    /**
     * branch.
     */
    private String active;

    /**
     * constructor.
     *
     * @param commit  commit
     * @param branch1 branch
     */
    public CommitTree(Commit commit, String branch1) {
        this.head = commit;
        this.branch = branch1;
        this.headBranch = branch1;
        this.active = branch1;
        treeBranch.put(commit.getID(), commit);
        branches.put(branch1, head);
        File current = Utils.join(branchFile, branch1);
        current.mkdirs();

    }

    /**
     * constructor.
     *
     * @param branchName branch
     */
    public CommitTree(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists");
        }
        branches.put(branchName, head);

    }

    /**
     * helper.
     *
     * @param dataType type
     */
    public void title(String dataType) {
        System.out.println(String.format("=== %s ===", dataType));
    }

    /**
     * addfunc.
     *
     * @param commit this
     * @param main   thistree
     */
    public void add(Commit commit, CommitTree main) {
        treeBranch.put(commit.getID(), commit);
    }

    /**
     * helper.
     *
     * @param commit this
     */
    public void branchCommit(Commit commit) {
        this.active = headBranch;
    }

    /**
     * helper.
     *
     * @param commit this
     */
    public void setHead(Commit commit) {
        this.head = commit;
        headBranch = Utils.sha1(Utils.serialize(commit));
    }

    /**
     * helper.
     *
     * @return branch
     */
    public String getBranch() {
        return this.branch;
    }

    /**
     * helper.
     *
     * @return String
     */
    public String getHeadBranch() {
        return this.headBranch;
    }

    /**
     * helper.
     *
     * @return String
     */
    public Commit getHead() {
        return this.head;
    }

    /**
     * helper.
     *
     * @param name thisname
     * @return String
     */
    public Commit getBranchHead(String name) {
        return treeBranch.get(name);
    }

    /**
     * helper.
     *
     * @param name thisname
     * @return Commit
     */
    public Commit getCurrentBranchHead(String name) {
        return branches.get(name);
    }

    /**
     * helper.
     *
     * @param commit  thisname
     * @param branch1 thisBranch
     */
    public void setUp(Commit commit, CommitTree branch1) {
        branch1.add(commit, branch1);
        branch1.setHead(commit);
        branch1.branchCommit(commit);
        branches.put(branch1.getBranch(), commit);
    }

    /**
     * return TreeBranch.
     *
     * @return tree
     */
    public LinkedHashMap<String, Commit> getTreeBranch() {
        return this.treeBranch;
    }

    /**
     * return Hashranch.
     *
     * @return tree
     */
    public HashMap<String, Commit> getBranches() {
        return this.branches;
    }

    /**
     * set branch.
     *
     * @param branch1 set
     */
    public void setBranch(String branch1) {
        this.branch = branch1;
    }

    /**
     * checkouthelper.
     *
     * @param thisCommit    set
     * @param thisBranch    branch
     * @param currentBranch string
     */
    public void checkoutHelper(Commit thisCommit,
                               CommitTree thisBranch,
                               String currentBranch) {
        HashMap<String, String> blobs2 = thisCommit.getBlob();
        Utils.writeContents(branches1, Utils.serialize(thisBranch));
        File newBranch = Utils.join(branchFile, thisBranch.getBranch());
        Commit newCommit = thisBranch.getHead();
        ArrayList<String> branchFiles = new ArrayList<>(
                Utils.plainFilenamesIn(newBranch));
        if (blobs2 != null) {
            for (String key : blobs2.keySet()) {
                File currBlob = Utils.join(blobs1, blobs2.get(key));
                File mainBlob = Utils.join(file2, key);
                if (currBlob.isFile()) {
                    newCommit.writeBlobs(mainBlob, currBlob);
                }
            }
        }
        for (File file3 : file2.listFiles()) {
            if (!branchFiles.contains(file3.getName()) && file3.isFile()) {
                file3.delete();
            }
        }
        ArrayList<String> directory1 = new ArrayList<>(
                Utils.plainFilenamesIn(file2));
        for (File file3 : newBranch.listFiles()) {
            File copy = Utils.join(file2, file3.getName());
            if (!directory1.contains(file3.getName())) {
                try {
                    copy.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utils.writeContents(copy, Utils.readContents(file3));
        }
        thisBranch.setHead(thisCommit);
        return;
    }

    /** merge Helper.
     *
     * @param givenBlobs given
     * @param currentBlobs curr
     * @param splitBlobs split
     * @param totalBlobs total
     * @param mergeBlobs merge
     * @param branchName branch
     */
    public void mergeHelper(HashMap<String, String> givenBlobs,
                            HashMap<String, String> currentBlobs,
                            HashMap<String, String> splitBlobs,
                            HashSet<String> totalBlobs,
                            HashMap<String, String> mergeBlobs,
                            String branchName) {
        String give = "";
        String curren = "";
        String spli = "";
        totalBlobs.addAll(givenBlobs.keySet());
        totalBlobs.addAll(currentBlobs.keySet());
        if (splitBlobs == null) {
            spli = "";
        } else {
            totalBlobs.addAll(splitBlobs.keySet());
        }
        for (String blobName : totalBlobs) {
            if (givenBlobs.containsKey(blobName)) {
                give = givenBlobs.get(blobName);
            } else {
                give = "";
            }
            if (currentBlobs.containsKey(blobName)) {
                curren = currentBlobs.get(blobName);
            } else {
                curren = "";
            }
            if (splitBlobs != null) {
                if (splitBlobs.containsKey(blobName)) {
                    spli = splitBlobs.get(blobName);
                }
            }
            if (give.equals(spli) && give.equals(curren)) {
                mergeBlobs.put(blobName, give);
            } else if (give.equals("") && curren.equals(spli)
                    && (!curren.equals(give))) {
                File removal = Utils.join(file2, blobName);
                File removestage = Utils.join(mainstage.getRemoval(), blobName);
                if (!removestage.exists()) {
                    try {
                        removestage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Utils.writeContents(removestage, Utils.readContents(removal));
                Utils.restrictedDelete(removal);
            } else if (!give.equals(spli) && !curren.equals(spli)
                    && give.equals(curren)) {
                mergeBlobs.put(blobName, curren);
            } else if (give.equals(spli) && !curren.equals(spli)) {
                mergeBlobs.put(blobName, curren);
            } else if (!give.equals(spli) && curren.equals(spli)
                    && !give.equals("")) {
                mergeBlobs.put(blobName, give);
                mergeBig(branchName, blobName);
            } else if (!give.equals(spli) && !curren.equals(spli)
                    && !give.equals(curren)) {
                mergeBlobs.put(blobName, give);
                writeContents(give, curren, blobName);
            }
        }
    }

    /** merges more.
     *
     * @param branchName this
     * @param blobName name
     */
    public void mergeBig(String branchName, String blobName) {
        File thisBranchName = Utils.join(branchFile, branchName);
        File branchFile1 = Utils.join(thisBranchName, blobName);
        File file3 = Utils.join(file2, blobName);
        File stageFile = Utils.join(mainstage.getStaging(), blobName);
        if (!file3.exists()) {
            try {
                file3.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeContents(file3, Utils.readContents(branchFile1));
        Utils.writeContents(stageFile, Utils.readContents(branchFile1));
    }

    /** writes blob.
     *
     * @param givenBlob given
     * @param currentBlob curr
     * @param blobName name
     */
    public void writeContents(String givenBlob,
                              String currentBlob,
                              String blobName) {
        File givenFile = Utils.join(blobs1, givenBlob);
        File currentFile = Utils.join(blobs1, currentBlob);
        File file3 = Utils.join(file2, blobName);
        File staging = Utils.join(mainstage.getStaging(), blobName);
        String givenString = "";
        String currString = "";
        if (givenFile.exists() && !givenBlob.equals("")) {
            givenString = Utils.readContentsAsString(givenFile);
        }
        if (currentFile.exists() && !currentBlob.equals("")) {
            currString = Utils.readContentsAsString(currentFile);
        }
        Formatter out = new Formatter();
        out.format("<<<<<<< HEAD%n%s=======%n%s>>>>>>>%n",
                currString, givenString);
        Utils.writeContents(file3, out.toString());
        Utils.writeContents(staging, Utils.readContents(file3));
        System.out.println("Encountered a merge conflict.");
    }

    /** reduces more.
     *
     * @param mergeCommit this
     * @param branchName name
     * @param thisBranch branch
     */
    public void mergeHelper2(Commit mergeCommit,
                              String branchName,
                              CommitTree thisBranch) {
        mergeCommit.setParentBranch(branchName);
        String mergeID = mergeCommit.getParent1().substring(0, 7) + " "
                + mergeCommit.getParent2().substring(0, 7);
        String iD = Utils.sha1(Utils.serialize(mergeCommit));
        mergeCommit.setID(iD);
        thisBranch.getTreeBranch().put(iD, mergeCommit);
        for (File file3 : mainstage.getStaging().listFiles()) {
            File newBranch = Utils.join(branchFile, thisBranch.getBranch());
            File branchFile1 = Utils.join(newBranch, file3.getName());
            if (!branchFile1.exists()) {
                try {
                    branchFile1.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utils.writeContents(branchFile1, Utils.readContents(file3));
            file3.delete();
        }
        for (File file3 : mainstage.getRemoval().listFiles()) {
            String fileName = file3.getName();
            File permaed = Utils.join(mainstage.getPermaed(), fileName);
            if (!permaed.exists()) {
                try {
                    permaed.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utils.writeContents(permaed, Utils.readContents(file3));
            file3.delete();
        }
        mergeCommit.setMergeID(mergeID);
        mergeCommit.setParent(thisBranch.getBranches().get
                (thisBranch.getBranch()).getID());
    }
}
