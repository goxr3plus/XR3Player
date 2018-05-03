# How to fork and support this project

To build XR3Player, you will need:

* [JDK 9+]
* [Maven](http://maven.apache.org/) - Version 3.5.3++ recommended

There is are some dependencies that are not on Maven Central ( you can find those .jars on the folder called **localLibraries**) :

**JAVE** and **JAVE(for it i created a repository on github so don't worry)**
**JAVASYSMON** and **JAVASYSMON(for it i created a repository on github so don't worry)**

For them [follow this tutorial to add them to your Local Maven Repository](https://www.mkyong.com/maven/how-to-include-library-manully-into-maven-local-repository/) 

For example (**JAVE**) [Github Repository by GOXR3PLUS](https://github.com/goxr3plus/JAVE) in my computer i do the following :

> mvn install:install-file -Dfile=D:\GitHub\XR3Player\localLibraries\jave-1.0.2.jar -DgroupId=it.sauronsoftware.jave -DartifactId=jave -Dversion=1.0.2 -Dpackaging=jar

```XML
<!-- JAVE -->
<dependency>
	<groupId>it.sauronsoftware.jave</groupId>
	<artifactId>jave</artifactId>
	<version>1.0.2</version>
</dependency>
```

For example (**JAVASYSMON**) [Github Repository by GOXR3PLUS](https://github.com/goxr3plus/javasysmon) in my computer i do the following :

> mvn install:install-file -Dfile=D:\GitHub\XR3Player\localLibraries\javasysmon-0.3.6.0.jar -DgroupId=local.github.goxr3plus -DartifactId=javasysmon -Dversion=3.6.0 -Dpackaging=jar

```XML
<!-- javasysmon -->
<dependency>                                      
	<groupId>local.github.goxr3plus</groupId>     
	<artifactId>javasysmon</artifactId>           
	<version>3.6.0</version>                      
</dependency>                                     
```
---

Follow the above instructions and run ``mvn clean package`` , be sure that you are compiling with Java 9
