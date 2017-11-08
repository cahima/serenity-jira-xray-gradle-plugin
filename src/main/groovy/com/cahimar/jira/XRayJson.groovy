package com.cahimar.jira

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

/**
 * Generates an XRay format json file of test results
 */
class XRayJson {

  /**
   * A jira project
   */
  def project

  /**
   * A folder to look in to find serenity json files
   */
  def folder

  /**
   * The Jira fixVersion (i.e. release) we're executing these tests for
   */
  def version

  /**
   * Queries jira to return a list of Tests
   */
  TestRetriever testRetriever

  /**
   * Map from Serenity test status to XRay equivalent
   */
  static final STATUS_MAP = ['ERROR':   'FAIL',
                             'FAILURE': 'FAIL',
                             'SUCCESS': 'PASS',
                             'PENDING': 'TODO']

  /**
   * Create an XRay info (test run meta data)
   * @return an xray info
   */
  def info() {
    [summary: 'Automated test execution',
     version: version,
     project: project]
  }

  /**
   * Get the jira test id from a serenity json test.
   * Does this by comparing the serenity json tags with a list of jira test ids we've cached.
   * @param serenityJson the serenity json test outputDirectory
   * @param a collection of jira test ids
   * @return the jira test id associated with the serenity json result
   */
  def jiraId(def serenityJson, def testJiraIds) {
    def testTag = serenityJson.tags.find { it.type == 'issue' && testJiraIds.contains(it.name) }
    if (testTag == null) {
      throw new GroovyRuntimeException('Could not get the jira id for all test results see\n$serenityJson.tags')
    }
    testTag.name
  }

  /**
   * Create a scenario test result
   * @param testKey the jira id of the test
   * @param start when the test started
   * @param finish when the test finished
   * @param comment a comment on the test execution
   * @param status the status of the test execution
   * @param examples a collection of statuses of each scenario outline example or null if there are no examples
   * @return a scenario or scenario outline test result
   */
  def scenario(def testKey, def start, def finish, def comment, def status, def examples) {
    if (examples == null) {
      return [testKey: testKey, start: start, finish: finish, comment: comment, status: status]
    } else {
      return [testKey: testKey, start: start, finish: finish, comment: comment, status: status, examples: examples]
    }
  }

  /**
   * Map a serenity status to an xray status
   * @param serenityStatus a serenity test result status
   * @return an xray status
   */
  def xrayStatus(def serenityStatus) {
    if (!STATUS_MAP.containsKey(serenityStatus)) {
      throw new GroovyRuntimeException("Could not convert all test results. There was a test in status $serenityStatus.\nThat status was unexpected and could not be converted to XRay format.")
    }
    STATUS_MAP.get(serenityStatus)
  }

  /**
   * Convert a serenity cucumber json file to an xray test execution scenario
   * @param cucumberJsonText the contents of a serenity .json result file
   * @param testJiraIds a list of Test jira ids
   * @return an xray scenario
   */
  def cucumberToScenario(def cucumberJsonText, def testJiraIds) {
    def serenityJson = new JsonSlurper().parseText(cucumberJsonText)
    def startTime = Date.from(LocalDateTime.parse(serenityJson['startTime'], DateTimeFormatter.ISO_ZONED_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant())
    def endTime = new Date(startTime.getTime() + serenityJson['duration'])
    def examples = (serenityJson.dataTable == null) ? null : serenityJson.dataTable.rows.collect { xrayStatus(it['result']) }
    def testKey = jiraId(serenityJson, testJiraIds)
    scenario(testKey, startTime, endTime, "${serenityJson['name']} run in ${serenityJson['context']}", xrayStatus(serenityJson['result']), examples)
  }

  /**
   * Convert XRay test results into a well formatted json text
   * @param info test run meta data
   * @param tests a collection of scenario test results
   * @return a pretty json string
   */
  def prettyPrint(def info, def tests) {
    def json = new JsonBuilder(info: info, tests: tests)
    JsonOutput.prettyPrint(json.toString())
  }

  /**
   * Retrieve all test jiras in the jira project
   * @return a collection jira test ids
   */
  def retrieveAllJiras() {
    testRetriever.retrieveAll()
  }

  /**
   * Convert the serenity json test results into xray scenario results
   * @return a collection of scenarios
   */
  def scenarioResults() {
    def files = new File("$folder").listFiles().findAll( { it.name.endsWith('.json') } )
    println "files = $files"
    def testJiras = retrieveAllJiras()
    files.collect { f -> cucumberToScenario(f.text, testJiras) }
  }

  /**
   * Create a json string of the test execution in json format
   * @return the json string
   */
  def json() {
    def info = info()
    println "info=$info"
    def scenarios = scenarioResults()
    println "scenarios=$scenarios"
    prettyPrint(info, scenarios)
  }

}
