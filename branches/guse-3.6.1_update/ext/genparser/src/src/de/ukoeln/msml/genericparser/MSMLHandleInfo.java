package de.ukoeln.msml.genericparser;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.util.wrapper.Job;

public class MSMLHandleInfo {

	private Job _curJob;
	private MSMLEditor _msmlEditor;

	public MSMLHandleInfo(Job curJob, MSMLEditor editor) {
		_curJob = curJob;
		_msmlEditor = editor;
	}

	public Job getCurJob() {
		return _curJob;
	}

	public MSMLEditor getCurEditor() {
		return _msmlEditor;
	}
}
