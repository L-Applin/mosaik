package indexing;

import app.AppConfig;
import app.Bundles;
import entity.Departement;
import entity.Student;
import entity.UnknownDate;
import lombok.NonNull;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.DeptYearSearchResult;
import result.MosaicSearchResult;
import result.StudentSearchResult;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;


// TODO: 2019-03-23 IOException handling





public class IndexService implements IMosaicInterface {

    public static final String first_name_field = "fname";
    public static final String last_name_field = "lname";
    public static final String year_field = "date";
    public static final String dept_field = "prog";
    public static final String grad_year = "grad";
    public static final int default_max_count = 25;

    private static Logger logger = LoggerFactory.getLogger(IndexService.class);

    private Path docPath;
    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;
    private String mosaicImgFolder;


    IndexService(Path directoryPath) {
        this.docPath = directoryPath;

    }





    /* **************************** */
    /*       PUBLIC INTERFACE       */
    /* **************************** */


    @Override
    public void setMosaicImagePath(String path) {
        this.mosaicImgFolder = path;
    }





    @Override
    public MosaicSearchResult
    search(String studentName, String departement, String year) throws IOException {
        BooleanQuery.Builder qb = new BooleanQuery.Builder();

        if (notNullSearchField(studentName)){
            String fname, lname;
            if (studentName.trim().contains(" ")) {

                fname = studentName.substring(0, studentName.indexOf(" ")).trim();
                lname = studentName.substring(studentName.indexOf(" ") + 1).trim();
                logger.info(fname + " " + lname);

                BooleanQuery.Builder firstNameQueryBuilder = new BooleanQuery.Builder();
                firstNameQueryBuilder.add(new FuzzyQuery(new Term(first_name_field, fname), 1), BooleanClause.Occur.SHOULD);
                //firstNameQueryBuilder.add(new RegexpQuery(new Term(first_name_field+".*", fname)), BooleanClause.Occur.SHOULD);

                BooleanQuery.Builder lastNameQueryBuilder = new BooleanQuery.Builder();
                lastNameQueryBuilder.add(new FuzzyQuery(new Term(last_name_field, lname), 1), BooleanClause.Occur.SHOULD);
                //lastNameQueryBuilder.add(new RegexpQuery(new Term(last_name_field+".*", lname)), BooleanClause.Occur.SHOULD);

                qb.add(firstNameQueryBuilder.build(), BooleanClause.Occur.MUST);
                qb.add(lastNameQueryBuilder.build(), BooleanClause.Occur.MUST);

            } else {
                studentName=studentName.trim();
                BooleanQuery.Builder nameQueryBuilder = new BooleanQuery.Builder();
                nameQueryBuilder.add(new FuzzyQuery(new Term(first_name_field, studentName), 1), BooleanClause.Occur.SHOULD);
                nameQueryBuilder.add(new FuzzyQuery(new Term(last_name_field, studentName), 1), BooleanClause.Occur.SHOULD);
                nameQueryBuilder.add(new RegexpQuery(new Term(first_name_field, studentName+".*")), BooleanClause.Occur.SHOULD);
                nameQueryBuilder.add(new RegexpQuery(new Term(last_name_field, studentName+".*")), BooleanClause.Occur.SHOULD);
                qb.add(nameQueryBuilder.build(), BooleanClause.Occur.MUST);
            }
        }

        if (notNullSearchField(departement)){
            qb.add(new TermQuery(new Term(dept_field, departement)), BooleanClause.Occur.MUST);
        }

        if (notNullSearchField(year)){
            qb.add(new TermQuery(new Term(year_field, year)), BooleanClause.Occur.MUST);
        }

        TopDocs hits = searcher.search(qb.build(), 5000);

        if (notNullSearchField(studentName)) {
            return toStudentResult(hits);
        }
        else {
            return toDeptYearResult(hits);
        }

    }


    private boolean notNullSearchField(String searchField){
        return searchField!=null && !searchField.trim().equals("");
    }


    private MosaicSearchResult toStudentResult(TopDocs hits) throws IOException {
        logger.info("résultat de recherche pour un étudiant : {} hits", hits.totalHits);
        StudentSearchResult result = new StudentSearchResult();
        for (ScoreDoc docu:hits.scoreDocs){
            Document document=searcher.doc(docu.doc);
            String fname = document.get(first_name_field);
            String lname = document.get(last_name_field);
            Departement departement = Departement.of(document.get(dept_field));

            String mosaicImgPath = String.format(mosaicImgFolder, departement.directory, departement.pathId, document.get(year_field));

            LocalDate gradYear;
            try {
                gradYear = LocalDate.parse(document.get(grad_year));
                result.addResult(new Student(departement, fname, lname, gradYear, document.get(year_field)), mosaicImgPath);
            } catch (DateTimeParseException dfe){
                logger.info("Error formating date : " + document.get(grad_year));
                result.addResult(new Student(departement, fname, lname, new UnknownDate(), document.get(year_field)), mosaicImgPath);
            }

        }

        return result;

    }


    private MosaicSearchResult toDeptYearResult(TopDocs hits) throws IOException {
        logger.info("résultat de recherche pour un dépt/année : {} hits", hits.totalHits);
        // TODO: 2019-04-29 complete
        DeptYearSearchResult resultItems = new DeptYearSearchResult();
        Set<DeptYearSearchResult.MosaicResult> allResultYears = new HashSet<>();
        for (ScoreDoc docu:hits.scoreDocs) {
            Document document=searcher.doc(docu.doc);
            Departement departement = Departement.of(document.get(dept_field));
            String mosaicImgPath = String.format(mosaicImgFolder, departement.directory, departement.pathId, document.get(year_field));
            allResultYears.add(new DeptYearSearchResult.MosaicResult(mosaicImgPath, document.get(year_field), departement));
        }
        allResultYears.forEach(res -> resultItems.getResults().add(res));


        return resultItems;
    }



    @Override
    public void update(String soucreFile) throws IOException {
        if (soucreFile.endsWith(".csv")) {
            logger.info("mise à jour à partir du csv {}", soucreFile);
            createIndexFromCSV(soucreFile);
            return;
        }

        if (soucreFile.endsWith(".xls")){
            logger.info("mise à jour à partir du fichier excel {}", soucreFile);
            createIndexFromXLS(soucreFile);
            return;
        }

    }




    /* *************** */
    /* PRIVATE METHODS */
    /* *************** */

    void initIndex() throws IOException {
        dir = new MMapDirectory(docPath);
        if (!dirAlreadyExists(dir)) {
            // FIXME: 2019-04-20 create from XLS
            // FIXME: 2019-04-28 load path dynamically
            ResourceBundle pathsResource = ResourceBundle.getBundle(Bundles.PATH.bundle(), AppConfig.getInstance().getLocale());
            createIndexFromCSV(pathsResource.getString("default_csv_location"));
        }
        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }



    private boolean dirAlreadyExists(Directory dir) throws IOException {
        return DirectoryReader.indexExists(dir);
    }


    
    private void createIndexFromXLS(String xlsPath) throws IOException {
        // TODO: 2019-04-20
        throw new UnsupportedOperationException("Excel format not yet supported");

    }



    private void createIndexFromCSV(String csvPath) throws IOException {
        // TODO: 2019-04-20 use excel file directly instead
        logger.info("CREATING INDEX");
        File csv = new File(csvPath);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        Scanner scanner = new Scanner(csv);

        while (scanner.hasNext()){
            Document doc = new Document();

            String[] line = scanner.nextLine().replace(";;", "; ;").split(";");

            Field progField = new StringField(dept_field, line[0], Field.Store.YES);
            Field lnameField = new TextField(last_name_field, line[1], Field.Store.YES);
            Field fnameField = new TextField(first_name_field, line[2], Field.Store.YES);
            Field gradYearField = new StringField(grad_year, line[6], Field.Store.YES);
            String mosYear = (line[7].equals("") || line[7].equals(" ")?"empty":line[7]);

            Field mosYearField = new StringField(year_field, mosYear, Field.Store.YES);

            doc.add(fnameField); doc.add(lnameField); doc.add(gradYearField); doc.add(progField); doc.add(mosYearField);
            writer.addDocument(doc);

        }

        writer.commit();

    }



    private Set<Document>
    queryResolution(@NonNull Query q, int maxCount) throws IOException {
        TopDocs hits = searcher.search(q, maxCount);
        // setting return result
        Set<Document> results = new HashSet<>(maxCount);
        for (ScoreDoc docu:hits.scoreDocs){
            results.add(searcher.doc(docu.doc));
        }
        return results;

    }


    private Set<Document>
    termQuery(@NonNull String fieldName, @NonNull String value, int maxCount) throws IOException {
        Query q = new TermQuery(new Term(fieldName, value));
        return queryResolution(q, maxCount);

    }






/**
 * The method will look into all documents in the index and
 * @param fieldName
 * @return
 * @throws IOException
 */


    public Set<String>
    getAllValueForFieldName(@NonNull String fieldName) throws IOException {
        Set<String> uniqueTerms = new HashSet<>();

        // first row is the CSV column names
        for (int i = 1; i < reader.maxDoc(); i++) {
            Document doc = reader.document(i);
            String termEntry = doc.get(fieldName);
            uniqueTerms.add(termEntry);
        }
        return uniqueTerms;

    }


}
