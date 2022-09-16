package effiban.scala2java.traversers

import effiban.scala2java.classifiers.ImporterClassifier
import effiban.scala2java.contexts.StatContext
import effiban.scala2java.writers.JavaWriter

import scala.meta.Import

trait ImportTraverser {
  def traverse(`import`: Import, context: StatContext = StatContext()): Unit
}

private[traversers] class ImportTraverserImpl(importerTraverser: => ImporterTraverser,
                                              importerClassifier: ImporterClassifier)
                                             (implicit javaWriter: JavaWriter) extends ImportTraverser {

  import javaWriter._

  override def traverse(`import`: Import, context: StatContext = StatContext()): Unit = {
    `import`.importers match {
      case List() => writeComment("Invalid import with no inner importers")
      // TODO instead of filtering out all scala imports, some can be converted to Java equivalents
      case importers => importers.filterNot(importerClassifier.isScala).foreach(importerTraverser.traverse)
    }
  }
}
