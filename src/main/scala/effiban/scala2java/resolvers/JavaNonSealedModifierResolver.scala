package effiban.scala2java.resolvers

import effiban.scala2java.classifiers.{ModsClassifier, ObjectClassifier}
import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.{JavaModifier, JavaScope}

import scala.meta.Defn

class JavaNonSealedModifierResolver(modsClassifier: ModsClassifier,
                                    objectClassifier: ObjectClassifier) extends JavaExtraModifierResolver {

  override def resolve(context: JavaModifiersContext): Option[JavaModifier] = {
    import context._

    (scalaTree, scalaMods, javaScope) match {
      case (_: Defn.Object, _, _) => None
      case (_, scMods, JavaScope.Sealed) if !(modsClassifier.includeSealed(scMods) || modsClassifier.includeFinal(scMods)) => Some(JavaModifier.NonSealed)
      case _ => None
    }
  }
}

object JavaNonSealedModifierResolver extends JavaNonSealedModifierResolver(ModsClassifier, ObjectClassifier)
