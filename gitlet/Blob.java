package gitlet;
import java.io.File;
import java.io.IOException;
/** Blob class for Gitlet, the tiny stupid version-control system.
 *  @author Ruobin
 */
public class Blob {
    /** name of Blob. */
    private String names;
    /** name of content. */
    private byte[] contents;
    /** name of ID. */
    private String id;
    /** blob file path.*/
    static final File BLOB = new File(".gitlet/blobs");

    /** blob constructor.
     * @param name d
     * @param content d
     * @param iD d*/
    Blob(String name, byte[] content, String iD) {
        this.names = name;
        this.contents = content;
        this.id = iD;
    }
    /** get ID.
     * @return iD*/
    public String getId() {
        return this.id;
    }
    /** get name.
     * @return name */
    public String getName() {
        return this.names;
    }
    /** get Content.
     * @return content*/
    public byte[] getContent() {
        return this.contents;
    }
    /** SaveBlob. */
    public void saveBlob() {
        try {
            File blob = Utils.join(BLOB, getId());
            if (!blob.exists()) {
                blob.createNewFile();
            }
            Utils.writeContents(blob, contents);
        } catch (IOException | ClassCastException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
