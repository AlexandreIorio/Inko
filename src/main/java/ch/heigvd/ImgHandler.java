package ch.heigvd;


import com.drew.imaging.ImageProcessingException;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class can handle an image and get EXIF data from it
 */
public class ImgHandler {

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
     * @throws IOException              if the image can't be read or doesn't exist
     */
    public ImgHandler(String imagePath) throws IOException {
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
     * @throws IOException if the image can't be saved
     */
    public static void SaveImage(BufferedImage image, String outputPath, String format) throws IOException {
        if (image != null) {
            String outputFileName = outputPath + '.' + format;
            try {
                File outputFile = new File(outputFileName);
                if ((format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) && _inputFormat == FORMAT.PNG) {
                    image = convertARGBtoRGB(image);
                }

                boolean test = ImageIO.write(image, format, outputFile);
                System.out.println("Image saved successfully: " + outputFile.getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Error during image saving: " + ex.getMessage());
                throw ex;
            }
        } else {
            System.out.println("Error: Image is null.");
        }
    }

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

    public static BufferedImage convertARGBtoRGB(BufferedImage argbImage) {
        int width = argbImage.getWidth();
        int height = argbImage.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argbColor = argbImage.getRGB(x, y);
                int rgbColor = argbToRgb(argbColor);
                if (argbColor >>> 24 == 0) { // Check if the alpha channel is transparent
                    rgbColor = 0xFFFFFF; // Set the color to white for transparent pixels
                }
                rgbImage.setRGB(x, y, rgbColor);
            }
        }
        return rgbImage;
    }

    public static int argbToRgb(int argbColor) {
        int red = (argbColor >> 16) & 0xFF;
        int green = (argbColor >> 8) & 0xFF;
        int blue = argbColor & 0xFF;

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
        JPEG, JPG, PNG, GIF;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

}
