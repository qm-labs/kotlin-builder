package tech.qmachines.builder.annotation

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class MutablePropertyType(val mutableProperty: MutableProperty)