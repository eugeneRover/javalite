package org.javalite.activeweb;

import org.javalite.common.Inflector;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class exists to aggregate some common functions that are used  by ActiveWeb as well as the Ope API plugin.
 */
public class RouteUtil {

    public final static Set<String> PRIMITIVES = new HashSet<>();
    static {
        PRIMITIVES.add("byte");
        PRIMITIVES.add("short");
        PRIMITIVES.add("int");
        PRIMITIVES.add("long");
        PRIMITIVES.add("float");
        PRIMITIVES.add("double");
        PRIMITIVES.add("char");
        PRIMITIVES.add("boolean");
    }


    /**
     * Methods by class name
     */
    private static ThreadLocal<Map<String, List<Method>>> methodsTL = new ThreadLocal<>();

    /**
     * Gets methods matching an action name. Excludes: methods of superclasses from JavaLite and all non-public methods
     *
     * @return all methods matching a method name.
     */
    public static List<Method> getNamedMethods(Class<? extends AppController> controllerClass, String actionMethodName){

        if(controllerClass == null){
            return new ArrayList<>();
        }

        Map<String, List<Method>> controllerMap  =  methodsTL.get();

        if(controllerMap == null){
            methodsTL.set(controllerMap = new HashMap<>());
        }
        List<Method> discoveredMethods;

        String controllerName = controllerClass.getName();

        //we do not want cached methods in case we are in development or reloading controllers on the fly
        if (!controllerMap.containsKey(controllerName) || Configuration.activeReload()) {
            discoveredMethods = new ArrayList<>();
            controllerMap.put(controllerName, discoveredMethods);

            for (Method m : controllerClass.getMethods()) {
                if (isAction(m)) {
                    discoveredMethods.add(m);
                }
            }

        } else {
            discoveredMethods = controllerMap.get(controllerName);
        }

        List<Method> nameMatchMethods = new ArrayList<>();
        for (Method discoveredMethod : discoveredMethods) {
            if(discoveredMethod.getName().equals(actionMethodName)){
                nameMatchMethods.add(discoveredMethod);
            }
        }
        return nameMatchMethods;
    }



    /**
     *  Tests  if a method is callable in an HTTP request.
     *  Rules:
     *
     *  1. Modifier (must be public)
     *  2. Return value (must be void)
     *  3. Parameters (count must be 1 or 0),
     *  4. Cannot be static
     *  5. Cannot be abstract
     */
    public static boolean isAction(Method method) {
        try{

            return  method.getParameterCount() <= 1
                    && Arrays.stream(AppController.class.getDeclaredMethods()).noneMatch(method::equals) // shuts off AppController methods
                    && AppController.class.isAssignableFrom(method.getDeclaringClass())  // shuts off super classes methods
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())
                    && !Modifier.isAbstract(method.getModifiers())
                    && method.getReturnType().equals(Void.TYPE);
        }catch(Exception e){
            throw new RouteException("Failed to determine if a method is an action.", e);
        }
    }

    /**
     * Finds if  a controller has an action method with a given HTTP method.
     *
     * @param controllerClass controller class
     * @param actionName name of an action
     * @param httpMethod HttpMethod instance.
     * @return true if such an action exists, false if not.
     */
    public static boolean hasAction(Class<? extends AppController> controllerClass, String actionName, HttpMethod httpMethod) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        String actionMethodName = Inflector.camelize(actionName, false);
        try{
            Method actionMethod = controllerClass.getMethod(actionMethodName);
            Annotation annotation = HttpMethod.annotation(httpMethod);
            return actionMethod.getAnnotation(annotation.getClass()) != null;
        }catch(NoSuchMethodException e){
            return false;
        }
    }

    /**
     * Provides an argument  class for an action method. Will find a type of an argument only if the method plays by the rules:
     *
     * <ul>
     *     <li>
     *         Method has just one argument
     *     </li>
     *     <li>
     *          Argument is not coming from the <code>java.*</code> packages
     *     </li>
     * </ul>
     *
     * if argument is not playing by these rules a <code>null</code> will be returned even if the method does have arguments.
     *
     * @param actionMethod action method, obviously.
     * @return type of an argument for this action method.
     */
    private static Class<?> getArgumentClass(Method actionMethod){

        Class<?> argumentClass;

        if (actionMethod.getParameterCount() == 1) {
            argumentClass = actionMethod.getParameterTypes()[0];
            // we do not need primitives, shooting for a class defined in the project.
            if (!argumentClass.getName().startsWith("java")
                    && !PRIMITIVES.contains(argumentClass.getName())) {
                return argumentClass;
            }
            throw  new RouteException("The argument exists, but we cannot use it. Technically, this exception should never be thrown...");
        }
        return null;
    }

    /**
     * Finds a first method that has one argument. If not found, will find a method that has no arguments.
     * If not found, will return null.
     */
     static ActionAndArgument getActionAndArgument(Class<? extends AppController> controllerClass, String actionName){

        String actionMethodName = Inflector.camelize(actionName.replace('-', '_'), false);

        List<Method> methods = RouteUtil.getNamedMethods(controllerClass, actionMethodName);
        if (methods.size() == 0) {
            return null;
        }else if(methods.size() > 1){ // must have exactly one method with the same name, regardless of arguments.
            throw new AmbiguousActionException("Ambiguous overloaded method: " + actionMethodName + ".");
        }
        return new ActionAndArgument(methods.get(0), getArgumentClass(methods.get(0)));
    }

}
