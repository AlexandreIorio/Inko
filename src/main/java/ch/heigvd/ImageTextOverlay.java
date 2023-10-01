/*
 * Class         : ImageTextOverlay
 *
 * Description   : This class can overlay two BufferedImage
 *
 * Version       : 1.0
 *
 * Date          : 1.10.2023
 *
 * Author        : Alexandre Iorio
 */

package ch.heigvd;

import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageTextOverlay {
    /**
     * Store the font type
     */
    private  String _font = "Arial";

    /**
     * Store the font color
     */
    private  Color _color = Color.BLACK;

    /**
     * Store the background color
     */
    private  Color _backgroundColor = new Color(0,0,0,0);

    /**
     * Store de Size of the font
     */
    private  int _fontSize = 50;

    /**
     * Store the font width style
     */
    private  int _fontWidth = Font.BOLD;

    /**
     * Store the margin
     */
    private  int _margin = 10;

    /**
     * Check if a string contains char
     * @param string the string to check
     * @return True if the string contains char
     */
    public static boolean ContainChar(String string) {
        return string.matches(".*[a-zA-Z]+.*");
    }

    /**
     * Overlay 2 images
     * @param image background image
     * @param imageToOverlay image to overlay
     * @param position the position on image
     * @return The overlaid image
     */
    public BufferedImage overlayImages(BufferedImage image, BufferedImage imageToOverlay, String position) {
        if (image == null) throw new NullPointerException("Base image is null");
        if (imageToOverlay == null) return image;
        // define base image width and height
        int width = Math.max(image.getWidth(), imageToOverlay.getWidth());
        int height = Math.max(image.getHeight(), imageToOverlay.getHeight());

        BufferedImage overlaidImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // get image in a Graphics2D
        Graphics2D g = overlaidImage.createGraphics();
        g.drawImage(image, 0, 0, null);

        // define position to apply the overlay
        Point overlayPosition = positionImage(image, imageToOverlay, POSITION.getPosition(position));

        // draw overlay image over the base image
        g.drawImage(imageToOverlay, overlayPosition.x, overlayPosition.y, null);

        // dispose resources
        g.dispose();

        return overlaidImage;
    }

    /**
     * Create an image with text
     * @param text the text to apply
     * @param maxWidth the max width to compute number of line
     * @return an image with the text
     */
    public BufferedImage CreateImageText(String text, int maxWidth) {

        if (text == null || text.isEmpty()) {
            return null;
        }
        Font font = new Font(_font, _fontWidth, _fontSize);
        // create a temporary image to get the width and height of the text image to compute multilines
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics tempGraphics = tempImage.getGraphics();
        tempGraphics.setFont(font);
        FontMetrics fontMetrics = tempGraphics.getFontMetrics();

        // Calculate the text width and height
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();

        // Calculate the number of char on a line
        double divisor = (double)textWidth / (maxWidth - 2 * _margin);
        int nbCharOnLine = (int)Math.floor (text.length() / divisor);

        // Calculate the number of lines needed based on text width and image width
        int numLines = (int) Math.ceil(divisor);

        // Create the image with adjusted height for multiple lines
        BufferedImage image = new BufferedImage((numLines > 1 ? maxWidth : textWidth), textHeight * numLines, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        // set graphics attributes
        g.setColor(_backgroundColor);
        g.fillRect(0, 0, maxWidth, textHeight * numLines);
        g.setColor(_color);
        g.setFont(font);

        // Split and draw the text on multiple lines
        int y = textHeight;
        int startIndex = 0;

        for (int i = 0; i < numLines; i++) {
            int endIndex;
            int remainingChar = text.length() - (i * nbCharOnLine);
            if (remainingChar < nbCharOnLine) {
                endIndex = startIndex   + remainingChar;
            } else {
                endIndex =  nbCharOnLine * (i + 1);
            }
            String line = text.substring(startIndex, endIndex);
            g.drawString(line, 0, y - fontMetrics.getDescent());
            y += textHeight;
            startIndex = endIndex;
        }

        // Dispose resources
        g.dispose();
        tempGraphics.dispose();

        return image;
    }

    /**
     * Set the width style of the font
     * @param fontWidth the font width style
     */
    public void SetFontWidth(String fontWidth) {
        switch (fontWidth) {
            case "b":
            case "bold":
                _fontWidth = Font.BOLD;
                break;
            case "i":
            case "italic":
                _fontWidth = Font.ITALIC;
                break;
            default:
                _fontWidth = Font.PLAIN;
                break;
        }
    }

    /**
     * Set the font family
     * @param font the font family
     */
    public void SetFont(String font) {
        _font = font;
    }

    /**
     * Set the font size
     * @param size the font size
     */
    public void SetFontSize(String size) {

        if (ContainChar(size)) {
            System.out.println("size must be a number, default value ["+size+"] will be applied");
            return;
        }
        _fontSize = Integer.parseInt(size);
    }

    /**
     * Set the color of the font
     * @param color ARGB color (#AARRGGBB)
     */
    public void SetColor(String color) {
        _color = GetColor(color);
    }

    /**
     * Set the background color
     * @param color The color to set
     */
    public void SetBackgroundColor(String color) {
        _backgroundColor = GetColor(color);
    }

    /**
     * Set the margin, and it used to compute the position of the overlaid image
     * @param margin the margin to set
     */
    public void SetMargin(String margin) {
        if (ContainChar(margin)) {
            System.out.println("size must be a number, default value ["+_margin+"] will be applied");
            return;
        }
        _margin = Integer.parseInt(margin);
    }

    /**
     * Compute the position of the overlaid image
     * @param baseImage the background image
     * @param overlayImage the overlaid image
     * @param position the position of the overlaid image
     * @return the coordinates of the position
     */
    public Point positionImage(BufferedImage baseImage, BufferedImage overlayImage, POSITION position) {
        int x = _margin;
        int y = _margin;

        int baseWidth = getImageWidth(baseImage);
        int baseHeight = getImageHeight(baseImage);

        int overlayWidth = overlayImage.getWidth();
        int overlayHeight = overlayImage.getHeight();

        switch (position) {
            case TOP:
                x = (baseWidth - overlayWidth) / 2;
                break;
            case BOTTOM:
                x = (baseWidth - overlayWidth) / 2;
                y = baseHeight - overlayHeight;
                break;
            case LEFT:
                y = (baseHeight - overlayHeight) / 2;
                break;
            case RIGHT:
                x = baseWidth - overlayWidth;
                y = (baseHeight - overlayHeight) / 2;
                break;
            case CENTER:
                x = (baseWidth - overlayWidth) / 2;
                y = (baseHeight - overlayHeight) / 2;
                break;
            case TOP_LEFT:
                break;
            case TOP_RIGHT:
                x = baseWidth - overlayWidth;
                break;
            case BOTTOM_LEFT:
                y = baseHeight - overlayHeight;
                break;
            case BOTTOM_RIGHT:
                x = baseWidth - overlayWidth;
                y = baseHeight - overlayHeight;
                break;
        }
        return new Point(x, y);
    }

    /**
     * Get the height of image
     * @param image the image
     * @return the height value of image
     */
    private int getImageHeight(BufferedImage image) {
        return image.getHeight() - _margin;
    }

    /**
     * Get the width of image
     * @param image the image
     * @return the width value of image
     */
    private int getImageWidth(BufferedImage image) {
        return image.getWidth() - _margin;
    }

    /**
     * Get Color from String
     * @param hexColor ARGB color in hexadecimal (#AARRGGBB)
     * @return the Color
     */
    public Color GetColor(String hexColor) {
        int alpha = Integer.parseInt(hexColor.substring(1, 3), 16);
        int red = Integer.parseInt(hexColor.substring(3, 5), 16);
        int green = Integer.parseInt(hexColor.substring(5, 7), 16);
        int blue = Integer.parseInt(hexColor.substring(7, 9), 16);
        return new Color(red, green, blue, alpha);
    }

    /**
     * Enumerate possible positions
     */
    public enum POSITION {
        TOP, BOTTOM, LEFT, RIGHT, CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

        private static POSITION getPosition(String Position) {
            switch (Position.toLowerCase()) {
                case "t":
                    return TOP;
                case "b":
                    return BOTTOM;
                case "l":
                    return LEFT;
                case "r":
                    return RIGHT;
                case "c":
                    return CENTER;
                case "lt":
                    return TOP_LEFT;
                case "rt":
                    return TOP_RIGHT;
                case "lb":
                    return BOTTOM_LEFT;
                default:
                    return BOTTOM_RIGHT;
            }
        }

    }
}
