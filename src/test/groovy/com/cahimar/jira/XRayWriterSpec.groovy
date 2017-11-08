package com.cahimar.jira

import spock.lang.Specification

class XRayWriterSpec extends Specification {

  def "check we can write json to file"() {
    setup:
      def expected = "{ test }"
    when:
      new XRayWriter(xrayJson: expected, folder: 'build/site/serenity').write()
      def actual = new File('build/site/serenity/jira/xray.json').text
    then:
      actual == expected
  }

}
