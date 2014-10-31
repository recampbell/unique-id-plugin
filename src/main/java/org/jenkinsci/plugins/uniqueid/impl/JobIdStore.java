package org.jenkinsci.plugins.uniqueid.impl;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.jenkinsci.plugins.uniqueid.IdStore;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores ids for jobs in {@link JobIdProperty}
 */
@Extension
public class JobIdStore extends IdStore<Job> {

    public JobIdStore() {
        super(Job.class);
    }

    @Override
    public void make(Job job) {
        if (job.getProperty(JobIdProperty.class) == null) {
            try {
                job.addProperty(new JobIdProperty());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to add property",e);
            }
        }
    }
    @Override
    public String get(Job thing) {
        return Id.getId((Actionable) thing);
    }


    /**
     * A unique Id for Jobs.
     */
    public static class JobIdProperty extends JobProperty<Job<?,?>> {
        private Id id = new Id();

        @Override
        public Collection<? extends Action> getJobActions(Job job) {
            return Collections.singleton(id);
        }
        @Extension
        public static class DescriptorImpl extends JobPropertyDescriptor {
            @Override
            public String getDisplayName() {
                return "Unique ID";
            }
        }
    }

    private final static Logger LOGGER = Logger.getLogger(JobIdStore.class.getName());

}
