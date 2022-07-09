package effiban.scala2java.traversers

import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.transformers._
import effiban.scala2java.writers.JavaWriter

class ScalaTreeTraversers(implicit javaWriter: JavaWriter) {

  object AlternativeTraverser extends AlternativeTraverserImpl(PatTraverser)

  object AnnotListTraverser extends AnnotListTraverserImpl(AnnotTraverser)

  object AnnotTraverser extends AnnotTraverserImpl(InitTraverser)

  object AnonymousFunctionTraverser extends AnonymousFunctionTraverserImpl(TermFunctionTraverser)

  object ApplyTypeTraverser extends ApplyTypeTraverserImpl(TypeTraverser, TermTraverser, TypeListTraverser)

  object ApplyUnaryTraverser extends ApplyUnaryTraverserImpl(TermNameTraverser, TermTraverser)

  object ArgumentListTraverser extends ArgumentListTraverserImpl

  object AscribeTraverser extends AscribeTraverserImpl(TypeTraverser, TermTraverser)

  object AssignTraverser extends AssignTraverserImpl(TermTraverser)

  object BindTraverser extends BindTraverserImpl(PatTraverser)

  object BlockTraverser extends BlockTraverserImpl(
    InitTraverser,
    IfTraverser,
    WhileTraverser,
    ReturnTraverser,
    StatTraverser
  )

  object CaseClassTraverser extends CaseClassTraverserImpl(
    AnnotListTraverser,
    TypeParamListTraverser,
    TermParamListTraverser,
    TemplateTraverser,
    JavaModifiersResolver
  )

  object CaseTraverser extends CaseTraverserImpl(PatTraverser, TermTraverser)

  object CatchHandlerTraverser extends CatchHandlerTraverserImpl(TermParamListTraverser, BlockTraverser)

  object ClassTraverser extends ClassTraverserImpl(CaseClassTraverser, RegularClassTraverser)

  object CtorPrimaryTraverser extends CtorPrimaryTraverserImpl(CtorPrimaryTransformer, DefnDefTraverser)

  object CtorSecondaryTraverser extends CtorSecondaryTraverserImpl(CtorSecondaryTransformer, DefnDefTraverser)

  object DeclDefTraverser extends DeclDefTraverserImpl(
    AnnotListTraverser,
    TypeTraverser,
    TermNameTraverser,
    TermParamListTraverser,
    JavaModifiersResolver
  )

  object DeclTraverser extends DeclTraverserImpl(
    DeclValTraverser,
    DeclVarTraverser,
    DeclDefTraverser,
    DeclTypeTraverser)

  object DeclTypeTraverser extends DeclTypeTraverserImpl(TypeParamListTraverser, JavaModifiersResolver)

  object DeclValTraverser extends DeclValTraverserImpl(
    AnnotListTraverser,
    TypeTraverser,
    PatListTraverser,
    JavaModifiersResolver
  )

  object DeclVarTraverser extends DeclVarTraverserImpl(
    AnnotListTraverser,
    TypeTraverser,
    PatListTraverser,
    JavaModifiersResolver
  )

  object DefnDefTraverser extends DefnDefTraverserImpl(
    AnnotListTraverser,
    TermNameTraverser,
    TypeTraverser,
    TermParamListTraverser,
    BlockTraverser,
    JavaModifiersResolver
  )

  object DefnTraverser extends DefnTraverserImpl(
    DefnValTraverser,
    DefnVarTraverser,
    DefnDefTraverser,
    DefnTypeTraverser,
    ClassTraverser,
    TraitTraverser,
    ObjectTraverser
  )

  object DefnTypeTraverser extends DefnTypeTraverserImpl(
    TypeParamListTraverser,
    TypeTraverser,
    JavaModifiersResolver
  )

  object DefnValTraverser extends DefnValTraverserImpl(
    AnnotListTraverser,
    TypeTraverser,
    PatListTraverser,
    TermTraverser,
    JavaModifiersResolver
  )

  object DefnVarTraverser extends DefnVarTraverserImpl(
    AnnotListTraverser,
    TypeTraverser,
    PatListTraverser,
    TermTraverser,
    JavaModifiersResolver
  )

  object DoTraverser extends DoTraverserImpl(TermTraverser, BlockTraverser)

  object EtaTraverser extends EtaTraverserImpl(TermTraverser)

  object FinallyTraverser extends FinallyTraverserImpl(BlockTraverser)

  object ForTraverser extends ForTraverserImpl(ForVariantTraverser)

  object ForVariantTraverser extends ForVariantTraverserImpl(TermTraverser)

  object ForYieldTraverser extends ForYieldTraverserImpl(ForVariantTraverser)

  object IfTraverser extends IfTraverserImpl(TermTraverser, BlockTraverser)

  object ImporteeTraverser extends ImporteeTraverserImpl(NameTraverser)

  object ImporterTraverser extends ImporterTraverserImpl(TermRefTraverser, ImporteeTraverser)

  object ImportTraverser extends ImportTraverserImpl(ImporterTraverser)

  object InitListTraverser extends InitListTraverserImpl(ArgumentListTraverser, InitTraverser)

  object InitTraverser extends InitTraverserImpl(TypeTraverser, TermListTraverser)

  object LitTraverser extends LitTraverserImpl()

  object NameIndeterminateTraverser extends NameIndeterminateTraverserImpl()

  object NameTraverser extends NameTraverserImpl(
    NameAnonymousTraverser,
    NameIndeterminateTraverser,
    TermNameTraverser,
    TypeNameTraverser
  )

  object NewAnonymousTraverser extends NewAnonymousTraverserImpl(TemplateTraverser)

  object NewTraverser extends NewTraverserImpl(InitTraverser)

  object ObjectTraverser extends ObjectTraverserImpl(
    AnnotListTraverser,
    TemplateTraverser,
    JavaModifiersResolver)

  object PartialFunctionTraverser extends PartialFunctionTraverserImpl(TermFunctionTraverser)

  object PatExtractInfixTraverser extends PatExtractInfixTraverserImpl(PatExtractTraverser)

  object PatExtractTraverser extends PatExtractTraverserImpl()

  object PatInterpolateTraverser extends PatInterpolateTraverserImpl()

  object PatListTraverser extends PatListTraverserImpl(ArgumentListTraverser, PatTraverser)

  object PatSeqWildcardTraverser extends PatSeqWildcardTraverserImpl()

  object PatTraverser extends PatTraverserImpl(
    LitTraverser,
    TermNameTraverser,
    PatWildcardTraverser,
    PatSeqWildcardTraverser,
    PatVarTraverser,
    BindTraverser,
    AlternativeTraverser,
    PatTupleTraverser,
    PatExtractTraverser,
    PatExtractInfixTraverser,
    PatInterpolateTraverser,
    PatTypedTraverser
  )

  object PatTupleTraverser extends PatTupleTraverserImpl()

  object PatTypedTraverser extends PatTypedTraverserImpl(TypeTraverser, PatTraverser)

  object PatVarTraverser extends PatVarTraverserImpl(TermNameTraverser)

  object PatWildcardTraverser extends PatWildcardTraverserImpl()

  object PkgTraverser extends PkgTraverserImpl(TermRefTraverser, StatTraverser)

  object RegularClassTraverser extends RegularClassTraverserImpl(
    AnnotListTraverser,
    TypeParamListTraverser,
    TemplateTraverser,
    ParamToDeclValTransformer,
    JavaModifiersResolver
  )

  object ReturnTraverser extends ReturnTraverserImpl(TermTraverser)

  object SelfTraverser extends SelfTraverserImpl

  object SourceTraverser extends SourceTraverserImpl(StatTraverser)

  object StatTraverser extends StatTraverserImpl(
    TermTraverser,
    ImportTraverser,
    PkgTraverser,
    DefnTraverser,
    DeclTraverser
  )

  object SuperTraverser extends SuperTraverserImpl(NameTraverser)

  object TemplateTraverser extends TemplateTraverserImpl(
    InitListTraverser,
    SelfTraverser,
    StatTraverser,
    CtorPrimaryTraverser,
    CtorSecondaryTraverser,
    JavaTemplateChildOrdering
  )

  object TermAnnotateTraverser extends TermAnnotateTraverserImpl(AnnotListTraverser, TermTraverser)

  object TermApplyInfixTraverser extends TermApplyInfixTraverserImpl(
    TermTraverser,
    TermNameTraverser,
    TermListTraverser
  )

  object TermApplyTraverser extends TermApplyTraverserImpl(TermTraverser, TermListTraverser)

  object TermFunctionTraverser extends TermFunctionTraverserImpl(
    TermParamTraverser,
    TermParamListTraverser,
    TermTraverser
  )

  object TermInterpolateTraverser extends TermInterpolateTraverserImpl(TermInterpolateTransformer, TermApplyTraverser)

  object TermListTraverser extends TermListTraverserImpl(ArgumentListTraverser, TermTraverser)

  object TermMatchTraverser extends TermMatchTraverserImpl(TermTraverser, CaseTraverser)

  object TermNameTraverser extends TermNameTraverserImpl

  object TermParamListTraverser extends TermParamListTraverserImpl(ArgumentListTraverser, TermParamTraverser)

  object TermParamTraverser extends TermParamTraverserImpl(
    AnnotListTraverser,
    TypeTraverser,
    NameTraverser,
    JavaModifiersResolver
  )

  object TermPlaceholderTraverser extends TermPlaceholderTraverserImpl

  object TermRefTraverser extends TermRefTraverserImpl(
    ThisTraverser,
    SuperTraverser,
    TermNameTraverser,
    TermSelectTraverser,
    ApplyUnaryTraverser
  )

  object TermRepeatedTraverser extends TermRepeatedTraverserImpl(TermTraverser)

  object TermSelectTraverser extends TermSelectTraverserImpl(TermTraverser, TermNameTraverser)

  object TermTraverser extends TermTraverserImpl(
    TermRefTraverser,
    TermApplyTraverser,
    ApplyTypeTraverser,
    TermApplyInfixTraverser,
    AssignTraverser,
    ReturnTraverser,
    ThrowTraverser,
    AscribeTraverser,
    TermAnnotateTraverser,
    TermTupleTraverser,
    BlockTraverser,
    IfTraverser,
    TermMatchTraverser,
    TryTraverser,
    TryWithHandlerTraverser,
    TermFunctionTraverser,
    PartialFunctionTraverser,
    AnonymousFunctionTraverser,
    WhileTraverser,
    DoTraverser,
    ForTraverser,
    ForYieldTraverser,
    NewTraverser,
    NewAnonymousTraverser,
    TermPlaceholderTraverser,
    EtaTraverser,
    TermRepeatedTraverser,
    TermInterpolateTraverser,
    LitTraverser
  )

  object TermTupleTraverser extends TermTupleTraverserImpl(TermListTraverser)

  object ThisTraverser extends ThisTraverserImpl(NameTraverser)

  object ThrowTraverser extends ThrowTraverserImpl(TermTraverser)

  object TraitTraverser extends TraitTraverserImpl(
    AnnotListTraverser,
    TypeParamListTraverser,
    TemplateTraverser,
    JavaModifiersResolver
  )

  object TryTraverser extends TryTraverserImpl(
    BlockTraverser,
    CatchHandlerTraverser,
    FinallyTraverser,
    PatToTermParamTransformer
  )

  object TryWithHandlerTraverser extends TryWithHandlerTraverserImpl(
    BlockTraverser,
    CatchHandlerTraverser,
    FinallyTraverser
  )

  object TypeAnnotateTraverser extends TypeAnnotateTraverserImpl(AnnotListTraverser, TypeTraverser)

  object TypeApplyInfixTraverser extends TypeApplyInfixTraverserImpl

  object TypeApplyTraverser extends TypeApplyTraverserImpl(TypeTraverser, TypeListTraverser)

  object TypeBoundsTraverser extends TypeBoundsTraverserImpl(TypeTraverser)

  object TypeByNameTraverser extends TypeByNameTraverserImpl(TypeApplyTraverser, TypeByNameToSupplierTypeTransformer)

  object TypeExistentialTraverser extends TypeExistentialTraverserImpl(TypeTraverser)

  object TypeFunctionTraverser extends TypeFunctionTraverserImpl(TypeApplyTraverser, ScalaToJavaFunctionTypeTransformer)

  object TypeLambdaTraverser extends TypeLambdaTraverserImpl

  object TypeListTraverser extends TypeListTraverserImpl(ArgumentListTraverser, TypeTraverser)

  object TypeNameTraverser extends TypeNameTraverserImpl(ScalaToJavaTypeNameTransformer)

  object TypeParamListTraverser extends TypeParamListTraverserImpl(ArgumentListTraverser, TypeParamTraverser)

  object TypeParamTraverser extends TypeParamTraverserImpl(
    NameTraverser,
    TypeParamListTraverser,
    TypeBoundsTraverser
  )

  object TypePlaceholderTraverser extends TypePlaceholderTraverserImpl(TypeBoundsTraverser)

  object TypeProjectTraverser extends TypeProjectTraverserImpl(TypeTraverser, TypeNameTraverser)

  object TypeRefineTraverser extends TypeRefineTraverserImpl(TypeTraverser)

  object TypeRefTraverser extends TypeRefTraverserImpl(
    TypeNameTraverser,
    TypeSelectTraverser,
    TypeProjectTraverser,
    TypeSingletonTraverser
  )

  object TypeRepeatedTraverser extends TypeRepeatedTraverserImpl(TypeTraverser)

  object TypeSelectTraverser extends TypeSelectTraverserImpl(TermRefTraverser, TypeNameTraverser)

  object TypeSingletonTraverser extends TypeSingletonTraverserImpl(TermTraverser, TypeSingletonToTermTransformer)

  object TypeTraverser extends TypeTraverserImpl(
    TypeRefTraverser,
    TypeApplyTraverser,
    TypeApplyInfixTraverser,
    TypeFunctionTraverser,
    TypeTupleTraverser,
    TypeWithTraverser,
    TypeRefineTraverser,
    TypeExistentialTraverser,
    TypeAnnotateTraverser,
    TypeLambdaTraverser,
    TypePlaceholderTraverser,
    TypeByNameTraverser,
    TypeRepeatedTraverser,
    TypeVarTraverser
  )

  object TypeTupleTraverser extends TypeTupleTraverserImpl

  object TypeVarTraverser extends TypeVarTraverserImpl

  object TypeWithTraverser extends TypeWithTraverserImpl(TypeTraverser)

  object WhileTraverser extends WhileTraverserImpl(TermTraverser, BlockTraverser)
}
