package com.cahimar.jira

import static groovyx.net.http.HttpBuilder.configure

class TestRetrieverImpl implements TestRetriever {

  //this looks like a magic number
  //but it's the max number of rows jira will typically allow us to retrieve in a single call
  static final MAX_RESULTS = 1000

  def username

  def password

  def project

  def url

  def retrieveJiraTestsByRange(def startAt, def maxResults) {
    def result = configure {
      request.uri = url
      request.contentType = 'application/json'
      request.uri.path = '/rest/api/2/search'
      request.uri.query = [jql: "issuetype=Test+and+project=${project}&fields=id,key&maxResults=${maxResults}&startAt=${startAt}"]
      request.headers['Authorization'] = 'Basic ' + ("$username:$password".bytes.encodeBase64().toString())
    }.get()
  }

  def retrieveAll() {
    println "Retrieving jira tests for $project"
    def startAt = 0
    def testCount = 1
    def issues = []
    while (startAt < testCount) {
      def tests = retrieveJiraTestsByRange(startAt, MAX_RESULTS)
      testCount = tests.total
      startAt += MAX_RESULTS
      issues.addAll tests.issues.collect { it.key }
    }
    println "Retrieved jira tests $issues"
    issues
  }
}
