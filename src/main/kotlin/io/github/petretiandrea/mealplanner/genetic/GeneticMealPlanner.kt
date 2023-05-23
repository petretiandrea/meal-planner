package io.github.petretiandrea.mealplanner.genetic

import io.github.petretiandrea.mealplanner.domain.Food
import io.github.petretiandrea.mealplanner.domain.MealPlan
import io.github.petretiandrea.mealplanner.domain.MealPlanner
import io.jenetics.*
import io.jenetics.engine.Constraint
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.math.abs


class GeneticMealPlanner(
    private val databaseFoods: List<Food>
) : MealPlanner {

    override fun generatePlan(
        targetCarbs: Double,
        targetProteins: Double,
        targetFats: Double
    ): Stream<MealPlan> {
        val targetMacros = Macros(targetCarbs, targetProteins, targetFats)
        val factoryGenotype = Genotype.of(
            IntegerChromosome.of(0, 500, databaseFoods.size)
        )

        val engine = Engine.builder({ eval(it, targetMacros) }, factoryGenotype)
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
        targetMacros: Macros
    ): Double {
        val macroPlan = foodsFromGenotype(genotype)
            .filter { it.grams >= 5 }
            .collect(Macros.foodCollector())

        return Macros.fitness()(macroPlan, targetMacros)
    }

    data class Macros(
        val carbs: Double = 0.0,
        val proteins: Double = 0.0,
        val fats: Double = 0.0
    ) {

        companion object {
            fun fitness(): (Macros, Macros) -> Double {
                return { current, target ->
                    val deltaCarbs = abs(target.carbs - current.carbs)
                    val deltaProteins = abs(target.proteins - current.proteins)
                    val deltaFats = abs(target.fats - current.fats)
                    -(deltaCarbs + deltaProteins + deltaFats)
                }
            }

            fun foodCollector(): Collector<Food, *, Macros> {
                return Collectors.reducing(
                    Macros(),
                    { food -> Macros(food.carbs, food.proteins, food.fats) },
                    { acc, macro ->
                        Macros(
                            acc.carbs + macro.carbs,
                            acc.proteins + macro.proteins,
                            acc.fats + macro.fats,
                        )
                    }
                )
            }
        }
    }

    private fun foodsFromGenotype(genotype: Genotype<IntegerGene>): Stream<Food> {
        val chromosome = genotype.chromosome()
        return IntStream.range(0, chromosome.length())
            .mapToObj { databaseFoods[it] to chromosome.get(it) }
            .map { it.first.ofGrams(it.second.doubleValue()) }
    }
}