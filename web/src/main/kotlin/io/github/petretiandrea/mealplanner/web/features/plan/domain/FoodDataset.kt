package io.github.petretiandrea.mealplanner.web.features.plan.domain

import io.github.petretiandrea.mealplanner.domain.Food

sealed interface FoodDataset {

    data class Raw(val foods: List<Food>): FoodDataset
    data class ByIds(val ids: List<String>): FoodDataset

    companion object {
        fun raw(foods: List<Food>) = Raw(foods)
        fun lazy(ids: List<String>) = ByIds(ids)
    }
}