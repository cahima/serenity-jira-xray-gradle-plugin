package com.cahimar.jira

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin that sends Serenity test results to Jira XRay
 */
class SerenityJiraXrayGradlePlugin implements Plugin<Project> {

  void apply(Project project) {
    def extension = project.extensions.create('jira', SerenityJiraXrayGradlePluginExtension, project.objects)
    project.task('jira') {
      doLast {
        TestRetriever testRetriever = new TestRetrieverImpl(username: extension.user,
                                                            password: extension.password,
                                                            project: extension.project,
                                                            url: extension.url)
        def json = new XRayJson(project: extension.project,
                                folder: extension.folder,
                                version: extension.version,
                                testRetriever: testRetriever).json()
        def fileName = new XRayWriter(xrayJson: json,
                                      folder: extension.folder).write()
        def jira = new XRayPublisher(username: extension.user,
                                     password: extension.password,
                                     file: fileName,
                                     url: extension.url).publish()
        new JiraPage(folder: extension.folder,
                     jiraId: jira,
                     url: extension.url).write()
      }
    }
  }

}
