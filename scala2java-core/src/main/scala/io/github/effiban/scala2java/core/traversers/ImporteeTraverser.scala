package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameIndeterminateRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Importee, Name}

trait ImporteeTraverser extends ScalaTreeTraverser[Importee]

private[traversers] class ImporteeTraverserImpl(nameIndeterminateRenderer: NameIndeterminateRenderer)
                                               (implicit javaWriter: JavaWriter) extends ImporteeTraverser {

  import javaWriter._

  // A single imported element within an Importer (can be one name, wildcard etc. see below)
  override def traverse(importee: Importee): Unit = {
    importee match {
      case Importee.Name(name: Name.Indeterminate) =>
        nameIndeterminateRenderer.render(name)
      case Importee.Name(name) => handleIllegalName(name)
      case Importee.Wildcard() => write("*")
      case Importee.Rename(name: Name.Indeterminate, rename: Name.Indeterminate) =>
        nameIndeterminateRenderer.render(name)
        write(" ")
        writeComment(s"Renamed in Scala to '${rename.toString}'")
      case Importee.Rename(name: Name, rename: Name) => handleIllegalNamePair(name, rename)
      case Importee.Unimport(name: Name.Indeterminate) =>
        nameIndeterminateRenderer.render(name)
        write(" ")
        writeComment(s"Hidden (unimported) in Scala")
      case Importee.Unimport(name) => handleIllegalName(name)
    }
  }

  private def handleIllegalName(name: Name) =
    throw new IllegalStateException(s"Invalid importee name '$name' - must be a Name.Indeterminate")

  private def handleIllegalNamePair(name: Name, rename: Name) =
    throw new IllegalStateException(
      s"Invalid importee name+rename pair ('$name', '$rename') - both must be a Name.Indeterminate but at least one isn't"
    )
}
