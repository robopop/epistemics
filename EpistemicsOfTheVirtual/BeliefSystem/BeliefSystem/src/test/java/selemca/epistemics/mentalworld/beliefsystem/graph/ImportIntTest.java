package selemca.epistemics.mentalworld.beliefsystem.graph;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemConfig;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemTestConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BeliefSystemTestConfig.class, BeliefSystemConfig.class})
@Transactional
public class ImportIntTest {
    private static final String BELIEF_SYSTEM_FILE_NAME = "BeliefSystem.zip";

    @Autowired
    private Importer importer;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testImport() throws IOException {
        File importFile = getBeliefsystemZip();
        importer.importDbData(importFile, false);
    }

    private File getBeliefsystemZip() throws IOException {
        File result = temporaryFolder.newFile(BELIEF_SYSTEM_FILE_NAME);
        InputStream inputStream = this.getClass().getResourceAsStream("/" + BELIEF_SYSTEM_FILE_NAME);
        OutputStream outputStream = new FileOutputStream(result);
        IOUtils.copy(inputStream, outputStream);
        return result;
    }
}
