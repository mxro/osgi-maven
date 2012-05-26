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

## Netty

OSGi bundle for [Netty 3.2.6](http://www.jboss.org/netty).

```xml
<dependency>
    <groupId>de.mxro.thrd.netty3</groupId>
    <artifactId>thrdNetty3</artifactId>
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