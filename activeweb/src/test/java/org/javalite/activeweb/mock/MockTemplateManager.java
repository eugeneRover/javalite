/*
Copyright 2009-(CURRENT YEAR) Igor Polevoy

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/
package org.javalite.activeweb.mock;

import org.javalite.activeweb.TemplateManager;

import jakarta.servlet.ServletContext;
import java.io.Writer;
import java.util.Map;

/**
 * @author Igor Polevoy
 */
public class MockTemplateManager extends TemplateManager {
    private Map values; String template, layout, format;

    public void merge(Map values, String template, String layout, String format, Writer writer, boolean customRoute) {
        this.values = values;
        this.template = template;
        this.layout = layout;
        this.format = format;
    }

    public Map getValues() {
        return values;
    }

    public String getTemplate() {
        return template;
    }

    public String getLayout() {
        return layout;
    }

    public void setServletContext(ServletContext ctx) {}

    public void merge(Map values, String template, Writer writer, boolean customRoute) {

    }

    public void setTemplateLocation(String templateLocation) {}

    @Override
    public String toString() {
        return "MockTemplateManager{" +
                "values=" + values +
                ", template='" + template + '\'' +
                ", layout='" + layout + '\'' +
                '}';
    }
}
