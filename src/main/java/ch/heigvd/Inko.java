/*
 * Class         : Inko
 *
 * Description   : Handle commandline and overlay text with image
 *
 * Version       : 1.0
 *
 * Date          : 1.10.2023
 *
 * Author     : Alexandre Iorio
 */

package ch.heigvd;

import com.drew.imaging.ImageProcessingException;
import picocli.CommandLine;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "Inko", mixinStandardHelpOptions = true, version = "1.0.0", description = "Inko lets you write custom text or exif values on an image")

public class Inko implements Callable {
    //private attributes

    /**
     * counter for sequence of text
     */
    private static int cptCmdText = 0;
    /**
     * Image handler
     */
    private ImageHandler _imgHandler;
    /**
     * Exif handler
     */
    private ExifHandler _exifHandler;
    /**
     * Image/Text overlayer
     */
    private ImageTextOverlay _overlayer = new ImageTextOverlay();
    /**
     * Image path
     */
    private String _imagePath;

    // Options which set private attributes
    @CommandLine.Option(names = {"-sh", "--show"}, description = "Show image after generation")
    private boolean _showImage = false;
    @CommandLine.Option(names = {"-o", "--output"}, description = "Output path without extension", defaultValue = "OverlaidImage")
    private String _outputPath = "OverlaidImage";
    @CommandLine.Option(names = {"-of", "--outputformat"}, description = "Output format : jpeg, png, gif")
    private String _outputFormat = "jpeg";
    @CommandLine.Option(names = {"-po", "--position"}, description = "Text Position: l, r, b, t, c, lt, rt, lb, rb", defaultValue = "rb")
    private String _position = "rb";

    // Options which call methods
    @CommandLine.Option(names = {"-p", "--imagePath"}, description = "Image path")
    private void setImagePath(String param) throws ImageProcessingException, IOException {
        _imagePath = param;
        _imgHandler = new ImageHandler(param);
        _exifHandler = new ExifHandler(param);
    }

    @CommandLine.Option(names = {"-s", "--sep"}, description = "Data separator")
    private void setSeparator(String param) {
        ExifHandler.separator = " " + param + " ";
    }

    @CommandLine.Option(names = {"-cr", "--credits"}, description = "Show credits")
    public void showCredit(boolean param) {
        System.out.println(Description.DESCRIPTION);
    }

    @CommandLine.Option(names = {"-t", "--text"}, description = "Text to overlay")
    private void addText(ArrayList<String> params) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        if (params.isEmpty()) return;
        _exifHandler.AddText(params.get(cptCmdText));
        cptCmdText++;
    }

    @CommandLine.Option(names = {"-d", "--date"}, description = "Get date of image")
    private void addDate(boolean called) {
        if (_exifHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.AddExifData(ExifHandler.EXIF.DateOriginal);
    }

    @CommandLine.Option(names = {"-df", "--dateformat"}, description = "Set the format of the date")
    private void setDateFormat(String param) {
        if (_exifHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.SetDateFormat(param);
    }

    @CommandLine.Option(names = {"-gmt", "--gmt"}, description = "Set GMT offset")
    private void setGMT(String param) {
        if (_exifHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.SetGMT(Integer.parseInt(param));
    }

    @CommandLine.Option(names = {"-cm", "--cammodel"}, description = "Get model of camera")
    private void addCamModel(boolean called) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.AddExifData(ExifHandler.EXIF.CameraModel);
    }

    @CommandLine.Option(names = {"-gps", "--gpslocation"}, description = "Get gps location of image")
    private void addGpsLocation(boolean called) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.AddExifData(ExifHandler.EXIF.GPSLocation);
    }

    @CommandLine.Option(names = {"-is", "--imagesize"}, description = "Get the size of image in pixels")
    private void addImageSize(boolean called) {
        if (_imgHandler == null) throw new NullPointerException("no image path has been given");
        _exifHandler.AddExifData(ExifHandler.EXIF.ImageSize);
    }

    @CommandLine.Option(names = {"-f", "--font"}, description = "font", defaultValue = "Arial")
    private void setFont(String param) {
        _overlayer.SetFont(param);
    }

    @CommandLine.Option(names = {"-fw", "--fontwidth"}, description = "font width : bold, italic, plain", defaultValue = "bold")
    private void setFontWidth(String param) {
        _overlayer.SetFontWidth(param);
    }

    @CommandLine.Option(names = {"-fs", "--fontSize"}, description = "font size px", defaultValue = "50")
    private void setFontSize(String param) {
        _overlayer.SetFontSize(param);
    }

    @CommandLine.Option(names = {"-fc", "--fontcolor"}, description = "font color: #RRGGBB -> #2e00ff")
    private void setFontColor(String param) {
        _overlayer.setFontColor(param);
    }

    @CommandLine.Option(names = {"-bg", "--backgroundcolor"}, description = "background color:  #AARRGGBB")
    private void setBackgroundColor(String param) {
        _overlayer.SetBackgroundColor(param);
    }

    @CommandLine.Option(names = {"-m", "--margin"}, description = "Set the margin of the overlay")
    private void setMargin(String param) {
        _overlayer.SetMargin(param);
    }

    /**
     * program input
     *
     * @param args arguments
     */
    public static void main(String[] args) throws Exception {

        long begin = System.currentTimeMillis(); // init timer
        if (args.length == 0) {
            System.out.println(Description.DESCRIPTION);
            System.exit(0);
        }

        int exitCode = new CommandLine(new Inko()).execute(args);
        long end = System.currentTimeMillis(); // end of timer
        long executionTime = end - begin;
        System.out.println("Execution time : " + executionTime + " ms");
        System.exit(exitCode);
    }
    @Override
    public Integer call() throws Exception {
        if (_imgHandler == null) return 0;

        BufferedImage textImg = _overlayer.CreateImageText(_exifHandler.ComputeImageText(), _imgHandler.getImage().getWidth());
        BufferedImage overlayedImage = _overlayer.overlayImages(_imgHandler.getImage(),
                textImg,
                _position,
                _outputFormat);
        ImageHandler.saveImage(overlayedImage, _outputPath, _outputFormat);
        if (_showImage) {
            ImageHandler.openImage(_outputPath + '.' + _outputFormat);
        }
        return 0;
    }

}
