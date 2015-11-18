package com.til.ibeat.script;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public final class Config {
	
	public static String getStackTrace(Exception exception){
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		exception.printStackTrace(printWriter);
		String s = writer.toString();
		return s;
	}}
