# Notes when upgrading:

After upgrading dependencies, run a build and check for the following:

1. No errors on dependency convergence in any project. If any errors occur, use compatible packages
2. Able to run `mvn allure:serve` in `mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate` and able to see the reports properly generated
3. Check the cucumber and cluecumber reports generated under `mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate/target` if properly generated
4. Check the dependency on `mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate` `aspectjweaver` dependency if matching version
5. Regenerate the DDL files located at `mcck-core-EXAMPLES-springboot-rest-hibernate/src/main/resources/ddl`, using the unit test classes located at `mcck-core-EXAMPLES-springboot-rest-hibernate/src/test/java/org/carthageking/mc/mcck/core/EXAMPLES/sbrb/ddl` i.e. `GenerateDDLFor*Test.java`
6. Check all `application.properties` or `application.yaml` files and replace deprecated properties with updated ones
7. Update the following custom classes from sources:

- all classes under `mcck-core-allure-cucumber7-jvm` module
- `mcck-core-csv/src/main/java/org/carthageking/mc/mcck/core/csv/CustomCsvWriter.java`
- `mcck-core-cucumber/src/main/java/org/carthageking/mc/mcck/core/cucumber/McckCucumberRandomFilenameJsonFormatter.java`
- `mcck-core-EXAMPLES-springboot-rest-hibernate/src/main/java/org/carthageking/mc/mcck/core/EXAMPLES/sbrb/config/McckCacheInterceptor.java`
- `mcck-core-EXAMPLES-springboot-rest-hibernate/src/main/java/org/carthageking/mc/mcck/core/EXAMPLES/sbrb/config/McckProxyCachingConfiguration.java`
