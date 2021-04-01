package com.commercehub.gradle.cucumber

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

/**
 * Created by jgelais on 11/19/15.
 */
@SuppressWarnings('DuplicateStringLiteral')
class CucumberIntegrationSpec extends IntegrationSpec {

    @Override
    protected List<String> calculateArguments(String... args) {
        def newArgs = ['--warning-mode', 'all']
        if (args) {
            newArgs.addAll(args)
        }
        super.calculateArguments(newArgs as String[])
    }

    def setup() {
        copyResources('teststeps/TestSteps.groovy', 'src/test/groovy/cucumber/steps/TestSteps.groovy')
        buildFile << '''
            apply plugin: 'groovy'
            apply plugin: 'com.patdouble.cucumber-jvm'

            addCucumberSuite 'test'

            cucumber {
                tags = ["@test", "@happypath", "~@ignore"]
            }
            repositories {
                mavenCentral()
            }

            dependencies {
                implementation 'org.codehaus.groovy:groovy-all:2.5.8'
                implementation 'io.cucumber:cucumber-java:4.8.0'
            }

            test {
                systemProperty 'foo', 'bar'
            }
        '''.stripIndent()
    }

    def testHappyPath() {
        given:
        copyResources('testfeatures/happypath.feature', 'src/test/resources/features/happypath.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('test')

        then:
        !result.wasUpToDate(':compileTestGroovy')
        result.wasExecuted(':test')
        new File(projectDir, 'build/reports/test/cucumber-html-reports').list().flatten()*.replace('\\', '/')
                .findResults { it.matches('report-feature_features-happypath-feature?\\.html') ? it : null } != null
    }

    def testFailingBackgroundStep() {
        given:
        copyResources('testfeatures/failing-background-test.feature',
                'src/test/resources/features/failing-background-test.feature')

        when:
        ExecutionResult result = runTasksWithFailure('test')

        then:
        fileExists('build/reports/test/cucumber-html-reports/overview-failures.html')
        result.standardError.contains("build/reports/test/cucumber-html-reports/overview-failures.html")
    }

    def testSysProps() {
        given:
        copyResources('testfeatures/check-sysprop.feature', 'src/test/resources/features/check-sysprop.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('test')

        then:
        result.wasExecuted(':test')
    }

    def ignoreScenario() {
        given:
        copyResources('testfeatures/ignored-test.feature', 'src/test/resources/features/ignored-test.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('test')

        then:
        result.wasExecuted(':test')
    }

    def testReportsDirectoryCreated() {
        given:
        copyResources('testfeatures/happypath.feature', 'src/test/resources/features/happypath.feature')

        when:
        ExecutionResult result = runTasksSuccessfully('test')

        then:
        fileExists('build/reports/test/cucumber-html-reports/overview-features.html')
        fileExists('build/reports/test/cucumber-html-reports/overview-failures.html')
        fileExists('build/reports/test/cucumber-html-reports/overview-steps.html')
        fileExists('build/reports/test/cucumber-html-reports/overview-tags.html')
    }

}
