package uk.gov.hmrc.jenkinsjobs.domain

import javaposse.jobdsl.dsl.Job
import spock.lang.Specification
import uk.gov.hmrc.jenkinsjobs.JobParents
import uk.gov.hmrc.jenkinsjobs.domain.builder.GradleLibraryJobBuilder

@Mixin(JobParents)
class GradleLibraryJobBuilderSpec extends Specification {

    void 'test XML output'() {
        given:
        GradleLibraryJobBuilder jobBuilder = new GradleLibraryJobBuilder('test-job')

        when:
        Job job = jobBuilder.build(jobParent())

        then:
        with(job.node) {
            scm.userRemoteConfigs.'hudson.plugins.git.UserRemoteConfig'.url.text() == 'git@github.com:hmrc/test-job.git'
            buildWrappers.'EnvInjectBuildWrapper'.info.propertiesContent.text().contains('CLASSPATH') == true
            buildWrappers.'EnvInjectBuildWrapper'.info.propertiesContent.text().contains('JAVA_HOME') == true
            buildWrappers.'EnvInjectBuildWrapper'.info.propertiesContent.text().contains('PATH') == true
            triggers.'com.cloudbees.jenkins.gitHubPushTrigger'.spec.text() == ''
            builders.'hudson.plugins.gradle.Gradle'.tasks.text().contains('clean test bintrayUpload --info')
            publishers.'hudson.tasks.junit.JUnitResultArchiver'.testResults.text() == 'build/test-results/**/*.xml'
            publishers.'htmlpublisher.HtmlPublisher'.reportTargets.'htmlpublisher.HtmlPublisherTarget'.reportDir [0].text() == 'build/reports/tests'
            publishers.'htmlpublisher.HtmlPublisher'.reportTargets.'htmlpublisher.HtmlPublisherTarget'.reportName [0].text() == 'HTML Report'
        }
    }
}
