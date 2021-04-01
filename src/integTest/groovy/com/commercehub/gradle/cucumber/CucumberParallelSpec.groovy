package com.commercehub.gradle.cucumber

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

@SuppressWarnings('DuplicateStringLiteral')
class CucumberParallelSpec extends IntegrationSpec {

    @Override
    protected List<String> calculateArguments(String... args) {
        def newArgs = ['--warning-mode', 'all']
        if (args) {
            newArgs.addAll(args)
        }
        super.calculateArguments(newArgs as String[])
    }

    def setup() {
        copyResources('teststeps/TestSteps.groovy', 'src/cucumberTest/groovy/cucumber/steps/TestSteps.groovy')
        buildFile << '''
            apply plugin: 'groovy'
            apply plugin: 'com.patdouble.cucumber-jvm'

            addCucumberSuite 'cucumberTest'

            cucumber {
                tags = ["@test", "@happypath", "~@ignore"]
                maxParallelForks = 2
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                implementation 'org.codehaus.groovy:groovy-all:2.5.8'
                implementation 'io.cucumber:cucumber-java:4.8.0'
            }

            cucumberTest {
                systemProperty 'foo', 'bar'
                junitReport true
            }
        '''.stripIndent()
    }

    def testHappyPath() {
        given:
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath1.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath2.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath3.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('cucumberTest')

        then:
        result.wasExecuted(':cucumberTest')
    }

    def testFailingBackgroundStep() {
        given:
        copyResources('testfeatures/failing-background-test.feature',
                'src/cucumberTest/resources/features/failing-background-test.feature')
        copyResources('testfeatures/failing-background-test.feature',
                'src/cucumberTest/resources/features/failing-background-test1.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath1.feature')

        when:
        ExecutionResult result = runTasksWithFailure('cucumberTest')

        then:
        fileExists('build/reports/cucumberTest/cucumber-html-reports/overview-failures.html')
        result.standardError.contains("build/reports/cucumberTest/cucumber-html-reports/overview-failures.html")
    }

    def testSysProps() {
        given:
        copyResources('testfeatures/check-sysprop.feature',
                'src/cucumberTest/resources/features/check-sysprop.feature')
        copyResources('testfeatures/check-sysprop.feature',
                'src/cucumberTest/resources/features/check-sysprop1.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath1.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath2.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('cucumberTest')

        then:
        result.wasExecuted(':cucumberTest')
    }

    def ignoreScenario() {
        given:
        copyResources('testfeatures/ignored-test.feature', 'src/cucumberTest/resources/features/ignored-test.feature')
        copyResources('testfeatures/ignored-test.feature', 'src/cucumberTest/resources/features/ignored-test1.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath.feature')
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath1.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('cucumberTest')

        then:
        result.wasExecuted(':cucumberTest')
    }

    def testReportsDirectoryCreated() {
        given:
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('cucumberTest')

        then:
        fileExists('build/reports/cucumberTest/cucumber-html-reports/overview-features.html')
        fileExists('build/reports/cucumberTest/cucumber-html-reports/overview-failures.html')
        fileExists('build/reports/cucumberTest/cucumber-html-reports/overview-steps.html')
        fileExists('build/reports/cucumberTest/cucumber-html-reports/overview-tags.html')
    }

    def testJunitReport() {
        given:
        copyResources('testfeatures/happypath.feature', 'src/cucumberTest/resources/features/happypath.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('cucumberTest')

        then:
        fileExists('build/test-results/cucumber/cucumberTest/features.happypath.feature.json')
        fileExists('build/test-results/cucumber/cucumberTest/features.happypath.feature.xml')
    }

}
