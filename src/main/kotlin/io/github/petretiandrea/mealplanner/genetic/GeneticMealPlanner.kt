package io.github.petretiandrea.mealplanner.genetic

import io.github.petretiandrea.mealplanner.domain.Food
import io.github.petretiandrea.mealplanner.domain.Macro
import io.github.petretiandrea.mealplanner.domain.MealPlan
import io.github.petretiandrea.mealplanner.domain.MealPlanner
import io.jenetics.*
import io.jenetics.engine.Constraint
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.engine.Limits
import java.util.concurrent.Executors
import java.util.stream.IntStream
import java.util.stream.Stream


class GeneticMealPlanner(
    private val databaseFoods: List<Food>
) : MealPlanner {

    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    override fun generatePlan(targetMacro: Macro): Stream<MealPlan> {
        val statistics = EvolutionStatistics.ofNumber<Double>()
        val factoryGenotype = Genotype.of(
            IntegerChromosome.of(0, 500, databaseFoods.size),
        )

        val engine = Engine.builder({ eval(it, targetMacro) }, factoryGenotype)
            .populationSize(1000)
            .maximizing()
            .executor(executor)
            .build()

        val results = engine.stream()
            .limit(Limits.bySteadyFitness(20))
            .limit(10_000)
            .peek(statistics)
            .collect(EvolutionResult.toBestEvolutionResult())

        println(statistics)

        return results.population().stream()
            .sorted(Optimize.MAXIMUM.descending())
            .map { MealPlan(foodsFromGenotype(it.genotype()).toList().toSet()) }
    }

    private fun eval(
        genotype: Genotype<IntegerGene>,
        targetMacro: Macro
    ): Double {
        val macroPlan = foodsFromGenotype(genotype)
            .collect(Macro.foodCollector())

        return macroPlan.similarity(targetMacro)
    }



    private fun foodsFromGenotype(genotype: Genotype<IntegerGene>): Stream<Food> {
        val chromosome = genotype.chromosome()
        return IntStream.range(0, chromosome.length())
            .mapToObj { databaseFoods[it] to chromosome.get(it) }
            .map { it.first.ofGrams(it.second.doubleValue()) }
    }
}