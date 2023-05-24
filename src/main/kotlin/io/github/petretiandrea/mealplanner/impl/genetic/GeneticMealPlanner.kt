package io.github.petretiandrea.mealplanner.impl.genetic

import io.github.petretiandrea.mealplanner.domain.Food
import io.github.petretiandrea.mealplanner.domain.Macro
import io.github.petretiandrea.mealplanner.domain.MealPlan
import io.github.petretiandrea.mealplanner.domain.MealPlanner
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.engine.Limits
import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.IntStream
import java.util.stream.Stream


class GeneticMealPlanner(
    private val databaseFoods: List<Food>
) : MealPlanner {

    companion object {
        private val logger = Logger.getLogger(GeneticMealPlanner::class.java.name)
        const val MAX_FOOD_GRAMS = 500
        const val POPULATION_SIZE = 1000
        const val GENERATIONS = 10_000L
        const val STEADY_GENERATIONS = 20
    }

    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun generatePlans(targetMacro: Macro): Stream<MealPlan> {
        val statistics = EvolutionStatistics.ofNumber<Double>()
        val factoryGenotype = Genotype.of(
            IntegerChromosome.of(0, MAX_FOOD_GRAMS, databaseFoods.size),
        )

        val engine = Engine.builder({ eval(it, targetMacro) }, factoryGenotype)
            .populationSize(POPULATION_SIZE)
            .maximizing()
            .executor(executor)
            .build()

        val results = engine.stream()
            .limit(Limits.bySteadyFitness(STEADY_GENERATIONS))
            .limit(GENERATIONS)
            .peek(statistics)
            .collect(EvolutionResult.toBestEvolutionResult())

        logger.log(Level.FINE, statistics.toString())

        return results.population().stream()
            .sorted(Optimize.MAXIMUM.descending())
            .map { MealPlan(foodsFromGenotype(it.genotype()).toList().toSet()) }
    }

    private fun eval(genotype: Genotype<IntegerGene>, targetMacro: Macro): Double =
        foodsFromGenotype(genotype)
            .collect(Macro.foodCollector())
            .similarity(targetMacro)


    private fun foodsFromGenotype(genotype: Genotype<IntegerGene>): Stream<Food> {
        val chromosome = genotype.chromosome()
        return IntStream.range(0, chromosome.length())
            .mapToObj { databaseFoods[it] to chromosome.get(it) }
            .map { it.first.withWeight(it.second.doubleValue()) }
    }
}