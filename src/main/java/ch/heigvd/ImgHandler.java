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
     * Enumeration of image format
     */
    public enum FORMAT {
        JPEG, PNG, GIF;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    /**
     * Store binary image
     */
    private BufferedImage _image;


    /**
     * Constructor
     *
     * @param imagePath path to the image
     * @throws ImageProcessingException if EXIF data can't be processed
     * @throws IOException              if the image can't be read or doesn't exist
     */
    public ImgHandler(String imagePath) throws ImageProcessingException, IOException {
        try {

            _image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            System.out.println("The path to image : " + imagePath + " doesn't exist");
            throw ex;
        }
    }

    public BufferedImage getImage() {
        return _image;
    }

    /**
     * Save an image on the disk with a specific format and path
     * @param image the buffered image to save
     * @param outputPath the path where the image will be saved
     * @param format the format of the image
     * @throws IOException if the image can't be saved
     */
    public static void SaveImage(BufferedImage image, String outputPath, String format) throws IOException {
        if (image != null) {
            String outputFileName = outputPath + '.' +format;
            try {
                File outputFile = new File(outputFileName);
                boolean test = ImageIO.write(image,format, outputFile);
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
}
