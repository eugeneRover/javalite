package org.javalite.db_migrator.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.maven.plugin.MojoExecutionException;

import static org.javalite.common.Util.*;
import org.javalite.db_migrator.DatabaseUtils;

public abstract class AbstractDbMigrationMojo extends AbstractMigrationMojo {

    /**
     * @parameter
     */
    private String url;

    /**
     * @parameter
     */
    private String driver;

    /**
     * @parameter
     */
    private String username;

    /**
     * @parameter
     */
    private String password;

    /**
     * @parameter
     */
    private String environments;

    /**
     * @parameter
     */
    private String configFile;

    public final void execute() throws MojoExecutionException {
        if (blank(environments)) {
            executeCurrentConfiguration();
        } else {
            Properties properties = new Properties();
            File file = new File(blank(configFile) ? "database.properties" : configFile);
            if (file.exists()) {
                InputStream is = null;
                try {
                    is = new FileInputStream(file);
                    properties.load(is);
                } catch (IOException e){
                    throw new MojoExecutionException("Error reading " + file + " file", e);
                } finally {
                    closeQuietly(is);
                }
            } else {
                throw new MojoExecutionException("File " + file + " not found");
            }
            for (String environment : environments.split("\\s*,\\s*")) {
                getLog().info("Environment: " + environment);
                url = properties.getProperty(environment + ".jdbc.url");
                driver = properties.getProperty(environment + ".jdbc.driver");
                username = properties.getProperty(environment + ".jdbc.username");
                password = properties.getProperty(environment + ".jdbc.password");
                executeCurrentConfiguration();
            }
        }
    }

    private void executeCurrentConfiguration() throws MojoExecutionException {
        if (blank(password)) {
            password = "";
        }
        if (blank(driver) && !blank(url)) {
            driver = DatabaseUtils.driverClass(url);
        }

        validateConfiguration();

        executeMojo();
    }

    private void validateConfiguration() throws MojoExecutionException {
        if (blank(driver)) {
            throw new MojoExecutionException("No database driver. Specify one in the plugin configuration.");
        }

        if (blank(url)) {
            throw new MojoExecutionException("No database url. Specify one in the plugin configuration.");
        }

        if (blank(username)) {
            throw new MojoExecutionException("No database username. Specify one in the plugin configuration.");
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Can't load driver class " + driver + ". Be sure to include it as a plugin dependency.");
        }
    }

    public abstract void executeMojo() throws MojoExecutionException;

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEnvironments() {
        return environments;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnvironments(String environments) {
        this.environments = environments;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
