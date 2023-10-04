import ch.heigvd.Inko;
import org.junit.*;
import picocli.CommandLine;

import java.io.File;

import static org.junit.Assert.*;

public class InkoTest {

    private static int run(String... args) {
        Inko inko = new Inko();
        CommandLine commandLine = new CommandLine(inko);
        return commandLine.execute(args);
    }

    private static boolean fileExists(String filePath) {
        java.io.File file = new java.io.File(filePath);
        return file.exists();
    }

    @Test
    public void jpgToPng() {
        String outputFormat = "png";
        String outputFilePath = "src/test/resources/jpgToPng";
        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat);
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void pngToJpg() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/pngToJpg";
        int exitCode = run("-p", "src/test/resources/test.png", "-o", outputFilePath, "-of", outputFormat);
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void pngSemiTransparentToJpg() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/pngSemiTransparentToJpg";
        int exitCode = run("-p", "src/test/resources/testSemiTransparent.png", "-o", outputFilePath, "-of", outputFormat);
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void charInFontSize() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/charInFontSize";
        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat, "-fs", "f5");
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void charInMargin() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/charInMargin";
        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat, "-fs", "f5");
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }


    @Test
    public void badFontColor() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/badFontColor";
        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat, "-fc", "#abcdefgh");
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void badBackgroundColor() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/badBackgroundColor";
        int exitCode = run("-p", "src/test/resources/test.jpg", "-o",outputFilePath, "-of", outputFormat, "-bg", "#abcdefgh");
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void fullParamWithExif() {
        String outputFormat = "png";
        String outputFilePath = "src/test/resources/fullExif";
        int exitCode = run("-p", "src/test/resources/exif.jpg", "-o", outputFilePath,
                "-of", outputFormat, "-t", "Date:", "-d", "-df", "dd.MM.yy", "-gmt", "-2", "-cm", "-gps", "-is", "-s", "_",
                "-f", "Ubuntu", "-fw", "Italic", "-fs", "80", "-fc", "#FFFF0000", "-bg", "#8000FFFF", "-po", "lt", "-m", "20");
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;
        boolean exists = fileExists(absolutePath);
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void runEmpty() {
        int exitCode = run();
        assertEquals(0, exitCode);
    }
}
