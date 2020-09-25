package com.comandante.creeper.cclient;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    @Override
    protected void paintComponent(Graphics g) {
        if (image != null) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }


}
