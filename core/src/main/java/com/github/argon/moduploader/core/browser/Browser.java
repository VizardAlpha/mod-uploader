package com.github.argon.moduploader.core.browser;

import javax.naming.OperationNotSupportedException;
import java.awt.*;
import java.net.URI;

public class Browser {

    public Browser() throws OperationNotSupportedException {
        if (!Desktop.isDesktopSupported()) {
            throw new OperationNotSupportedException("Desktop web browser is not supported on this system");
        }
    }

    public void open(String uri)  {
        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (Exception e) {
            throw new BrowserException("Error opening the desktop web browser", e);
        }
    }
}
