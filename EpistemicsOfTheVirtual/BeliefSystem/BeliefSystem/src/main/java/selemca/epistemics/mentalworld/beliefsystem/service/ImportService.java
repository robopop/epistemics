package selemca.epistemics.mentalworld.beliefsystem.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ImportService {
    void importDbData(File exportFile, boolean wipeDb) throws IOException;
    void importZipFile(InputStream input, boolean writeDb) throws IOException;
}
