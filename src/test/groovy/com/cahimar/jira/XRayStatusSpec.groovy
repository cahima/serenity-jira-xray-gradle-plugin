package com.cahimar.jira

import spock.lang.Specification

class XRayStatusSpec extends Specification {

  def "check we can convert serenity success to xray pass"() {
    setup:
      XRayJson xrayJson = new XRayJson(project: '',
                                       folder: '',
                                       version: '')
    when:
     def actual = xrayJson.xrayStatus('SUCCESS')
    then:
      actual == "PASS"
  }

  def "check we can convert serenity error to xray fail"() {
    setup:
      XRayJson xrayJson = new XRayJson()
    when:
     def actual = xrayJson.xrayStatus('ERROR')
    then:
      actual == 'FAIL'
  }

  def "check we can convert serenity failure to xray fail"() {
    setup:
      XRayJson xrayJson = new XRayJson()
    when:
     def actual = xrayJson.xrayStatus('FAILURE')
    then:
      actual == 'FAIL'
  }

  def "check we can convert serenity pending to xray todo"() {
    setup:
      XRayJson xrayJson = new XRayJson()
    when:
     def actual = xrayJson.xrayStatus('PENDING')
    then:
      actual == 'TODO'
  }

  def "check we cannot convert nonsense serenity status to xray and exception is thrown"() {
    setup:
      XRayJson xrayJson = new XRayJson()
    when:
      def actual = xrayJson.xrayStatus('NOTAREALSTATUS')
    then:
      GroovyRuntimeException ex = thrown()
      ex.message == 'Could not convert all test results. There was a test in status NOTAREALSTATUS.\nThat status was unexpected and could not be converted to XRay format.'
  }

}
