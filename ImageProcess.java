import java.util.Arrays;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
* Converts an image to a bitmap, used for texture mapping.
* Created for use with Tanvas hardware at Calhacks 2016.
* Based on code written by Prof. Josh Hug at UC Berkeley.
*
* @author  Jessica Hu, David Nahm, Aaron Chai, Jerry Chen, Eric Kim
* @version 1.0
* @since   2016-11-13
*/

public class ImageProcess {
    private Picture pic;

    public ImageProcess(Picture pic) {
        this.pic = new Picture(pic);
    }

    public int width() {
        return pic.width();
    }

    public int height() {
        return pic.height();
    }

    private double energy(int x, int y) {
        if ((x < 0) || (x >= pic.width()) || (y < 0) || (y > pic.height())) {
            return Double.POSITIVE_INFINITY;
        }

        if ((x == 0) || (x == pic.width() - 1)
         || (y == 0) || (y == pic.height() - 1)) {
            return 1000000;
          }

        Color left  = pic.get(x - 1, y);
        Color right = pic.get(x + 1, y);
        Color up    = pic.get(x, y + 1);
        Color down  = pic.get(x, y - 1);
        return calculateGradient(left, right) + calculateGradient(up, down);
      }

    // Calculates the square of the (R,G,B) gradient between two pixels (p1,p2)
    private static double calculateGradient(Color p1, Color p2) {
        double r = p1.getRed() - p2.getRed();
        double g = p1.getGreen() - p2.getGreen();
        double b = p1.getBlue() - p2.getBlue();

        return r*r + g*g + b*b;
    }

    public double[][] energyMatrix() {
        double[][] energyMatrix = new double[height()][width()];
        double energy;
        for (int c = 0; c < width(); c++) {
            for (int r = 0; r < height(); r++) {
                energyMatrix[r][c] = energy(c, r);
            }
        }
        return energyMatrix;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:\njava ImageProcess [image filename]");
            return;
        }

        Picture inputImg = new Picture(args[0]);
        ImageProcess sc = new ImageProcess(inputImg);
        double[][] energyMat = sc.energyMatrix();
        int height = energyMat.length;
        int width = energyMat[0].length;
        int min = 0;
        int max = 0;

        int[][] pass = new int[height][width];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int val = (int)(energyMat[y][x]);
                if (val > max) {
                    max = val;
                } else if (val < min) {
                    min = val;
                }
                pass[y][x] = val << 16 | val << 8 | val;
            }
        }

        BufferedImage theImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int val = (pass[y][x] - min)/max;
                theImage.setRGB(x, y, val);
            }
        }

        File outputfile = new File("processedImage.png");

        try{
            ImageIO.write(theImage, "png", outputfile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
