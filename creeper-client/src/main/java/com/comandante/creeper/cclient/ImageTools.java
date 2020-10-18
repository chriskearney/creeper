package com.comandante.creeper.cclient;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class ImageTools {

    public static BufferedImage convertToDarkGray(BufferedImage sourceImage) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);

        return op.filter(sourceImage, null);
    }
}
