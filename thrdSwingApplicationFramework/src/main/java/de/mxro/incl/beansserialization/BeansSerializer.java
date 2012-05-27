package de.mxro.incl.beansserialization;

import java.awt.Rectangle;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BeansSerializer {
	
	 private static class LSException extends IOException {
			public LSException(String s, Throwable e) {
			    super(s);
			    initCause(e);
			}
			public LSException(String s) {
			    super(s);
			}
		    }
	
	private static class AbortExceptionListener implements ExceptionListener {
		public Exception exception = null;
		public void exceptionThrown(Exception e) {
		    if (exception == null) { exception = e; }
		}
	    }
	
	private static class RectanglePD extends DefaultPersistenceDelegate {
		public RectanglePD() {
		    super(new String[]{"x", "y", "width", "height"});
		}
		protected Expression instantiate(Object oldInstance, Encoder out) {
		    Rectangle oldR = (Rectangle)oldInstance;
		    Object[] constructorArgs = new Object[]{
			oldR.x, oldR.y, oldR.width, oldR.height
		    };
		    return new Expression(oldInstance, oldInstance.getClass(), "new", constructorArgs);
		}
	    }
	
	public static void save(Object bean, OutputStream toStream) throws IOException {
		AbortExceptionListener el = new AbortExceptionListener();
		XMLEncoder e = null;
		ByteArrayOutputStream bst = new ByteArrayOutputStream();
		try {
		   
		    
		    Thread cur = Thread.currentThread();
		    ClassLoader ccl = cur.getContextClassLoader();
		    ClassLoader classLoader =  BeansSerializer.class.getClassLoader(); // bean.getClass().getClassLoader(); 
		    if (classLoader == null) {
		    	System.out.println("BeansSeralizer: Classloader null");
		    }
		    cur.setContextClassLoader(classLoader);
		    e = new XMLEncoder(bst);
		    try {
		    e.setPersistenceDelegate(Rectangle.class, new RectanglePD());
	           /* if (!persistenceDelegatesInitialized) {
	                e.setPersistenceDelegate(Rectangle.class, new RectanglePD());
	                persistenceDelegatesInitialized = true;
	            }*/
		    e.setExceptionListener(el);
		    e.writeObject(bean);
		    } finally {
		    	cur.setContextClassLoader(ccl);
		    }
		}
		finally {
		    if (e != null) { e.close(); }
		}
		if (el.exception != null) {
		    throw new LSException("save failed for class \"" + bean.getClass().getName() + "\"", el.exception);
		}
		OutputStream ost = toStream;
		try {
		    //ost = openOutputFile(fileName);
		    ost.write(bst.toByteArray());
		}
		finally {
		    if (ost != null) { ost.close(); }
		}
	    }
	
	
	public static Object load(InputStream is) throws IOException {
		InputStream ist = is; 
		
		AbortExceptionListener el = new AbortExceptionListener();
		XMLDecoder d = null;	
		 
		Thread cur = Thread.currentThread();
		    ClassLoader ccl = cur.getContextClassLoader();
		    ClassLoader classLoader = BeansSerializer.class.getClassLoader(); 
		    cur.setContextClassLoader(classLoader);
		try {

		   
		    
			d = new XMLDecoder(ist);
		    d.setExceptionListener(el);
		    Object bean = d.readObject();
		    if (el.exception != null) {
			throw new LSException("BeansSerilizer cannot load class [" + "" + "]", el.exception);
		    }
		    return bean;
		}
		finally {
		    if (d != null) { d.close(); }
		    cur.setContextClassLoader(ccl);
		}
	    }
	
}
