@allure.label.epic:Cucumber_Tests_Epic
@allure.label.feature:Feature_2
@allure.label.story:Story_REST_API_Endpoints
@allure.label.parentSuite:Cucumber_Tests_Parent_Suite
@allure.label.suite:Suite_2
#@allure.label.sub_suite:Sub_Suite_2
Feature: REST API Endpoints
  A feature file to test the application's REST APIs (excluding actuator endpoints)

  Scenario: Search for Books
    When I call the GET API "/books/o?nameStartsWith=sameer&isbnContains=HAT&atLeastNumPages=4"
    Then The response must be "JSON"
    Then The JSON response must contain the following:
    	| Path              | Value |
    	| header.statusCode | 200   |
    	| data.numPages     | 0     |
    Then The response status must be 200

