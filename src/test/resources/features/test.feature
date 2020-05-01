Feature:  Run Zap Scans

  @yes
  Scenario: Spider and passively scan a site
    Given I have spidered and passively scanned 'http://20.0.1.187/LegalDevTest/main.aspx?appid=7eda802f-91fc-e911-aa0e-067e3e789e92&pagetype=dashboard&id=9e37e871-00b9-e911-aa0d-067e3e789e92&_canOverride=true'
    When I get the results
    And save the HTML report as 'passive.html'
    Then there are 0 High risk alerts

  @no
  Scenario: Ajax spider an passive scan a site
    Given I have ajax spidered and passively scanned 'http://20.0.1.187/LegalDevTest/main.aspx?appid=7eda802f-91fc-e911-aa0e-067e3e789e92&pagetype=dashboard&id=9e37e871-00b9-e911-aa0d-067e3e789e92&_canOverride=true'
    When I get the results
    And save the HTML report as 'ajaxPassive.html'
    Then there are 0 High risk alerts

  @no
  Scenario: Active Scan
    Given I actively scan 'http://20.0.1.187/LegalDevTest/main.aspx?appid=7eda802f-91fc-e911-aa0e-067e3e789e92&pagetype=dashboard&id=9e37e871-00b9-e911-aa0d-067e3e789e92&_canOverride=true'
    When I get the results
    And save the HTML report as 'active.html'
    Then there are 0 High risk alerts
