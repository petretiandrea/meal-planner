package io.github.petretiandrea.mealplanner.impl

import io.github.petretiandrea.mealplanner.domain.Food
import io.github.petretiandrea.mealplanner.domain.FoodRepository
import java.io.File
import java.util.logging.Logger

class CsvFoodRepository(
    private val sourceFile: File
) : FoodRepository {

    companion object {
        private val logger = Logger.getLogger(CsvFoodRepository::class.java.name)
        const val DEFAULT_FOOD_WEIGHT = 100.0 // grams
    }

    private var foodCache: List<Food> = emptyList()

    override fun getFoodByIndex(index: Int): Food? = loadAll().let { foodCache.getOrNull(index) }

    override fun getFoodCount(): Int = loadAll().let { foodCache.size }

    override fun getAll(): List<Food> = loadAll().let { foodCache }

    private fun loadAll() {
        if (foodCache.isEmpty()) {
            sourceFile.bufferedReader()
                .lines()
                .skip(1)
                .map(this::mapLineToFood)
                .forEach { it?.let { foodCache += it } }
            logger.info("Loaded ${foodCache.size} foods")
        }
    }

    private fun mapLineToFood(line: String): Food? {
        val pieces = line.split(';', ignoreCase = true, limit = 4)
        if (pieces.size == 4) {
            return Food(
                pieces[0],
                pieces[1].toDouble(),
                pieces[2].toDouble(),
                pieces[3].toDouble(),
                DEFAULT_FOOD_WEIGHT // by default 100g assumed
            )
        }
        return null
    }
}