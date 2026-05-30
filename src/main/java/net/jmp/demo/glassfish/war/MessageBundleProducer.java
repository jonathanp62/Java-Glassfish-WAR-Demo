package net.jmp.demo.glassfish.war;

import jakarta.enterprise.context.RequestScoped;

import jakarta.enterprise.inject.Produces;

import jakarta.faces.context.FacesContext; // If using JSF/Jakarta Faces

import java.util.Locale;
import java.util.ResourceBundle;

@RequestScoped
public class MessageBundleProducer {
    @Produces
    public ResourceBundle getBundle() {
        // Option A: If using Jakarta Faces (JSF), get the user's browser locale
        // Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

        // Option B: Fallback to system default or custom logic if not using JSF
        final Locale locale = Locale.getDefault();

        return ResourceBundle.getBundle("messages", locale);
    }
}
