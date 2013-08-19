package de.ukoeln.msml.genericparser.parameterhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.mosgrid.msml.util.wrapper.JobParserConfig;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public class ParameterHandler {
	
	private final static Hashtable<String, List<IParamHandler>> _handler = new Hashtable<String, List<IParamHandler>>();
	private static boolean isInit = false;
	
	private static void Init() {
		// add handler here

		if (isInit)
			return;
		try {
			// TODO whole parameter-handling is crap. refactor!
			add(new ConfigHandler());
			add(new MoleculeInfoHandler());
			add(new OutputLocationHandler());
			add(new JSONOutputHandler());
		} finally {
			isInit = true;
		}
	}
	
	public static void handle(GenericParserMainDocument doc, JobListEditor msmlEdit, JobParserConfig parmList) {
		Init();
		ArrayList<IParamHandler> curHandler = new ArrayList<IParamHandler>();
		
		// parserPrefix must have a value here as it has been checked that parserconfig
		// exists in the current MSML-document. otherwise the document would be malformed.
		String parserPrefix = msmlEdit.getPrefixToNamespace(Namespaces.PARSER.getNamespace());
		
		boolean jsonHandlerPresent = false, outputHandlerPresent = false;
		// search parserconfig for parameter with parser-prefix... right now all parameter
		// should be parser parameter, but this might change.
		for (ParameterType param : parmList.getListForGenParser().getParameter()) {
			String paramPrefix = XmlHelper.getInstance().getPrefix(param.getDictRef());
			if (!parserPrefix.equals(paramPrefix))
				continue;
			
			String paramSuffix = XmlHelper.getInstance().getSuffix(param.getDictRef());
			if (!_handler.containsKey(paramSuffix))
				StackTraceHelper.handleException(new Throwable("Parameter " + paramSuffix + " not found."), ON_EXCEPTION.QUIT);
			
			for (IParamHandler handler : _handler.get(paramSuffix)) {
				IParamHandler handlerInstance = handler.getInstance(param);
				curHandler.add(handlerInstance);
				
				if (handlerInstance.getClass().equals(OutputLocationHandler.class))
					outputHandlerPresent = true;
				else if (handlerInstance.getClass().equals(JSONOutputHandler.class))
					jsonHandlerPresent = true;
			}
		}
		
		// since we always want output to be handled we push the outputlocation-handler to the handlerlist
		if (!outputHandlerPresent)
			curHandler.add(new OutputLocationHandler());
		
		if (!jsonHandlerPresent)
			curHandler.add(new JSONOutputHandler());
		
		// sort handlers according to weight to ensure correct execution order
		Collections.sort(curHandler, new Comparator<IParamHandler>() {

			@Override
			public int compare(IParamHandler o1, IParamHandler o2) {
				return ((Float)o1.getWeight()).compareTo(o2.getWeight());
			}
		});
		
		handle(doc, curHandler);
	}

	public static void handleTestRun(GenericParserMainDocument doc) {
		Init();
		ArrayList<IParamHandler> curHandler = new ArrayList<IParamHandler>();
		curHandler.add(new ConfigHandler());
		curHandler.add(new OutputLocationHandler());
		handle(doc, curHandler);
	}
	
	private static void handle(GenericParserMainDocument doc, List<IParamHandler> curHandler) {
		// start handlers
		for (IParamHandler handler : curHandler) {
			handler.handle(doc);
		}		
	}

	private static void add(IParamHandler handler) {
		List<IParamHandler> handlers = _handler.get(handler.getParameterName());
		if (handlers == null) {
			handlers = new ArrayList<IParamHandler>();
			_handler.put(handler.getParameterName(), handlers);
		}
		handlers.add(handler);
	}
}
