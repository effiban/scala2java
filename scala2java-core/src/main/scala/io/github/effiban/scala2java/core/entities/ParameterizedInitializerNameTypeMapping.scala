package io.github.effiban.scala2java.core.entities

import scala.meta.{Term, Type}

trait ParameterizedInitializerNameTypeMapping {

  def typeInitializedBy(termName: Term.Name): Option[Type.Ref]
}

object ParameterizedInitializerNameTypeMapping extends ParameterizedInitializerNameTypeMapping {

  private val InitializerNameToType = Map(
    Term.Name(TermNameValues.ScalaRange) -> TypeSelects.ScalaRange,
    Term.Name(TermNameValues.ScalaOption) -> TypeSelects.ScalaOption,
    Term.Name(TermNameValues.ScalaSome) -> TypeSelects.ScalaSome,
    Term.Name(TermNameValues.ScalaRight) -> TypeSelects.ScalaRight,
    Term.Name(TermNameValues.ScalaLeft) -> TypeSelects.ScalaLeft,
    Term.Name(TermNameValues.Try) -> TypeSelects.ScalaTry,
    Term.Name(TermNameValues.ScalaSuccess) -> TypeSelects.ScalaSuccess,
    Term.Name(TermNameValues.ScalaFailure) -> TypeSelects.ScalaFailure,
    Term.Name(TermNameValues.Future) -> TypeSelects.ScalaFuture,
    Term.Name(TermNameValues.Stream) -> TypeSelects.ScalaStream,
    Term.Name(TermNameValues.ScalaArray) -> TypeSelects.ScalaArray,
    Term.Name(TermNameValues.List) -> TypeSelects.ScalaList,
    Term.Name(TermNameValues.ScalaVector) -> TypeSelects.ScalaVector,
    Term.Name(TermNameValues.Seq) -> TypeSelects.ScalaSeq,
    Term.Name(TermNameValues.Set) -> TypeSelects.ScalaSet,
    Term.Name(TermNameValues.Map) -> TypeSelects.ScalaMap
  )
  // TODO add more collection initializers and durations

  val x: Some[Int] = Some(3)

  def typeInitializedBy(termName: Term.Name): Option[Type.Ref] =
    InitializerNameToType
      .find { case(aTermName, _) => aTermName.structure == termName.structure }
      .map { case (_, typeName) => typeName }
}
