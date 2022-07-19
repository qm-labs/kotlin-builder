package tech.qmachines.builder.processor.generator


internal object Formatter {

    private val regEx = Regex("%(\\d+)?([a-zA-Z%])")

    fun format(str: String, property: String? = null): String{
        return regEx.replace(str){
            when(it.groupValues.getOrNull(2)){
                "p" -> property ?: error("${it.value} is not allowed here")
                else -> it.value
            }
        }

    }

}