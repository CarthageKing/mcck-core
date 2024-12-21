Notes when upgrading:

After upgrading dependencies, run a build and check for the following:

1. No errors on dependency convergence in any project
2. Able to run mvn allure:serve in mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate and able to see the reports properly generated
3. Check the dependency on mcck-core/mcck-core-EXAMPLES/mcck-core-EXAMPLES-springboot-rest-hibernate aspectjweaver if matching
