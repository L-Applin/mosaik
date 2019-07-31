package indexing;

import java.io.IOException;
import java.nio.file.Path;

public class IndexServiceFactory {

    private Path path;

    public IndexServiceFactory(Path path) {
        this.path = path;
    }

    public IMosaicInterface createService() throws IOException {
        IndexService service = new IndexService(path);
        service.initIndex();
        return service;
    }

}
