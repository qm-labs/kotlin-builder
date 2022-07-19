package tech.qmachines.builder.processor.model

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation


internal fun KSAnnotated.getAnnotationOf(qualifiedName: String) = annotations.toList().filter {
    it.annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName
}

internal inline fun <reified T> KSAnnotated.getAnnotationOf() =
    getAnnotationOf(T::class.qualifiedName.orEmpty())


internal fun KSAnnotated.getInheritedAnnotationsOf(qualifiedName: String, typeAnnotationQualifiedName: String): List<KSAnnotation> =
    getAnnotationOf(qualifiedName) + annotations.flatMap { subAnnotation ->
        subAnnotation.annotationType.resolve().declaration.getAnnotationOf(typeAnnotationQualifiedName)
            .map { it.arguments[0].value as KSAnnotation }
    }

internal inline fun <reified T, reified S> KSAnnotated.getInheritedAnnotationsOf() =
    getInheritedAnnotationsOf(T::class.qualifiedName.orEmpty(), S::class.qualifiedName.orEmpty())

internal inline fun <reified T> KSAnnotation.getArgument(name: String) =
    arguments.first { it.name?.asString() == name }.value as T