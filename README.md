osgi-maven
==========

This project contains various libraries prepared as OSGI ready maven bundles. 

Please read the following blog post for more information:


All maven artifacts are in the following repository:

```xml
<repositories>
    <repository>
        <id>onedb Releases</id>
        <url>http://dl.dropbox.com/u/957046/onedb/mvn-releases</url>
    </repository>
</repositories>
```

This repository may contain maven pom definitions to link to other maven projects and/or parts of the source code of the referenced libraries. **Use all poms and sources under the license of the referenced original project.** For instance, since Netty is released under the Apache License 2.0, you should also use the [pom provided in this repository](https://github.com/mxro/osgi-maven/blob/master/thrdNetty3/pom.xml) and all sources within the [thrdNetty3](https://github.com/mxro/osgi-maven/tree/master/thrdNetty3) folder under the Apache License 2.0.

## Features

All maven artifacts in this repository are configured to provide the following features:

### Create MANIFEST.MF

Using the [Maven Bundle Plugin](http://svn.apache.org/repos/asf/felix/releases/maven-bundle-plugin-2.3.7/doc/site/index.html), 
a MANIFEST.MF file will be created as part of the install phase. This file will be copied to the location `{project root}/META-INF/MANIFEST.MF`.

Hence, to create a MANIFEST.MF file for the projects, simply run maven with the `install` goal after downloading the project director:

```
mvn clean install
```

### Create Eclipse PDE Projects

Since OSGi is an important cornerstone of the eclipse IDE, eclipse provides some of the very best tooling to work with OSGi modules. All projects in this repository allow you to generate an eclipse PDE project from the downloaded sources.

Just download the project, you would like to include and run maven with the goals listed below:

```
eclipse:clean clean package eclipse:eclipse -Declipse.pde -Declipse.useProjectReferences=false install -DskipTests 
```

## Libraries

Currently, the following libraries are provided in this repository:

### Hamcrest

[Hamcrest](http://code.google.com/p/hamcrest/) is a powerful little library to define conditions in Java using an expressive API.

The wrapper included in this repository only contains a subset of the Hamcrest library; in particular, only those parts which can be compiled using the GWT compiler.

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

OSGi bundle for [async-http-client](https://github.com/sonatype/async-http-client) library. 
This bundle depends on the [Netty](#netty) bundle.

```xml
<dependency>
    <groupId>de.mxro.thrd.asynchttpclient17</groupId>
    <artifactId>thrdAsyncHttpClient17</artifactId>
    <version>0.0.3</version>
</dependency>
```

### Jetty

OSGi bundle for [Jetty 6.1.26](http://jetty.codehaus.org/jetty/).

```xml
<dependency>
	<groupId>de.mxro.thrd.jetty6</groupId>
	<artifactId>thrdJetty6</artifactId>
	<version>0.0.2-SNAPSHOT</version>
</dependency>
```

### Kryo

[Kryo](http://code.google.com/p/kryo/) is a powerful object serialization library for Java.

This repository provides a simple wrapper to enable running Kryo (1.03) in OSGi/eclipse PDE apps:

```xml
<dependency>
	<groupId>de.mxro.thrd.kryo</groupId>
	<artifactId>thrdKryo</artifactId>
	<version>0.0.2</version>
</dependency>
```

### XStream

OSGi bundle for [XStream](http://xstream.codehaus.org/) version 1.3.1.

```xml
<dependency>
	<groupId>de.mxro.thrd.xstream</groupId>
	<artifactId>thrdXStream</artifactId>
	<version>0.0.4</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdXStream)

### Mysql Connector/J

OSGi bundle for [Mysql Connector/J 5.1.18](http://dev.mysql.com/downloads/connector/j/):

```xml
<dependency>
	<groupId>de.mxro.thrd.mysqlconnectorj51</groupId>
	<artifactId>thrdMysqlConnectorJ51</artifactId>
	<version>0.0.4</version>
</dependency>
```

### Restlet

OSGi bundle for [Restlet](http://www.restlet.org/) (+'Simple' embedded web server):

```xml
<dependency>
	<groupId>de.mxro.thrd.restletextsimple</groupId>
	<artifactId>thrdRestletExtSimple</artifactId>
	<version>0.0.4</version>
</dependency>
```

[Sources](https://github.com/mxro/osgi-maven/tree/master/thrdRestletExtSimple)

### Java Mail API

OSGi-ready bundle for the [Java Mail API](http://javamail.kenai.com/nonav/javadocs/javax/mail/package-summary.html).

```xml
<dependency>
    <groupId>de.mxro.thrd.javaxmail14</groupId>
     <artifactId>thrdJavaxMail14</artifactId>
     <version>0.0.2</version>
</dependency>
```

### JDBM2

[jdbm2](http://code.google.com/p/jdbm2/) is a branch of the 100% Java key-value store [JDBM](http://jdbm.sourceforge.net/).

This repository contains a variant of this library with non-colliding package names and an OSGi configured pom. 

```xml
<dependency>
	<groupId>de.mxro.thrd.jdbm2V22</groupId>
	<artifactId>thrdJDVM2V22</artifactId>
	<version>0.0.2</version>
</dependency>
```

### BabuDB

[BabuDB](http://code.google.com/p/babudb/) is a not often mentioned but seriously great key-value store (read NOSQL!) written in 100% Java.

This artifact compiles BabuDB and all its dependencies together in one artifact and allows deploying it as embedded Java database in OSGi and vanilla Java applications.

```xml
<dependency>
    <groupId>de.mxro.thrd.babudb05</groupId>
    <artifactId>thrdBabuDb05</artifactId>
    <version>0.0.2</version>
</dependency>
```

### Jettison

[Jettison](http://jettison.codehaus.org/) is a powerful library to process JSON in Java.

```xml
<dependency>
    <groupId>de.mxro.thrd.jettison12</groupId>
    <artifactId>thrdJettison12</artifactId>
    <version>0.0.2</version>
</dependency>
```

### Dom4J

[Dom4J](http://dom4j.sourceforge.net/) is a helpful library to work with XML. This artifact is a simple wrapper for this library allowing deploying it as OSGi bundle.

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

### JTidy

[JTidy](http://jtidy.sourceforge.net/) is a simple Java library to clean malformed HTML.

This OSGi bundle wraps JTidy version 8.0.

```xml
<dependency>
	<artifactId>thrdJTidy</artifactId>
	<name>thrdJTidy</name>
	<version>8.0.1</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdJTidy)

### jenabean

[jenabean](http://code.google.com/p/jenabean/) is a Java library, which allows to convert Java objects into RDF.

The maven artifact in this repository allows deploying jenabean to an OSGi container. 

```xml
<dependency>
	<groupId>de.mxro.thrd.jenabean</groupId>
	<artifactId>thrdJenaBean</artifactId>
	<version>0.0.3</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdJenaBean)

### Ext GWT (GXT)

[Ext GWT](http://www.sencha.com/store/gxt/) is a rich-client library to enhance Google’s Web Toolkit.

Please note that this library is provided under the **GPLv3 license**. If you use this library (or the wrapper provided here) you will need to make your source code available under the terms of the GPL.

```xml
<dependency>
	<groupId>de.mxro.thrd.gwtgxt</groupId>
	<artifactId>thrdGwtGxt</artifactId>
	<version>0.0.2</version>
</dependency>
```

### Swing Application Framework

The [Swing Application Framework](http://java.net/projects/appframework/) provides an extensible platform to build Swing applications.

```xml
<dependency>
	<groupId>de.mxro.thrd.swingapplicationframework</groupId>
	<artifactId>thrdSwingApplicationFramework</artifactId>
	<version>2.0.2</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdSwingApplicationFramework)

### Netbeans Wizard

[The Netbeans Wizard API](http://www.javaworld.com/javaworld/jw-04-2008/jw-04-opensourcejava-wizard-api.html) provides a convenient way to define wizards in Java Swing.

```xml
<dependency>
	<groupId>de.mxro.thrd.netbeanswizard</groupId>
	<artifactId>thrdNetbeansWizard</artifactId>
	<version>0.0.2RC</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdNetbeansWizard)

### Swing Action Manager

[Swing Action Manager](http://java.net/projects/sam) provides an event-bus for Java Swing applications.

```xml
<dependency>
	<groupId>de.mxro.thrd.swingactionmanager</groupId>
	<artifactId>thrdSwingActionManager</artifactId>
	<version>0.0.4</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdSwingActionManager)

### SHEF

The [SHEF](http://shef.sourceforge.net/) library provides a rich text editor for Swing GUIs.

```xml
<dependency>
	<groupId>de.mxro.thrd.shef</groupId>
	<artifactId>thrdShef</artifactId>
	<version>0.0.4</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdShef)

### Novaworx Syntax

The (Novaworx Syntax Library)[http://freecode.com/projects/xmlgui/releases/158875] allows to highlight text in Swing UIs.

```xml
<dependency>
	<groupId>de.mxro.thrd.novaworxsyntax</groupId>
	<artifactId>thrdNovaworxSyntax</artifactId>
	<version>0.0.3RC</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdNovaworxSyntax)

### Apache Commons XML APIs

The [Apache Commons XML APIs](http://mvnrepository.com/artifact/xml-apis/xml-apis) bundle a number of useful classes to work with XML.

```xml
<dependency>
	<groupId>de.mxro.thrd.xmlapis</groupId>
	<artifactId>thrdXmlApis</artifactId>
	<version>0.0.2</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdXmlApis)

### Apache VFS

OSGi bundle for [Apache Commons VFS](http://commons.apache.org/vfs/):

```xml
<dependency>
   <groupId>de.mxro.thrd.apachevfs</groupId>
   <artifactId>thrdApacheVFS</artifactId>
   <version>0.0.2</version>
</dependency>
```

### gwt-exporter

OSGi bundle for [gwt-exporter](http://code.google.com/p/gwt-exporter/) library.
```xml
<dependency>
    <groupId>de.mxro.thrd.gwtexporter24</groupId>
    <artifactId>thrdGwtExporter24</artifactId>
    <version>0.0.1</version>
</dependency>
```

[Source](https://github.com/mxro/osgi-maven/tree/master/thrdGwtExporter24)
