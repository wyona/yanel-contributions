package org.wyona.yanel.impl.jelly.validators;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem;
import org.wyona.yanel.core.api.attributes.creatable.ValidationMessage;
import org.wyona.yanel.core.api.attributes.creatable.Validator;
import org.wyona.yanel.impl.jelly.FileItem;

/**
 * This validator checks, if the items value, which is a fileitem and a image,
 * has the right dimensions.
 * 
 */
public class ImageDimensionValidator implements Validator {

    protected static final Logger LOG = Logger.getLogger(ImageDimensionValidator.class);

    private String failMessage = null;

    private int minWidth = 0;

    private int maxWidth = 0;

    private int minHeight = 0;

    private int maxHeight = 0;

    public ImageDimensionValidator(String failMessage, int minWidth, int maxWidth, int minHeight, int maxHeight) {
        this.failMessage = failMessage;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
    
    /**
     * ImageDimensionValidator for exact dimension validation.
     * The value 0 means no constraint (i.e. min=0, max=10000)
     * 
     * @param failMessage
     * @param exactWidth
     * @param exactHeight
     */
    public ImageDimensionValidator(String failMessage, int exactWidth, int exactHeight) {
        this.failMessage = failMessage;
        this.minWidth = exactWidth;
        this.maxWidth = exactWidth <= 0 ? 10000 : exactWidth;
        this.minHeight = exactHeight;
        this.maxHeight = exactHeight <= 0 ? 10000 : exactHeight;
    }

    public ValidationMessage validate(ResourceInputItem item) {
        Object value = item.getValue();
        ValidationMessage vm = null;
        if (value == null) {
            vm = new ValidationMessage(item.getName(), value, true);
        } else {
            try {
                FileItem fileItem = (FileItem) value;
                byte[] b = fileItem.getData();
                ByteArrayInputStream bin = new ByteArrayInputStream(b);
                BufferedImage img = ImageIO.read(bin);

                if (img.getWidth() < minWidth || img.getWidth() > maxWidth || img.getHeight() < minHeight || img.getHeight() > maxHeight) {
                    vm = new ValidationMessage(item.getName(), value, failMessage, false);
                } else {
                    vm = new ValidationMessage(item.getName(), value, true);
                }
            } catch (IOException e) {
                LOG.error(e);
            }
        }
        return vm;
    }

}
