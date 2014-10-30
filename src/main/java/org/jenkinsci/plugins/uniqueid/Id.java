package org.jenkinsci.plugins.uniqueid;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.hudson.plugins.folder.FolderProperty;
import com.cloudbees.hudson.plugins.folder.FolderPropertyDescriptor;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Run;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A globally unique id for Actionable things in Jenkins.
 *
 * Intended use is to call addIfMissing(*)
 * whenever you need something {@link Actionable}(build, project, folder)
 * to have a unique id. 
 *
 * This is useful for tracking Jenkins objects in external systems
 * such as databases.
 *
 *
 * @author Ryan Campbell
 */
public class Id implements Action {

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

    /**
     * A unique ID for folders.
     */
    public static class FolderIdProperty extends FolderProperty {
        private Id id = new Id();

        @Override
        public Collection<? extends Action> getFolderActions() {
            return Collections.singleton(id);
        }

        @Extension
        public static class DescriptorImpl extends FolderPropertyDescriptor {
            @Override
            public String getDisplayName() {
                return "Unique ID";
            }
        }

        /**
         * Since {@link Folder#getAction(Class)} does return actions from a property,
         * (like {@link Job#getAction(Class)} does)
         * this method is added for convenience.
         *
         * @return the id for this folder
         */
        public String getId() {
            return id.getId();
        }
    }


    private final String id;

    protected Id() {
        this.id = Base64.encodeBase64String(UUID.randomUUID().toString().getBytes()).substring(0, 30);
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }

    public String getId() {
        return id;
    }

    /**
     * Create an id for this run if it doesn't have on yet. Saves the run if an Id is added.
     *
     * @param run
     */
    public static void addIfMissing(Run run) {
        if (run.getAction(Id.class) == null) {
            run.addAction(new Id());
            try {
                run.save();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE,"Failed to save id",e);
            }
        }
    }

    /**
     * Create an id for this project if it doesn't have on yet. Saves the run if an Id is added.
     *
     * @param project
     */
    public static void addIfMissing(Job project) {
        if (project.getProperty(JobIdProperty.class) == null) {
            try {
                project.addProperty(new JobIdProperty());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to add property",e);
            }
        }
    }

    /**
     * Create an id for this project if it doesn't have on yet. Saves the run if an Id is added.
     *
     * @param folder
     */
    public static void addIfMissing(Folder folder) {
        if (folder.getProperties().get(FolderIdProperty.class) == null) {
            try {
                folder.addProperty(new FolderIdProperty());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to add property",e);
            }
        }
    }


    @Nullable
    private static String getId(Actionable actionable) {
        Id id = actionable.getAction(Id.class);
        if (id != null) {
            return id.getId();
        } else {
            return null;
        }
    }

    /**
     * Get the Id
     * @param run
     * @return the id if any, or null if none
     */
    @Nullable
    public static String getId(Run run) {
        return getId((Actionable)run);
    }

    /**
     * Get the Id
     * @param job
     * @return the id if any, or null if none
     */
    @Nullable
    public static String getId(Job job) {
        return getId ((Actionable)job);
    }

    /**
     * Get the Id
     * @param folder
     * @return the id if any, or null if none
     */
    @Nullable
    public static String getId(Folder folder) {
        String id = getId((Actionable)folder);
        if (id != null) {
            return id;
        } else {
            FolderIdProperty idProperty = folder.getProperties().get(FolderIdProperty.class);
            if (idProperty != null) {
                return idProperty.getId();
            }
        }
        return null;
    }

    private final static Logger LOGGER = Logger.getLogger(Id.class.getName());
}
