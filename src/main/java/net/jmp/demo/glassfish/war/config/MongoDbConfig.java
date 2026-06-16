package net.jmp.demo.glassfish.war.config;

/*
 * (#)MongoDbConfig.java    0.2.0   06/16/2026
 *
 * @author   Jonathan Parker
 *
 * MIT License
 *
 * Copyright (c) 2026 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;

import jakarta.enterprise.event.Observes;

import jakarta.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

/// The configuration class for MongoDB
@ApplicationScoped
public class MongoDbConfig {
    /// The JNDI lookup for the MongoDB connection string
    @Resource(lookup = "jndi/mongoURI")
    private String mongoConnectionUri;

    /// The MongoDB client
    private MongoClient mongoClient;

    // Initialize the SLF4J Logger
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final java.util.logging.Logger JUL_LOGGER = java.util.logging.Logger.getLogger(MongoDbConfig.class.getName());

    void onStartup(@Observes @Initialized(ApplicationScoped.class) final Object event) {
        JUL_LOGGER.log(Level.INFO, "MongoDbConfig starting");
    }

    @PostConstruct
    void init() {
        this.mongoClient = MongoClients.create(this.mongoConnectionUri);
        this.mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
        this.logger.info("MongoDB pinged OK");
        JUL_LOGGER.log(Level.INFO, "MongoDB pinged OK");
    }

    @PreDestroy
    void destroy() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    @Produces
    @ApplicationScoped
    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    /// The MongoDB database
    ///
    /// @return com.mongodb.client.MongoDatabase
    @Produces
    @ApplicationScoped
    public MongoDatabase getDatabase() {
        return this.mongoClient.getDatabase("react_learning");
    }
}
