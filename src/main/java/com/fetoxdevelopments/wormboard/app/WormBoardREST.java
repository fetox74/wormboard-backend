package com.fetoxdevelopments.wormboard.app;

import com.fetoxdevelopments.wormboard.config.DatabaseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(DatabaseConfig.class)
public class WormBoardREST
  extends SpringBootServletInitializer
{
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
  {
    return application.sources(WormBoardREST.class);
  }

  public static void main(String[] args)
  {
    SpringApplication.run(WormBoardREST.class, args);
  }
}