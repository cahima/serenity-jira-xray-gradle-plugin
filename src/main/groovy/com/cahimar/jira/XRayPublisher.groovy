package com.cahimar.jira

import static groovyx.net.http.HttpBuilder.configure

/**
 * Publishes an xray json file to the jira api
 */
class XRayPublisher {

  /**
   * A jira user name
   */
  def username

  /**
   * A jira password
   */
  def password

  /**
   * An xray json file
   */
  def file

  /**
   * A jira url
   */
  def url

  /**
   * Publish the xray json file to jira
   * @return the jira id of the test execution that was generated
   */
  def publish() {
    println 'Sending results to jira'
    def result = configure {
        request.uri = url
        request.contentType = 'application/json'
        request.uri.path = '/rest/raven/1.0/import/execution'
        request.body = new File(file).text
        request.headers['Authorization'] = 'Basic ' + ("$username:$password".bytes.encodeBase64().toString())
    }.post()
    println "Test results uploaded to xray:\n${result}"
    result.testExecIssue.key
  }
}
