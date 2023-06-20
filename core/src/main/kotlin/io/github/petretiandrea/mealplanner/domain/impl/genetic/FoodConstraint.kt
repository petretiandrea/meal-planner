package io.github.petretiandrea.mealplanner.domain.impl.genetic

import io.github.petretiandrea.mealplanner.domain.FixedWeight
import io.github.petretiandrea.mealplanner.domain.FoodDataset
import io.github.petretiandrea.mealplanner.domain.RangeWeight
import io.jenetics.Genotype
import io.jenetics.IntegerChromosome
import io.jenetics.IntegerGene
import io.jenetics.Phenotype
import io.jenetics.engine.Constraint
import io.jenetics.util.ISeq

class FoodConstraint(
    private val dataset: FoodDataset,
    private val alleleRange: IntRange
) : Constraint<IntegerGene, Double> {

    override fun test(individual: Phenotype<IntegerGene, Double>): Boolean = individual.genotype()
        .chromosome()
        .`as`(IntegerChromosome::class.java)
        .toArray()
        .let { isValid(it) }

    override fun repair(individual: Phenotype<IntegerGene, Double>, generation: Long): Phenotype<IntegerGene, Double> {
        val toRepair = individual.genotype()
            .chromosome()
            .`as`(IntegerChromosome::class.java)
            .toArray()
        return newPhenotype(repair(toRepair), generation)
    }

    private fun isValid(values: IntArray): Boolean {
        return values
            .mapIndexed { index, i -> dataset.getConstraint(index)?.isValidGrams(i.toDouble()) ?: true }
            .all { it }
    }

    private fun repair(
        values: IntArray
    ): IntArray {
        fun repairGeneWithConstraint(index: Int, gene: Int): Int {
            return when (val constraint = dataset.getConstraint(index)) {
                is FixedWeight -> if (!constraint.isValidGrams(gene.toDouble())) constraint.grams.toInt() else gene
                is RangeWeight -> if (!constraint.isValidGrams(gene.toDouble())) IntegerGene.of( // use same distribution of jenetics
                    constraint.minGrams.toInt(),
                    constraint.maxGrams.toInt()
                ).allele() else gene
                else -> gene
            }
        }
        return values.mapIndexed { index, integerGene -> repairGeneWithConstraint(index, integerGene) }.toIntArray()
    }

    private fun newPhenotype(values: IntArray, generation: Long): Phenotype<IntegerGene, Double> {
        val genotype = Genotype.of(
            IntegerChromosome.of(ISeq.of(values.map { IntegerGene.of(it, alleleRange.first, alleleRange.last) }))
        )
        return Phenotype.of(genotype, generation)
    }
}