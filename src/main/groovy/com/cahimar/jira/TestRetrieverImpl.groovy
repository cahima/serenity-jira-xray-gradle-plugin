package com.cahimar.jira

import static groovyx.net.http.HttpBuilder.configure

/**
 * Implementation of TestRetriever that uses the jira api to retrieve all Test tickets in a project
 */
class TestRetrieverImpl implements TestRetriever {

  /**
   * The maximum number of rows to retrieve in a single jira query.
   */
  static final MAX_RESULTS = 1000

  /**
   * A jira username
   */
  def username

  /**
   * A jira password
   */
  def password

  /**
   * A jira project
   */
  def project

  /**
   * A jira url
   */
  def url

  /**
   * Retrieve a subset of Test jiras from a potentially large set.
   * @param startAt the index to start at
   * @param maxResults the max number of results to return
   */
  def retrieveJiraTestsByRange(def startAt, def maxResults) {
    def result = configure {
      request.uri = url
      request.contentType = 'application/json'
      request.uri.path = '/rest/api/2/search'
      request.uri.query = [jql: "issuetype=Test+and+project=${project}&fields=id,key&maxResults=${maxResults}&startAt=${startAt}"]
      request.headers['Authorization'] = 'Basic ' + ("$username:$password".bytes.encodeBase64().toString())
    }.get()
  }

  /**
   * Retrieve all jira Test ids in the project
   */
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
