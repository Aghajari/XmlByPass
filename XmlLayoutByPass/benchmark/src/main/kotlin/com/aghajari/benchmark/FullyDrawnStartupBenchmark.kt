package com.aghajari.benchmark

import android.annotation.SuppressLint
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@LargeTest
@RunWith(Parameterized::class)
class FullyDrawnStartupBenchmark(
    private val type: String,
    private val startupMode: StartupMode,
) {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @SuppressLint("NewApi")
    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.aghajari.content",
        metrics = listOf(
            StartupTimingMetric(),
            TraceSectionMetric("LayoutInflation"),
            MemoryUsageMetric(mode = MemoryUsageMetric.Mode.Last),
        ),
        startupMode = startupMode,
        compilationMode = CompilationMode.None(),
        iterations = ITERATIONS,
    ) {
        startActivityAndWait {
            it.putExtra("benchmark_type", type)
        }
    }

    companion object {

        @Parameterized.Parameters(name = "benchmarkType={0}_startupMode={1}")
        @JvmStatic
        fun parameters(): List<Array<Any>> {
            val types = listOf("COMPOSE", "NOTHING")
            val startupModes = listOf(StartupMode.COLD)

            return types.flatMap { type ->
                startupModes.map { mode ->
                    arrayOf(type, mode)
                }
            }
        }

        private const val ITERATIONS = 10
    }
}