package io.github.petretiandrea.mealplanner.domain.impl.genetic

import io.github.petretiandrea.mealplanner.domain.*
import io.jenetics.Genotype
import io.jenetics.IntegerChromosome
import io.jenetics.IntegerGene
import io.jenetics.Optimize
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.engine.Limits
import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.IntStream
import java.util.stream.Stream

class GeneticMealPlanner : MealPlanner {

    companion object {
        private val logger = Logger.getLogger(GeneticMealPlanner::class.java.name)
        const val MAX_FOOD_GRAMS = 500
        const val POPULATION_SIZE = 1000
        const val GENERATIONS = 10_000L
        const val STEADY_GENERATIONS = 20
    }

    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun generatePlans(dataset: FoodDataset, targetMacro: Macro): Stream<MealPlan> {
        val statistics = EvolutionStatistics.ofNumber<Double>()
        val factoryGenotype = Genotype.of(
            IntegerChromosome.of(0, MAX_FOOD_GRAMS, dataset.size)
        )
        val constraint = FoodConstraint(dataset, IntRange(0, MAX_FOOD_GRAMS))

        val engine = Engine.builder({ eval(dataset, it, targetMacro) }, constraint.constrain(factoryGenotype))
            .constraint(constraint)
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
            .map { MealPlan(foodsFromGenotype(dataset, it.genotype()).toList().toSet()) }
    }

    private fun eval(dataset: FoodDataset, genotype: Genotype<IntegerGene>, targetMacro: Macro): Double =
        foodsFromGenotype(dataset, genotype)
            .collect(Macro.foodCollector())
            .similarity(targetMacro)


    private fun foodsFromGenotype(dataset: FoodDataset, genotype: Genotype<IntegerGene>): Stream<Food> {
        val chromosome = genotype.chromosome()
        return IntStream.range(0, chromosome.length())
            .mapToObj { dataset[it]!! to chromosome.get(it) }
            .map { it.first.withWeight(it.second.doubleValue()) }
    }
}