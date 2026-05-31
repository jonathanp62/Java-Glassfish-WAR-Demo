package net.jmp.demo.glassfish.war;

import jakarta.enterprise.context.RequestScoped;

import jakarta.enterprise.inject.Produces;

import jakarta.inject.Named;

import java.util.Locale;
import java.util.ResourceBundle;

/// The message bundle producer class
@RequestScoped
public class MessageBundleProducer {
    /// The get bundle method
    ///
    /// @return java.util.ResourceBundle
    @Produces
    @Named("messages")
    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("messages", Locale.getDefault());
    }
}
