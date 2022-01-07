package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/** Commit file.
 * @author Ruobin
 */
public class Commit implements Serializable {
    /** name of message. */
    private String message;
    /** Date. */
    private Date date;
    /** Parent. */
    private String parent;
    /** Parent1. */
    private String parent1;
    /** Parent2. */
    private String parent2;
    /** Blob. */
    private HashMap<String, String> blob;
    /** Blobs. */
    private ArrayList<String> blobs;
    /** id. */
    private String id;
    /** mergeid. */
    private String mergeID;
    /** name. */
    private String name1;
    /** parentBranch. */
    private String parentBranch;
    /** nBlobMap. */
    private HashMap<String, String> blobMap;
    /** main. */
    private static File file2 = new File(System.getProperty("user.dir"));
    /** commit path. */
    private static File commitfile = Utils.join(file2, ".gitlet/commits");
    /** branchFile. */
    private static File branchFile = Utils.join(file2, "branches");
    /** branch. */
    private static File branch = Utils.join(branchFile, "branches");

    /** Commit constructor. */
    Commit() {
        this.id = "";
        this.date = null;
        this.message = "";
        this.parent = "";
        this.blob = null;
        this.blobs = null;
        this.mergeID = "";
        this.parentBranch = "";
    }
    /** Commit constructor.
     * @param message1 k
     * @param date1 date
     * @param blob1 no
     * @param parent1w parent*/
    public Commit(String message1, Date date1,
                  HashMap<String, String> blob1, String parent1w) {
        this.message = message1;
        this.date = date1;
        this.parent = parent1w;
        blob = blob1;
        blobs = new ArrayList<>();
        this.parentBranch = "";
    }
    /** Commit constructor3.
     * @param commit commit */
    public Commit(Commit commit) {
        this.message = commit.getMessage();
        this.parent = commit.getParent();
        this.date = commit.getTimestamp();
        blob = new HashMap<>();
        blobs = new ArrayList<>();
        this.parentBranch = commit.getParentBranch();
    }
    /** Commit constructor.
     * @param message1 k
     * @param date1 date
     * @param blob1 no
     * @param parent11 parent
     * @param parent21 parent2*/
    public Commit(String message1, Date date1, HashMap<String,
            String> blob1, String parent11, String parent21) {
        this.message = message1;
        this.date = date1;
        this.parent = "";
        this.parent1 = parent11;
        this.parent2 = parent21;
        this.blob = blob1;
        this.mergeID = "";
        blobs = new ArrayList<>();
        this.parentBranch = "";
    }
    /** Commit constructor5.
     * @param message1 yikes
     * @param commit commit*/
    public Commit(String message1, Commit commit) {
        this.message = commit.getMessage();
        this.date = commit.getTimestamp();
        this.parent = commit.getParent();
        this.parent1 = commit.getParent1();
        this.parent2 = commit.getParent2();
        this.blob = commit.getBlob();
        blobs = commit.getBlobs();
        this.mergeID = commit.getMergeID();
        this.parentBranch = commit.getParentBranch();
    }
    /** Set Parent Branch.
     * @param branch1 branch*/
    public void setParentBranch(String branch1) {
        this.parentBranch = branch1;
    }
    /** return Merge Id.
     * @return mergeID. */
    public String getMergeID() {
        return this.mergeID;
    }
    /** Set Merge ID.
     * @param iD ID*/
    public void setMergeID(String iD) {
        this.mergeID = iD;
    }
    /** return ParentBranch.
     * @return parentBranch */
    public String getParentBranch() {
        return this.parentBranch;
    }

    /** return is Merge.
     * @return boolean
     */
    public boolean isMerge() {
        if (getMessage().contains("Merged")) {
            return true;
        }
        return false;
    }

    /** return hashmap.
     * @return blob.
     */
    public HashMap<String, String> getBlob() {
        return this.blob;
    }

    /** adds Blob.
     * @param name2 name
     * @param theblob blob!
     * @param iD id
     */
    public void addBlob(String name2, File theblob, String iD) {
        blob.put(name2, iD);
        blobs.add(iD);
        Blob newBlob = new Blob(name1, Utils.readContents(theblob), iD);
        newBlob.saveBlob();
    }

    /** sets ID.
     *
     * @param iD1 ID
     */
    public void setID(String iD1) {
        this.id = iD1;
    }

    /** returns name.
     *
     * @return name
     */
    public String getName() {
        return this.name1;
    }

    /** returns iterator.
     *
     * @param commit commit
     * @param branch1 branch
     * @return iterator
     */
    public Iterator<Commit> commitIterator(Commit commit,
                                           CommitTree branch1) {
        return new CommitIterator(commit, branch1);
    }

    /** returns all blobs.
     *
     * @param commit 1
     * @param givenName 2
     * @return 3
     */

    public HashMap<String, String> allBlobs(Commit commit, String givenName) {
        CommitTree thisBranch = Utils.readObject(branch, CommitTree.class);
        File branchFiles = Utils.join(branchFile, givenName);
        ArrayList<String> fileNames =
                new ArrayList<>(Utils.plainFilenamesIn(branchFiles));
        blobMap = new HashMap<>();
        if (commit.isMerge()) {
            if (commit.getBlob() != null) {
                if (!blobMap.containsKey(givenName)) {
                    commit.blobMap.put(givenName,
                            commit.getBlob().get(givenName));
                }
            }
            Commit parent1Copy = new Commit("copy1",
                    thisBranch.getTreeBranch().get(commit.getParent1()));
            Commit parent2Copy = new Commit("copy2",
                    thisBranch.getTreeBranch().get(commit.getParent2()));
            HashMap<String, String> parent1Blobs =
                    allBlobs(parent1Copy, parent1Copy.getParentBranch());
            HashMap<String, String> parent2Blobs =
                    allBlobs(parent2Copy, parent1Copy.getParentBranch());
        }
        while (commit != null) {
            if (commit.getBlob() != null) {
                for (String name : commit.getBlob().keySet()) {
                    if (!blobMap.containsKey(name)
                            && fileNames.contains(name)) {
                        blobMap.putIfAbsent(name, commit.getBlob().get(name));
                    }
                }
            }
            if (commit.getParent() != null) {
                commit = thisBranch.getTreeBranch().get(commit.getParent());
            } else {
                commit = null;
            }
        }
        return blobMap;
    }
    /** setParent.
     * @param name ok*/
    public void setParent(String name) {
        this.parent = name;
    }
    /** setID.
     * @ return id */
    public String getID() {
        return this.id;
    }

    /** return blob.
     *
     * @return blob
     */
    public ArrayList<String> getBlobs() {
        return blobs;
    }
    /** get message.
     * @return message*/
    public String getMessage() {
        return this.message;
    }
    /** get message1.
     * @return message*/
    public Date getTimestamp() {
        return this.date;
    }
    /** get parent1.
     * @return parent*/
    public String getParent() {
        return this.parent;
    }
    /** get parent1.
     * @ return parent*/
    public String getParent1() {
        return this.parent1;
    }
    /** get parent1.
     * @return parent */
    public String getParent2() {
        return this.parent2;
    }

    /** new date.
     *
     */
    public void updateDate() {
        this.date = new Date();
    }
    /** new date.
     * @param newMessage yes
     */
    public void updateMessage(String newMessage) {
        this.message = newMessage;
    }

    /** write blobs.
     *
     * @param main 1
     * @param read read
     */
    public void writeBlobs(File main, File read) {
        if (!main.isFile()) {
            try {
                main.createNewFile();
            } catch (IOException | ClassCastException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        Utils.writeContents(main, Utils.readContents(read));
    }

    /** class iterator.
     *
     */
    private class CommitIterator implements Iterator<Commit> {

        /** commit. */
        private Commit commit;
        /** commit. */
        private CommitTree branch;
        /** commit. */
        private Commit temp;

        /** commit iterator.
         *
         * @param commit1 com1
         * @param branch1 bran1
         */
        CommitIterator(Commit commit1, CommitTree branch1) {
            this.commit = commit1;
            this.branch = branch1;

        }
        /** hasNext. */
        @Override
        public boolean hasNext() {
            if (commit.getParent().equals("")) {
                return false;
            }
            return true;
        }
        /** hasNext. */
        @Override
        public Commit next() {
            temp = Utils.readObject(
                    Utils.join(commitfile, commit.getParent()), Commit.class);
            return temp;
        }

    }
}

