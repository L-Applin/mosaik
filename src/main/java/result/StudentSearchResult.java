package result;

import entity.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class StudentSearchResult implements MosaicSearchResult {

    /**
     * The list of returned results
     */
    private List<ResultItem> results;
    public List<ResultItem> getResults() {
        results.sort(Comparator.comparing(
                resultItem -> ((MosaicResult) resultItem).student.getLastName(),
                String.CASE_INSENSITIVE_ORDER));
        return results;
    }

    public StudentSearchResult() {
        results=new ArrayList<>();
    }

    public void addResult(MosaicResult res){
        results.add(res);
    }

    public void addResult(Student student, String path){
        results.add(new MosaicResult(path, student));
    }

    @Override
    public Iterator<ResultItem> iterator() {
        return results.iterator();
    }


    /**
     * Wrapper around the student and the path to its mosaic image
     */
    public static class MosaicResult implements ResultItem {
        public final String mosaicPath;
        public final Student student;
        MosaicResult(String mosaicPath, Student student) {
            this.mosaicPath = mosaicPath;
            this.student = student;
        }
        public String getPath() { return mosaicPath; }
        public String toString() { return student.toString(); }
    }

    public String toString() { return results.toString(); }
}
