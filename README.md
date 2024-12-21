# Notes when upgrading:

After upgrading dependencies, run a build and check for the following:

1. No errors on dependency convergence in any project. If any errors occur, use compatible packages
2. Able to run `mvn allure:serve` in `mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate` and able to see the reports properly generated
3. Check the cucumber and cluecumber reports generated under `mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate/target` if properly generated
4. Check the dependency on `mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate` `aspectjweaver` dependency if matching version
