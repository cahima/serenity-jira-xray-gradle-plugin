package com.cahimar.jira

import groovy.json.JsonSlurper

/**
 * Interface for an object that can retrieve Test issues from Jira
 */
interface TestRetriever {

  /**
   * Return all the Test jira ids
   */
  def retrieveAll()

}
