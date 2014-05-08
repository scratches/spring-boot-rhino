Spring Boot autoconfig support for Rhino (the Mozilla
JavaScript engine).

Currently supported: rendering HTML templates using HAML.
Example template (in `classpath:/templates/home.html`):

```haml
!!! XML
!!! strict
%html{ xmlns: "http://www.w3.org/1999/xhtml" }
%head
    %title=title
  %body
  	%h2 A message
  	%div= message + ' at ' + time
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
		return "home";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```
