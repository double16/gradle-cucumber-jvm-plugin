# Changelog

All notable changes to this project will be documented in this file.

## [0.20] - 2021-04-01

### Changed

- cucumber-reporting to 5.5.2
- other dependency updates
- move away from bintray

## [0.19] - 2019-12-10

### Changed

- Shadow Jar replaced with dependencies

### Fixed

- NoClassDefFoundError: groovyx/gpars/GParsPool

## [0.18] - 2019-12-03

### Added

- Task added to the `verification` group, so it is listed in the `gradle tasks` output.
- Changelog
- Release process documented
- Compatibility info to the README
- JavaDoc and describe undocumented parameters.
- Additional tests, including parallel run

### Changed

- Cucumber-reporting upgraded to the latest version
- Do not use deprecated Cucumber API and use new `tags` syntax
- Use Gradle logger instead of Slf4j

### Fixed

Regression for non-standard suite name

## [0.17] - 2019-11-25

### Added
- Gradle 6 and 7 compatibility

### Changed

- Plugin name changed to `com.patdouble.cucumber-jvm`
- The minimum required Java version is 8

### Removed

- Support for Java 7
- Support for very old Gradle versions

### Fixed

- Fix path to the failure file

## [0.14]

- Initial fork from [commercehub-oss/gradle-cucumber-jvm-plugin](https://github.com/commercehub-oss/gradle-cucumber-jvm-plugin).
