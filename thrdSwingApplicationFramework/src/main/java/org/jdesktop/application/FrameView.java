

/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JRootPane;


public class FrameView extends View {
    private static final Logger logger = Logger.getLogger(FrameView.class.getName());
    private JFrame frame = null;

    public FrameView(Application application) {
        super(application);
    }

    /**
     * Return the JFrame used to show this View
     * 
     * <p>
     * This method may be called at any time; the JFrame is created lazily
     * and cached.  For example:
     * <pre>
     *  &#064;Override protected void startup() {
     *     getFrame().setJMenuBar(createMenuBar());
     *     show(createMainPanel());
     * }
     * </pre>
     * 
     * @return this application's  main frame
     */
    public JFrame getFrame() {
	if (frame == null) {
	    String title = getContext().getResourceMap().getString("Application.title");
	    frame = new JFrame(title);
	    frame.setName("mainFrame");
	}
	return frame;
    }

     /**
     * Sets the JFrame use to show this View
     * <p>
     * This method should be called from the startup method by a
     * subclass that wants to construct and initialize the main frame
     * itself.  Most applications can rely on the fact that {code
     * getFrame} lazily constructs the main frame and initializes
     * the {@code frame} property.
     * <p>
     * If the main frame property was already initialized, either 
     * implicitly through a call to {@code getFrame} or by
     * explicitly calling this method, an IllegalStateException is 
     * thrown.  If {@code frame} is null, an IllegalArgumentException
     * is thrown.
     * <p>
     * This property is bound.
     * 
     * 
     * 
     * @param frame the new value of the frame property
     * @see #getFrame
     */
    public void setFrame(JFrame frame) {
	if (frame == null) {
	    throw new IllegalArgumentException("null JFrame");
	}
	if (this.frame != null) {
	    throw new IllegalStateException("frame already set");
	}
	this.frame = frame;
	firePropertyChange("frame", null, this.frame);
    }

    public JRootPane getRootPane() {
        return getFrame().getRootPane();
    }
}
