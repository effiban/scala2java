package io.github.effiban.scala2java.core

import io.github.effiban.scala2java.core.collectors.MainClassInitCollector
import io.github.effiban.scala2java.core.desugarers.semantic.SemanticDesugarers
import io.github.effiban.scala2java.core.desugarers.syntactic.SourceDesugarer
import io.github.effiban.scala2java.core.enrichers.Enrichers
import io.github.effiban.scala2java.core.extensions.{ExtensionRegistry, ExtensionRegistryBuilder}
import io.github.effiban.scala2java.core.factories.Factories
import io.github.effiban.scala2java.core.importmanipulation.SourceImportAdder
import io.github.effiban.scala2java.core.predicates.Predicates
import io.github.effiban.scala2java.core.renderers.Renderers
import io.github.effiban.scala2java.core.renderers.contextfactories.RenderContextFactories.sourceRenderContextFactory
import io.github.effiban.scala2java.core.resolvers.JavaFileResolverImpl
import io.github.effiban.scala2java.core.transformers.CompositeFileNameTransformer
import io.github.effiban.scala2java.core.traversers.ScalaTreeTraversers
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.core.unqualifiers.SourceUnqualifier
import io.github.effiban.scala2java.core.writers.{ConsoleJavaWriter, JavaWriterImpl}
import io.github.effiban.scala2java.spi.transformers.FileNameTransformer

import java.io.FileWriter
import java.nio.file.{Files, Path}
import scala.meta.Source
import scala.meta.inputs.Input
import scala.util.Using

object Scala2JavaTranslator {

  def translate(scalaPath: Path, maybeOutputJavaBasePath: Option[Path] = None): Unit = {
    val scalaText = Files.readString(scalaPath)
    val scalaFileName = scalaPath.getFileName.toString
    val input = Input.VirtualFile(scalaFileName, scalaText)
    val sourceTree = input.parse[Source].get

    implicit val extensionRegistry: ExtensionRegistry = ExtensionRegistryBuilder.buildFor(sourceTree)
    implicit val fileNameTransformer: FileNameTransformer = new CompositeFileNameTransformer()
    implicit val predicates: Predicates = new Predicates()
    implicit lazy val factories: Factories = new Factories(typeInferrers)
    implicit lazy val typeInferrers: TypeInferrers = new TypeInferrers(factories, predicates)

    // Run the translation flow
    val syntacticDesugaredSource = SourceDesugarer.desugar(sourceTree)
    val semanticDesugaredSource = new SemanticDesugarers().sourceDesugarer.desugar(syntacticDesugaredSource)
    val traversedSource = new ScalaTreeTraversers().sourceTraverser.traverse(semanticDesugaredSource)
    val sourceWithImports = SourceImportAdder.addTo(traversedSource)
    val unqualifiedSource = SourceUnqualifier.unqualify(sourceWithImports)
    val enrichedSource = Enrichers.sourceEnricher.enrich(unqualifiedSource)
    val sourceRenderContext = sourceRenderContextFactory(enrichedSource)
    Using(createJavaWriter(scalaPath, maybeOutputJavaBasePath, sourceTree)) { implicit writer =>
      new Renderers().sourceRenderer.render(enrichedSource.source, sourceRenderContext)
    }
  }

  private def createJavaWriter(scalaPath: Path,
                               maybeOutputJavaBasePath: Option[Path],
                               sourceTree: Source)
                              (implicit fileNameTransformer: FileNameTransformer)= {
    maybeOutputJavaBasePath match {
      case Some(outputJavaBasePath) => createJavaFileWriter(scalaPath, sourceTree, outputJavaBasePath)
      case None => ConsoleJavaWriter
    }
  }

  private def createJavaFileWriter(scalaPath: Path, sourceTree: Source, outputJavaBasePath: Path)
                                  (implicit fileNameTransformer: FileNameTransformer) = {
    val javaFile = new JavaFileResolverImpl(MainClassInitCollector).resolve(scalaPath, sourceTree, outputJavaBasePath)
    new JavaWriterImpl(new FileWriter(javaFile))
  }
}




