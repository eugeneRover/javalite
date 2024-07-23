package org.javalite.db_migrator.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.javalite.activejdbc.Base;
import org.javalite.db_migrator.DbUtils;
import org.javalite.db_migrator.MigrationManager;

import java.io.File;

import static java.lang.String.format;
import static org.javalite.db_migrator.DbUtils.blank;
import static org.javalite.db_migrator.DbUtils.exec;


@Mojo(name = "create")
public class CreateMojo extends AbstractDbMigrationMojo {

    @SuppressWarnings("unchecked")
    public void executeMojo() throws MojoExecutionException {

        String databaseName = DbUtils.extractDatabaseName(getUrl());
        try {
            String createSql = blank(getCreateSql()) ? "create database %s" : getCreateSql();
            openConnection(true);
            exec(format(createSql, databaseName));
            getLog().info("Created database " + getUrl());
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to create database: " + getUrl(), e);
        } finally {
            Base.close();
        }

        try{
            openConnection();
            new MigrationManager(getProject().getCompileClasspathElements(), new File(toAbsolutePath(getMigrationsPath(), true))).createSchemaVersionTableIfDoesNotExist();
        }catch(Exception e){
            throw  new MojoExecutionException("failed to create SCHEMA_VERSION table", e);
        }finally {
            Base.close();
        }
    }
}
