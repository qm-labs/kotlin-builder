package tech.qmachines.builder.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec
import tech.qmachines.builder.annotation.WithBuilder
import tech.qmachines.builder.processor.generator.BuilderGenerator
import tech.qmachines.builder.processor.model.Model

class BuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {


    override fun process(resolver: Resolver): List<KSAnnotated> {
        val withBuilders = getAllWithBuilderClasses(resolver)
        withBuilders.forEach { theClass ->
            genFile(theClass)?.let { writeFile(codeGenerator, it) }
        }
        return emptyList()
    }

    private fun getAllWithBuilderClasses(resolver: Resolver): Set<KSClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(WithBuilder::class.qualifiedName.orEmpty())
            .filterIsInstance<KSClassDeclaration>()
            .toSet()
    }

    private fun genFile(withBuilderClass: KSClassDeclaration): FileSpec? {
        val model = Model(withBuilderClass)

        if (!model.validate(logger))
            return null

        return BuilderGenerator.generate(model)
    }

    private fun writeFile(codeGenerator: CodeGenerator, fileSpec: FileSpec) {
        codeGenerator.createNewFile(
            dependencies = Dependencies(true),
            packageName = fileSpec.packageName,
            fileName = fileSpec.name
        ).use {
            it.writer().use(fileSpec::writeTo)
        }
    }

}