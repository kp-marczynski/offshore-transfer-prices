import kotlin.system.measureTimeMillis

val RANGE: IntRange = 100..1000
val RANGE_LAST: IntRange = -100..1000
const val RANGE_STEP = 10
const val RANGE_DIVIDER = 1000
const val PROFIT_THRESHOLD = 500
val countries = listOf(
    Country("A", listOf(80, 140, 90, 80), 520, 0.3),
    Country("B", listOf(220, 60, 100, 230), 600, 0.05),
    Country("C", listOf(100, 220, 60, 150), 640, 0.45),
    Country("D", listOf(150, 90, 300, 40), 400, 0.2)
)

var bestProfit = 0.0
var bestCountriesWithMargins: List<Pair<Country, Double>>? = null
var iterations = 0

fun main(args: Array<String>) {
    val elapsed = measureTimeMillis {
        permutations(countries).forEach { iterateWithMargins(it) }
    }.toDouble() / 1000
    println("Time: $elapsed")
    println("Iterations: $iterations")
    if (bestCountriesWithMargins != null) {
        println(
            toString(bestCountriesWithMargins!!) + ", tax: " +
                    "%.3f".format(tax(bestCountriesWithMargins!!)) +
                    ", profit: " + "%.3f".format(bestProfit)
        )
    }
}

fun iterateWithMargins(countries: List<Country>, margins: List<Double> = listOf()) {
    if (margins.size == countries.size - 1) {
        val secondToLastMargin =
            (((countries.last().price.toDouble() / (1 + margins.last())) - countries.last().steps.last()) / (cost(
                countries.subList(0, countries.size - 2).zip(margins.subList(0, margins.lastIndex))
            ) + countries[countries.size - 2].steps[countries.size - 2])) - 1
        if (secondToLastMargin >= 0.1) {
            iterations++
            val countriesWithMargins =
                countries.zip(margins.subList(0, margins.lastIndex) + secondToLastMargin + margins.last())
            val profit = profit(countriesWithMargins)
            if (profit > bestProfit) {
                bestProfit = profit
                bestCountriesWithMargins = countriesWithMargins
            }
            if (profit > PROFIT_THRESHOLD) {
                println(
                    toString(countriesWithMargins) + ", podatek: " +
                            "%.3f".format(tax(countriesWithMargins)) +
                            ", zysk: " + "%.3f".format(profit)
                )
            }
        }
    } else {
        for (m in (if (margins.size == countries.size - 2) RANGE_LAST else RANGE) step RANGE_STEP) {
            iterateWithMargins(countries, margins + m.toDouble() / RANGE_DIVIDER)
        }
    }
}

fun toString(countriesWithMargins: List<Pair<Country, Double>>): String {
    return countriesWithMargins.joinToString { it.first.name + " " + "%.2f".format(it.second * 100) }
}

fun permutations(countries: List<Country>): List<List<Country>> {
    return if (countries.size == 1) {
        listOf(listOf(countries[0]))
    } else {
        countries.flatMap { country -> permutations(countries.filter { it != country }).map { it + country } }
    }
}

fun tax(countriesWithMargins: List<Pair<Country, Double>>): Double {
    var index = 0
    var result = 0.0
    while (index < countriesWithMargins.size) {
        val taxPart = ((cost(
            countriesWithMargins.subList(0, index)
        ) + countriesWithMargins[index].first.steps[index]) * countriesWithMargins[index].second * countriesWithMargins[index].first.cit)
        if (taxPart > 0) {
            result += taxPart
        }
        index++
    }
    return result
}

fun cost(countriesWithMargins: List<Pair<Country, Double>>): Double {
    var index = 0
    var result = 0.0
    while (index < countriesWithMargins.size) {
        result = (result + countriesWithMargins[index].first.steps[index]) * (1 + countriesWithMargins[index].second)
        index++
    }
    return result
}

fun profit(countriesWithMargins: List<Pair<Country, Double>>): Double {
    var result = countriesWithMargins.last().first.price
    var index = 0
    while (index < countriesWithMargins.size) {
        result -= countriesWithMargins[index].first.steps[index]
        index++
    }
    return result - tax(countriesWithMargins)
}

data class Country(
    val name: String,
    val steps: List<Int>,
    val price: Int,
    val cit: Double
)