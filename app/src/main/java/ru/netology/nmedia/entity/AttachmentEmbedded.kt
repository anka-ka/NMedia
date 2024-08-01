package ru.netology.nmedia.entity

import ru.netology.nmedia.datatransferobjects.Attachment
import ru.netology.nmedia.datatransferobjects.AttachmentType

data class AttachmentEmbedded (
    val url: String,
    val type: AttachmentType,
){
    fun toDto()=Attachment(url, type)


    companion object{
        fun fromDto(dto:Attachment):AttachmentEmbedded= with(dto){
            AttachmentEmbedded(
                url, type
            )
        }
    }
}