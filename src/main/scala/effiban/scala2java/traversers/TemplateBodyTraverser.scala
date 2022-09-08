package effiban.scala2java.traversers

import effiban.scala2java.entities.{ClassInfo, CtorContext}
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Init, Stat}

trait TemplateBodyTraverser {

  def traverse(statements: List[Stat],
               inits: List[Init],
               maybeClassInfo: Option[ClassInfo]): Unit
}

private[traversers] class TemplateBodyTraverserImpl(templateChildTraverser: => TemplateChildTraverser,
                                                    javaTemplateChildOrdering: JavaTemplateChildOrdering)
                                                   (implicit javaWriter: JavaWriter) extends TemplateBodyTraverser {

  import javaWriter._

  def traverse(stats: List[Stat],
               inits: List[Init],
               maybeClassInfo: Option[ClassInfo]): Unit = {
    val children = stats ++ maybeClassInfo.flatMap(_.maybePrimaryCtor)
    val maybeCtorContext = maybeClassInfo.map(classInfo => CtorContext(classInfo.className, inits))
    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, maybeCtorContext)
    )
    writeBlockEnd()
  }
}
