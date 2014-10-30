package org.jenkinsci.plugins.uniqueid;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Project;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

public class IdTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void project() throws Exception {
        Project p = jenkinsRule.createFreeStyleProject();
        assertNull(Id.getId(p));
        Id.addIfMissing(p);
        String id = Id.getId(p);
        AbstractBuild build = jenkinsRule.buildAndAssertSuccess(p);
        assertNull(Id.getId(build));
        Id.addIfMissing(build);
        String buildId = Id.getId(build);
        jenkinsRule.jenkins.reload();

        AbstractProject resurrectedProject = jenkinsRule.jenkins.getItemByFullName(p.getFullName(), AbstractProject.class);
        assertEquals(id, Id.getId(resurrectedProject));
        assertEquals(buildId, Id.getId(resurrectedProject.getBuild(build.getId())));
    }

    @Test
    public void folder() throws Exception {
        Folder f = jenkinsRule.jenkins.createProject(Folder.class,"folder");
        assertNull(Id.getId(f));
        Id.addIfMissing(f);
        String id = Id.getId(f);
        jenkinsRule.jenkins.reload();
        assertEquals(id, Id.getId(jenkinsRule.jenkins.getItemByFullName("folder", Folder.class)));

    }
}
