package com.aghajari.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.aghajari.xmlbypass.XmlByPass
import com.aghajari.xmlbypass.XmlLayout

@XmlByPass(
    layouts = [XmlLayout(layout = "*")]
)
@RunWith(AndroidJUnit4::class)
class HelloWorldBenchmarkTest {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    // Use ActivityScenarioRule when you're not specifically testing Compose.
    // It's more straightforward for traditional View-based activities.
    @get:Rule
    val activityRule = ActivityScenarioRule(BenchmarkActivity::class.java)

    @Test
    fun benchmarkXmlHelloWorld() {
        benchmarkRule.measureRepeated {
            runWithMeasurementDisabled {
                // Setup: recreate the activity before each measurement
                activityRule.scenario.recreate()
            }

            // Measure XML inflation
            activityRule.scenario.onActivity { activity ->
                activity.setContentView(R.layout.hello_world)
            }
        }
    }

    @Test
    fun benchmarkProgrammaticHelloWorld() {
        benchmarkRule.measureRepeated {
            runWithMeasurementDisabled {
                // Setup: recreate the activity before each measurement
                activityRule.scenario.recreate()
            }

            // Measure programmatic view creation
            activityRule.scenario.onActivity { activity ->
                // Assuming 'hello_world' is a function that returns a View
                activity.setContentView(hello_world(activity))
            }
        }
    }
}