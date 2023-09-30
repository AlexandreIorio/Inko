package ch.heigvd;

import com.drew.imaging.ImageProcessingException;
import picocli.CommandLine;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "Inko", mixinStandardHelpOptions = true, version = "1.0", description = "Inko lets you write custom text or exif values on an image")

public class Inko implements Callable {

    private static ImgHandler _imgHandler;
    private static String _imagePath;

    @CommandLine.Command(name = "-do", mixinStandardHelpOptions = true, version = "1.0", description = "Get date")
    void addDate() {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _imgHandler.AddExifData(ImgHandler.EXIF.DateOriginal);
    }
    @CommandLine.Command(name = "credits", description = "Show credits")
    public void ShowCredit() {
        System.out.println(Description.DESCRIPTION);
    }

    @CommandLine.Option(names = {"-p", "--imagePath"}, description = "Image path")
    private static void setImagePath(String param) throws ImageProcessingException, IOException {
        _imagePath = param;
        _imgHandler = new ImgHandler(param);
    }
    @CommandLine.Option(names = {"-o", "--output"}, description = "Output path without extension", defaultValue = "OverlayedImage" )
    private static String _outputPath = "OverlayedImage";

    @CommandLine.Option(names = {"-of", "--outputformat"}, description = "Output format : jpeg, png, gif")
    private static String _outputFormat = "jpeg";

    @CommandLine.Option(names = {"-t", "--text"}, description = "Text to overlay")
    private void addText(String param) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _imgHandler.AddText(param);
    }

    @CommandLine.Option(names = {"-f", "--font"}, description = "font", defaultValue = "Arial")
    private void setFont(String param) {
        ImageTextOverlay.SetFont(param);
    }

    @CommandLine.Option(names = {"-fw", "--fontwidth"}, description = "font width : bold, italic, plain", defaultValue = "bold")
    private void setFontWidth(String param) {
        ImageTextOverlay.SetFontWidth(param);
    }

    @CommandLine.Option(names = {"-fs", "--fontSize"}, description = "font size px", defaultValue = "50")
    private void setFontSize(String param) {
        ImageTextOverlay.SetFontSize(param);
    }
    @CommandLine.Option(names = {"-fc", "--fontcolor"}, description = "font color: #RRGGBB -> #2e00ff", defaultValue = "#000000")
    private void setFontColor(String param) {
        ImageTextOverlay.SetColor(param);
    }

    @CommandLine.Option(names = {"-bc", "--backcolor"}, description = "background color:  #AARRGGBB")
    private void setBackgroundColor(String param) {
        ImageTextOverlay.SetBackgroundColor(param);
    }
    @CommandLine.Option(names = {"-s", "--sep"}, description = "Data separator")
    private static void SetSeparator(String param) {
        ImgHandler.separator = " " + param + " ";
    }
    @CommandLine.Option(names = {"-sh", "--show"}, description = "Show image after generation: y/n", defaultValue = "n")
    private static char _showImage = 'n';
    @CommandLine.Option(names = {"-po", "--position"}, description = "Text Position: l, r, b, t, c, lt, rt, lb, rb", defaultValue = "br")
    private static String _position = "rb";



    @Override
    public Integer call() throws Exception {
        if (_imgHandler == null) return 0;
        System.out.println(_imgHandler.ComputeImageText());
        ImageTextOverlay overlayer = new ImageTextOverlay();
        BufferedImage textImg = overlayer.CreateImageText(_imgHandler.ComputeImageText());
        BufferedImage overlayedImage = overlayer.OverlayImage(_imgHandler.getImage(),
                textImg,
                _position,
                _outputFormat);
        ImgHandler.SaveImage(overlayedImage, _outputPath, _outputFormat);
        if (_showImage == 'y') {
            ImgHandler.openImage(_outputPath + '.' + _outputFormat);
        }
        return 0;
    }

    /**
     * program input on the command
     *
     * @param args arguments
     */
    public static void main(String[] args) throws Exception {

         int exitCode = new CommandLine(new Inko()).execute(args);
        exitCode = new Inko().call();
        System.exit(exitCode);
    }

}
