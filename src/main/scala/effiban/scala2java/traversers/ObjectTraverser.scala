package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait ObjectTraverser extends ScalaTreeTraverser[Defn.Object]

private[scala2java] class ObjectTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaModifiersResolver: JavaModifiersResolver)
                                             (implicit javaWriter: JavaWriter) extends ObjectTraverser {

  import javaWriter._

  override def traverse(objectDef: Defn.Object): Unit = {
    writeLine()
    writeComment("originally a Scala object")
    writeLine()
    annotListTraverser.traverseMods(objectDef.mods)
    writeTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(objectDef.mods),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaScope = javaScope
    javaScope = JavaScope.Class
    templateTraverser.traverse(objectDef.templ)
    javaScope = outerJavaScope
  }
}

object ObjectTraverser extends ObjectTraverserImpl(
  AnnotListTraverser,
  TemplateTraverser,
  JavaModifiersResolver)

