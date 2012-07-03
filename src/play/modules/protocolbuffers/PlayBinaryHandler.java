package play.modules.protocolbuffers;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;

import org.quickserver.net.server.ClientBinaryHandler;
import org.quickserver.net.server.ClientHandler;
import org.quickserver.net.server.DataMode;
import org.quickserver.net.server.DataType;

import play.Play;
import play.PlayPlugin;
import play.classloading.enhancers.LocalvariablesNamesEnhancer.LocalVariablesNamesTracer;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors.FieldDescriptor;

public class PlayBinaryHandler implements ClientBinaryHandler {

	@Override
	public void handleBinary(ClientHandler clientHandler, byte[] bytesFromClient)
			throws SocketTimeoutException, IOException {
		System.out.println("Processing");
		try {
			final String incomingRequestWrapperName = (String)Play.configuration.get("gpb.incoming.request.wrapper");
			final Class<?> incomingRequestWrapper = Class.forName(incomingRequestWrapperName);
			final Method parseFromMethod = incomingRequestWrapper.getMethod("parseFrom", Array.newInstance(byte.class, 0).getClass());
			final GeneratedMessage wrapper = (GeneratedMessage)parseFromMethod.invoke(null, bytesFromClient);
			final Map<FieldDescriptor, Object> allFields = wrapper.getAllFields();
			for (Entry<FieldDescriptor, Object> entry : allFields.entrySet()) {
				final Object messageToHandle = entry.getValue();
				//find a suitable method in the controller
				try {
					final Method methodToInvoke = findMethodForMessage(messageToHandle);
					before();
					final GeneratedMessage result = (GeneratedMessage)methodToInvoke.invoke(null, messageToHandle);
					after();
					System.out.println("ClientHandler is:"+clientHandler.getClass().getName());
					clientHandler.setDataMode(DataMode.BINARY, DataType.OUT);
					clientHandler.sendClientBinary(result.toByteArray());
					clientHandler.closeConnection();
					System.out.println("Response sent");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NegativeArraySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
    public void before() {
        Thread.currentThread().setContextClassLoader(Play.classloader);
        for (PlayPlugin plugin : Play.plugins) {
            plugin.beforeInvocation();
        }
    }

    /**
     * Things to do after an Invocation.
     * (if the Invocation code has not thrown any exception)
     */
    public void after() {
        for (PlayPlugin plugin : Play.plugins) {
            plugin.afterInvocation();
        }
        LocalVariablesNamesTracer.checkEmpty(); // detect bugs ....
    }
	

	private Method findMethodForMessage(Object messageToHandle) throws Exception {
		System.out.println("Message:"+messageToHandle.getClass().getName());
		final String controller = (String) Play.configuration.get("gpb.controller");
		final Method[] methods = Class.forName(controller,false, Play.classloader).getMethods();
		for (Method method : methods) {
			final Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length == 1 && parameterTypes[0].equals(messageToHandle.getClass()) && Modifier.isStatic(method.getModifiers())) {
				return method;
			}
		}
		throw new Exception("Unable to find method for class "+messageToHandle.getClass());
		
	}

}
