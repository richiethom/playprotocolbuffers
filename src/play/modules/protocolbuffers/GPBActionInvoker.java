package play.modules.protocolbuffers;

import java.lang.reflect.Method;

import play.Play;
import play.classloading.enhancers.ControllersEnhancer.ControllerSupport;
import play.exceptions.ActionNotFoundException;
import play.exceptions.PlayException;
import play.utils.Java;

public class GPBActionInvoker {
	
    public static Object[] getActionMethod(String fullAction) {
        Method actionMethod = null;
        Class<?> controllerClass = null;
        try {
            if (!fullAction.startsWith("controllers.")) {
                fullAction = "controllers." + fullAction;
            }
            String controller = fullAction.substring(0, fullAction.lastIndexOf("."));
            String action = fullAction.substring(fullAction.lastIndexOf(".") + 1);
            controllerClass = Play.classloader.getClassIgnoreCase(controller);
            if (!ControllerSupport.class.isAssignableFrom(controllerClass)) {
                throw new ActionNotFoundException(fullAction, new Exception("class " + controller + " does not extend play.mvc.Controller"));

            }
            actionMethod = Java.findActionMethod(action, controllerClass);
            if (actionMethod == null) {
                throw new ActionNotFoundException(fullAction, new Exception("No method public static void " + action + "() was found in class " + controller));
            }
        } catch (PlayException e) {
            throw e;
        } catch (Exception e) {
            throw new ActionNotFoundException(fullAction, e);
        }
        return new Object[]{controllerClass, actionMethod};
    }

}
