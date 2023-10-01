import ch.heigvd.Inko;
import org.junit.*;
import picocli.CommandLine;

import static org.junit.Assert.*;

public class InkoTest {

    private static void RunSuccess(String... args) {
        Inko inko = new Inko();
        CommandLine commandLine = new CommandLine(inko);
        int exitCode = commandLine.execute(args);
        assertEquals(0, exitCode);
    }
    @Test
    public void jpgToPng() {
        RunSuccess("-p", "src/test/resources/test.jpg", "-o", "src/test/resources/output/jpgToPng", "-of", "png");
    }

    @Test
    public void pngToJpg() {
        RunSuccess("-p", "src/test/resources/test.png", "-o", "src/test/resources/output/pngToJpg", "-of", "jpg");
    }

    @Test
    public void pngSemiTransparentToJpg() {
        RunSuccess("-p", "src/test/resources/testSemiTransparent.png", "-o", "src/test/resources/output/pngSemiTransparentToJpg", "-of", "jpg");
    }

    @Test
    public void charInNum() {
        RunSuccess("-p", "src/test/resources/test.jpg", "-o", "src/test/resources/output/charInNum", "-of", "jpg", "-fs", "f5");
        RunSuccess("-p", "src/test/resources/test.jpg", "-o", "src/test/resources/output/charInNum", "-of", "jpg", "-m", "m5");
    }

    @Test
    public void fullParamWithExif() {
        RunSuccess("-p", "src/test/resources/exif.jpg", "-o", "src/test/resources/output/fullExif",
                "-of", "png", "-t", "Date:", "-d", "-df", "dd.MM.yy", "-gmt", "-2", "-cm", "-gps", "-is", "-s", "_",
                "-f", "Ubuntu", "-fw", "Italic", "-fs", "80", "-fc", "#FFFF0000", "-bg", "#8000FFFF", "-po", "lt", "-m", "20");
    }




}
