package ch.heigvd;

import com.drew.imaging.ImageProcessingException;
import picocli.CommandLine;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "Inko", mixinStandardHelpOptions = true, version = "1.0", description = "Inko lets you write custom text or exif values on an image")

public class Inko implements Callable {
    private static int cptCmdText = 0;

    private static ImgHandler _imgHandler;
    private static ExifHandler _exifHandler;
    private static String _imagePath;


    @CommandLine.Option(names = {"-cr","--credits"}, description = "Show credits")
    public void ShowCredit(boolean param) {
        System.out.println(Description.DESCRIPTION);
    }

    @CommandLine.Option(names = {"-p", "--imagePath"}, description = "Image path")
    private static void setImagePath(String param) throws ImageProcessingException, IOException {
        _imagePath = param;
        _imgHandler = new ImgHandler(param);
        _exifHandler = new ExifHandler(param);
    }

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output path without extension", defaultValue = "OverlayedImage")
    private static String _outputPath = "OverlayedImage";

    @CommandLine.Option(names = {"-of", "--outputformat"}, description = "Output format : jpeg, png, gif")
    private static String _outputFormat = "jpeg";

    @CommandLine.Option(names = {"-t", "--text"}, description = "Text to overlay")
    private void addText(ArrayList<String> params) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        if(params.isEmpty()) return;
        _exifHandler.AddText(params.get(cptCmdText));
        cptCmdText++;
    }

    @CommandLine.Option(names = {"-d", "-date"}, description = "Get date of image")
    void addDate(boolean called) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.AddExifData(ExifHandler.EXIF.DateOriginal);
    }

    @CommandLine.Option(names = {"-cm", "--cam_model"}, description = "Get model of camera")
    void addCamModel(boolean called) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.AddExifData(ExifHandler.EXIF.CameraModel);
    }

    @CommandLine.Option(names = {"-gps", "--gps_location"}, description = "Get gps location of image")
    void addGpsLocation(boolean called) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.AddExifData(ExifHandler.EXIF.GPSLocation);
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

    @CommandLine.Option(names = {"-fc", "--fontcolor"}, description = "font color: #RRGGBB -> #2e00ff")
    private void setFontColor(String param) {
        ImageTextOverlay.SetColor(param);
    }

    @CommandLine.Option(names = {"-bg", "--backgroundcolor"}, description = "background color:  #AARRGGBB")
    private void setBackgroundColor(String param) {
        ImageTextOverlay.SetBackgroundColor(param);
    }

    @CommandLine.Option(names = {"-s", "--sep"}, description = "Data separator")
    private static void SetSeparator(String param) {
        ExifHandler.separator = " " + param + " ";
    }

    @CommandLine.Option(names = {"-sh", "--show"}, description = "Show image after generation")
    boolean _showImage = false;
    @CommandLine.Option(names = {"-po", "--position"}, description = "Text Position: l, r, b, t, c, lt, rt, lb, rb", defaultValue = "rb")
    private static String _position = "rb";


    @Override
    public Integer call() throws Exception {
        if (_imgHandler == null) return 0;

        ImageTextOverlay overlayer = new ImageTextOverlay();
        BufferedImage textImg = overlayer.CreateImageText(_exifHandler.ComputeImageText(), _imgHandler.getImage().getWidth());
        BufferedImage overlayedImage = overlayer.OverlayImage(_imgHandler.getImage(),
                textImg,
                _position,
                _outputFormat);
        ImgHandler.SaveImage(overlayedImage, _outputPath, _outputFormat);
        if (_showImage) {
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
        System.exit(exitCode);
    }

}
