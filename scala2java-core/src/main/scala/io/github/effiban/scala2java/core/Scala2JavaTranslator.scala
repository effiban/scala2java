package io.github.effiban.scala2java.core

import io.github.effiban.scala2java.core.cleanup.Cleanups
import io.github.effiban.scala2java.core.collectors.JavaTopLevelTypeNameCollector
import io.github.effiban.scala2java.core.desugarers.semantic.SemanticDesugarers
import io.github.effiban.scala2java.core.desugarers.syntactic.SourceDesugarer
import io.github.effiban.scala2java.core.enrichers.Enrichers
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedSource
import io.github.effiban.scala2java.core.extensions.{ExtensionRegistry, ExtensionRegistryBuilder}
import io.github.effiban.scala2java.core.factories.Factories
import io.github.effiban.scala2java.core.importmanipulation.{SourceImportAdder, SourceImportRemover}
import io.github.effiban.scala2java.core.predicates.Predicates
import io.github.effiban.scala2java.core.qualifiers.SourceQualifier
import io.github.effiban.scala2java.core.renderers.Renderers
import io.github.effiban.scala2java.core.renderers.contextfactories.RenderContextFactories.sourceRenderContextFactory
import io.github.effiban.scala2java.core.resolvers.JavaFileResolverImpl
import io.github.effiban.scala2java.core.transformers.Transformers
import io.github.effiban.scala2java.core.traversers.ScalaTreeTraversers
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.core.unqualifiers.SourceUnqualifier
import io.github.effiban.scala2java.core.writers.{ConsoleJavaWriter, JavaWriterImpl}

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
    val source = input.parse[Source].get

    implicit val extensionRegistry: ExtensionRegistry = ExtensionRegistryBuilder.buildFor(source)
    implicit val predicates: Predicates = new Predicates()
    implicit lazy val factories: Factories = new Factories(typeInferrers)
    implicit lazy val typeInferrers: TypeInferrers = new TypeInferrers(factories, predicates)
    implicit val cleanups: Cleanups = new Cleanups()

    val flowRunner: Source => Unit =
      Function.chain[Source](
          Seq(
            SourceDesugarer.desugar,
            SourceQualifier.qualify,
            SourceImportRemover.removeUnusedFrom,
            new SemanticDesugarers().sourceDesugarer.desugar,
            new ScalaTreeTraversers().sourceTraverser.traverse,
            new Transformers().sourceTransformer.transform,
            cleanups.sourceInitCleanup.cleanup,
            SourceImportAdder.addTo,
            SourceUnqualifier.unqualify,
            cleanups.sourceCleanup.cleanup
          )
        ).andThen(Enrichers.sourceEnricher.enrich)
        .andThen(enrichedSource => renderJava(enrichedSource, scalaPath, maybeOutputJavaBasePath))

    flowRunner(source)
  }

  private def renderJava(enrichedSource: EnrichedSource,
                         scalaPath: Path,
                         maybeOutputJavaBasePath: Option[Path]) = {
    val sourceRenderContext = sourceRenderContextFactory(enrichedSource)
    Using(createJavaWriter(scalaPath, maybeOutputJavaBasePath, enrichedSource.source)) { implicit writer =>
      new Renderers().sourceRenderer.render(enrichedSource.source, sourceRenderContext)
    }
  }

  private def createJavaWriter(scalaPath: Path,
                               maybeOutputJavaBasePath: Option[Path],
                               sourceTree: Source)= {
    maybeOutputJavaBasePath match {
      case Some(outputJavaBasePath) => createJavaFileWriter(scalaPath, sourceTree, outputJavaBasePath)
      case None => ConsoleJavaWriter
    }
  }

  private def createJavaFileWriter(scalaPath: Path, sourceTree: Source, outputJavaBasePath: Path) = {
    val javaFile = new JavaFileResolverImpl(JavaTopLevelTypeNameCollector).resolve(scalaPath, sourceTree, outputJavaBasePath)
    new JavaWriterImpl(new FileWriter(javaFile))
  }
}




