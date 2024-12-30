Feature: Login Page
  A feature file to test the login page

  Scenario Outline: Login success by Enter key on any element
    When I navigate to "<firstPage>" page
    When I enter on element "username" with value "<uname>"
    When I enter on element "password" with value "<pword>"
    When I press "ENTER" key on element "<loginElemId>"
    When I wait 1 second(s) after waiting for 10 second(s) for element "welcomeUserSpan" to appear
    Then I am redirected to "<nextPage>" page
    Then The text of element "welcomeUserSpan" ends with "<uname>"
    
    Examples:
      | firstPage | uname       | pword | loginElemId | nextPage |
      | /         | huckleberry | finn  | username    | /home    |
      | /         | jamberry    | snow  | password    | /home    |
      | /login    | mister      | crow  | username    | /home    |
      | /login    | polar       | bear  | password    | /home    |

  Scenario: Login success by submit button
    When I navigate to "/" page
    When I enter on element "username" with value "jormungand"
    When I enter on element "password" with value "atelier"
    When I click on element "loginSubmitBtn"
    When I wait 1 second(s) after waiting for 10 second(s) for element "welcomeUserSpan" to appear
    Then I am redirected to "/home" page
    Then The text of element "welcomeUserSpan" ends with "jormungand"

  Scenario Outline: Login failure username or password cannot be blank
    When I navigate to "/" page
    When I enter on element "username" with value "<uname>"
    When I enter on element "password" with value "<pword>"
    When I press "ENTER" key on element "username"
    When I wait 1 second(s) after waiting for 10 second(s) for element class "err-text" to appear
    Then I am redirected to "/login" page
    Then The element class "err-text" contains text "Username or password cannot be blank"
    
    Examples:
      | uname | pword |
      | john  |       |
      | john  | BLANK |
      | BLANK | doe   |
      |       | doe   |
      | BLANK | BLANK |
      | BLANK |       |
      |       | BLANK |
      |       |       |

