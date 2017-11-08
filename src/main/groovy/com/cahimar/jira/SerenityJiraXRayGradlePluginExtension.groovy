package com.cahimar.jira

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory

/**
 * Extension class that defines parameters you can pass from the calling project.
 */
class SerenityJiraXrayGradlePluginExtension {

  /**
   * A jira user name
   */
  String user

  /**
   * A jira password
   */
  String password

  /**
   * A jira project short name
   */
  String project

  /**
   * The Jira fixVersion (i.e. release) we're executing these tests for
   */
  String version

  /**
   * The folder where the Serenity json results can be found. Output will be put in a sub-folder of this called /jira
   */
  String folder

  /**
   * The url of your jira instance
   */
  String url

  @Inject
  SerenityJiraXrayGradlePluginExtension(ObjectFactory objectFactory) {
  }

}
