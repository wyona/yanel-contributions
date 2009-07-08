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
 * has one correct dimension. Another dimension must be smaller or equal.
 * E.g. 450xXXX and XXXx450, where XXX is less or equal 450
 * 
 */
public class ImageValidatorForOneDimension implements Validator {

    protected static final Logger log = Logger.getLogger(ImageValidatorForOneDimension.class);

    private String failMessage = null;

    private int dimSize = 0;

    public ImageValidatorForOneDimension(String failMessage, int dimSize) {
        this.failMessage = failMessage;
        this.dimSize = dimSize;
    }
    
    public ValidationMessage validate(ResourceInputItem item) {
        log.info("Validate item: " + item.getName() + "(Type: " + item.getType() + ")");
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
                
                if((img.getWidth() <= img.getHeight() && img.getHeight()==dimSize) || // portrait, height=dimSize
                        (img.getHeight() <= img.getWidth() && img.getWidth()==dimSize) // landscape, width=dimSize
                        ){
                    vm = new ValidationMessage(item.getName(), value, true);
                }else{
                    vm = new ValidationMessage(item.getName(), value, failMessage, false);
                }
            } catch (IOException e) {
                log.error(e);
            }
        }
        return vm;
    }
}
