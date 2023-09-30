package ch.heigvd;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.image.BufferedImage;
import java.awt.*;

public class ImageTextOverlay {

    private  String _font = "Arial";
    private  Color _color = Color.BLACK;
    private  Color _backgroundColor = new Color(0,0,0,0);
    private  int Size = 50;
    private  int _fontWidth = Font.BOLD;
    private  int _margin = 10;

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

    public ImageTextOverlay() {
    }

    public BufferedImage OverlayImage(BufferedImage image, BufferedImage imageToOverlay, String position, String format) {
        if (image == null) throw new NullPointerException("Base image is null");
        if (imageToOverlay == null) return image;
        // define base image width and height
        int width = Math.max(image.getWidth(), imageToOverlay.getWidth());
        int height = Math.max(image.getHeight(), imageToOverlay.getHeight());

        BufferedImage overlaidImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                // get image
        Graphics2D g = overlaidImage.createGraphics();
        g.drawImage(image, 0, 0, null);

        Point overlayPosition = positionImage(image, imageToOverlay, POSITION.getPosition(position));
        // draw overlay image over the base image
        g.drawImage(imageToOverlay, overlayPosition.x, overlayPosition.y, null);

        // dispose resources
        g.dispose();
        if (format.equalsIgnoreCase("jpeg") || format.equalsIgnoreCase("jpg")) {
            overlaidImage = ChangeColorType(overlaidImage, BufferedImage.TYPE_INT_RGB);
        }
        return overlaidImage;
    }


    public BufferedImage CreateImageText(String text, int maxWidth) {

        if (text == null || text.isEmpty()) {
            return null;
        }
        Font font = new Font(_font, _fontWidth, Size);
        // create a temporary image to get the width and height of the text
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
    public BufferedImage ChangeColorType(BufferedImage originalImage, int newType) {
        // Create a new BufferedImage with the desired type
        BufferedImage newImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                newType
        );

        // Create a Graphics2D object to draw the original image onto the new one
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose(); // Dispose of the Graphics2D object

        return newImage;
    }

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
    public void SetFont(String font) {
        _font = font;
    }
    public void SetFontSize(String size) {
        Size = Integer.parseInt(size);
    }
    public void SetColor(String color) {
        _color = GetColor(color);
    }
    public void SetBackgroundColor(String color) {
        _backgroundColor = GetColor(color);
    }
    public void SetMargin(String margin) {
        _margin = Integer.parseInt(margin);
    }
    public void SetSize(String size) {
        Size = Integer.parseInt(size);
    }
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

    private int getImageHeight(BufferedImage image) {
        return image.getHeight() - _margin;
    }

    private int getImageWidth(BufferedImage image) {
        return image.getWidth() - _margin;
    }

    public Color GetColor(String hexColor) {
        int alpha = Integer.parseInt(hexColor.substring(1, 3), 16); // Extraction de la valeur alpha en hexadécimal
        int red = Integer.parseInt(hexColor.substring(3, 5), 16);
        int green = Integer.parseInt(hexColor.substring(5, 7), 16);
        int blue = Integer.parseInt(hexColor.substring(7, 9), 16);
        return new Color(red, green, blue, alpha);
    }
}
