Spring Boot autoconfig support for Rhino (the Mozilla
JavaScript engine).

Currently supported: rendering HTML templates using [haml.js](https://github.com/creationix/haml-js). 
HAML is a YAML-like templating DSL popular in the Ruby community, and the JavaScript version copies 
the basic feature set (but uses JavaScript instead of Ruby for dynamic content).

Example template (in `classpath:/templates/home.html.haml`):

```haml
!!! XML
!!! strict
%html{ xmlns: "http://www.w3.org/1999/xhtml" }
%head
    %title=title
  %body
  	%h2 A message
  	%div= message + ' at ' + time
  	%ul
  	  :each item in items
  	    %li= item
```

application code:

```java
@Configuration
@EnableAutoConfiguration
@Controller
public static class Application {

	@RequestMapping("/")
	public String home(Map<String, Object> model) {
		model.put("time", new Date());
		model.put("message", "Hello World");
		model.put("title", "Hello App");
		model.put("items", Arrays.asList("foo", "bar"));
		return "home";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```

Run the app and then load the HTML page at http://localhost:8080.
