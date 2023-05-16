package org.strykeforce.tct;

import static io.javalin.rendering.template.TemplateUtil.model;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class Main {

  public static void main(String[] args) {
    var templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("/templates/");
    var templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);
    JavalinThymeleaf.init(templateEngine);

    var app = Javalin.create().start(7070);
    app.get("/", ctx -> ctx.render("hello.html", model("name", "Hello, Jeff")));
  }
}
