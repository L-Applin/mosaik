package result;


/**
A search result item that can return that path to an image
 */
public interface ResultItem {

    /**
     * @return the path to the image that corresponds to an item of the search results
     */
    String getPath();

}
