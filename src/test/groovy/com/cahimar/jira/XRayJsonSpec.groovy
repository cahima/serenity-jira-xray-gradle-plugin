package com.cahimar.jira

import groovy.json.JsonSlurper
import spock.lang.Specification

class XRayJsonSpec extends Specification {

  def "check we can generate xray json from serenity json"() {
    setup:
      TestRetriever testRetriever = Stub(TestRetriever)
      testRetriever.retrieveAll() >> ['TAD-5', 'TAD-6', 'TAD-7', 'TAD-8']
      XRayJson converter = new XRayJson(project: 'TAD',
                                        folder: 'src/test/resources/serenity',
                                        version: '1.0',
                                        testRetriever: testRetriever)
      def expected = new JsonSlurper().parseText(new File('src/test/resources/xray/xray.json').text)
    when:
      def actual = new JsonSlurper().parseText(converter.json())
    then:
      actual == expected
  }

}
