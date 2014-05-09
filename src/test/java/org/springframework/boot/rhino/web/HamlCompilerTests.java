package org.springframework.boot.rhino.web;

import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.springframework.boot.rhino.HamlCompiler;
import org.springframework.boot.rhino.HamlCompiler.Template;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

public class HamlCompilerTests {
	
	private String world = "World";
	
	public String getWorld() {
		return world;
	}

	@Test
	public void compile() throws Exception {
		Template compiled = new HamlCompiler().compile(StreamUtils.copyToString(
				new ClassPathResource("templates/foo.html.haml").getInputStream(),
				Charset.forName("UTF-8")));
		assertTrue(compiled.getScript().contains("'Hello ' + world"));
	}

	@Test
	public void execute() throws Exception {
		HamlCompiler compiler = new HamlCompiler();
		Template compiled = compiler.compile(StreamUtils.copyToString(
				new ClassPathResource("templates/foo.html.haml").getInputStream(),
				Charset.forName("UTF-8")));
		String rendered = compiled.execute(this);
		assertTrue(rendered.contains("Hello World"));
	}

	@Test
	public void executeWithArray() throws Exception {
		HamlCompiler compiler = new HamlCompiler();
		Template compiled = compiler.compile(StreamUtils.copyToString(
				new ClassPathResource("templates/each.html.haml").getInputStream(),
				Charset.forName("UTF-8")));
		String rendered = compiled.execute(Collections.singletonMap("stuff", new String[]{"soup", "nuts"}));
		assertTrue(rendered.contains("soup"));
	}

	@Test
	public void executeWithCollection() throws Exception {
		HamlCompiler compiler = new HamlCompiler();
		Template compiled = compiler.compile(StreamUtils.copyToString(
				new ClassPathResource("templates/each.html.haml").getInputStream(),
				Charset.forName("UTF-8")));
		String rendered = compiled.execute(Collections.singletonMap("stuff", Arrays.asList("soup", "nuts")));
		assertTrue(rendered.contains("soup"));
	}

	@Test
	public void executeWithMap() throws Exception {
		HamlCompiler compiler = new HamlCompiler();
		Template compiled = compiler.compile(StreamUtils.copyToString(
				new ClassPathResource("templates/foo.html.haml").getInputStream(),
				Charset.forName("UTF-8")));
		String rendered = compiled.execute(Collections.singletonMap("world", "World"));
		assertTrue(rendered.contains("Hello World"));
	}

}
