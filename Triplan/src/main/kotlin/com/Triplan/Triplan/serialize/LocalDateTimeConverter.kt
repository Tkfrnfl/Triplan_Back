package com.Triplan.Triplan.serialize

import jakarta.persistence.AttributeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

class LocalDateTimeConverter():AttributeConverter<LocalDate, java.time.LocalDate> {
    override fun convertToDatabaseColumn(attribute: LocalDate?): java.time.LocalDate? {
        return attribute?.toJavaLocalDate()
    }

    override fun convertToEntityAttribute(dbData: java.time.LocalDate?): LocalDate {
        return LocalDate.parse(dbData.toString())
    }
}