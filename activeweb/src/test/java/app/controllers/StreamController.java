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

package app.controllers;

import org.javalite.activeweb.AppController;

import static org.javalite.common.Collections.map;

import java.io.*;

/**
 * @author Igor Polevoy
 */
public class StreamController extends AppController {
    public void streamOut(){
        InputStream in = getClass().getResourceAsStream("/hello.pdf");
        streamOut(in).contentType("application/pdf");
    }

    public void file() throws FileNotFoundException {
        File file = new File("src/test/resources/hello.pdf");        
        sendFile(file).contentType("application/pdf");
    }

    public void write() throws IOException {
        writer().write("hello");
    }

    public void writeWithContentTypeAndHeaders() {
        writer("text/xml", map("Content-Length", 5), 200).write("hello");
    }

    public void streamWithContentTypeAndHeaders() throws IOException {
        outputStream("text/plain", map("Content-Length", 5), 200).write("hello".getBytes());
    }

    public void deleteFile()  {
        File f = new File(param("file"));
        sendFile(f, true);
    }


    public void withHeader() throws IOException {
        OutputStream out  = outputStream("application/json");
        out.write("[1,2]".getBytes());
        out.flush();
    }


    public void withHeaderBefore() throws IOException {
        header("Content-type", "application/json");
        OutputStream out  = outputStream();
        out.write("[1,2]".getBytes());
        out.flush();
    }

    public void withHeaderBeforeAndOn() throws IOException {
        header("Content-type", "application/xml");
        OutputStream out  = outputStream("text/xml");
        out.write("blah".getBytes());
        out.flush();
    }
}
