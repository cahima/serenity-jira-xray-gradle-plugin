package com.cahimar.jira

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

class XRayJson {

  def project

  def folder

  def version

  TestRetriever testRetriever

  static final STATUS_MAP = ['ERROR':   'FAIL',
                             'FAILURE': 'FAIL',
                             'SUCCESS': 'PASS',
                             'PENDING': 'TODO']

  def info() {
    [summary: 'Automated test execution',
     version: version,
     project: project]
  }

  def jiraId(def serenityJson, def testJiraIds) {
    def testTag = serenityJson.tags.find { it.type == 'issue' && testJiraIds.contains(it.name) }
    if (testTag == null) {
      throw new GroovyRuntimeException('Could not get the jira id for all test results see\n$serenityJson.tags')
    }
    testTag.name
  }

  def scenario(def testKey, def start, def finish, def comment, def status, def examples) {
    if (examples == null) {
      return [testKey: testKey, start: start, finish: finish, comment: comment, status: status]
    } else {
      return [testKey: testKey, start: start, finish: finish, comment: comment, status: status, examples: examples]
    }
  }

  def xrayStatus(def serenityStatus) {
    if (!STATUS_MAP.containsKey(serenityStatus)) {
      throw new GroovyRuntimeException("Could not convert all test results. There was a test in status $serenityStatus.\nThat status was unexpected and could not be converted to XRay format.")
    }
    STATUS_MAP.get(serenityStatus)
  }

  def cucumberToScenario(def cucumberJsonText, def testJiraIds) {
    def serenityJson = new JsonSlurper().parseText(cucumberJsonText)
    def startTime = Date.from(LocalDateTime.parse(serenityJson['startTime'], DateTimeFormatter.ISO_ZONED_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant())
    def endTime = new Date(startTime.getTime() + serenityJson['duration'])
    def examples = (serenityJson.dataTable == null) ? null : serenityJson.dataTable.rows.collect { xrayStatus(it['result']) }
    def testKey = jiraId(serenityJson, testJiraIds)
    scenario(testKey, startTime, endTime, "${serenityJson['name']} run in ${serenityJson['context']}", xrayStatus(serenityJson['result']), examples)
  }

  def prettyPrint(def info, def tests) {
    def json = new JsonBuilder(info: info, tests: tests)
    JsonOutput.prettyPrint(json.toString())
  }

  def retrieveAllJiras() {
    testRetriever.retrieveAll()
  }

  def scenarioResults() {
    def files = new File("$folder").listFiles().findAll( { it.name.endsWith('.json') } )
    println "files = $files"
    def testJiras = retrieveAllJiras()
    files.collect { f -> cucumberToScenario(f.text, testJiras) }
  }

  def json() {
    def info = info()
    println "info=$info"
    def scenarios = scenarioResults()
    println "scenarios=$scenarios"
    prettyPrint(info, scenarios)
  }

}
