package com.reborn.wasteless.data

//Enum classes for waste type & calc type
enum class WasteType(val displayName: String) {
    UNAVOIDABLE("Unavoidable"),
    AVOIDABLE("Avoidable"),
    FOOD_RELATED("Food-related");
}

enum class CalcType {GRAMS, PORTION}