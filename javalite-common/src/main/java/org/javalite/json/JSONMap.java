/*
Copyright 2009-present Igor Polevoy

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

package org.javalite.json;

import org.javalite.common.Convert;

import java.math.BigDecimal;
import java.util.*;

import static org.javalite.common.Collections.map;

public class JSONMap extends HashMap<String, Object> {

    public JSONMap(Map map){
        super(map);
    }

    /**
     * Creates an instance from the array,
     * where odd arguments are map keys and the even are values.
     *
     * @param keysAndValues keys and
     */
    public JSONMap(String key, Object value, Object... keysAndValues){
        super(map(keysAndValues));
        put(key, value);
    }

    /**
     * Converts the String into a JSONMap.
     *
     * @param jsonString JSON Object document as string.
     */
    public JSONMap(String jsonString){
        super(JSONHelper.toJSONMap(jsonString));
    }

    public JSONMap() {}

    /**
     * Returns a <code>JSONList</code> for a list name. It is expected that this list is an immediate child of this map.
     * If the object is not a list, the method will throw an exception.
     * The object is similar to that of <code>Map.get("name")</code> but will also convert the returned value to a
     * JSONList so you could go further down the JSON hierarchy.
     *
     * @param listName name (map key) of the list object in this map.
     *
     * @throws JSONParseException;
     * @return instance of <code>JSONList</code> for a name
     */
    private JSONList getChildList(String listName){
        if(!containsKey(listName)){
            return null;
        }
        Object attr = get(listName);
        if(attr instanceof List){
            return new JSONList((List) attr);
        }else {
            throw new JSONParseException("Object named" + listName + " is not a List.");
        }

    }

    /**
     * Returns a <code>JSONMap</code> for a name. It is expected that this map is an immediate child of the current map.
     * If the object is not a map, the method will throw an exception.
     * The object is similar to that of <code>Map.get("name")</code> but will also convert the returned value to a
     * JSONList so you could go further down the JSON hierarchy.
     *
     * @param attribute name (map key).
     *
     * @throws JSONParseException;
     * @return instance of <code>JSONMap</code> for a name
     */
    private JSONMap getChildMap(String attribute){
        Object map = super.get(attribute);
        if(map == null){
            return null;
        }else if(map instanceof Map){
            return new JSONMap((Map) map);
        }else {
            throw new JSONParseException("Object named: " + attribute + " is not a Map.");
        }
    }

    /**
     * Returns an object deep from the structure of a JSON document.
     *
     * <pre>
     *     {
     *         "university": {
     *             students : {
     *                 "mary" : {
     *                           "first_name": "Mary",
     *                           "last_name": "Smith",
     *                  },
     *                 "joe" : {
     *                     "first_name": "Joe",
     *                     "last_name": "Shmoe",
     *                 }
     *             }
     *         }
     *     }
     * </pre>
     *
     * @param attributePath accepts a dot-delimited format: "university.students.joe" where every entry except the last one  must be a map
     * @return an object from the depths of the JSON structure.
     */
    @Override
    public Object get(Object attributePath) {
        if (!attributePath.toString().contains(".")) {
            return super.get(attributePath);
        } else {
            StringTokenizer st = new StringTokenizer(attributePath.toString(), ".");
            Object  parent = this;
            Object  child = null;

            while (st.hasMoreTokens()) {
                String attr = st.nextToken();
                if(parent instanceof  Map){
                    child = ((Map)parent).get(attr);
                }

                if(child !=  null && !st.hasMoreTokens()){
                    return child;
                }else if(child != null && st.hasMoreTokens() && child instanceof Map){
                    parent = new JSONMap((Map) child);
                }else if(child != null && !st.hasMoreTokens()){
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Returns a map deep from the structure of a JSON document.
     *
     * <pre>
     *     {
     *         "university": {
     *             students : {
     *                 "mary" : {
     *                           "first_name": "Mary",
     *                           "last_name": "Smith",
     *                  },
     *                 "joe" : {
     *                     "first_name": "Joe",
     *                     "last_name": "Shmoe",
     *                 }
     *             }
     *         }
     *     }
     * </pre>
     *
     * @param attributePath accepts a dot-delimited format: "university.students.joe" where every entry must  be a map
     * @return a map from the depths of the JSON structure.
     */
    public JSONMap getMap(String attributePath) {

        Object o = get(attributePath);

        if(o instanceof Map){
            return new JSONMap((Map) o);
        }else if(o != null){
            throw  new JSONParseException(attributePath + " is not a Map");
        }else {
            return null;
        }
    }

    /**
     * Returns a list deep from the structure of JSON document.
     *
     * <pre>
     *     {
     *         "university": {
     *             students : ["mary", joe]
     *
     *         }
     *     }
     * </pre>
     *
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map
     * @return list from the depths of the JSON structure.
     */
    public JSONList getList(String attributePath) {

        Object o = get(attributePath);

        if(o instanceof List){
            return new JSONList((List) o);
        }else if(o != null){

            throw new JSONParseException(attributePath + " is not a List");
        }else{
            return null;
        }
    }


    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public Boolean getBoolean(String attributePath){
        return Convert.toBoolean(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public BigDecimal getBigDecimal(String attributePath){
        return Convert.toBigDecimal(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public Date getDate(String attributePath){
        return Convert.toSqlDate(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public Double getDouble(String attributePath){
        return Convert.toDouble(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public Float getFloat(String attributePath){
        return Convert.toFloat(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public Integer getInteger(String attributePath){
        return Convert.toInteger(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public Long getLong(String attributePath){
        return Convert.toLong(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public Short getShort(String attributePath){
        return Convert.toShort(get(attributePath));
    }

    /**
     * @param attributePath accepts a dot-delimited format: "university.students" where every entry must  be a map.
     */
    public String getString(String attributePath){
        return Convert.toString(get(attributePath));
    }

    /**
     * @return  a JSON representation  of this object
     */
    @Override
    public String toString() {
        return JSONHelper.toJSON(this);
    }


    /**
     * @param pretty - true for formatted JSON.
     *
     * @return a JSON representation  of this object
     */
    public String toJSON(boolean pretty){
        return JSONHelper.toJSON(this, pretty);
    }

    /**
     * @return a JSON representation  of this object
     */
    public String toJSON(){
        return JSONHelper.toJSON(this, false);
    }
}
