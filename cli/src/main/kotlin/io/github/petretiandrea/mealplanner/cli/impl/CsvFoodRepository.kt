package io.github.petretiandrea.mealplanner.cli.impl

import io.github.petretiandrea.mealplanner.domain.*
import java.io.File
import java.util.logging.Logger

class CsvFoodRepository(
    private val sourceFile: File
) : FoodDataset {

    companion object {
        private val logger = Logger.getLogger(CsvFoodRepository::class.java.name)
        const val DEFAULT_FOOD_WEIGHT = 100.0 // grams
    }

    private var foodCache: List<FoodWithConstraint> = emptyList()

    override fun get(index: Int): Food? = loadAll().let { foodCache.getOrNull(index)?.food }

    override fun getConstraint(index: Int): FoodConstraint? = loadAll().let { foodCache.getOrNull(index)?.constraint }

    override fun getWithConstraint(index: Int): FoodWithConstraint? = loadAll().let { foodCache.getOrNull(index) }

    override fun getAll(): List<Food> = loadAll().let { foodCache.map { it.food } }

    override val size: Int = loadAll().let { foodCache.size }

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

    private fun mapLineToFood(line: String): FoodWithConstraint? {
        val pieces = line.split(';', ignoreCase = true)
        if (pieces.size >= 4) {
            val fixedWeight = if (pieces.size >= 5) FixedWeight(pieces[4].toDouble()) else null
            val food = Food(
                pieces[0],
                pieces[1].toDouble(),
                pieces[2].toDouble(),
                pieces[3].toDouble(),
                DEFAULT_FOOD_WEIGHT // by default 100g assumed
            )
            return FoodWithConstraint(food, fixedWeight)
        }
        return null
    }
}