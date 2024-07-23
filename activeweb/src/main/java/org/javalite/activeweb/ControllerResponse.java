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
package org.javalite.activeweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Igor Polevoy
 */
abstract class ControllerResponse {


    private int status = 200;
    private boolean statusSet;


    int getStatus() {
        return status;
    }

    void setStatus(int status) {
        RequestContext.getHttpResponse().setStatus(status);
        this.status = status;
        statusSet = true;
    }


    protected Map values(){
        return new HashMap();
    }

    final void process(){

        if(!statusSet){
            RequestContext.getHttpResponse().setStatus(status);
        }
        doProcess();
    }

    protected final void  stream(InputStream in, OutputStream out) throws IOException {
        byte[] bytes = new byte[1024];
        int x;
        while((x = in.read(bytes)) != -1){
            out.write(bytes, 0, x);
        }
        in.close();
    }

    abstract void doProcess();
}