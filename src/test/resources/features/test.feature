Feature:  Run Zap Scans

  @no
  Scenario: Spider a passive scan a site
    Given I have spidered and passively scanned 'https://pet-portraits-online.co.uk/'
    When I get the results
    And save the HTML report as 'passive.html'
    Then there are 0 High risk alerts

  @yes
  Scenario: Ajax spider an passive scan a site
    Given I have ajax spidered and passively scanned 'https://pet-portraits-online.co.uk/'
    When I get the results
    And save the HTML report as 'ajaxPassive.html'
    Then there are 0 High risk alerts

  @no
  Scenario: Active Scan
    Given I actively scan 'https://pet-portraits-online.co.uk/'
    When I get the results
    And save the HTML report as 'active.html'
    Then there are 0 High risk alerts
