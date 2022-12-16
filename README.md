## Scala to Java Translation Tool

### Overview
Scala is an advanced language built on top of the JVM, offering concise syntax and extensive support 
for functional programming.
Unfortunately, although Scala has been around for quite a while (and is my personal favorite) - it hasn't gained
the popularity that some expected, and is used today by only a small percentage of developers and applications (this is true for
both the industry in general, and the JVM-based languages specifically).  
At my company, we started out many years ago with a purely-Java codebase, and about 6-7 years ago began to introduce Scala. 
Over the years we accumulated a significant amount of Scala code in our microservices.  
Recently, due to a lack of Scala expertise in the company and difficulty recruiting Scala developers, management
decided to halt all Scala development and gradually replace existing code with Java. 
This presents a real challenge to us, since there is no straightforward way to do this without a lot of tedious manual work. 
Using a decompiler will of course produce equivalent Java code, but it is unreadable and unmaintainable. 

Therefore, I decided to develop this tool - which is aimed at translating (transpiling) Scala code into _readable_ and _maintainable_ Java code,
while adhering to the original style of the code (with certain limitations). Even though the generated code is not complete, I still believe 
it will save at least 80% of the manual work, that would otherwise be needed to carry out the translation.  
I also hope it may prove useful to other companies/individuals as well.  

### Supported Language Versions
**Tool**: The tool itself is built with Scala 2.13 and Java 17.   
**Scala Input**: The tool was tested with sources in Scala 2.13, and will probably work with earlier versions. There is no support for Scala 3.    
**Java Output**: The generated files are in Java 17. In the future (if needed) other versions may be supported.

### Maven Central Coordinates
Group: **io.github.effiban**  
Artifact: **scala2java-core_2.13**

### Usage Guide and Documentation
Refer to the [Wiki](https://github.com/effiban/scala2java/wiki/Home)

### Licensing

Scala2Java is licensed under the Apache License, Version 2.0.