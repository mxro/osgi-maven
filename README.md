osgi-maven
==========

This project contains various libraries prepared as OSGI ready maven bundles. 

Please read the following blog post for more informations:



All maven artifacts are in the following repository:

```xml
<repositories>
    <repository>
        <id>onedb Releases</id>
        <url>http://dl.dropbox.com/u/957046/onedb/mvn-releases</url>
    </repository>
</repositories>
```

This repository may contain maven pom definitions to link to other maven projects and/or parts of the source code of the referenced libraries. **Use all poms and sources under the license of the referenced original project.** For instance, since Netty is released
under the Apache License 2.0, you should also use the [pom provided in this repository](https://github.com/mxro/osgi-maven/blob/master/thrdNetty3/pom.xml) 
and all sources within the [thrdNetty3](https://github.com/mxro/osgi-maven/tree/master/thrdNetty3) folder under ther Apache License 2.0.

## Features

All maven artifacts in this repository are configured to provide the following features:

### Create MANIFEST.MF

Using the [Maven Bundle Plugin](http://svn.apache.org/repos/asf/felix/releases/maven-bundle-plugin-2.3.7/doc/site/index.html), 
a MANIFEST.MF file will be created as part of the install phase. This file will be copied to the location `{project root}/META-INF/MANIFEST.MF`.

Hence, to create a MANIFEST.MF file for the projects, simply run maven with the `install` goal after downloading the
project director:

```
mvn clean install
```

### Create Eclipse PDE Projects

Since OSGi is an important cornerstone of the eclipse IDE, eclipse provides some of the very best tooling to work
with OSGi modules. All projects in this repository allow you to generate an eclipse PDE project from the downloaded sources.

Just download the project, you would like to include and run maven with the goals listed below:

```
eclipse:clean clean package eclipse:eclipse -Declipse.pde -Declipse.useProjectReferences=false install -DskipTests 
```

## Libraries

Currently, the following libraries are provided in this repository:

### Hamcrest

[Hamcrest](http://code.google.com/p/hamcrest/) is a powerful little library to define conditions in Java using an expressive API.

The wrapper included in this repository only contains a subset of the Hamcrest library. In particular, only those parts, which can be compiled using the GWT compiler.

To avoid namespace collisions with other Hamcrest bundles (eclipse has one by default as part of the platform), all classes have been moved into a unique namespace.

```xml
<dependency>
    <groupId>de.mxro.thrd.hamcrestgwt</groupId>
    <artifactId>thrdHamcrestGWT</artifactId>
    <version>0.0.4</version>
</dependency>
```

### Netty

OSGi bundle for [Netty 3.2.6](http://www.jboss.org/netty).

```xml
<dependency>
    <groupId>de.mxro.thrd.netty3</groupId>
    <artifactId>thrdNetty3</artifactId>
    <version>0.0.2</version>
</dependency>
```

### async-http-client

OSGi bunlde for [async-http-client](https://github.com/sonatype/async-http-client) library. 
This bundle depends on the [Netty](#netty) bundle.

```xml
<dependency>
    <groupId>de.mxro.thrd.asynchttpclient17</groupId>
    <artifactId>thrdAsyncHttpClient17</artifactId>
    <version>0.0.3</version>
</dependency>
```

### Java Mail API

OSGi-ready bundle for the [Java Mail API](http://javamail.kenai.com/nonav/javadocs/javax/mail/package-summary.html).

```xml
<dependency>
    <groupId>de.mxro.thrd.javaxmail14</groupId>
     <artifactId>thrdJavaxMail14</artifactId>
     <version>0.0.2</version>
</dependency>
```


### BabuDB

[BabuDB](http://code.google.com/p/babudb/) is a not often mentioned but seriously great key-value store (read NOSQL!) written in 100% Java.

This artifact compiles BabuDB and all its dependencies together in one artifact and allows to deploy it as embedded Java database in OSGi and vanilla Java applications.

```xml
<dependency>
    <groupId>de.mxro.thrd.babudb05</groupId>
    <artifactId>thrdBabuDb05</artifactId>
    <version>0.0.2</version>
</dependency>
```

### Dom4J

[Dom4J](http://dom4j.sourceforge.net/) is a helpful library to work with XML. This artifact is a simple wrapper for this library allowing to deploy it as OSGi bundle.

```xml
<dependency>
    <groupId>de.mxro.thrd.dom4j</groupId>
    <artifactId>thrdDom4j</artifactId>
    <version>0.0.2</version>
</dependency>
```

### GWT User Library

[Google Web Toolkit](https://developers.google.com/web-toolkit/) applications need to link during compile- (and development-)time to the GWT User library.

This repository contains two versions of the GWT client libraries: 2.2.0 and 2

**Version 2.4.0:**
```xml
<dependency>
    <groupId>de.mxro.thrd.gwtuser24</groupId>
    <artifactId>thrdGWTUser24</artifactId>
    <version>0.0.2</version>
</dependency>
```

**Version 2.2.0:**
```xml
<dependency>
    <groupId>de.mxro.thrd.gwtuser</groupId>
    <artifactId>thrdGWTUser</artifactId>
    <version>0.0.4</version>
</dependency>
```



### Ext GWT (GXT)

[Ext GWT](http://www.sencha.com/store/gxt/) is a rich-client library to enhance Googles Web Toolkit.

Please note that this library is provided under the **GPLv3 license**. If you use this library (or the wrapper provided here) you will need to make your source code available under the terms of the GPL.

```xml
<dependency>
	<groupId>de.mxro.thrd.gwtgxt</groupId>
	<artifactId>thrdGwtGxt</artifactId>
	<version>0.0.2</version>
</dependency>
```

### Apache VFS

OSGi bundle for [Apache Commons VFS](http://commons.apache.org/vfs/):

```xml
<dependency>
   <groupId>de.mxro.thrd.apachevfs</groupId>
   <artifactId>thrdApacheVFS</artifactId>
   <version>0.0.2</version>
</dependency>
```