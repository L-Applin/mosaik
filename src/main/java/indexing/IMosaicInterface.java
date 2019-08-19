package indexing;

import lombok.NonNull;
import result.MosaicSearchResult;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface IMosaicInterface {


    /**
     * This methods sets the path were the mosaic should be kept.<p></p>
     * By default, the implementation of the interface should look at the folder located at
     * <emph>./data/img/mosaique</emph>, if this method is not called.
     * @param path
     */
    void
    setMosaicImagePath(@NonNull String path);


    /**
     *
     */
    void update(String soucreFile) throws IOException;



    MosaicSearchResult
    search(String studentName, String departement, String year) throws IOException;


    Set<String>
    getAllValueForFieldName(@NonNull String fieldName) throws IOException;

    List<String>
    getAllyearsForDept(String departement) throws IOException;
}
