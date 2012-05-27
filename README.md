osgi-maven
==========

Various libraries prepared as OSGI ready maven bundles.

All maven artifacts are in the following repository.

```xml
<repositories>
    <repository>
        <id>onedb Releases</id>
        <url>http://dl.dropbox.com/u/957046/onedb/mvn-releases</url>
    </repository>
</repositories>
```

This repository may contain maven pom definitions to link to other maven projects and/or parts of the source code of the referenced libraries.  
**Use all poms and sources under the license of the referenced original project.** For instance, since Netty is released
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
mvn install
```

### Create eclipse PDE projects

Since OSGi is an important 

## Netty

OSGi bundle for [Netty 3.2.6](http://www.jboss.org/netty).

```xml
<dependency>
    <groupId>de.mxro.thrd.netty3</groupId>
    <artifactId>thrdNetty3</artifactId>
    <version>0.0.2</version>
</dependency>
```

## async-http-client

OSGi bunlde for [async-http-client](https://github.com/sonatype/async-http-client) library. 
This bundle depends on the [Netty](#netty) bundle.

```xml
<dependency>
    <groupId>de.mxro.thrd.asynchttpclient17</groupId>
    <artifactId>thrdAsyncHttpClient17</artifactId>
    <version>0.0.3</version>
</dependency>
```

## BabuDB

[BabuDB](http://code.google.com/p/babudb/) is a not often mentioned but seriously great key-value store (read NOSQL!) written in 100% Java.

This artifact compiles BabuDB and all its dependencies together in one artifact and allows to deploy it as embedded Java database in OSGi and vanilla Java applications.

```xml
<dependency>
    <groupId>de.mxro.thrd.babudb05</groupId>
    <artifactId>thrdBabuDb05</artifactId>
    <version>0.0.2</version>
</dependency>
```


## Apache VFS

OSGi bundle for [Apache Commons VFS](http://commons.apache.org/vfs/):

```xml
<dependency>
   <groupId>de.mxro.thrd.apachevfs</groupId>
   <artifactId>thrdApacheVFS</artifactId>
   <version>0.0.2</version>
</dependency>
```