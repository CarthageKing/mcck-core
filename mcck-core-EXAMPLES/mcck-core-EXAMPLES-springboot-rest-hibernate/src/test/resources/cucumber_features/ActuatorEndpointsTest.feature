#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@allure.label.epic:Cucumber_Tests_Epic
@allure.label.feature:Feature_1
#@allure.label.story:Story_1
@allure.label.parentSuite:Cucumber_Tests_Parent_Suite
@allure.label.suite:Suite_1
@allure.label.sub_suite:Sub_Suite_Actuator_Endpoints
Feature: Actuator Endpoints
  A feature file to test the actuator endpoints in the application

  Scenario: Health check endpoint
    When I call the GET API "/actuator/health"
    Then The response must be "JSON"
    Then The JSON response must contain the following:
    	| Path                 | Value |
    	| status               | DOWN  |
    	| components.db.status | UP    |
    Then The response status must be 503

  Scenario: Health liveness endpoint
    When I call the GET API "/actuator/health/liveness"
    Then The response must be "JSON"
    Then The JSON response must contain the following:
    	| Path                 | Value |
    	| status               | UP    |
    Then The response status must be 200

