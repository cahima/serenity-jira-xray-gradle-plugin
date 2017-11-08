package com.cahimar.jira

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory

class SerenityJiraXrayGradlePluginExtension {

  String user

  String password

  String project

  String version

  String folder

  String url

  @Inject
  SerenityJiraXrayGradlePluginExtension(ObjectFactory objectFactory) {
  }

}
