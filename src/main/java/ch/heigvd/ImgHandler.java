package ch.heigvd;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

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
     * Separator between EXIF data or personal text
     */
    public static String separator = " - ";

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
     * Store EXIF data
     */
    private Metadata metadata;

    /**
     * Store binary image
     */
    private BufferedImage _image;

    /**
     * Store the EXIF data to compute
     */
    private ArrayList<EXIF> _exifDatas = new ArrayList<>();

    /**
     * Personal text to include
     */
    private String _personalText;

    /**
     * Index of the text to include
     */
    private int _indexOfText =-1;

    /**
     * Constructor
     *
     * @param imagePath path to the image
     * @throws ImageProcessingException if EXIF data can't be processed
     * @throws IOException              if the image can't be read or doesn't exist
     */
    public ImgHandler(String imagePath) throws ImageProcessingException, IOException {
        try {
            metadata = ImageMetadataReader.readMetadata(new File(imagePath));
            _image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            System.out.println("The path to image : " + imagePath + " doesn't exist");
            throw ex;
        } catch (ImageProcessingException ex) {
            System.out.println("Error when processing EXIF data with image : " + imagePath);
            throw ex;
        }

    }

    /**
     * Add an EXIF data to compute
     *
     * @param exifData EXIF data to compute
     */
    public void AddExifData(EXIF exifData) {
        _exifDatas.add(exifData);
    }
    public void AddText(String text) {
        _personalText = text;
        _indexOfText = _exifDatas.size();
    }

    /**
     * Enum EXIF data type
     */
    public enum EXIF {
        makeDescriptor, DateOriginal
    }

    /**
     * Get EXIF data
     *
     * @param exifField EXIF data type tu get
     * @return EXIF data
     */
    public String getExifData(EXIF exifField) {
        String data;
        switch (exifField) {
            case DateOriginal -> data = GetDate();
            default -> data = "no Data";
        }
        return data;
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

    /**
     * Compute all EXIF data and add personal text to a string
     *
     * @return EXIF data computed
     */
    public String ComputeImageText() {

        if (_exifDatas.isEmpty()) return _personalText;

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (EXIF exif : _exifDatas) {
            if (i == _indexOfText) {
                sb.append(_personalText).append(separator);
            }
            sb.append(getExifData(exif)).append(separator);
        }
        sb.delete(sb.length() - separator.length(), sb.length());
        return sb.toString();
    }

    /**
     * Get the date from EXIF data
     *
     * @return date from EXIF data
     */
    private String GetDate() {
        //get the directory with the date
        ExifSubIFDDirectory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        String dateNotFound = "Date parameter doesn't exist";
        if (subIFDDirectory != null) {
            // Get date from EXIF
            Date date = subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            if (date != null) {
                return date.toString();
            } else {
                return dateNotFound;
            }
        } else {
            return dateNotFound;
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
