[![Build Status](https://travis-ci.org/riccardobl/DDSWriter.svg?branch=master)](https://travis-ci.org/riccardobl/DDSWriter) 


# DDSWriter

DDSWriter is a command-line utility and java library to write DDS. 
It can be extended to support other formats (including compressed formats) with the use of delegates and CLI modules.

A delegate is a class to which is delegated the task to write the header and the body of the DDS, a CLI module is an extension for the CLI interface.


## The Java library
#### Requirements
````gradle
def jme_version = "v3.1"
def jme_group =  "com.github.jMonkeyEngine.jmonkeyengine"

repositories {
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
Collection<DDSDelegate> delegates=...; // List of delegate (can be empty) 
DDSWriter.write(tx,options,delegators,fo);
```

#### Options
```gen-mipmaps``` = ```true```/```false``` - Enable/Disable mipmap generation

```debug``` = ```true```/```false``` - Enable/Disable debug info

```format``` = ```ARGB8```/```RGB8```/```RGB565``` - Output format (Note: delegates can add more output formats)

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

### LWJGL2 S3TC (DXT compression)
Delegate that provides S3tc (DXT) compression, it requires graphical drivers that support such compression and works only in an LWJGL2 context.

This delegate adds the formats S3TC_DXT1,S3TC_DXT2,S3TC_DXT5.
#### Gradle depencency
```gradle
compile 'com.github.riccardobl.DDSWriter:dds_writer__s3tc_lwjgl2_delegate:$version'
```


### LWJGL2 RGTC (ATI compression)
Delegate that provides RGTC (ATI) compression, it requires graphical drivers that support such compression and works only in an LWJGL2 context.

This delegate adds the formats RGTC1,RGTC2.
#### Gradle depencency
```gradle
compile 'com.github.riccardobl.DDSWriter:dds_writer__rgtc_lwjgl2_delegate:$version'
```

## Usage examples

#### CLI with s3tc delegator
```
java -cp "dds_writer__cli-fat-0.1.jar:dds_writer__s3tc_ati_lwjgl2_delegator-fat-0.1.jar"  ddswriter.cli.CLI109 --help
```


## Write a delegate
TODO

## Write a CLI module
TODO

[TODO LIST](TODO.md)

