package io.github.petretiandrea.mealplanner.web.features.foods

data class FoodUpdateDto(
    val name: String,
    val carbs: Double,
    val proteins: Double,
    val fats: Double
)
