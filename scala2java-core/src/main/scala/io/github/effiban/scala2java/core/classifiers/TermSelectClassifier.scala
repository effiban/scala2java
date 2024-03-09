package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.TermSelects._
import io.github.effiban.scala2java.core.entities.TreeElemSet

import scala.meta.Term

trait TermSelectClassifier {
  def isJavaStreamLike(termSelect: Term.Select): Boolean

  def isJavaListLike(termSelect: Term.Select): Boolean

  def isJavaSetLike(termSelect: Term.Select): Boolean

  def isJavaMapLike(termSelect: Term.Select): Boolean

  def hasApplyMethod(termSelect: Term.Select): Boolean

  def hasEmptyMethod(termSelect: Term.Select): Boolean

  def supportsNoArgInvocation(termSelect: Term.Select): Boolean
}

object TermSelectClassifier extends TermSelectClassifier {

  final val JavaStreamLike: Set[Term.Select] = Set(
    ScalaLazyList,
    ScalaStream,
  )

  final val JavaListLike: Set[Term.Select] = Set(
    ScalaSeq,
    ScalaLinearSeq,
    ScalaIndexedSeq,
    ScalaArraySeq,
    ScalaList,
    ScalaVector
  )

  final val JavaSetLike: Set[Term.Select] = Set(
    ScalaSet,
    ScalaHashSet
  )

  final val JavaMapLike: Set[Term.Select] = Set(
    ScalaMap,
    ScalaHashMap
  )

  final val IterableLike: Set[Term.Select] =
    JavaStreamLike ++
      JavaListLike ++
      JavaSetLike ++
      Set(ScalaSortedSet, ScalaTreeSet, ScalaListSet) ++
      JavaMapLike ++
      Set(ScalaSortedMap, ScalaTreeMap, ScalaListMap)


  final val ObjectsWithApplyMethod = Set(
    ScalaArray,
    ScalaRange,
    ScalaOption,
    ScalaSome,
    ScalaRight,
    ScalaLeft,
    ScalaTry,
    ScalaSuccess,
    ScalaFailure,
    ScalaFuture
  ) ++ IterableLike

  final val ObjectsWithEmptyMethod = Set(
    ScalaArray,
    ScalaOption
  ) ++ IterableLike

  // TODO - add more
  final val SupportNoArgInvocations = Set(
    ScalaPrintln
  )

  override def isJavaStreamLike(termSelect: Term.Select): Boolean = TreeElemSet.contains(JavaStreamLike, termSelect)

  override def isJavaListLike(termSelect: Term.Select): Boolean = TreeElemSet.contains(JavaListLike, termSelect)

  override def isJavaSetLike(termSelect: Term.Select): Boolean = TreeElemSet.contains(JavaSetLike, termSelect)

  override def isJavaMapLike(termSelect: Term.Select): Boolean = TreeElemSet.contains(JavaMapLike, termSelect)

  override def hasApplyMethod(termSelect: Term.Select): Boolean = TreeElemSet.contains(ObjectsWithApplyMethod, termSelect)

  override def hasEmptyMethod(termSelect: Term.Select): Boolean = TreeElemSet.contains(ObjectsWithEmptyMethod, termSelect)

  override def supportsNoArgInvocation(termSelect: Term.Select): Boolean = TreeElemSet.contains(SupportNoArgInvocations, termSelect)
}
