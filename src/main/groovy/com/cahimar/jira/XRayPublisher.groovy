package com.cahimar.jira

import static groovyx.net.http.HttpBuilder.configure

class XRayPublisher {

  def username

  def password

  def file

  def url

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
