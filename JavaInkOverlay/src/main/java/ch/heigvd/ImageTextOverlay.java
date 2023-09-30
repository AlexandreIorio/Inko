package ch.heigvd;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.*;

public class ImageTextOverlayer {

    private BufferedImage _image;
    public ImageTextOverlayer(BufferedImage image, String text) {
        _image = image;


        Font font = new Font("Arial", Font.BOLD, 40);

        Graphics g = image.getGraphics();
        g.setFont(font);
        g.setColor(Color.RED);
        g.drawString("AAAAAAAAAAAAAAAAAA", 50, 50);

        // Créez une nouvelle image pour enregistrer le contenu de g
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();

        // Copiez le contenu de g dans la nouvelle image
        g2d.drawImage(image, 0, 0, null);

        // Libérez les ressources de g
        g.dispose();
        g2d.dispose();

        // Enregistrez la nouvelle image en tant qu'image JPG
        File outputfile = new File("textedImg.jpg");
        ImageIO.write(outputImage, "png", outputfile);
    }

}
