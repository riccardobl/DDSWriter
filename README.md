[![Build Status](https://travis-ci.org/riccardobl/DDSWriter.svg?branch=master)](https://travis-ci.org/riccardobl/DDSWriter) 


# DDSWriter

DDSWriter is a command-line utility and java library to write DDS. 
It supports extensions with the use of delegates and CLI modules.

A delegate is a class to which is delegated the task to write the header and the body of the DDS, a CLI module is an extension for the CLI interface.


## The Java library
#### Requirements
````gradle
def jme_version = "v3.1"
def jme_group =  "com.github.jMonkeyEngine.jmonkeyengine"

repositories {
    mavenCentral()	
    maven { url 'https://jitpack.io' }
}
dependencies {
	compile "${jme_group}:jme3-core:${jme_version}"
	compile "${jme_group}:jme3-desktop:${jme_version}"
}
````

#### Library
````gradle
def version = "1.0"

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compile "com.github.riccardobl.DDSWriter:dds_writer:$version"
}

````

#### Usage
```java
com.jme3.texture.Texture tx=...; // Texture loaded with jmonkey
OutputStream fo=...; 
 Map<String,String> options=...; // Options for ddswriter and for the delegates
Collection<DDSDelegate> delegates=...; // List of delegate (needs at least one) 
DDSWriter.write(tx,options,delegates,fo);
```

#### Options
```gen-mipmaps``` = ```true```/```false``` - Enable/Disable mipmap generation

```debug``` = ```true```/```false``` - Enable/Disable debug info

```format``` - Output format. (See delegates for available formats) 

## The Command line
```
Usage: <CMD> --in path/file.png --out path/out.dds [options]
Options: 
   --in <FILE>: Input file
   --out <FILE.dds>: Output file
   --format: Output format. Default: ARGB8 (uncompressed)
   --gen-mipmaps: Generate mipmaps
   --exit: Exit interactive console
   --debug: Show debug informations
Input formats:
   jpg,bmp,png,tga,dds
Output formats:
   ARGB8,RGB8,RGB565
```

To use one or more delegates in CLI, they must be added to the classpath. 
--help will be updated to show informations related to the delegate.


## Delegates

### Generic (uncompressed)
Delegate that provides the following uncompressed formats:  *RGB8* *ARGB9* *RGB565*

```ddswriter.delegates.GenericDelegate delegate=new ddswriter.delegates.GenericDelegate();```
#### Gradle depencency
None: included in dds writer


### LWJGL2 S3TC (DXT compression)
Delegate that provides S3tc (DXT) compression, it requires graphical drivers that support such compression and works only in an LWJGL2 context.

This delegate adds the formats *S3TC_DXT1*,*S3TC_DXT2*,*S3TC_DXT5*.

```java
ddswriter.delegates.lwjgl2_s3tc.S3TC_LWJGL2CompressionDelegate delegate=new ddswriter.delegates.lwjgl2_s3tc.S3TC_LWJGL2CompressionDelegate();
```

#### Gradle depencency
```gradle
compile 'com.github.riccardobl.DDSWriter:dds_writer__s3tc_lwjgl2_delegate:$version'
```


### LWJGL2 RGTC (ATI compression)
Delegate that provides RGTC (ATI) compression, it requires graphical drivers that support such compression and works only in an LWJGL2 context.

This delegate adds the formats *`RGTC1*,*RGTC2*.

```java
ddswriter.delegates.lwjgl2_rgtc.RGTC_LWJGL2CompressionDelegate delegate=new ddswriter.delegates.lwjgl2_rgtc.RGTC_LWJGL2CompressionDelegate();
```
#### Gradle depencency
```gradle
compile 'com.github.riccardobl.DDSWriter:dds_writer__rgtc_lwjgl2_delegate:$version'
```

## Usage examples

#### CLI with s3tc delegator
```
java -cp "dds_writer__cli-fat-1.0.jar:dds_writer__s3tc_ati_lwjgl2_delegate-fat-1.0.jar"  ddswriter.cli.CLI109 --help
```


## Write a delegate
TODO

## Write a CLI module
TODO

[TODO LIST](TODO.md)

