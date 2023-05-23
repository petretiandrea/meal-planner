package io.github.petretiandrea.mealplanner.domain

data class Food(
    val name: String,
    val carbs: Double,
    val fats: Double,
    val proteins: Double,
    val grams: Double
) {
    companion object {
        const val KCAL_PROTEIN_PER_GRAM = 4
        const val KCAL_CARB_PER_GRAM = 4
        const val KCAL_FAT_PER_GRAM = 9
    }

    val kCal: Double
        get() = carbs * KCAL_CARB_PER_GRAM + fats * KCAL_FAT_PER_GRAM + proteins * KCAL_PROTEIN_PER_GRAM;

    fun ofGrams(grams: Double): Food {
        val carbsRatio = carbs / this.grams
        val fatsRatio = fats / this.grams
        val proteinsRatio = proteins / this.grams

        return Food(
            name,
            grams * carbsRatio,
            grams * fatsRatio,
            grams * proteinsRatio,
            grams
        )
    }
}
