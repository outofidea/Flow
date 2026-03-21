package org.rtyuio.flow.shared

enum class DialogTriggerType {
    ADD, EDIT, NONE
}


data class DialogTriggerInfo (val type: DialogTriggerType = DialogTriggerType.NONE, val idx: Int? = null)
