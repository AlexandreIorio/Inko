/*
 * Class         : ExifHandler
 *
 * Description   : This class handle EXIF metadata from a BufferedImage
 *
 * Version       : 1.0
 *
 * Date          : 1.10.2023
 *
 * Author        : Alexandre Iorio
 */

package ch.heigvd;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.GpsDirectory;


public class ExifHandler {

    /**
     * Separator between EXIF data or personal text
     */
    public static String separator = " - ";
    /**
     * Store EXIF data
     */
    private Metadata _metadata;

    /**
     * Store the image path
     */
    private String _imagePath;

    /**
     * Store the EXIF data to compute
     */
    private ArrayList<EXIF> _exifDatas = new ArrayList<>();
    /**
     * Define the gmt offset
     */
    private int _gmt = -2;
    /**
     * Define the date format
     */
    private String _dateFormat = "dd.MM.yyyy HH:mm:ss";
    /**
     * Personal text to include
     */
    private Queue _personalTexts = new LinkedList<String>();

    /**
     * Constructor
     * @param imagePath path to the image
     * @throws ImageProcessingException if EXIF data can't be processed
     * @throws IOException if the image can't be read or doesn't exist
     */
    public ExifHandler(String imagePath) throws ImageProcessingException, IOException {

        try { // don't need to use try with resources because the metadata implement a close in case of error
            _metadata = ImageMetadataReader.readMetadata(new File(imagePath));
            _imagePath = imagePath;
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

    /**
     * Add a personal text to include
     * @param text the text to include
     */
    public void AddText(String text) {
        _exifDatas.add(EXIF.Text);
        _personalTexts.add(text);
    }

    /**
     * Set the gmt offset
     * @param gmt
     */
    public void SetGMT(int gmt) {
        _gmt = gmt;
    }

    /**
     * Set the date format
     * @param dateFormat the date format to set
     */
    public void SetDateFormat(String dateFormat) {
        if (dateFormat == null || dateFormat.isEmpty()) return;
        _dateFormat = dateFormat;
    }

    /**
     * Get EXIF data
     *
     * @param exifField EXIF data type to get
     * @throws IOException if the image can't be read or doesn't exist
     * @return EXIF data
     */
    private String getExifDataToString(EXIF exifField) throws IOException {
        String data = "";
        switch (exifField) {
            case DateOriginal:
                Date date = GetDate();
                if (date == null) {
                    data = "no date";
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(_dateFormat);
                    data = dateFormat.format(date);
                }
                break;
            case CameraModel:
                data = getCameraModel();
                break;
            case ImageSize:
                data = getImageSize();
                break;
            case GPSLocation:
                data = getGPSLocation();
                break;
            default:
                data = "no data for " + exifField.toString();
                break;
        }
        return data;
    }

    /**
     * Compute all EXIF data and add personal text to a string
     *
     * @return EXIF data computed
     *
     */
    public String ComputeImageText() throws IOException {

        if(_exifDatas.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int idx = 0;

        //Concat all EXIF data
        for (EXIF exifData : _exifDatas) {
            if (exifData == EXIF.Text) {
                sb.append(_personalTexts.poll());
                // Doesn't remove separator if text is the last element
                if (idx == _exifDatas.size() - 1) {
                    return sb.toString();
                }
            } else {
                sb.append(getExifDataToString(exifData)).append(separator);
            }
            idx++;
        }
        // remove the last separator
         sb.delete(sb.length() - separator.length(), sb.length());
        return sb.toString();
    }

    /**
     * Get the date from EXIF data
     *
     * @return date from EXIF data
     */
    private Date GetDate() {
        //get the directory with the date
        ExifSubIFDDirectory subIFDDirectory = _metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        Date date = null;
        if (subIFDDirectory != null) {
            // Get date from EXIF
            date = subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            //set GMT offset
            calendar.add(Calendar.HOUR_OF_DAY, _gmt);
            date = calendar.getTime();
        }
        return date;
    }

    /**
     * Get the camera model from EXIF data
     * @return the camera model from EXIF data
     */
    public String getCameraModel() {
        // Recherche du répertoire ExifIFD0, qui contient des informations sur la caméra
        Directory exifIFD0Directory = _metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        if (exifIFD0Directory != null) {
            String cameraModel = exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL);
            return cameraModel;
        } else {
            return "Camera model information not found in EXIF data.";
        }
    }

    /**
     * Get the GPS location from EXIF data
     * @return The GPS location from EXIF data
     */
    public String getGPSLocation() {
        // Looking for gps directory in EXIF data
        Directory gpsDirectory = _metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory != null) {
            // get GPS coordinates if exists
            String latitude = gpsDirectory.getDescription(GpsDirectory.TAG_LATITUDE);
            String longitude = gpsDirectory.getDescription(GpsDirectory.TAG_LONGITUDE);

            if (latitude != null && longitude != null) {
                return "Latitude: " + latitude + ", Longitude: " + longitude;
            } else {
                return "GPS coordinates not found in EXIF data.";
            }
        } else {
            return "GPS directory not found in EXIF data.";
        }
    }

    /**
     * Get the size from the image
     * @return the size of the image in pixels
     */
    public String getImageSize() throws IOException {
        BufferedImage image;
        try(InputStream stream = new FileInputStream(_imagePath)){
            image = ImageIO.read(stream);
            return image.getWidth() +" x "+image.getHeight() + "px";
        } catch (IOException ex){
            System.out.println("Error when reading " + _imagePath);
            throw ex;
        }
    }

    /**
     * Enum EXIF data type
     */
    public enum EXIF {
        DateOriginal,
        CameraModel,
        ImageSize,
        GPSLocation,
        Text
    }

}
