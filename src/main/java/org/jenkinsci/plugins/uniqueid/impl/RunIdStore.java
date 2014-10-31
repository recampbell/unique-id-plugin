package org.jenkinsci.plugins.uniqueid.impl;

import hudson.Extension;
import hudson.model.Actionable;
import hudson.model.Run;
import org.jenkinsci.plugins.uniqueid.IdStore;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores id's for runs as an action on the Run.
 */
@Extension
public class RunIdStore extends IdStore<Run> {
    public RunIdStore() {
        super(Run.class);
    }

    @Override
    public void make(Run run) {
        if (run.getAction(Id.class) == null) {
            run.addAction(new Id());
            try {
                run.save();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE,"Failed to save id",e);
            }
        }
    }
    @Override
    public String get(Run thing) {
        return Id.getId((Actionable) thing);
    }

    private final static Logger LOGGER = Logger.getLogger(RunIdStore.class.getName());

}
