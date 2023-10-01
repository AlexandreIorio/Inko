/*
 * Class         : ImageHandler
 *
 * Description   : This class handle open, convert color mode and save image
 *
 * Version       : 1.0
 *
 * Date          : 1.10.2023
 *
 * Author        : Alexandre Iorio
 */

package ch.heigvd;


import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class handle open, convert color mode and save image
 */
public class ImageHandler {

    /**
     * Store input format
     */
    private static FORMAT _inputFormat;
    /**
     * Store binary image
     */
    private BufferedImage _image;

    /**
     * Constructor
     *
     * @param imagePath path to the image
     * @throws IOException if the image can't be read or doesn't exist
     */
    public ImageHandler(String imagePath) throws IOException {
        try {
            _image = ImageIO.read(new File(imagePath));
            _inputFormat = FORMAT.valueOf(imagePath.substring(imagePath.lastIndexOf('.') + 1).toUpperCase());
        } catch (IOException ex) {
            System.out.println("The path to image : " + imagePath + " doesn't exist");
            throw ex;
        }
    }

    /**
     * Save an image on the disk with a specific format and path
     *
     * @param image      the buffered image to save
     * @param outputPath the path where the image will be saved
     * @param format     the format of the image
     */
    public static void saveImage(BufferedImage image, String outputPath, String format) {
        if (image != null) {
            String outputFileName = outputPath + '.' + format;

                File outputFile = new File(outputFileName);
                // important: to save png to jpg, is necessary to change color mode
                if ((format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) && _inputFormat == FORMAT.PNG) {
                    image = convertARGBtoRGB(image);
                }
                System.out.println("Image saved successfully: " + outputFile.getAbsolutePath());
        } else {
            System.out.println("Nothing to save");
        }
    }

    /**
     * Open the image on the default user viewer
     * @param imagePath the path to the image
     * @throws IOException if an error occurs during the reading
     */
    public static void openImage(String imagePath) throws IOException {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(imageFile);
            } else {
                System.out.println("Image file not found: " + imagePath);
            }
        } catch (IOException ex) {
            System.out.println("Error opening image: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Convert color mode from ARGB to RGB
     * @param image the image to convert
     * @return the converted image
     */
    public static BufferedImage convertARGBtoRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // write all pixel in RGB mode
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argbColor = image.getRGB(x, y);
                int rgbColor = argbToRgb(argbColor);
                if (argbColor >>> 24 == 0) { // Check if the alpha channel is transparent
                    rgbColor = 0xFFFFFF; // Set the color to white for transparent pixels
                }
                rgbImage.setRGB(x, y, rgbColor);
            }
        }
        return rgbImage;
    }

    /**
     * Convert a Pixel from ARGB to RGB mode
     * @param pixelColor the pixel color to convert
     * @return the converted pixel color
     */
    public static int argbToRgb(int pixelColor) {
        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;

        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Get the image
     * @return the buffered image
     */
    public BufferedImage getImage() {
        return _image;
    }

    /**
     * Enumeration of image format
     */
    public enum FORMAT {
        JPEG, JPG, PNG;
    }

}
