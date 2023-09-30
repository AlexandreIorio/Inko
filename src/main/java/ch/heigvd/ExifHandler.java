package ch.heigvd;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.GpsDirectory;

public class ExifHandler {
    public ExifHandler(String imagePath) throws ImageProcessingException, IOException {
        try {
            metadata = new Metadata();
            metadata = ImageMetadataReader.readMetadata(new File(imagePath));
        } catch (IOException ex) {
            System.out.println("The path to image : " + imagePath + " doesn't exist");
            throw ex;
        } catch (ImageProcessingException ex) {
            System.out.println("Error when processing EXIF data with image : " + imagePath);
            throw ex;
        }
    }

    /**
     * Separator between EXIF data or personal text
     */
    public static String separator = " - ";

    /**
     * Store EXIF data
     */
    private Metadata metadata;

    /**
     * Store the EXIF data to compute
     */
    private ArrayList<EXIF> _exifDatas = new ArrayList<>();

    /**
     * Personal text to include
     */
    private Queue _personalTexts = new LinkedList<String>();


    /**
     * Add an EXIF data to compute
     *
     * @param exifData EXIF data to compute
     */
    public void AddExifData(EXIF exifData) {
        _exifDatas.add(exifData);
    }

    public void AddText(String text) {
        _exifDatas.add(EXIF.Text);
        _personalTexts.add(text);
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

    /**
     * Get EXIF data
     *
     * @param exifField EXIF data type tu get
     * @return EXIF data
     */
    private String getExifDataToString(EXIF exifField) {
        String data = "";
        switch (exifField) {
            case DateOriginal:
                Date date = GetDate();
                if (date == null) {
                    data = "no date";
                } else {
                    data = date.toString();
                }
                break;
            case CameraModel:
                data = getCameraModel();
                break;
            case ImageSize:
                // Processing for the image size
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
     */
    public String ComputeImageText() {

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
        ExifSubIFDDirectory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        Date date = null;
        if (subIFDDirectory != null) {
            // Get date from EXIF
            date = subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        }
        return date;
    }

    public String getCameraModel() {
        // Recherche du répertoire ExifIFD0, qui contient des informations sur la caméra
        Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        if (exifIFD0Directory != null) {
            String cameraModel = exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL);
            return cameraModel;
        } else {
            return "Camera model information not found in EXIF data.";
        }
    }

    public String getGPSLocation() {
        // Recherche du répertoire GPS, qui contient des informations de localisation
        Directory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory != null) {
            // Obtenez les coordonnées GPS (latitude et longitude) si disponibles
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

    public String getImageSize() throws MetadataException {
        // Recherche du répertoire ExifSubIFD, qui contient des informations sur la taille de l'image
        Directory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (exifSubIFDDirectory != null) {
            int imageWidth = exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
            int imageHeight = exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);

            if (imageWidth > 0 && imageHeight > 0) {
                return imageWidth + " x " + imageHeight + " pixels";
            } else {
                return "Image size information not found in EXIF data.";
            }
        } else {
            return "ExifSubIFD directory not found in EXIF data.";
        }
    }

}
