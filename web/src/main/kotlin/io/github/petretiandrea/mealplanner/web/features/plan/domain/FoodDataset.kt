package io.github.petretiandrea.mealplanner.web.features.plan.domain

import io.github.petretiandrea.mealplanner.domain.FoodWithConstraint

sealed interface FoodDataset {

    data class Raw(val foods: List<FoodWithConstraint>): FoodDataset
    data class ByIds(val ids: List<String>): FoodDataset

    companion object {
        fun raw(foods: List<FoodWithConstraint>) = Raw(foods)
        fun lazy(ids: List<String>) = ByIds(ids)
    }
}