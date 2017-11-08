# Allows you to report results generated by Serenity to Jira XRayJson

## To use add the following to your gradle build script

`buildscript {`
`  ...`
`  dependencies {`
`    ...`
`    classpath group: 'com.cahimar.jira', name: 'serenity-jira-xray-gradle-plugin', version: '1.0-SNAPSHOT'`
`  }`
`}`

`apply plugin: 'com.cahimar.jira'`

`jira {`
`  user System.getProperty('user')`
`  password System.getProperty('password')`
`  url "${jira.url}"`
`  folder "${serenity.outputDirectory}"`
`  project "${jira.project}"`
`  version "${version}"`
`}`

### NB You probably don't want to hardcode your password into the build script. This can be passed as an argument at run-time.
### NB The version will be used for reporting to XRay. So the version you use here should match the fixVersion in Jira.
### NB This will look for Serenity json files in your serenity output directory. Make sure you are outputting json from serenity.
If you are not then add the following property to serenity.properties:
output.formats=json, html
### NB This hasn't been published to maven central yet. If you want to use it you'll have to build it yourself and publish it to your artifactory/nexus/repo of choice.
