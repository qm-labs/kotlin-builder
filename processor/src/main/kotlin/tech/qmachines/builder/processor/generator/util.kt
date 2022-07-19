package tech.qmachines.builder.processor.generator

import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName

internal fun KSDeclaration.toClassName(): ClassName {
    return ClassName(packageName.asString(), asNameList())
}

internal fun KSDeclaration.toParametrizedClassName(typeArguments: List<TypeName>): TypeName {
    return toClassName().toParametrizedClassName(typeArguments)
}

internal fun ClassName.toParametrizedClassName(typeArguments: List<TypeName>) =
    if (typeArguments.isEmpty()) this else this.parameterizedBy(typeArguments)

internal fun KSDeclaration.asNameList(): List<String> {
    val list = mutableListOf<String>()
    var definition: KSDeclaration? = this
    while (definition != null) {
        list.add(0, definition.simpleName.asString())
        if (definition is KSTypeParameter) {
            break
        }
        definition = definition.parentDeclaration
    }
    return list
}

internal fun KSTypeReference.toTypeName(): TypeName = resolve().toTypeName()

internal fun KSType.toTypeName(): TypeName {
    val className = declaration.toClassName()
    val parametrizedClassName =  if (arguments.isNotEmpty()){
        className.parameterizedBy(arguments.mapNotNull { it.type?.toTypeName() })
    } else className
    return parametrizedClassName.copy(nullable = isMarkedNullable)
}

internal fun KSTypeParameter.toTypeVariableName() = TypeVariableName(
    name.asString(),
    bounds.map { it.toTypeName() }.toList(),
    when (this.variance) {
        Variance.STAR,
        Variance.INVARIANT -> null
        Variance.COVARIANT -> KModifier.OUT
        Variance.CONTRAVARIANT -> KModifier.IN
    }
)