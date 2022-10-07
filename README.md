## Scala to Java Translation Tool

### Overview
Scala is an advanced language built on top of the JVM, offering concise syntax and extensive support 
for functional programming.
Unfortunately, although Scala has been around for quite a while (and is my personal favorite) - it hasn't gained
the popularity that some expected, and is used today by only a small percentage of developers and applications (this is true for
both the industry in general, as well as the JVM-based languages).  
At my company, we started out many years ago with a purely-Java codebase, and about 6-7 years ago began to introduce Scala. 
Over the years we accumulated a significant amount of Scala code in our microservices.  
Recently, due to a lack of Scala expertise in the company and difficulty recruiting Scala developers, management
decided to halt all Scala development and gradually replace existing code with Java. 
This presents a real challenge to us, since there is no straightforward way to do this without a lot of tedious manual work. 
Using a decompiler will of course produce equivalent Java code, but it is unreadable and unmaintainable. 

Therefore, I decided to develop this tool - which is aimed at translating (transpiling) Scala code into _readable_ and _maintainable_ Java code,
while adhering to the original style of the code (with certain limitations). Even though the generated code is not complete, I still believe 
it will save at least 80% of the manual work that would otherwise be needed to carry out the translation.  
I also hope it may prove useful to other companies/individuals as well.  

### Usage Guide

The tool receives one or more Scala source files and translates them one by one to Java.  
The output can either be printed to the console or save to a given directory.  
In the second case, each Scala file will be translated into a corresponding file with the same name and **.java** suffix

There are two options for running the tool:

**Option 1 - CLI tool** 

1. Download the executable jar [scala2Java-1.0.0-all.jar](TBD) 
1. To generate output to the console:  
   ```java -jar scala2Java-1.0.0-all.jar MyClass1.scala MyClass2.scala```  
1. To generate output to a directory:  
   ```java -jar scala2Java-1.0.0-all.jar --outDir=myDir  MyClass.scala MyClass2.scala```



**Option 2 - SDK _(currently supports only one file at a time)_**

1. Add the scala2java dependency to your project as follows:

Maven:

```xml
<dependency>
  <groupId>effiban</groupId>
  <artifactId>scala2java</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'effiban:scala2java:1.0.0'
```

2. To generate output to the console (Scala example)
   ```scala
   import effiban.scala2java.Scala2JavaTranslator.translate
   import java.nio.file.Path

   class Translator {
       def doTranslate(): Unit = {
           val scalaPath = Path.of("myRootDir", "mypackage", "MyClass.scala")
           translate(scalaPath)
       }
   }
   ```
3. To generate output to a directory (Scala example)
   ```scala
   import effiban.scala2java.Scala2JavaTranslator.translate
   import java.nio.file.Path

   class Translator {
       def doTranslate(): Unit = {
           val scalaPath = Path.of("myScalaRoot", "mypackage", "MyClass.scala")
           val javaOutputDir = Path.of("myJavaRoot", "mypackage")
           translate(scalaPath, Some(javaOuptutDir))
       }
   }
   ```

### Technical Details
Scala2Java is developed on top of the excellent [ScalaMeta](https://scalameta.org/) library, which provides a convenient API for
parsing Scala programs into an AST. This AST is traversed by the tool and converted into equivalent Java code, as much as possible.  
At this point, the tool supports _syntactic_ translation only - so each file in a codebase must be translated separately into Java,
and the tool will not be able to utilize or infer _semantic_ information such as method definitions from other files.    
In the future I plan to utilize Scalameta's [SemanticDB](https://scalameta.org/docs/semanticdb/guide.html) feature, which should generate a more
precise and comprehensive translation into Java code.

### Supported Language Versions
**Scala**: The tool was tested with Scala 2.13, and will probably work with earlier versions. There is no support for Scala 3.    
**Java**: The generated files are in Java 17. In the future (if needed) other versions can be supported.

### Supported Features
Scala2Java is able to translate all of the basic Scala syntax into equivalent Java. Here is a partial list: 
- class/method/variable definitions
- control structures (if / while)
- visibility modifiers (which can have different rules and names in Java)
- Generic types 
- Annotations  
  
It is also able to rewrite some language constructs which are supported differently or completely unsupported in Java such as:  
- Primary constructors
- Adding the 'return' keyword
- 'for' comprehension
- string interpolation
- by-name parameters

### Limitations 
- **Scala advanced features**  
Scala has some advanced features that have no parallel in Java, and they either cannot be translated to Java based on syntax alone, 
or else they require a full rewrite that is quite complex. These features are not supported by the tool and it will generate a comment as a hint 
for the manual translation that will have to been done by the developer, for example:
  - Implicit definitions
  - imports inside a class/method
  - named parameters and method arguments
  - Advanced pattern matching (Java 17 'switch' cases still have very limited capabilities)  

- **Scala built-in types**    
Scala has many built-in types and methods that need to be translated into the Java equivalent such as `Option`, `Future`, collection types, etc.  
At this time the tool provides only basic support for these, and the rest will simply be written in the Java code as-is.  
Improvements in this area are planned for the future.  

- **Semantic Data**   
As explained above , the tool currently does not have access to semantic information so it cannot process more than one file at a time.    
It will not be able to validate and infer types of symbols created in different files, and even some of the symbols in the same file. 
For such cases comments will be generated in the Java code with hints regarding the missing values/types.


## Licensing

Scala2Java is licensed under the Apache License, Version 2.0.