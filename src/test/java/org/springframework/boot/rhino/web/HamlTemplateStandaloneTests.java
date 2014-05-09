package org.springframework.boot.rhino.web;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.rhino.HamlCompiler;
import org.springframework.boot.rhino.web.HamlTemplateStandaloneTests.Application;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest({ "spring.main.web_environment=false", "env.foo=Heaven",
		"foo=World" })
public class HamlTemplateStandaloneTests {

	@Autowired
	private HamlCompiler compiler;

	@Value("classpath:/templates/foo.html.haml")
	private Resource simple;

	public String getWorld() {
		return "World";
	}

	@Test
	public void directCompilation() throws Exception {
		String result = compiler.compile(simple).execute(
				Collections.singletonMap("world", "World"));
		assertTrue("Wrong content", result.contains("Hello World"));
	}

	@EnableAutoConfiguration
	@Configuration
	protected static class Application {

	}

}
