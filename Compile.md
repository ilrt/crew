## Obtaining the source code ##

You can download a source code release from http://www.crew-vre.net/?page_id=9. Alternatively, you can obtain the source code from the Subversion repository with your favourite svn client. For example,

> `svn checkout http://crew.googlecode.com/svn/trunk/Crew Crew`

## Build the code with Maven 2 ##

Maven 2 is used to build the CREW software. If Maven is not installed on your system it can be downloaded from http://maven.apache.org/download.html.

You need to ensure that the `bin/` directory of your Maven installation is set on your `PATH` variable.

To install the different modules into your local maven repository:

```
cd Crew/
mvn install
```

This process might take a few minutes because maven will automatically connect to third party repositories to download the libraries that are used by CREW.