package ru.strcss.projects.moneycalcmigrator;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = MigratorMain.class)
public class ParserTest extends AbstractTestNGSpringContextTests {

    @MockBean(name = "FileReaderMock")
    private FileReader fileReader;

    @Test
    public void testParse() {
    }
}