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

/**
 * Internal class if the framework, do not use directly.
 * 
 * @author Igor Polevoy
 */
class NopResponse extends ControllerResponse{

    NopResponse(String contentType, int status){
        RequestContext.getHttpResponse().setContentType(contentType);
        setStatus(status);
    }
    @Override
    void doProcess() {
        //do nothing
    }
}
