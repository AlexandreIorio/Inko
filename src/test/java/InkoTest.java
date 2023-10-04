import ch.heigvd.Inko;
import org.junit.*;
import picocli.CommandLine;

import java.io.File;

import static org.junit.Assert.*;

public class InkoTest {

    /**
     * Run the program with the given arguments
     * @param args
     * @return the exit code
     */
    private static int run(String... args) {
        Inko inko = new Inko();
        CommandLine commandLine = new CommandLine(inko);
        return commandLine.execute(args);
    }

    /**
     * Get a file to test and remove it first if it exists
     * @param outputFilePath
     * @param outputFormat
     * @return the output file
     */
    private static File getTestFile(String outputFilePath, String outputFormat) {
        String absolutePath = System.getProperty("user.dir")+'/'+ outputFilePath +'.'+ outputFormat;

        File outputFile = new File(absolutePath);

        //remove file if exists
        if (outputFile.exists()) {
            if (!outputFile.delete()) {
                throw new RuntimeException("Failed to delete file " + outputFile.getAbsolutePath());
            }
        }
        return outputFile;
    }

    @Test
    public void jpgToPng() {
        String outputFormat = "png";
        String outputFilePath = "src/test/resources/jpgToPng";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat);
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void pngToJpg() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/pngToJpg";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/test.png", "-o", outputFilePath, "-of", outputFormat);

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void pngSemiTransparentToJpg() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/pngSemiTransparentToJpg";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/testSemiTransparent.png", "-o", outputFilePath, "-of", outputFormat);

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void charInFontSize() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/charInFontSize";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat, "-fs", "f5");
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void charInMargin() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/charInMargin";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat, "-fs", "f5");
        String absolutePath = System.getProperty("user.dir")+'/'+outputFilePath +'.'+ outputFormat;

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }


    @Test
    public void badFontColor() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/badFontColor";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/test.jpg", "-o", outputFilePath, "-of", outputFormat, "-fc", "#abcdefgh");

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void badBackgroundColor() {
        String outputFormat = "jpg";
        String outputFilePath = "src/test/resources/badBackgroundColor";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/test.jpg", "-o",outputFilePath, "-of", outputFormat, "-bg", "#abcdefgh");

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void fullParamWithExif() {
        String outputFormat = "png";
        String outputFilePath = "src/test/resources/fullExif";

        File outputFile = getTestFile(outputFilePath, outputFormat);

        int exitCode = run("-p", "src/test/resources/exif.jpg", "-o", outputFilePath,
                "-of", outputFormat, "-t", "Date:", "-d", "-df", "dd.MM.yy", "-gmt", "-2", "-cm", "-gps", "-is", "-s", "_",
                "-f", "Ubuntu", "-fw", "Italic", "-fs", "80", "-fc", "#FFFF0000", "-bg", "#8000FFFF", "-po", "lt", "-m", "20");

        boolean exists = outputFile.exists();
        assertTrue(exists && exitCode == 0);
    }

    @Test
    public void runEmpty() {
        int exitCode = run();
        assertEquals(0, exitCode);
    }
}
