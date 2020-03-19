Feature:  Run Zap Scans

  @yes
  Scenario: test Zap
    Given I have spidered and passively scanned 'https://pet-portraits-online.co.uk/'
    When I get the results
    And save the HTML report as 'passive.html'
    Then there are 0 High risk alerts

  @no
  Scenario: Active Scan
    Given I actively scan 'https://pet-portraits-online.co.uk/'
    When I get the results
    And save the HTML report as 'active.html'
    Then there are 0 High risk alerts
