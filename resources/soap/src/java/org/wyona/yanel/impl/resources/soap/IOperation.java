package org.wyona.yanel.impl.resources.soap;

import org.w3c.dom.Element;

public interface IOperation {
    public void performOperation(Element payload) throws Exception;
}
