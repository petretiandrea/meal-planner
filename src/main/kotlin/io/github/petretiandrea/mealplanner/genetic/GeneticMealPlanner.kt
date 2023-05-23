package io.github.petretiandrea.mealplanner.genetic

import io.github.petretiandrea.mealplanner.domain.Food
import io.github.petretiandrea.mealplanner.domain.Macro
import io.github.petretiandrea.mealplanner.domain.MealPlan
import io.github.petretiandrea.mealplanner.domain.MealPlanner
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import java.util.stream.IntStream
import java.util.stream.Stream


class GeneticMealPlanner(
    private val databaseFoods: List<Food>
) : MealPlanner {

    override fun generatePlan(targetMacro: Macro): Stream<MealPlan> {
        val factoryGenotype = Genotype.of(
            IntegerChromosome.of(0, 500, databaseFoods.size)
        )

        val engine = Engine.builder({ eval(it, targetMacro) }, factoryGenotype)
            .populationSize(1000)
            .maximizing()
            .build()

        val best = engine.stream()
            .limit(1000)
            .collect(EvolutionResult.toBestEvolutionResult())

        best.genotypes()
        return Stream.of(MealPlan(
            foodsFromGenotype(best.bestPhenotype().genotype()).toList().toSet()
        ))
    }

    private fun eval(
        genotype: Genotype<IntegerGene>,
        targetMacro: Macro
    ): Double {
        val macroPlan = foodsFromGenotype(genotype)
            .filter { it.grams >= 5 }
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