package org.wyona.yanel.impl.jelly;

import java.util.Arrays;


/**
 * An instance of this class represents a File which was uploaded with a
 * HTTPRequest. The standard FileItem from
 * org.apache.commons.fileupload.FileItem can not be used since
 * org.wyona.yanel.servlet.communication.HttpRequest removes uploaded fileitems.
 * 
 */
public final class FileItem {
    // Original file name
    private String fileName = null;
    // The url in the repository where the contents are put.
    private String url = null;
    private byte [] data = null;
    private String contentType = null;

    /**
     * Create a file item out of a stream and the content type.
     * @param data - can be <code>null</code>
     * */
    public FileItem(byte [] data, String contentType) throws NullPointerException{
        this.data = data;
        this.contentType = contentType;
    }
    
    public FileItem(byte [] data, String contentType, String url) throws NullPointerException{
        this.data = data;
        this.contentType = contentType;
        this.url = url;
    }

    public byte [] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }
    
    public boolean hasData(){
        return data != null && data.length > 0;
    }
    
    /**
     * @return the path in the repository. When the file has not been saved the url is <code>null</code>
     * */
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FileItem other = (FileItem) obj;
        if (contentType == null) {
            if (other.contentType != null)
                return false;
        } else if (!contentType.equals(other.contentType))
            return false;
        if (!Arrays.equals(data, other.data))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }
    
    
}
