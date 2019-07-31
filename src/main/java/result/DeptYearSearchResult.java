package result;

import entity.Departement;
import entity.Student;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DeptYearSearchResult implements MosaicSearchResult {

    private List<ResultItem> results;
    @Override public List<ResultItem> getResults() {
        return results;
    }


    public DeptYearSearchResult() {
        results=new ArrayList<>();
    }

    public void addResult(String path, String year, Departement departement){
        results.add(new MosaicResult(path, year, departement));
    }


    public Iterator<ResultItem> iterator() {
        return results.iterator();
    }


    /**
     * Wrapper around specific mosaic and its path
     */
    public static class MosaicResult implements ResultItem {
        final String path;
        final String year;
        final Departement departement;
        public MosaicResult(String path, String year, Departement departement) {
            this.path = path;
            this.year = year;
            this.departement = departement;
        }
        public String getPath() {
            return path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MosaicResult that = (MosaicResult) o;
            return Objects.equals(path, that.path) &&
                    Objects.equals(year, that.year) &&
                    departement == that.departement;
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, year, departement);
        }

        @Override
        public String toString() {
            return "MosaicResult{" +
                    "path='" + path + '\'' +
                    ", year='" + year + '\'' +
                    ", departement=" + departement +
                    '}';
        }
    }
}
