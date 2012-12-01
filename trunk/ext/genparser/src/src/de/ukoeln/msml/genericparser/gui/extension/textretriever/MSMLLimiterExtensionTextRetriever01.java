package de.ukoeln.msml.genericparser.gui.extension.textretriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.worker.StringH;
import de.ukoeln.msml.genericparser.worker.WithBufferedReaderDelegate;

public class MSMLLimiterExtensionTextRetriever01 implements
IMSMLExtensionTextRetriever {


	@Override
	public void getTextToConfig(IMSMLExtensionVisitor val,
			HashMap<String, String> conf) {

		boolean _isLine = Boolean.parseBoolean(conf.get("isLine"));
		final boolean _useLastMatch = Boolean.parseBoolean(conf.get("useLastMatch"));
		final int _lineFrom = Integer.parseInt(conf.get("lineFrom"));
		final int _lineTo = Integer.parseInt(conf.get("lineTo"));
		String _regexFrom = conf.get("regexFrom");
		String _regexTo = conf.get("regexTo");

		final List<String> text = new ArrayList<String>();
		final List<String> reversedText = new ArrayList<String>();
		StringH.withBr(val.getText(), new WithBufferedReaderDelegate() {				
			@Override
			public String Do(BufferedReader br) throws IOException {
				while(true) {
					String line = br.readLine();
					if (line == null)
						break;
					text.add(line);
					reversedText.add(line);
				}
				if (_useLastMatch)
					Collections.reverse(reversedText);
				return "";				
			}
		});

		List<String> searchText = null;
		if (_useLastMatch)
			searchText = reversedText;
		else 
			searchText = text;
		
		// search for first occurrence of regexFrom. if last match is desired,
		// we search for first occurrence in reversed list, otherwise in normal list.
		// Nevertheless we have the trailing text from the first or last occurrence. 
		if (!StringH.isNullOrEmpty(_regexFrom)) {
			final String regexFrom = ".*" + _regexFrom + ".*";
			Pattern p = Pattern.compile(regexFrom);
			int counter = 0;
			for (String line : searchText) {
				if (!p.matcher(line).matches())
					counter++;
				else
					break;
			}
			// if counter == searchtext.size, then no match has been found => do not alter searchtext.
			if (counter == searchText.size()) {
				if (_useLastMatch)
					Collections.reverse(searchText);
			} else {
				if (_useLastMatch) {
					searchText = searchText.subList(0, Math.min(searchText.size(), counter + 1));
					Collections.reverse(searchText);
				}
				else
					searchText = searchText.subList(counter, searchText.size());				
			}
		}
		
		// at this point we have the trailing list from the first resp. last occurrence of regexFrom
		// or we have the whole text in reversed resp. normal order.
		
		// we do now the same thing as for regexFrom with different boundaries.
		// if regexFrom has not been set, we just search in the reversed list for
		// regexTo and take all lines from the normal text between 0 and line of occurrence.
		// if regexFrom has been set, then we only need to progress the text in normal order,
		// until regexTo has been found.
		if (!StringH.isNullOrEmpty(_regexTo)) {
			final String regexTo = ".*" + _regexTo + ".*";
			Pattern p = Pattern.compile(regexTo);
			int counter = 0;
			for (String line : searchText) {
				if (!p.matcher(line).matches())
					counter++;
				else
					break;
			}
			if (_useLastMatch && StringH.isNullOrEmpty(_regexFrom)) {
				searchText = text.subList(0, text.size() - counter);
			}
			else
				searchText = searchText.subList(0, Math.min(searchText.size(), counter + 1));
		}

		if (_isLine) {
			int lineTo = _lineTo == 0 ? searchText.size() : Math.min(searchText.size(), _lineTo);
			int lineFrom = Math.min(_lineFrom, lineTo);
			searchText = searchText.subList(lineFrom, lineTo);
		}
		String res = "";
		for (String s : searchText) {
			res += s + "\n";
		}
		val.setText(res);

		//		}
		//
		//
		//		
		//			
		//			String res = StringH.withBr(text, new WithBufferedReaderDelegate() {				
		//				@Override
		//				public String Do(BufferedReader br) throws IOException {
		//					String result = "";
		//					for (int i = 0; i < _lineFrom; i++) {
		//						br.readLine();
		//					}
		//
		//					for (int i = 0; i < _lineTo - _lineFrom; i++) {
		//						result += br.readLine() + "\n";
		//					}
		//					return result;
		//				}
		//			});			
		//		}
		//		if (_isLine && (_lineFrom != 0 || _lineTo != 0)) {
		//			String res = StringH.withBr(text, new WithBufferedReaderDelegate() {				
		//				@Override
		//				public String Do(BufferedReader br) throws IOException {
		//					String result = "";
		//					for (int i = 0; i < _lineFrom; i++) {
		//						br.readLine();
		//					}
		//
		//					for (int i = 0; i < _lineTo - _lineFrom; i++) {
		//						result += br.readLine() + "\n";
		//					}
		//					return result;
		//				}
		//			});
		//			val.setText(res);
		//		}
		//
		//		if (_isRegex && (!StringH.isNullOrEmpty(_regexFrom) || !StringH.isNullOrEmpty(_regexTo))) {
		//
		//			final String regexFrom = !StringH.isNullOrEmpty(_regexFrom) ? ".*" + _regexFrom + ".*" : "";
		//			final String regexTo = !StringH.isNullOrEmpty(_regexTo) ? ".*" + _regexTo + ".*" : "";
		//
		//			String res = StringH.withBr(text, new WithBufferedReaderDelegate() {
		//
		//				@Override
		//				public String Do(BufferedReader br) throws IOException {
		//					String line; Matcher m;
		//
		//					if (!StringH.isNullOrEmpty(regexFrom)) {
		//						Pattern p = Pattern.compile(regexFrom);
		//						do {
		//							line = br.readLine();
		//							m = p.matcher(line);
		//						} while(line != null && !m.matches());
		//						if (line == null)
		//							return null;
		//					}
		//
		//					String result = "";
		//					if (!StringH.isNullOrEmpty(regexTo)) {
		//						Pattern p = Pattern.compile(regexTo);
		//						do {
		//							line = br.readLine();
		//							m = p.matcher(line);
		//							result += line + "\n";
		//						} while(line != null && !m.matches());
		//					}
		//					return result;
		//				}
		//			});
		//			val.setText(res);
		//		}
	}

	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public boolean isEmpty(HashMap<String, String> config) {

		boolean _isLine = Boolean.parseBoolean(config.get("isLine"));
		final int _lineFrom = Integer.parseInt(config.get("lineFrom"));
		final int _lineTo = Integer.parseInt(config.get("lineTo"));
		String _regexFrom = config.get("regexFrom");
		String _regexTo = config.get("regexTo");

		if (!_isLine && StringH.isNullOrEmpty(_regexFrom) && StringH.isNullOrEmpty(_regexTo))
			return true;
		if (_isLine && (_lineFrom != 0 || _lineTo != 0)) {
			return false;
		} else if (!StringH.isNullOrEmpty(_regexFrom) || !StringH.isNullOrEmpty(_regexTo)) { 
			return false;
		}
		return true;
	}
}
