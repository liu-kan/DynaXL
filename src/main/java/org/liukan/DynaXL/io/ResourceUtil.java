package org.liukan.DynaXL.io;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.liukan.DynaXL.db.thePath;
import org.liukan.DynaXL.io.ImageUtil;

/**
 * Created by liuk on 16-7-19.
 */
public class ResourceUtil {
    public static final Image IMAGE_ICON_16 = ResourceUtil.getIconAsImage("icon16.png");
    public static final Image IMAGE_ICON_32 = ResourceUtil.getIconAsImage("icon32.png");
    public static final Image IMAGE_ICON_64 = ResourceUtil.getIconAsImage("icon64.png");
    public static final Image IMAGE_ICON_128 = ResourceUtil.getIconAsImage("icon128.png");
    public static final String ICON_PATH = "res/icons/";
    public static final String IMAGE_PATH = "res/icons/";

    /**
     * Return an image with wanted size located in res/icon from its name<br>
     * For any other location use the ImageUtil.loadImage() method
     *
     * @param name
     */
    public static Image getIconAsImage(String name, int size)
    {
        final Image image;
        //final URL url = ResourceUtil.class.getResource("/" + ICON_PATH + name);
        final String url = thePath.getPath()+"/" +ICON_PATH + name;
        if (url != null) {
            image = ImageUtil.load(url, true);
            //System.out.println("url != null");
        }
        else
            image = ImageUtil.load(new File(ICON_PATH + name), true);

        if (image == null)
        {
            System.out.println("Resource name can't be found: " + name);
            return null;
        }

        return scaleImage(image, size);
    }
    private static Image scaleImage(Image image, int size)
    {
        // resize if needed
        if ((image != null) && (size != -1))
        {
            // resize only if image has different size
            if ((image.getWidth(null) != size) || (image.getWidth(null) != size))
                return ImageUtil.scale(image, size, size);
        }

        return image;
    }
    /**
     * Return an image located in res/icon from its name<br>
     * For any other location use the ImageUtil.loadImage() method
     *
     * @param name
     */
    public static Image getIconAsImage(String name)
    {
        return getIconAsImage(name, -1);
    }
    /**
     * Return an image with wanted size located in res/image<br>
     * For any other location use the ImageUtil.loadImage() method
     *
     * @param name
     */
    public static BufferedImage getImage(String name)
    {
        final BufferedImage result;
        final URL url = ResourceUtil.class.getResource("/" + IMAGE_PATH + name);

        if (url != null)
            result = ImageUtil.load(url, true);
        else
            result = ImageUtil.load(new File(IMAGE_PATH + name), true);

        return result;
    }
    public static ArrayList<Image> getIconImages()
    {
        final ArrayList<Image> result = new ArrayList<Image>();
        result.add(ResourceUtil.IMAGE_ICON_128);
        result.add(ResourceUtil.IMAGE_ICON_64);
        result.add(ResourceUtil.IMAGE_ICON_32);
        result.add(ResourceUtil.IMAGE_ICON_16);

        return result;
    }
}
