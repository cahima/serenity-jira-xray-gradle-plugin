package com.cahimar.jira

import spock.lang.Specification

class InfoSpec extends Specification {

  def "check we can generate an xray info (execution metadata)"() {
    setup:
      XRayJson xrayJson = new XRayJson(project: 'TAD',
                                       version: '1.0')
    when:
     def actual = xrayJson.info()
    then:
      actual == [summary: 'Automated test execution',
                 version: '1.0',
                 project: 'TAD']
  }

}
