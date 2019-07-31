package result;

import java.util.List;

public interface MosaicSearchResult extends Iterable<ResultItem>{
    List<ResultItem> getResults();
}
